package db.migration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.oztrack.geoserver.GeoServerClient;
import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.migration.java.JavaMigration;

public class V63__TruncateBathymetryCache implements JavaMigration {
    private final String propertiesFilePath = "conf/properties/application.properties";

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // This code has the potential to be duplicated in future migrations.
        // Refactoring should be done where appropriate for property lookup and GeoServer client code.

        Properties fileProperties = new Properties();
        InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream(propertiesFilePath);
        fileProperties.load(new InputStreamReader(propertiesStream, "UTF-8"));

        String username = getProperty(fileProperties, "application.geoServerUsername");
        String password = getProperty(fileProperties, "application.geoServerPassword");
        String baseUrl = getProperty(fileProperties, "application.geoServerLocalUrl");
        String templateBasePath = "/geoserver";

        GeoServerClient client = new GeoServerClient(username, password, baseUrl, templateBasePath);
        for (String gridSetId : new String[] {"EPSG:4326", "EPSG:900913"}) {
            for (String imageFormat : new String[] {"image/png", "image/jpeg"}) {
                for (String styleName : new String[] {"oztrack_bathymetry", null}) {
                    client
                        .seed("oztrack:gebco_08")
                        .template("gwc/seed.xml.ftl")
                        .param("layerName", "oztrack:gebco_08")
                        .param("requestType", "truncate")
                        .param("gridSetId", gridSetId)
                        .param("zoomStart", "00")
                        .param("zoomStop", "20")
                        .param("imageFormat", imageFormat)
                        .param("styleName", styleName)
                        .param("threadCount", 1)
                        .post();
                }
            }
        }
    }

    private String getProperty(Properties fileProperties, String key) {
        return System.getProperty(key, fileProperties.getProperty(key));
    }
}
