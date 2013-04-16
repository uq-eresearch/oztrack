package org.oztrack.geoserver;

import java.util.HashMap;

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
        createFireFrequencyLayer(client, workspaceName);
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
            .coverage("workspaces/" + workspaceName + "/coveragestores/gebco_08/coverages/gebco_08")
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

    private void createFireFrequencyLayer(GeoServerClient client, final String workspaceName) throws Exception {
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/fire-frequency-avhrr-1997-2009")
            .template("coveragestores/fire-frequency-avhrr-1997-2009.xml.ftl")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/fire-frequency-avhrr-1997-2009/coverages/fire-frequency-avhrr-1997-2009")
            .template("coverages/fire-frequency-avhrr-1997-2009.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "fire-frequency")
            .template("styles/fire-frequency.sld.ftl")
            .replace();
        client
            .layer("layers/fire-frequency-avhrr-1997-2009")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "fire-frequency-avhrr-1997-2009")
            .param("coverageName", "fire-frequency-avhrr-1997-2009")
            .param("defaultStyle", "oztrack_fire-frequency")
            .param("styles", new String[] {"oztrack_fire-frequency"})
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
            .param("regionColour", new HashMap<String, String>() {{
                put("ARC", "#76B399");
                put("ARP", "#91EDC2");
                put("AUA", "#6F9FC9");
                put("AVW", "#EDE7BC");
                put("BBN", "#8BFEB3");
                put("BBS", "#9BFED4");
                put("BEL", "#9999F5");
                put("BHC", "#CCBEB0");
                put("BRT", "#E0917E");
                put("CAR", "#F8DB9D");
                put("CEA", "#C1CF74");
                put("CEK", "#94CFA5");
                put("CER", "#AB9076");
                put("CHC", "#E9C2A1");
                put("CMC", "#8DC8D0");
                put("COO", "#DDCF8F");
                put("COP", "#BBB6EF");
                put("COS", "#EBFFFF");
                put("CYP", "#7BDD7A");
                put("DAB", "#A1E6A9");
                put("DAC", "#57A057");
                put("DAL", "#87A973");
                put("DEU", "#C3F5DD");
                put("DMR", "#ECAA96");
                put("DRP", "#CDD8FB");
                put("EIU", "#86FE91");
                put("ESP", "#E3CC64");
                put("EYB", "#D7C6E6");
                put("FIN", "#CC8A8A");
                put("FLB", "#CF9BB6");
                put("FUR", "#BEE8FF");
                put("GAS", "#E9E6A1");
                put("GAW", "#DAB2B2");
                put("GES", "#E9F5CD");
                put("GFU", "#9BBB9D");
                put("GID", "#CCA373");
                put("GSD", "#C3B9A6");
                put("GUC", "#8CBB69");
                put("GUP", "#A3E9A1");
                put("GVD", "#CCAC8D");
                put("HAM", "#ECC7A3");
                put("ITI", "#EBFFFF");
                put("JAF", "#C6FEBB");
                put("KAN", "#A18AA9");
                put("KIN", "#7FBFFE");
                put("LSD", "#EDCA81");
                put("MAC", "#DD7575");
                put("MAL", "#E0BD86");
                put("MDD", "#BAB9D7");
                put("MGD", "#E6DCC1");
                put("MII", "#C6CCA5");
                put("MUL", "#D9D9D9");
                put("MUR", "#E0D5A2");
                put("NAN", "#B6C3EF");
                put("NCP", "#CDD8FB");
                put("NET", "#C2BDD7");
                put("NNC", "#B9C5CC");
                put("NOK", "#96BB7B");
                put("NSS", "#BEE8FF");
                put("NUL", "#E0D3B5");
                put("OVP", "#C1D492");
                put("PCK", "#83D7C7");
                put("PIL", "#CCDAB2");
                put("PSI", "#EBFFFF");
                put("RIV", "#9DC5E3");
                put("SAI", "#EBFFFF");
                put("SCP", "#9FB6CD");
                put("SEC", "#98ADDD");
                put("SEH", "#A4D3EE");
                put("SEQ", "#B1D285");
                put("SSD", "#EDBC89");
                put("STP", "#E0BABA");
                put("STU", "#D7F8D6");
                put("SVP", "#73DFFF");
                put("SWA", "#FFFACD");
                put("SYB", "#918DBA");
                put("TAN", "#CCA886");
                put("TCH", "#6959CD");
                put("TIW", "#92EC80");
                put("TNM", "#5BA1EF");
                put("TNS", "#63B8FF");
                put("TSE", "#6191DD");
                put("TSR", "#1289E0");
                put("TWE", "#5B72EF");
                put("VIB", "#AED4AB");
                put("VIM", "#BCC9E6");
                put("WAR", "#A6E9A9");
                put("WET", "#71C0A6");
                put("YAL", "#FEE7C4");
            }})
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