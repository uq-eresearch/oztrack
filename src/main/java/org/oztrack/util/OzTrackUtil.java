package org.oztrack.util;

import org.oztrack.app.OzTrackApplication;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Author: alabri
 * Date: 9/03/11
 * Time: 11:41 AM
 */
public class OzTrackUtil {

    private static Logger logger = Logger.getLogger(OzTrackUtil.class);

    public static void sendEmail(String toEmailAddress, String fromEmailAddress, String subject, String text) throws MessagingException {
        String smtpServer = OzTrackApplication.getApplicationContext().getSmtpServer();
        Properties submissionEmailConfig = new Properties();
        submissionEmailConfig.setProperty("mail.smtp.host", smtpServer);
        Session session = Session.getDefaultInstance(submissionEmailConfig);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmailAddress));
        message.setSubject(subject);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        message.setSentDate(new Date());
        message.setText(text);
        Transport.send(message);
        logger.info("Email Sent to " + toEmailAddress);
    }
}
