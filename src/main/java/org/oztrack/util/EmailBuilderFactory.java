package org.oztrack.util;

import org.oztrack.app.OzTrackConfiguration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailBuilderFactory implements FactoryBean<EmailBuilder> {
    @Autowired
    private OzTrackConfiguration configuration;

    @Override
    public EmailBuilder getObject() throws Exception {
        return new EmailBuilder(
            configuration.getMailServerHostName(),
            configuration.getMailServerPort(),
            configuration.getMailFromEmail(),
            configuration.getMailFromName()
        );
    }

    @Override
    public Class<?> getObjectType() {
        return EmailBuilder.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
