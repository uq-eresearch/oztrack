package org.oztrack.geoserver;

import java.util.Arrays;

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
        createDLCDClassLayer(client, workspaceName);
        createFireFrequencyLayer(client, workspaceName);
        createCAPADLayers(client, workspaceName, namespaceUri);
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

    private void createDLCDClassLayer(GeoServerClient client, String workspaceName) throws Exception {
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/dlcd-class")
            .template("coveragestores/dlcd-class.xml.ftl")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/dlcd-class/coverages/dlcd-class")
            .template("coverages/dlcd-class.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "dlcd-class")
            .template("styles/dlcd-class.sld.ftl")
            .param("dlcdClasses", Arrays.asList(
                new String[] {"1", "#828282", "Extraction Sites"},
                new String[] {"2", "#000000", "Bare Areas"},
                new String[] {"3", "#0046AD", "Inland Waterbodies"},
                new String[] {"4", "#96E1FF", "Salt Lakes"},
                new String[] {"5", "#5A245A", "Irrigated Cropping"},
                new String[] {"6", "#A626AA", "Irrigated Pasture"},
                new String[] {"7", "#B71234", "Irrigated Sugar"},
                new String[] {"8", "#C68D99", "Rainfed Cropping"},
                new String[] {"9", "#E2C2C7", "Rainfed Pasture"},
                new String[] {"10", "#DB4D69", "Rainfed Sugar"},
                new String[] {"11", "#00B2A0", "Wetlands"},
                new String[] {"12", "#FFFF73", "Forbs - Open"},
                new String[] {"13", "#FFFFCB", "Forbs - Sparse"},
                new String[] {"14", "#FF7900", "Tussock Grasses - Closed"},
                new String[] {"15", "#FFFFFF", "Alpine Grasses - Open"},
                new String[] {"16", "#FFFF73", "Hummock Grasses - Open"},
                new String[] {"17", "#E1E1E1", "Sedges - Open"},
                new String[] {"18", "#FFA952", "Tussock Grasses - Open"},
                new String[] {"19", "#F7E859", "Grassland - Scattered"},
                new String[] {"20", "#FBCE92", "Tussock Grasses - Scattered"},
                new String[] {"21", "#F9E526", "Grassland - Sparse"},
                new String[] {"22", "#FFFFCB", "Hummock Grasses - Sparse"},
                new String[] {"23", "#FDC480", "Tussock Grasses - Sparse"},
                new String[] {"24", "#AF8850", "Shrubs - Closed"},
                new String[] {"25", "#C1A875", "Shrubs - Open"},
                new String[] {"26", "#7D3228", "Chenopod Shrubs - Open"},
                new String[] {"27", "#DDCCA5", "Shrubs - Scattered"},
                new String[] {"28", "#EAAA7A", "Chenopod Shrubs - Scattered"},
                new String[] {"29", "#D1BF91", "Shrubs - Sparse"},
                new String[] {"30", "#8C5F46", "Chenopod Shrubs - Sparse"},
                new String[] {"31", "#008500", "Trees - Closed"},
                new String[] {"32", "#14C200", "Trees - Open"},
                new String[] {"33", "#D6FF8A", "Trees - Scattered"},
                new String[] {"34", "#BAE860", "Trees - Sparse"}
            ))
            .replace();
        client
            .layer("layers/dlcd-class")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "dlcd-class")
            .param("coverageName", "dlcd-class")
            .param("defaultStyle", "oztrack_dlcd-class")
            .param("styles", new String[] {"oztrack_dlcd-class"})
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

    private void createCAPADLayers(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client
            .datastore("workspaces/" + workspaceName + "/datastores/capad10_external_all")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "capad10_external_all")
            .param("shapefileUrl", "file:shapefiles/capad10_external_all/capad10_external_all.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .datastore("workspaces/" + workspaceName + "/datastores/capad10_m_external_all")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "capad10_m_external_all")
            .param("shapefileUrl", "file:shapefiles/capad10_m_external_all/capad10_m_external_all.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "capad10_external_all" + "/featuretypes/capad10_external_all")
            .template("featuretypes/capad10_external_all.xml.ftl")
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "capad10_m_external_all" + "/featuretypes/capad10_m_external_all")
            .template("featuretypes/capad10_m_external_all.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "capad")
            .template("styles/capad.sld.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "capad_m")
            .template("styles/capad_m.sld.ftl")
            .replace();
        client
            .layer("layers/capad10_external_all")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "capad10_external_all")
            .param("featuretypeName", "capad10_external_all")
            .param("defaultStyle", "oztrack_capad")
            .param("styles", new String[] {"oztrack_capad", "polygon"})
            .replace();
        client
            .layer("layers/capad10_m_external_all")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "capad10_m_external_all")
            .param("featuretypeName", "capad10_m_external_all")
            .param("defaultStyle", "oztrack_capad_m")
            .param("styles", new String[] {"oztrack_capad_m", "polygon"})
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
            .param("regions", Arrays.asList(
                new String[] {"ARC", "#76B399", "Arnhem Coast"},
                new String[] {"ARP", "#91EDC2", "Arnhem Plateau"},
                new String[] {"AUA", "#6F9FC9", "Australian Alps"},
                new String[] {"AVW", "#EDE7BC", "Avon Wheatbelt"},
                new String[] {"BBN", "#8BFEB3", "Brigalow Belt North"},
                new String[] {"BBS", "#9BFED4", "Brigalow Belt South"},
                new String[] {"BEL", "#9999F5", "Ben Lomond"},
                new String[] {"BHC", "#CCBEB0", "Broken Hill Complex"},
                new String[] {"BRT", "#E0917E", "Burt Plain"},
                new String[] {"CAR", "#F8DB9D", "Carnarvon"},
                new String[] {"CEA", "#C1CF74", "Central Arnhem"},
                new String[] {"CEK", "#94CFA5", "Central Kimberley"},
                new String[] {"CER", "#AB9076", "Central Ranges"},
                new String[] {"CHC", "#E9C2A1", "Channel Country"},
                new String[] {"CMC", "#8DC8D0", "Central Mackay Coast"},
                new String[] {"COO", "#DDCF8F", "Coolgardie"},
                new String[] {"COP", "#BBB6EF", "Cobar Peneplain"},
                new String[] {"COS", "#EBFFFF", "Coral Sea"},
                new String[] {"CYP", "#7BDD7A", "Cape York Peninsula"},
                new String[] {"DAB", "#A1E6A9", "Daly Basin"},
                new String[] {"DAC", "#57A057", "Darwin Coastal"},
                new String[] {"DAL", "#87A973", "Dampierland"},
                new String[] {"DEU", "#C3F5DD", "Desert Uplands"},
                new String[] {"DMR", "#ECAA96", "Davenport Murchison Ranges"},
                new String[] {"DRP", "#CDD8FB", "Darling Riverine Plains"},
                new String[] {"EIU", "#86FE91", "Einasleigh Uplands"},
                new String[] {"ESP", "#E3CC64", "Esperance Plains"},
                new String[] {"EYB", "#D7C6E6", "Eyre Yorke Block"},
                new String[] {"FIN", "#CC8A8A", "Finke"},
                new String[] {"FLB", "#CF9BB6", "Flinders Lofty Block"},
                new String[] {"FUR", "#BEE8FF", "Furneaux"},
                new String[] {"GAS", "#E9E6A1", "Gascoyne"},
                new String[] {"GAW", "#DAB2B2", "Gawler"},
                new String[] {"GES", "#E9F5CD", "Geraldton Sandplains"},
                new String[] {"GFU", "#9BBB9D", "Gulf Fall and Uplands"},
                new String[] {"GID", "#CCA373", "Gibson Desert"},
                new String[] {"GSD", "#C3B9A6", "Great Sandy Desert"},
                new String[] {"GUC", "#8CBB69", "Gulf Coastal"},
                new String[] {"GUP", "#A3E9A1", "Gulf Plains"},
                new String[] {"GVD", "#CCAC8D", "Great Victoria Desert"},
                new String[] {"HAM", "#ECC7A3", "Hampton"},
                new String[] {"ITI", "#EBFFFF", "Indian Tropical Islands"},
                new String[] {"JAF", "#C6FEBB", "Jarrah Forest"},
                new String[] {"KAN", "#A18AA9", "Kanmantoo"},
                new String[] {"KIN", "#7FBFFE", "King"},
                new String[] {"LSD", "#EDCA81", "Little Sandy Desert"},
                new String[] {"MAC", "#DD7575", "MacDonnell Ranges"},
                new String[] {"MAL", "#E0BD86", "Mallee"},
                new String[] {"MDD", "#BAB9D7", "Murray Darling Depression"},
                new String[] {"MGD", "#E6DCC1", "Mitchell Grass Downs"},
                new String[] {"MII", "#C6CCA5", "Mount Isa Inlier"},
                new String[] {"MUL", "#D9D9D9", "Mulga Lands"},
                new String[] {"MUR", "#E0D5A2", "Murchison"},
                new String[] {"NAN", "#B6C3EF", "Nandewar"},
                new String[] {"NCP", "#CDD8FB", "Naracoorte Coastal Plain"},
                new String[] {"NET", "#C2BDD7", "New England Tablelands"},
                new String[] {"NNC", "#B9C5CC", "NSW North Coast"},
                new String[] {"NOK", "#96BB7B", "Northern Kimberley"},
                new String[] {"NSS", "#BEE8FF", "NSW South Western Slopes"},
                new String[] {"NUL", "#E0D3B5", "Nullarbor"},
                new String[] {"OVP", "#C1D492", "Ord Victoria Plain"},
                new String[] {"PCK", "#83D7C7", "Pine Creek"},
                new String[] {"PIL", "#CCDAB2", "Pilbara"},
                new String[] {"PSI", "#EBFFFF", "Pacific Subtropical Islands"},
                new String[] {"RIV", "#9DC5E3", "Riverina"},
                new String[] {"SAI", "#EBFFFF", "Subantarctic Islands"},
                new String[] {"SCP", "#9FB6CD", "South East Coastal Plain"},
                new String[] {"SEC", "#98ADDD", "South East Corner"},
                new String[] {"SEH", "#A4D3EE", "South Eastern Highlands"},
                new String[] {"SEQ", "#B1D285", "South Eastern Queensland"},
                new String[] {"SSD", "#EDBC89", "Simpson Strzelecki Dunefields"},
                new String[] {"STP", "#E0BABA", "Stony Plains"},
                new String[] {"STU", "#D7F8D6", "Sturt Plateau"},
                new String[] {"SVP", "#73DFFF", "Southern Volcanic Plain"},
                new String[] {"SWA", "#FFFACD", "Swan Coastal Plain"},
                new String[] {"SYB", "#918DBA", "Sydney Basin"},
                new String[] {"TAN", "#CCA886", "Tanami"},
                new String[] {"TCH", "#6959CD", "Tasmanian Central Highlands"},
                new String[] {"TIW", "#92EC80", "Tiwi Cobourg"},
                new String[] {"TNM", "#5BA1EF", "Tasmanian Northern Midlands"},
                new String[] {"TNS", "#63B8FF", "Tasmanian Northern Slopes"},
                new String[] {"TSE", "#6191DD", "Tasmanian South East"},
                new String[] {"TSR", "#1289E0", "Tasmanian Southern Ranges"},
                new String[] {"TWE", "#5B72EF", "Tasmanian West"},
                new String[] {"VIB", "#AED4AB", "Victoria Bonaparte"},
                new String[] {"VIM", "#BCC9E6", "Victorian Midlands"},
                new String[] {"WAR", "#A6E9A9", "Warren"},
                new String[] {"WET", "#71C0A6", "Wet Tropics"},
                new String[] {"YAL", "#FEE7C4", "Yalgoo"}
            ))
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