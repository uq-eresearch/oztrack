package org.oztrack.util;

import java.io.IOException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.resolver.DataSourceClassPathResolver;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.User;

public class EmailBuilder {
    HtmlEmail email;
    String firstName;
    DataSourceClassPathResolver imageResolver = new DataSourceClassPathResolver("/org/oztrack/images");

    public EmailBuilder(String hostName, Integer smtpPort, String fromEmail, String fromName)
    throws EmailException {
        email = new HtmlEmail();
        email.setCharset("UTF-8");
        email.setHostName(hostName);
        email.setSmtpPort(smtpPort);
        email.setFrom(fromEmail, fromName);
    }

    public EmailBuilder to(User user) throws EmailException {
        return to(user.getPerson());
    }

    public EmailBuilder to(Person person) throws EmailException {
        email.addTo(person.getEmail(), person.getFullName());
        firstName = person.getFirstName();
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
        htmlMsg.append("<head>\n");
        htmlMsg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n");
        htmlMsg.append("</head>\n");
        htmlMsg.append("<body>\n");
        htmlMsg.append("<p><img src=\"" + oztrackLogoImgSrc + "\" /></p>\n");
        if (firstName != null) {
            htmlMsg.append("<p>Dear " + firstName + ",</p>\n");
        }
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