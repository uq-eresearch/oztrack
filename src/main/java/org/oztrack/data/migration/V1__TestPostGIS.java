package org.oztrack.data.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.migration.java.JavaMigration;

public class V1__TestPostGIS implements JavaMigration {
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // Try querying for GDA94 spatial reference system:
        // - tests whether spatial_ref_sys table exists (means postgis.sql has been run);
        // - tests that row for GDA94 exists (means that spatial_ref_sys.sql has also been run).
        int count = jdbcTemplate.queryForInt(
            "select count(*)\n" +
            "from spatial_ref_sys\n" +
            "where auth_name = 'EPSG' and auth_srid = 4283"
        );
        if (count != 1) {
            throw new Exception("PostGIS not installed");
        }
    }

}
