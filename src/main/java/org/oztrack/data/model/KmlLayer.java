package org.oztrack.data.model;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/08/11
 * Time: 12:20 PM
 */
public class KmlLayer {

    private SearchQuery searchQuery;
    private String kmlFilePath;
    private File kmlFile;

    public KmlLayer(SearchQuery searchQuery) {
        this.searchQuery = searchQuery;
        String projectId = searchQuery.getProjectId().toString();
        //kmlFilePath =

    }





}
