package org.oztrack.util;

import org.oztrack.app.OzTrackApplication;
import org.apache.log4j.Logger;
import org.oztrack.data.model.DataFile;
import org.oztrack.error.FileProcessingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Date;
import java.util.HashSet;
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
             throw new FileProcessingException("File not found.");
        }

        DataInputStream in = new DataInputStream(fileInputStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedWriter bw;

        try {
            bw = new BufferedWriter(new FileWriter(outFile));
        } catch (IOException e) {
            throw new FileProcessingException("Couldn't create new file");
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

        } catch (IOException e) {
             throw new FileProcessingException("Problem creating de-duplicates file.");
        }


    }



}
