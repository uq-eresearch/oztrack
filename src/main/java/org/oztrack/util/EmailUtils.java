package org.oztrack.util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.User;

public class EmailUtils {
    public static HtmlEmail createHtmlEmail(User user, String subject) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(OzTrackApplication.getApplicationContext().getMailServerHostName());
        email.setSmtpPort(OzTrackApplication.getApplicationContext().getMailServerPort());
        email.addTo(user.getEmail(), user.getFullName());
        email.setFrom(
            OzTrackApplication.getApplicationContext().getMailFromEmail(),
            OzTrackApplication.getApplicationContext().getMailFromName()
        );
        email.setSubject(subject);
        return email;
    }
}