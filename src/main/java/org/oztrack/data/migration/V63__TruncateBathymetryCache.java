package org.oztrack.data.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.migration.java.JavaMigration;

public class V63__TruncateBathymetryCache implements JavaMigration {
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // This migration had code that used GeoServerClient to empty a GeoWebCache layer.
        // However, it caused issues in the production environment where both the GeoServer
        // and OzTrack webapps run in the same Tomcat instance: OzTrack started first and
        // tried to make REST API requests to GeoServer. Need to be smarter about waiting
        // for GeoServer to start before deciding that a request has failed. Disabling this
        // migration for now until we solve this problem. See git history for old code.
    }
}
