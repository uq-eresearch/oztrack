package org.oztrack.geoserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    private final String geoServerBaseUrl;
    private final String databaseHost;
    private final Integer databasePort;
    private final String databaseName;
    private final String databaseUsername;
    private final String databasePassword;

    public GeoServerUploader(
        final String geoServerUsername,
        final String geoServerPassword,
        final String geoServerBaseUrl,
        final String databaseHost,
        final Integer databasePort,
        final String databaseName,
        final String databaseUsername,
        final String databasePassword
    ) {
        this.geoServerUsername = geoServerUsername;
        this.geoServerPassword = geoServerPassword;
        this.geoServerBaseUrl = geoServerBaseUrl;
        this.databaseHost = databaseHost;
        this.databasePort = databasePort;
        this.databaseName = databaseName;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
    }

    public void upload() throws Exception {
        GeoServerClient client = new GeoServerClient(geoServerUsername, geoServerPassword, geoServerBaseUrl);

        final String namespacePrefix = Constants.namespacePrefix;
        final String namespaceUri = Constants.namespaceURI;
        final String workspaceName = namespacePrefix;

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

        createOzTrackLayers(client, workspaceName, namespaceUri);
        createGEBCOLayers(client, workspaceName);
        createIBRALayers(client, workspaceName, namespaceUri);
        createIMCRALayers(client, workspaceName, namespaceUri);
    }

    private void createOzTrackLayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        final String datastoreName = workspaceName;

        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            datastoreName,
            "application/xml",
            createFreemarkerEntity(
                "datastores/postgis-datastore.xml.ftl",
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
            workspaceName + "_" + "positionfixlayer",
            createFreemarkerEntity(
                "styles/positionfixlayer.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "positionfixlayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "positionfixlayer");
                    put("featuretypeName", "positionfixlayer");
                    put("defaultStyle", "oztrack_positionfixlayer");
                    put("styles", new String[] {"oztrack_positionfixlayer", "point"});
                }}
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
            workspaceName + "_" + "trajectorylayer",
            createFreemarkerEntity(
                "styles/trajectorylayer.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "trajectorylayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "trajectorylayer");
                    put("featuretypeName", "trajectorylayer");
                    put("defaultStyle", "oztrack_trajectorylayer");
                    put("styles", new String[] {"oztrack_trajectorylayer", "line"});
                }}
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
            workspaceName + "_" + "startendlayer",
            createFreemarkerEntity(
                "styles/startendlayer.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "startendlayer",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "startendlayerlayer");
                    put("featuretypeName", "startendlayerlayer");
                    put("defaultStyle", "oztrack_startendlayerlayer");
                    put("styles", new String[] {"oztrack_startendlayerlayer", "point"});
                }}
            )
        );
    }

    private void createGEBCOLayers(GeoServerClient client, final String workspaceName) throws Exception {
        client.replace(
            "workspaces/" + workspaceName + "/coveragestores",
            "gebco_08",
            "application/xml",
            createFreemarkerEntity(
                "coveragestores/gebco_08.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/coveragestores" + "/" + "gebco_08" + "/coverages",
            "gebco_08",
            "application/xml",
            createFreemarkerEntity(
                "coverages/gebco_08.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            workspaceName + "_" + "bathymetry",
            createFreemarkerEntity(
                "styles/bathymetry.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            workspaceName + "_" + "elevation",
            createFreemarkerEntity(
                "styles/elevation.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "gebco_08",
            "application/xml",
            createFreemarkerEntity(
                "layers/coverage-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "gebco_08");
                    put("coverageName", "gebco_08");
                    put("defaultStyle", "oztrack_bathymetry");
                    put("styles", new String[] {"oztrack_bathymetry", "oztrack_elevation"});
                }}
            )
        );
    }

    private void createIBRALayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            "ibra7_regions",
            "application/xml",
            createFreemarkerEntity(
                "datastores/shapefile-datastore.xml.ftl",
                new HashMap<String, Object>() {{
                    put("datastoreName", "ibra7_regions");
                    put("shapefileUrl", "file:shapefiles/IBRA7_regions/IBRA7_regions.shp");
                    put("shapefileCharset", "UTF-8");
                    put("shapefileTimezone", "Australia/Brisbane");
                    put("namespaceUri", namespaceUri);
                }}
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            "ibra7_subregions",
            "application/xml",
            createFreemarkerEntity(
                "datastores/shapefile-datastore.xml.ftl",
                new HashMap<String, Object>() {{
                    put("datastoreName", "ibra7_subregions");
                    put("shapefileUrl", "file:shapefiles/IBRA7_subregions/IBRA7_subregions.shp");
                    put("shapefileCharset", "UTF-8");
                    put("shapefileTimezone", "Australia/Brisbane");
                    put("namespaceUri", namespaceUri);
                }}
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + "ibra7_regions" + "/featuretypes",
            "ibra7_regions",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/ibra7_regions.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + "ibra7_subregions" + "/featuretypes",
            "ibra7_subregions",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/ibra7_subregions.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            workspaceName + "_" + "ibra",
            createFreemarkerEntity(
                "styles/ibra.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "ibra7_regions",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "ibra7_regions");
                    put("featuretypeName", "ibra7_regions");
                    put("defaultStyle", "oztrack_ibra");
                    put("styles", new String[] {"oztrack_ibra", "polygon"});
                }}
            )
        );
        client.replace(
            "layers",
            "ibra7_subregions",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "ibra7_subregions");
                    put("featuretypeName", "ibra7_subregions");
                    put("defaultStyle", "oztrack_ibra");
                    put("styles", new String[] {"oztrack_ibra", "polygon"});
                }}
            )
        );
    }

    private void createIMCRALayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            "imcra4_pb",
            "application/xml",
            createFreemarkerEntity(
                "datastores/shapefile-datastore.xml.ftl",
                new HashMap<String, Object>() {{
                    put("datastoreName", "imcra4_pb");
                    put("shapefileUrl", "file:shapefiles/imcra_provincial_bioregions/imcra4_pb.shp");
                    put("shapefileCharset", "UTF-8");
                    put("shapefileTimezone", "Australia/Brisbane");
                    put("namespaceUri", namespaceUri);
                }}
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores",
            "imcra4_meso",
            "application/xml",
            createFreemarkerEntity(
                "datastores/shapefile-datastore.xml.ftl",
                new HashMap<String, Object>() {{
                    put("datastoreName", "imcra4_meso");
                    put("shapefileUrl", "file:shapefiles/imcra_mesoscale_bioregions/imcra4_meso.shp");
                    put("shapefileCharset", "UTF-8");
                    put("shapefileTimezone", "Australia/Brisbane");
                    put("namespaceUri", namespaceUri);
                }}
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + "imcra4_pb" + "/featuretypes",
            "imcra4_pb",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/imcra4_pb.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "workspaces/" + workspaceName + "/datastores/" + "imcra4_meso" + "/featuretypes",
            "imcra4_meso",
            "application/xml",
            createFreemarkerEntity(
                "featuretypes/imcra4_meso.xml.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replaceStyle(
            workspaceName + "_" + "imcra",
            createFreemarkerEntity(
                "styles/imcra.sld.ftl",
                new HashMap<String, Object>()
            )
        );
        client.replace(
            "layers",
            "imcra4_pb",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "imcra4_pb");
                    put("featuretypeName", "imcra4_pb");
                    put("defaultStyle", "oztrack_imcra");
                    put("styles", new String[] {"oztrack_imcra", "polygon"});
                }}
            )
        );
        client.replace(
            "layers",
            "imcra4_meso",
            "application/xml",
            createFreemarkerEntity(
                "layers/featuretype-layer.xml.ftl",
                new HashMap<String, Object>() {{
                    put("layerName", "imcra4_meso");
                    put("featuretypeName", "imcra4_meso");
                    put("defaultStyle", "oztrack_imcra");
                    put("styles", new String[] {"oztrack_imcra", "polygon"});
                }}
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