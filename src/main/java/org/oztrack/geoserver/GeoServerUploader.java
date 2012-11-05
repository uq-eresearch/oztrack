package org.oztrack.geoserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.oztrack.app.Constants;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GeoServerUploader {
    private final String filesBasePath = "/geoserver";

    private final String geoServerUsername;
    private final String geoServerPassword;
    private final URI geoServerLocalUri;
    private final String databaseHost;
    private final Integer databasePort;
    private final String databaseName;
    private final String databaseUsername;
    private final String databasePassword;

    public GeoServerUploader(
        final String geoServerUsername,
        final String geoServerPassword,
        final String geoServerLocalUri,
        final String databaseHost,
        final Integer databasePort,
        final String databaseName,
        final String databaseUsername,
        final String databasePassword
    ) {
        this.geoServerUsername = geoServerUsername;
        this.geoServerPassword = geoServerPassword;
        this.geoServerLocalUri = URI.create(geoServerLocalUri);
        this.databaseHost = databaseHost;
        this.databasePort = databasePort;
        this.databaseName = databaseName;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
    }

    public void upload() throws Exception {
        GeoServerClient client = new GeoServerClient(geoServerUsername, geoServerPassword, geoServerLocalUri);

        final String namespacePrefix = Constants.namespacePrefix;
        final String namespaceUri = Constants.namespaceURI;
        final String workspaceName = namespacePrefix;
        final String datastoreName = namespacePrefix;

        // Note: creating /namespaces/${name} implicitly creates /workspaces/${name})
        client.replace(
            "namespaces",
            namespacePrefix,
            "application/xml",
            createFreemarkerEntity(
                "namespace.xml.ftl",
                new HashMap<String, Object>() {{
                    put("prefix", namespacePrefix);
                    put("uri", namespaceUri);
                }}
            )
        );

        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            datastoreName,
            "application/xml",
            createFreemarkerEntity(
                "datastore.xml.ftl",
                new HashMap<String, Object>() {{
                    put("datastoreName", datastoreName);
                    put("databaseHost", databaseHost);
                    put("databasePort", databasePort);
                    put("databaseName", databaseName);
                    put("databaseUsername", databaseUsername);
                    put("databasePassword", databasePassword);
                    put("namespaceUri", namespaceUri);
                }}
            )
        );

        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes",
            "positionfixlayer",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/positionfixlayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            "positionfixlayer",
            createFreemarkerEntity(
                "styles/positionfixlayer.sld.ftl",
                new HashMap<String, Object>() {{
                }}
            )
        );
        client.replace(
            "layers",
            "positionfixlayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/positionfixlayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );

        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes",
            "trajectorylayer",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/trajectorylayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            "trajectorylayer",
            createFreemarkerEntity(
                "styles/trajectorylayer.sld.ftl",
                new HashMap<String, Object>() {{
                }}
            )
        );
        client.replace(
            "layers",
            "trajectorylayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/trajectorylayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );

        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes",
            "startendlayer",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/startendlayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            "startendlayer",
            createFreemarkerEntity(
                "styles/startendlayer.sld.ftl",
                new HashMap<String, Object>() {{
                }}
            )
        );
        client.replace(
            "layers",
            "startendlayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/startendlayer.xml.ftl",
                new HashMap<String, Object>()
            )
        );
    }

    private ContentProducer createFreemarkerContentProvider(String templateName, final Map<String, Object> datamodel) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass(), filesBasePath));
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