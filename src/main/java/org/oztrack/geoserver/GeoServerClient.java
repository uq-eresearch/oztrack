package org.oztrack.geoserver;

import java.net.ProxySelector;
import java.net.URI;

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
import org.apache.http.entity.ContentType;
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

public class GeoServerClient {

    private static final Logger logger = LoggerFactory.getLogger(GeoServerClient.class);

    private final URI geoServerRestUri;

    private final HttpClient httpClient;
    private final HttpContext httpContext;

    public GeoServerClient(
        String username,
        String password,
        String geoServerBaseUrl
    ) {
        this.geoServerRestUri = URI.create(geoServerBaseUrl + "/rest/");

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

        // Create execution context wiht AuthCache for preemptive authentication
        BasicHttpContext httpContext = new BasicHttpContext();
        AuthCache authCache = new BasicAuthCache();
        authCache.put(httpHost, new BasicScheme());
        httpContext.setAttribute(ClientContext.AUTH_CACHE, authCache);

        this.httpClient = httpClient;
        this.httpContext = httpContext;
    }

    public void replace(String parentPath, String name, String contentType, HttpEntity entity) throws Exception {
        boolean exists = checkExists(parentPath + "/" + name);
        if (exists) {
            put(parentPath, name, contentType, entity);
        }
        else {
            post(parentPath, name, contentType, entity);
        }
    }

    public void replaceStyle(String style, HttpEntity entity) throws Exception {
        if (probe(String.format("styles/%s.sld", style)) == 200) {
            put("styles", style+".sld", "application/vnd.ogc.sld+xml", entity);
        }
        else if (postProbe("styles.sld", "application/vnd.ogc.sld+xml", entity) != 201) {
            // this is the dodgy part were we assume that the sld file is still on the server
            // but its style xml is missing (due to flaky geoserver delete of styles)
            // the workaround posts a new style xml and then update the sld file
            String stylesXml =
                "<style>" +
                "<name>" + style + "</name>" +
                "<sldVersion><version>1.0.0</version></sldVersion>" +
                "<filename>" + style + ".sld</filename>" +
                "</style>";
            logger.debug(stylesXml);
            StringEntity stylesXmlEntity = new StringEntity(stylesXml, ContentType.create("application/xml", "utf-8"));
            if (postProbe("styles.xml", "application/xml", stylesXmlEntity) == 201) {
                put("styles", style+".sld", "application/vnd.ogc.sld+xml", entity);
            }
            else {
                throw new RuntimeException("failed to replace style " + style);
            }
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

    public void post(String parentPath, String name, String contentType, HttpEntity entity) throws Exception {
        post(geoServerRestUri.resolve(parentPath), contentType, entity);
    }

    private void post(URI uri, String contentType, HttpEntity entity) throws Exception {
        HttpResponse response = postInternal(uri, contentType, entity);
        checkResponse(response);
        EntityUtils.consume(response.getEntity());
    }

    private int postProbe(String path, String contentType, HttpEntity entity) throws Exception {
        return postProbe(geoServerRestUri.resolve(path), contentType, entity);
    }

    private int postProbe(URI uri, String contentType, HttpEntity entity) throws Exception {
        HttpResponse response = postInternal(uri, contentType, entity);
        int status = response.getStatusLine().getStatusCode();
        EntityUtils.consume(response.getEntity());
        return status;
    }

    private HttpResponse postInternal(URI uri, String contentType, HttpEntity entity) throws Exception {
        HttpEntityEnclosingRequestBase request = new HttpPost();
        request.setURI(uri);
        request.setHeader("Content-Type", contentType);
        request.setEntity(entity);
        logger.debug("post " + uri.toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        logger.debug("response status code: " + response.getStatusLine().getStatusCode());
        return response;
    }

    public void put(String parentPath, String name, String contentType, HttpEntity entity) throws Exception {
        HttpEntityEnclosingRequestBase request = new HttpPut();
        request.setURI(geoServerRestUri.resolve(parentPath + "/" + name));
        request.setHeader("Content-Type", contentType);
        request.setEntity(entity);
        logger.debug("put " + request.getURI().toString());
        HttpResponse response = httpClient.execute(request, httpContext);
        logger.debug("response status code: " + response.getStatusLine().getStatusCode());
        checkResponse(response);
        EntityUtils.consume(response.getEntity());
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
}