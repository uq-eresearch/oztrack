package org.oztrack.data.migration;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.oztrack.util.OzTrackUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.migration.java.JavaMigration;

public class V14__RemoveProjectImageFiles implements JavaMigration {
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        String dataDir = OzTrackUtils.getConfigProperty("org.oztrack.conf.dataDir");
        for (File file : new File(dataDir).listFiles()) {
            if (file.isDirectory() && file.getName().startsWith("project-")) {
                File imgDir = new File(file, "img");
                if (imgDir.exists() && imgDir.isDirectory()) {
                    System.out.println("Deleting " + imgDir);
                    FileUtils.deleteDirectory(imgDir);
                }
            }
        }
    }
}
