package org.oztrack.util;

import java.io.IOException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.User;

public class EmailBuilder {
    HtmlEmail email;
    String firstName;
    DataSourceClassPathResolver imageResolver = new DataSourceClassPathResolver("/images");

    public EmailBuilder() throws EmailException {
        email = new HtmlEmail();
        email.setHostName(OzTrackApplication.getApplicationContext().getMailServerHostName());
        email.setSmtpPort(OzTrackApplication.getApplicationContext().getMailServerPort());
        email.setFrom(
            OzTrackApplication.getApplicationContext().getMailFromEmail(),
            OzTrackApplication.getApplicationContext().getMailFromName()
        );
    }

    public EmailBuilder to(User user) throws EmailException {
        email.addTo(user.getEmail(), user.getFullName());
        firstName = user.getFirstName();
        return this;
    }

    public EmailBuilder subject(String subject) throws EmailException {
        email.setSubject(subject);
        return this;
    }

    public EmailBuilder htmlMsgContent(String htmlMsgContent) throws EmailException, IOException {
        String oztrackLogoImgSrc = embedImg("oztrack-logo.png");
        StringBuilder htmlMsg = new StringBuilder();
        htmlMsg.append("<html>\n");
        htmlMsg.append("<body>\n");
        htmlMsg.append("<p><img src=\"" + oztrackLogoImgSrc + "\" /></p>\n");
        htmlMsg.append("<p>Dear " + firstName + ",</p>\n");
        htmlMsg.append(htmlMsgContent);
        htmlMsg.append("</body>\n");
        htmlMsg.append("</html>");
        email.setHtmlMsg(htmlMsg.toString());
        return this;
    }

    public EmailBuilder textMsgContent(String textMsgContent) throws EmailException {
        StringBuilder textMsg = new StringBuilder();
        textMsg.append("Dear " + firstName + ",\n");
        textMsg.append("\n");
        textMsg.append(textMsgContent);
        email.setTextMsg(textMsg.toString());
        return this;
    }

    public String embedImg(String resourceLocation) throws EmailException, IOException {
        return "cid:" + email.embed(imageResolver.resolve(resourceLocation), resourceLocation);
    }

    public HtmlEmail build() {
        return email;
    }
}