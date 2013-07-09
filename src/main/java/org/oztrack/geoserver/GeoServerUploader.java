package org.oztrack.geoserver;

import java.util.Arrays;

import org.oztrack.app.Constants;

public class GeoServerUploader {
    private final String templateBasePath = "/org/oztrack/geoserver";

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
        createCARSLayers(client, workspaceName);
        createDLCDClassLayer(client, workspaceName);
        createNVISLayers(client, workspaceName);
        createFireFrequencyLayer(client, workspaceName);
        createIUCNLayers(client, workspaceName, namespaceUri);
        createNRMRegionsLayer(client, workspaceName, namespaceUri);
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
            .param("layerName", "startendlayer")
            .param("featuretypeName", "startendlayer")
            .param("defaultStyle", "oztrack_startendlayer")
            .param("styles", new String[] {"oztrack_startendlayer", "point"})
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

    private void createCARSLayers(GeoServerClient client, final String workspaceName) throws Exception {
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009_nitrate")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009_nitrate")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009_nitrate/coverages/cars2009_nitrate")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009_nitrate")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009_nitrate")
            .template("styles/cars2009_nitrate.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009_nitrate")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009_nitrate")
            .param("coverageName", "cars2009_nitrate")
            .param("defaultStyle", "oztrack_cars2009_nitrate")
            .param("styles", new String[] {"oztrack_cars2009_nitrate"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009_oxygen")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009_oxygen")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009_oxygen/coverages/cars2009_oxygen")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009_oxygen")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009_oxygen")
            .template("styles/cars2009_oxygen.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009_oxygen")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009_oxygen")
            .param("coverageName", "cars2009_oxygen")
            .param("defaultStyle", "oztrack_cars2009_oxygen")
            .param("styles", new String[] {"oztrack_cars2009_oxygen"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009_phosphate")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009_phosphate")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009_phosphate/coverages/cars2009_phosphate")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009_phosphate")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009_phosphate")
            .template("styles/cars2009_phosphate.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009_phosphate")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009_phosphate")
            .param("coverageName", "cars2009_phosphate")
            .param("defaultStyle", "oztrack_cars2009_phosphate")
            .param("styles", new String[] {"oztrack_cars2009_phosphate"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009_silicate")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009_silicate")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009_silicate/coverages/cars2009_silicate")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009_silicate")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009_silicate")
            .template("styles/cars2009_silicate.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009_silicate")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009_silicate")
            .param("coverageName", "cars2009_silicate")
            .param("defaultStyle", "oztrack_cars2009_silicate")
            .param("styles", new String[] {"oztrack_cars2009_silicate"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009a_salinity")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_salinity")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009a_salinity/coverages/cars2009a_salinity")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_salinity")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009a_salinity")
            .template("styles/cars2009a_salinity.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009a_salinity")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009a_salinity")
            .param("coverageName", "cars2009a_salinity")
            .param("defaultStyle", "oztrack_cars2009a_salinity")
            .param("styles", new String[] {"oztrack_cars2009a_salinity"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009a_temperature")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_temperature")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009a_temperature/coverages/cars2009a_temperature")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_temperature")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009a_temperature")
            .template("styles/cars2009a_temperature.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009a_temperature")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009a_temperature")
            .param("coverageName", "cars2009a_temperature")
            .param("defaultStyle", "oztrack_cars2009a_temperature")
            .param("styles", new String[] {"oztrack_cars2009a_temperature"})
            .replace();

        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/cars2009a_hgt2000")
            .template("coveragestores/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_hgt2000")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/cars2009a_hgt2000/coverages/cars2009a_hgt2000")
            .template("coverages/cars2009.xml.ftl")
            .param("coverageName", "cars2009a_hgt2000")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "cars2009a_hgt2000")
            .template("styles/cars2009a_hgt2000.sld.ftl")
            .replace();
        client
            .layer("layers/cars2009a_hgt2000")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "cars2009a_hgt2000")
            .param("coverageName", "cars2009a_hgt2000")
            .param("defaultStyle", "oztrack_cars2009a_hgt2000")
            .param("styles", new String[] {"oztrack_cars2009a_hgt2000"})
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

    private void createNVISLayers(GeoServerClient client, String workspaceName) throws Exception {
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/nvis_4_1_aust_mvg")
            .template("coveragestores/nvis_4_1_aust_mvg.xml.ftl")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/nvis_4_1_aust_mvg/coverages/nvis_4_1_aust_mvg")
            .template("coverages/nvis_4_1_aust_mvg.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "nvis-mvg")
            .template("styles/nvis-mvg.sld.ftl")
            .param("mvgList", Arrays.asList(
                new String[] {"1", "#FF0000", "Rainforests and Vine Thickets"},
                new String[] {"2", "#034D00", "Eucalypt Tall Open Forests"},
                new String[] {"3", "#008200", "Eucalypt Open Forests"},
                new String[] {"4", "#4CE600", "Eucalypt Low Open Forests"},
                new String[] {"5", "#C1D6C8", "Eucalypt Woodlands"},
                new String[] {"6", "#92AD2F", "Acacia Forests and Woodlands"},
                new String[] {"7", "#90BA8D", "Callitris Forests and Woodlands"},
                new String[] {"8", "#00D6A8", "Casuarina Forests and Woodlands"},
                new String[] {"9", "#B2EBB2", "Melaleuca Forests and Woodlands"},
                new String[] {"10", "#73FFDE", "Other Forests and Woodlands"},
                new String[] {"11", "#E0FFEB", "Eucalypt Open Woodlands"},
                new String[] {"12", "#C8C2FF", "Tropical Eucalypt Woodlands/Grasslands"},
                new String[] {"13", "#F0E48D", "Acacia Open Woodlands"},
                new String[] {"14", "#BDB66A", "Mallee Woodlands and Shrublands"},
                new String[] {"15", "#8A7213", "Low Closed Forests and Tall Closed Shrublands"},
                new String[] {"16", "#FABEBE", "Acacia Shrublands"},
                new String[] {"17", "#8A7265", "Other Shrublands"},
                new String[] {"18", "#FFA07A", "Heathlands"},
                new String[] {"19", "#B8AB8D", "Tussock Grasslands"},
                new String[] {"20", "#FFF8DB", "Hummock Grasslands"},
                new String[] {"21", "#FCE4A7", "Other Grasslands, Herblands, Sedgelands and Rushlands"},
                new String[] {"22", "#FCE4DC", "Chenopod Shrublands, Samphire Shrublands and Forblands"},
                new String[] {"23", "#15A3AB", "Mangroves"},
                new String[] {"24", "#006FFF", "Inland aquatic - freshwater, salt lakes, lagoons"},
                new String[] {"25", "#FFFFFF", "Cleared, non-native vegetation, buildings"},
                new String[] {"26", "#4F4F4F", "Unclassified native vegetation"},
                new String[] {"27", "#CCCCCC", "Naturally bare - sand, rock, claypan, mudflat"},
                new String[] {"28", "#96DBF2", "Sea and estuaries"},
                new String[] {"29", "#9C9C9C", "Regrowth, modified native vegetation"},
                new String[] {"30", "#FFAA00", "Unclassified Forest"},
                new String[] {"31", "#D69DBC", "Other Open Woodlands"},
                new String[] {"32", "#E0D988", "Mallee Open Woodlands and Sparse Mallee Shrublands"},
                new String[] {"99", "#EBEBEB", "Unknown/no data"}
            ))
            .replace();
        client
            .layer("layers/nvis_4_1_aust_mvg")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "nvis_4_1_aust_mvg")
            .param("coverageName", "nvis_4_1_aust_mvg")
            .param("defaultStyle", "oztrack_nvis-mvg")
            .param("styles", new String[] {"oztrack_nvis-mvg"})
            .replace();
        client
            .coveragestore("workspaces/" + workspaceName + "/coveragestores/nvis_4_1_aust_mvs")
            .template("coveragestores/nvis_4_1_aust_mvs.xml.ftl")
            .replace();
        client
            .coverage("workspaces/" + workspaceName + "/coveragestores/nvis_4_1_aust_mvs/coverages/nvis_4_1_aust_mvs")
            .template("coverages/nvis_4_1_aust_mvs.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "nvis-mvs")
            .template("styles/nvis-mvs.sld.ftl")
            .param("mvsList", Arrays.asList(
                new String[] {"1", "#DF7F7F", "Cool temperate rainforest"},
                new String[] {"2", "#DF7F00", "Tropical or sub-tropical rainforest"},
                new String[] {"3", "#005F00", "Eucalyptus (+/- tall) open forest with a dense broad-leaved and/or tree-fern understorey (wet sclerophyll)"},
                new String[] {"4", "#007F00", "Eucalyptus open forests with a shrubby understorey"},
                new String[] {"5", "#007F5F", "Eucalyptus open forests with a grassy understorey"},
                new String[] {"6", "#DF7F5F", "Warm Temperate Rainforest"},
                new String[] {"7", "#3FBF5F", "Tropical Eucalyptus forest and woodlands with a tall annual grassy understorey"},
                new String[] {"8", "#9F9F00", "Eucalyptus woodlands with a shrubby understorey"},
                new String[] {"9", "#9F9F5F", "Eucalyptus woodlands with a tussock grass understorey"},
                new String[] {"10", "#9F9F7F", "Eucalyptus woodlands with a hummock grass understorey"},
                new String[] {"11", "#9F7F3F", "Tropical mixed spp forests and woodlands"},
                new String[] {"12", "#BFBF00", "Callitris forests and woodlands "},
                new String[] {"13", "#DFBF00", "Brigalow (Acacia harpophylla) forests and woodlands"},
                new String[] {"14", "#DFBF5F", "Other Acacia forests and woodlands"},
                new String[] {"15", "#9FBFDF", "Melaleuca open forests and woodlands"},
                new String[] {"16", "#9F7F7F", "Other forests and woodlands"},
                new String[] {"17", "#7FBFBF", "Boulders/rock with algae, lichen or scattered plants, or alpine fjaeldmarks"},
                new String[] {"18", "#9FBF9F", "Eucalyptus low open woodlands with hummock grass"},
                new String[] {"19", "#9FBF7F", "Eucalyptus low open woodlands with tussock grass"},
                new String[] {"20", "#DFBF3F", "Mulga (Acacia aneura) woodlands +/- tussock grass +/- forbs"},
                new String[] {"21", "#DFDF5F", "Other Acacia tall open shrublands and [tall] shrublands"},
                new String[] {"22", "#DFBF7F", "Acacia (+/- low) open woodlands and shrublands with chenopods"},
                new String[] {"23", "#DFBF9F", "Acacia (+/- low) open woodlands and shrublands with hummock grass"},
                new String[] {"24", "#DFBFBF", "Acacia (+/- low) open woodlands and shrublands +/- tussock grass"},
                new String[] {"25", "#DFDFBF", "Acacia (+/- low) open woodlands and sparse shrublands with a shrubby understorey"},
                new String[] {"26", "#BFBF7F", "Casuarina and Allocasuarina forests and woodlands"},
                new String[] {"27", "#DF9FBF", "Mallee with hummock grass"},
                new String[] {"28", "#FFBF5F", "Low closed forest or tall closed shrublands (including Acacia, Melaleuca and Banksia)"},
                new String[] {"29", "#DF9F5F", "Mallee with a dense shrubby understorey"},
                new String[] {"30", "#FF9F7F", "Heath"},
                new String[] {"31", "#BFFFBF", "Saltbush and Bluebush shrublands"},
                new String[] {"32", "#BFBF9F", "Other shrublands"},
                new String[] {"33", "#FFFFDF", "Hummock grasslands"},
                new String[] {"34", "#FFDF5F", "Mitchell grass (Astrebla) tussock grasslands"},
                new String[] {"35", "#FFDF7F", "Blue grass (Dicanthium) and tall bunch grass (Vitiveria syn: Chrysopogon) tussock grasslands"},
                new String[] {"36", "#FFDF9F", "Temperate tussock grasslands"},
                new String[] {"37", "#FFDFBF", "Other tussock grasslands"},
                new String[] {"38", "#7FFFBF", "Wet tussock grassland with herbs, sedges or rushes, herblands or ferns"},
                new String[] {"39", "#BFDFFF", "Mixed chenopod, samphire +/- forbs"},
                new String[] {"40", "#009FBF", "Mangroves"},
                new String[] {"41", "#7FFF9F", "Saline or brackish sedgelands or grasslands"},
                new String[] {"42", "#FFFF9F", "Naturally bare, sand, rock, claypan, mudflat"},
                new String[] {"43", "#007FDF", "Salt lakes and lagoons"},
                new String[] {"44", "#007FFF", "Freshwater, dams, lakes, lagoons or aquatic plants"},
                new String[] {"45", "#DFDF7F", "Mulga (Acacia aneura) open woodlands and sparse shrublands +/- tussock grass"},
                new String[] {"46", "#009FFF", "Sea, estuaries (includes seagrass)"},
                new String[] {"47", "#9FBF00", "Eucalyptus open woodlands with shrubby understorey"},
                new String[] {"48", "#9FBF3F", "Eucalyptus open woodlands with a grassy understorey"},
                new String[] {"49", "#BFDFDF", "Melaleuca shrublands and open shrublands"},
                new String[] {"50", "#9F7F5F", "Banksia woodlands"},
                new String[] {"51", "#DFDF3F", "Mulga (Acacia aneura) woodlands and shrublands with hummock grass"},
                new String[] {"52", "#D60039", "Mulga (Acacia aneura) open woodlands and sparse shrublands with hummock grass"},
                new String[] {"53", "#C4BF5F", "Eucalyptus low open woodlands with a shrubby understorey"},
                new String[] {"54", "#005F5F", "Eucalyptus tall open forest with a fine-leaved shrubby understorey"},
                new String[] {"55", "#DF9F7F", "Mallee  with an open shrubby understorey"},
                new String[] {"56", "#9FBFBF", "Eucalyptus (+/- low) open woodlands with a chenopod or samphire understorey"},
                new String[] {"57", "#BFBFDF", "Lignum shrublands and wetlands"},
                new String[] {"58", "#9F7F00", "Leptospermum forests and woodlands"},
                new String[] {"59", "#9F9F9F", "Eucalyptus woodlands with ferns, herbs, sedges, rushes or wet tussock grassland"},
                new String[] {"60", "#007F7F", "Eucalyptus tall open forests and open forests with ferns, herbs, sedges, rushes or wet tussock grasses"},
                new String[] {"61", "#DF9F9F", "Mallee with a tussock grass understorey"},
                new String[] {"62", "#DF7F3F", "Dry rainforest or vine thickets"},
                new String[] {"63", "#7FFFDF", "Sedgelands, rushs or reeds"},
                new String[] {"64", "#7FFF7F", "Other grasslands"},
                new String[] {"65", "#9F9FBF", "Eucalyptus woodlands with a chenopod or samphire understorey"},
                new String[] {"66", "#FFBFDF", "Open mallee woodlands and sparse mallee shrublands with a hummock grass understorey"},
                new String[] {"67", "#FFBFBF", "Open mallee woodlands and sparse mallee shrublands with a tussock grass understorey"},
                new String[] {"68", "#E1BF9F", "Open mallee woodlands and sparse mallee shrublands with an open shrubby understorey"},
                new String[] {"69", "#FFBF7F", "Open mallee woodlands and sparse mallee shrublands with a dense shrubby understorey"},
                new String[] {"70", "#BFBF3F", "Callitris open woodlands"},
                new String[] {"71", "#BF9F3F", "Casuarina and Allocasuarina open woodlands with a tussock grass understorey"},
                new String[] {"72", "#BF9F5F", "Casuarina and Allocasuarina open woodlands with a hummock grass understorey"},
                new String[] {"73", "#BF9F9F", "Casuarina and Allocasuarina open woodlands with a chenopod shrub understorey"},
                new String[] {"74", "#BF9F00", "Casuarina and Allocasuarina open woodlands with a shrubby understorey"},
                new String[] {"75", "#BFBF5F", "Melaleuca open woodlands"},
                new String[] {"79", "#BF9F7F", "Other open Woodlands"},
                new String[] {"80", "#BFFFDF", "Other sparse shrublands and sparse heathlands "},
                new String[] {"90", "#BF9FFF", "Regrowth or modified forests and woodlands"},
                new String[] {"91", "#BF9FDF", "Regrowth or modified shrublands"},
                new String[] {"92", "#BF9FBF", "Regrowth or modified graminoids"},
                new String[] {"93", "#000000", "Regrowth or modified chenopod shrublands, samphire or forblands"},
                new String[] {"96", "#7F7F7F", "Unclassified Forest"},
                new String[] {"97", "#BFBFBF", "Unclassified native vegetation"},
                new String[] {"98", "#DFDFDF", "Cleared, non-native vegetation, buildings"},
                new String[] {"99", "#FFFFFF", "Unknown/No data"}
            ))
            .replace();
        client
            .layer("layers/nvis_4_1_aust_mvs")
            .template("layers/coverage-layer.xml.ftl")
            .param("layerName", "nvis_4_1_aust_mvs")
            .param("coverageName", "nvis_4_1_aust_mvs")
            .param("defaultStyle", "oztrack_nvis-mvs")
            .param("styles", new String[] {"oztrack_nvis-mvs"})
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

    private void createIUCNLayers(
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
            .datastore("workspaces/" + workspaceName + "/datastores/commonwealth_marine_reserves_2012")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "commonwealth_marine_reserves_2012")
            .param("shapefileUrl", "file:shapefiles/commonwealth_marine_reserves_network_2012/commonwealth_marine_reserves_network_2012.shp")
            .param("shapefileCharset", "UTF-8")
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
            .featuretype("workspaces/" + workspaceName + "/datastores/commonwealth_marine_reserves_2012/featuretypes/commonwealth_marine_reserves_2012")
            .template("featuretypes/commonwealth_marine_reserves_2012.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "iucn")
            .template("styles/iucn.sld.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "iucn_m")
            .template("styles/iucn_m.sld.ftl")
            .replace();
        client
            .layer("layers/capad10_external_all")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "capad10_external_all")
            .param("featuretypeName", "capad10_external_all")
            .param("defaultStyle", "oztrack_iucn")
            .param("styles", new String[] {"oztrack_iucn", "polygon"})
            .replace();
        client
            .layer("layers/capad10_m_external_all")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "capad10_m_external_all")
            .param("featuretypeName", "capad10_m_external_all")
            .param("defaultStyle", "oztrack_iucn_m")
            .param("styles", new String[] {"oztrack_iucn_m", "polygon"})
            .replace();
        client
            .layer("layers/commonwealth_marine_reserves_2012")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "commonwealth_marine_reserves_2012")
            .param("featuretypeName", "commonwealth_marine_reserves_2012")
            .param("defaultStyle", "oztrack_iucn_m")
            .param("styles", new String[] {"oztrack_iucn_m", "polygon"})
            .replace();
    }

    private void createNRMRegionsLayer(
        GeoServerClient client,
        final String workspaceName,
        final String namespaceUri
    ) throws Exception {
        client
            .datastore("workspaces/" + workspaceName + "/datastores/nrm_regions_2010")
            .template("datastores/shapefile-datastore.xml.ftl")
            .param("datastoreName", "nrm_regions_2010")
            .param("shapefileUrl", "file:shapefiles/NRM_Regions_2010/NRM_Regions_2010.shp")
            .param("shapefileCharset", "UTF-8")
            .param("shapefileTimezone", "Australia/Brisbane")
            .param("namespaceUri", namespaceUri)
            .replace();
        client
            .featuretype("workspaces/" + workspaceName + "/datastores/" + "nrm_regions_2010" + "/featuretypes/nrm_regions_2010")
            .template("featuretypes/nrm_regions_2010.xml.ftl")
            .replace();
        client
            .style("styles/" + workspaceName + "_" + "nrm_regions")
            .template("styles/nrm_regions.sld.ftl")
            .replace();
        client
            .layer("layers/nrm_regions_2010")
            .template("layers/featuretype-layer.xml.ftl")
            .param("layerName", "nrm_regions_2010")
            .param("featuretypeName", "nrm_regions_2010")
            .param("defaultStyle", "oztrack_nrm_regions")
            .param("styles", new String[] {"oztrack_nrm_regions", "polygon"})
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