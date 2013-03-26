package org.oztrack.geoserver;

import org.oztrack.app.Constants;

public class GeoServerUploader {
    private final String templateBasePath = "/geoserver";

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
        GeoServerClient client = new GeoServerClient(geoServerUsername, geoServerPassword, geoServerBaseUrl, templateBasePath);

        final String namespacePrefix = Constants.namespacePrefix;
        final String namespaceUri = Constants.namespaceURI;
        final String workspaceName = namespacePrefix;

        client
            .namespace("namespaces/" + namespacePrefix)
            .template("namespace.xml.ftl")
            .param("prefix", namespacePrefix)
            .param("uri", namespaceUri)
            .replace();

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

        client
            .datastore("workspaces/" + workspaceName + "/datastores/" + datastoreName)
            .template("datastores/postgis-datastore.xml.ftl")
            .param("datastoreName", datastoreName)
            .param("databaseHost", databaseHost)
            .param("databasePort", databasePort)
            .param("databaseName", databaseName)
            .param("databaseUsername", databaseUsername)
            .param("databasePassword", databasePassword)
            .param("namespaceUri", namespaceUri)
            .replace();

        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes/positionfixlayer")
            .template("featuretypes/positionfixlayer.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "positionfixlayer")
            .template("styles/positionfixlayer.sld.ftl")
            .replace();
        client
            .layer("layers/positionfixlayer")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "positionfixlayer")
            .param("featuretypeName", "positionfixlayer")
            .param("defaultStyle", "oztrack_positionfixlayer")
            .param("styles", new String[] {"oztrack_positionfixlayer", "point"})
            .replace();

        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes/trajectorylayer")
            .template("featuretypes/trajectorylayer.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "trajectorylayer")
            .template("styles/trajectorylayer.sld.ftl")
            .replace();
        client
            .layer("layers/trajectorylayer")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "trajectorylayer")
            .param("featuretypeName", "trajectorylayer")
            .param("defaultStyle", "oztrack_trajectorylayer")
            .param("styles", new String[] {"oztrack_trajectorylayer", "line"})
            .replace();

        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + datastoreName + "/featuretypes/startendlayer")
            .template("featuretypes/startendlayer.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "startendlayer")
            .template("styles/startendlayer.sld.ftl")
            .replace();
        client
            .layer("layers/startendlayer")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "startendlayerlayer")
            .param("featuretypeName", "startendlayerlayer")
            .param("defaultStyle", "oztrack_startendlayerlayer")
            .param("styles", new String[] {"oztrack_startendlayerlayer", "point"})
            .replace();
    }

    private void createGEBCOLayers(GeoServerClient client, final String workspaceName) throws Exception {
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/gebco_08")
            .template("coveragestores/gebco_08.xml.ftl")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores" + "/" + "gebco_08" + "/coverages/gebco_08")
            .template("coverages/gebco_08.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "bathymetry")
            .template("styles/bathymetry.sld.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "elevation")
            .template("styles/elevation.sld.ftl")
            .replace();
        client
            .layer("layers/gebco_08")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "gebco_08")
            .param("coverageName", "gebco_08")
            .param("defaultStyle", "oztrack_bathymetry")
            .param("styles", new String[] {"oztrack_bathymetry", "oztrack_elevation"})
            .replace();
    }

    private void createIBRALayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client
            .datastore("workspaces/" + workspaceName + "/datastores/ibra7_regions")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "ibra7_regions")
            .param("shapefileUrl", "file:shapefiles/IBRA7_regions/IBRA7_regions.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .datastore("workspaces/" + workspaceName + "/datastores/ibra7_subregions")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "ibra7_subregions")
            .param("shapefileUrl", "file:shapefiles/IBRA7_subregions/IBRA7_subregions.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "ibra7_regions" + "/featuretypes/ibra7_regions")
            .template("featuretypes/ibra7_regions.xml.ftl")
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "ibra7_subregions" + "/featuretypes/ibra7_subregions")
            .template("featuretypes/ibra7_subregions.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "ibra")
            .template("styles/ibra.sld.ftl")
            .replace();
        client
            .layer("layers/ibra7_regions")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "ibra7_regions")
            .param("featuretypeName", "ibra7_regions")
            .param("defaultStyle", "oztrack_ibra")
            .param("styles", new String[] {"oztrack_ibra", "polygon"})
            .replace();
        client
            .layer("layers/ibra7_subregions")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "ibra7_subregions")
            .param("featuretypeName", "ibra7_subregions")
            .param("defaultStyle", "oztrack_ibra")
            .param("styles", new String[] {"oztrack_ibra", "polygon"})
            .replace();
    }

    private void createIMCRALayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client
            .datastore("workspaces/" + workspaceName + "/datastores/imcra4_pb")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "imcra4_pb")
            .param("shapefileUrl", "file:shapefiles/imcra_provincial_bioregions/imcra4_pb.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .datastore("workspaces/" + workspaceName + "/datastores/imcra4_meso")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "imcra4_meso")
            .param("shapefileUrl", "file:shapefiles/imcra_mesoscale_bioregions/imcra4_meso.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "imcra4_pb" + "/featuretypes/imcra4_pb")
            .template("featuretypes/imcra4_pb.xml.ftl")
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "imcra4_meso" + "/featuretypes/imcra4_meso")
            .template("featuretypes/imcra4_meso.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "imcra")
            .template("styles/imcra.sld.ftl")
            .replace();
        client
            .layer("layers/imcra4_pb")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "imcra4_pb")
            .param("featuretypeName", "imcra4_pb")
            .param("defaultStyle", "oztrack_imcra")
            .param("styles", new String[] {"oztrack_imcra", "polygon"})
            .replace();
        client
            .layer("layers/imcra4_meso")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "imcra4_meso")
            .param("featuretypeName", "imcra4_meso")
            .param("defaultStyle", "oztrack_imcra")
            .param("styles", new String[] {"oztrack_imcra", "polygon"})
            .replace();
    }
}