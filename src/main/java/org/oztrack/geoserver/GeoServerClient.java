package org.oztrack.geoserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ProxySelector;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GeoServerClient {
    private static final Logger logger = LoggerFactory.getLogger(GeoServerClient.class);

    private final URI geoServerRestUri;
    private final String templateBasePath;

    private final HttpClient httpClient;
    private final HttpContext httpContext;

    public class GeoServerResourceRequest {
        protected String parentPath;
        protected String name;
        protected String contentType;
        protected String templatePath;
        protected HashMap<String, Object> params;

        protected GeoServerResourceRequest(String resourcePath, String contentType) {
            int lastIndexOfSlash = resourcePath.lastIndexOf("/");
            this.parentPath = resourcePath.substring(0, lastIndexOfSlash);
            this.name = resourcePath.substring(lastIndexOfSlash + 1);
            this.contentType = contentType;
            this.params = new HashMap<String, Object>();
        }

        protected GeoServerResourceRequest(String resourcePath) {
            this(resourcePath, "application/xml");
        }

        public GeoServerResourceRequest template(String templatePath) {
            this.templatePath = templatePath;
            return this;
        }

        public GeoServerResourceRequest param(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public void replace() throws Exception {
            HttpEntity entity = createFreemarkerEntity(templatePath, params);
            replaceInternal(parentPath, name, contentType, entity);
        }
    }

    public class GeoServerStyleRequest extends GeoServerResourceRequest {
        protected GeoServerStyleRequest(String resourcePath) {
            super(resourcePath, "application/vnd.ogc.sld+xml");
        }

        @Override
        public void replace() throws Exception {
            HttpEntity entity = createFreemarkerEntity(templatePath, params);
            if (probe(parentPath + "/" + name + ".sld") == 200) {
                putInternal(geoServerRestUri.resolve(parentPath + "/" + name + ".sld"), "application/vnd.ogc.sld+xml", entity);
            }
            else if (postProbe("styles.sld", "application/vnd.ogc.sld+xml", entity) != 201) {
                // this is the dodgy part were we assume that the sld file is still on the server
                // but its style xml is missing (due to flaky geoserver delete of styles)
                // the workaround posts a new style xml and then update the sld file
                String stylesXml =
                    "<style>" +
                    "<name>" + name + "</name>" +
                    "<sldVersion><version>1.0.0</version></sldVersion>" +
                    "<filename>" + name + ".sld</filename>" +
                    "</style>";
                logger.debug(stylesXml);
                StringEntity stylesXmlEntity = new StringEntity(stylesXml, ContentType.create("application/xml", "utf-8"));
                if (postProbe("styles.xml", "application/xml", stylesXmlEntity) == 201) {
                    putInternal(geoServerRestUri.resolve(parentPath + "/" + name + ".sld"), "application/vnd.ogc.sld+xml", entity);
                }
                else {
                    throw new RuntimeException("failed to replace style " + name);
                }
            }
        }
    }

    public GeoServerClient(
        String username,
        String password,
        String geoServerBaseUrl,
        String templateBasePath
    ) {
        this.geoServerRestUri = URI.create(geoServerBaseUrl + "/rest/");
        this.templateBasePath = templateBasePath;

        DefaultHttpClient httpClient = new DefaultHttpClient();

        // Use HTTP proxy settings from JVM: see system properties
        // http.proxyHost, http.proxyPort, and http.nonProxyHosts.
        ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
            httpClient.getConnectionManager().getSchemeRegistry(),
            ProxySelector.getDefault()
        );
        httpClient.setRoutePlanner(routePlanner);

        // Set username/password credentials for all GeoServer requests
        HttpHost httpHost = new HttpHost(geoServerRestUri.getHost(), geoServerRestUri.getPort(), geoServerRestUri.getScheme());
        AuthScope authScope = new AuthScope(httpHost.getHostName(), httpHost.getPort());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        httpClient.getCredentialsProvider().setCredentials(authScope, credentials);

        // Create execution context with AuthCache for preemptive authentication
        BasicHttpContext httpContext = new BasicHttpContext();
        AuthCache authCache = new BasicAuthCache();
        authCache.put(httpHost, new BasicScheme());
        httpContext.setAttribute(ClientContext.AUTH_CACHE, authCache);

        this.httpClient = httpClient;
        this.httpContext = httpContext;
    }

    // Note: creating /namespaces/${name} implicitly creates /workspaces/${name})
    public GeoServerResourceRequest namespace(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerResourceRequest datastore(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerResourceRequest featuretype(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerResourceRequest layer(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerResourceRequest coveragestore(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerResourceRequest coverage(String resourcePath) {
        return new GeoServerResourceRequest(resourcePath);
    }

    public GeoServerStyleRequest style(String resourcePath) {
        return new GeoServerStyleRequest(resourcePath);
    }

    private int postInternal(URI uri, String contentType, HttpEntity entity) throws Exception {
        HttpEntityEnclosingRequestBase request = new HttpPost();
        request.setURI(uri);
        request.setHeader("Content-Type", contentType);
        request.setEntity(entity);
        logger.debug("post " + uri.toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("response status code: " + status);
        checkResponse(response);
        EntityUtils.consume(response.getEntity());
        return status;
    }

    private int putInternal(URI uri, String contentType, HttpEntity entity) throws Exception {
        HttpEntityEnclosingRequestBase request = new HttpPut();
        request.setURI(uri);
        request.setHeader("Content-Type", contentType);
        request.setEntity(entity);
        logger.debug("put " + request.getURI().toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("response status code: " + status);
        checkResponse(response);
        EntityUtils.consume(response.getEntity());
        return status;
    }

    private boolean checkExists(String path) throws Exception {
        HttpHead request = new HttpHead();
        request.setURI(geoServerRestUri.resolve(path));
        HttpResponse response = httpClient.execute(request, httpContext);
        if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
            return true;
        }
        else if (response.getStatusLine().getStatusCode() == 404) {
            return false;
        }
        checkResponse(response);
        throw new RuntimeException("Could not check existence of GeoServer resource");
    }

    public void replaceInternal(String parentPath, String name, String contentType, HttpEntity entity) throws Exception {
        if (checkExists(parentPath + "/" + name)) {
            putInternal(geoServerRestUri.resolve(parentPath + "/" + name), contentType, entity);
        }
        else {
            postInternal(geoServerRestUri.resolve(parentPath), contentType, entity);
        }
    }

    private void checkResponse(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return; // Success
        }
        else {
            String message =
                (statusCode == 401) ? "are username and password configured?" :
                (statusCode == 405) ? "possible conflict with existing data" :
                "unexpected status";
            throw new RuntimeException(
                "Status " + statusCode + ": " + response.getStatusLine().getReasonPhrase() + "\n" +
                "GeoServer message: " + IOUtils.toString(response.getEntity().getContent()) + "\n" +
                "Our message: " + message
            );
        }
    }

    private int probe(String path) throws Exception {
        HttpGet request = new HttpGet();
        request.setURI(geoServerRestUri.resolve(path));
        logger.debug("get " + request.getURI().toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("response status code: " + status);
        EntityUtils.consume(response.getEntity());
        return status;
    }

    private int postProbe(String path, String contentType, HttpEntity entity) throws Exception {
        URI uri = geoServerRestUri.resolve(path);
        HttpEntityEnclosingRequestBase request = new HttpPost();
        request.setURI(uri);
        request.setHeader("Content-Type", contentType);
        request.setEntity(entity);
        logger.debug("post " + uri.toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        int status = response.getStatusLine().getStatusCode();
        logger.debug("response status code: " + status);
        EntityUtils.consume(response.getEntity());
        return status;
    }

    private ContentProducer createFreemarkerContentProvider(String templateName, final Map<String, Object> datamodel) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass(), templateBasePath));
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        final Template template = configuration.getTemplate(templateName);
        ContentProducer contentProducer = new ContentProducer() {
            @Override
            public void writeTo(OutputStream out) throws IOException {
                OutputStreamWriter writer = null;
                try {
                    writer = new OutputStreamWriter(out);
                    template.process(datamodel, writer);
                }
                catch (TemplateException e) {
                    throw new IOException(e);
                }
                finally {
                    IOUtils.closeQuietly(writer);
                }
            }
        };
        return contentProducer;
    }

    private HttpEntity createFreemarkerEntity(String templateName, final Map<String, Object> datamodel) throws Exception {
        return new EntityTemplate(createFreemarkerContentProvider(templateName, datamodel));
    }
}