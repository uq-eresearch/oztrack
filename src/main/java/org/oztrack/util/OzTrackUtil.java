package org.oztrack.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.error.FileProcessingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public static void removeDuplicateLinesFromFile(String fileName) throws FileProcessingException {
        File inFile = new File(fileName);
        File outFile = new File(fileName + ".dedup");
        HashSet<String> hashSet = new HashSet<String>(10000);
        FileInputStream fileInputStream;
        String headers;
        String strLine;

        try {
             fileInputStream = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
             throw new FileProcessingException("File not found.", e);
        }
        
        if (inFile.getTotalSpace() == 0) {
        	throw new FileProcessingException("No data in file");
        }

        DataInputStream in = new DataInputStream(fileInputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException e) {
            throw new FileProcessingException("Couldn't create new file", e);
        }
        
        try {
             headers = br.readLine();
             bw.write(headers);
             bw.newLine();

             while ((strLine = br.readLine()) != null) {
                 if (!hashSet.contains(strLine)) {
                   hashSet.add(strLine);
                   bw.write(strLine);
                   bw.newLine();
                 }
             }
             hashSet.clear();
             br.close();
             bw.close();

             File finalFile = new File(fileName);
             File origFile = new File(fileName.replace(".csv",".orig"));

             inFile.renameTo(origFile);
             outFile.renameTo(finalFile);
             outFile.delete();
        }
        catch (Exception e) {
             throw new FileProcessingException("File Processing problem (dedup.");
        }
    }

    public static User getCurrentUser(Authentication authentication, UserDao userDao) {
        User currentUser = null;
        if (
            (authentication != null) &&
            authentication.isAuthenticated() &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))
        ) {
            currentUser = userDao.getByUsername((String) authentication.getPrincipal());
        }
        return currentUser;
    }
}