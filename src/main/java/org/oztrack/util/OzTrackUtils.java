package org.oztrack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.error.FileProcessingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class OzTrackUtils {
    public static void removeDuplicateLinesFromFile(String fileName) throws FileProcessingException {
        File inFile = new File(fileName);
        File outFile = new File(fileName + ".dedup");

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        CSVReader csvReader = null;
        CSVWriter csvWriter = null;
        try {
            try {
                fileInputStream = new FileInputStream(inFile);
            }
            catch (FileNotFoundException e) {
                throw new FileProcessingException("File not found.", e);
            }
            if (inFile.getTotalSpace() == 0) {
                throw new FileProcessingException("No data in file");
            }
            try {
                csvReader = new CSVReader(new InputStreamReader(fileInputStream, "UTF-8"));
            }
            catch (IOException e) {
                throw new FileProcessingException("Couldn't read CSV file", e);
            }

            try {
                fileOutputStream = new FileOutputStream(outFile);
                csvWriter = new CSVWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));
            }
            catch (IOException e) {
                throw new FileProcessingException("Couldn't create CSV file", e);
            }

            try {
                String[] headers = csvReader.readNext();
                csvWriter.writeNext(headers);

                HashSet<List<String>> hashSet = new HashSet<List<String>>();
                String[] values;
                while ((values = csvReader.readNext()) != null) {
                    List<String> valuesList = Arrays.asList(values);
                    if (!hashSet.contains(valuesList)) {
                        hashSet.add(valuesList);
                        csvWriter.writeNext(values);
                    }
                }

                inFile.renameTo(new File(fileName.replace(".csv",".orig")));
                outFile.renameTo(new File(fileName));
                outFile.delete();
            }
            catch (Exception e) {
                throw new FileProcessingException("File Processing problem (dedup).");
            }
        }
        finally {
            try {csvReader.close();} catch (Exception e) {}
            try {csvWriter.close();} catch (Exception e) {}
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(fileOutputStream);
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

    public static String getConfigProperty(String key) throws IOException, FileNotFoundException {
        String value = System.getProperty(key);
        if (StringUtils.isBlank(value)) {
            String customConfigFile = System.getProperty("org.oztrack.conf.customConfigFile");
            if (StringUtils.isNotBlank(customConfigFile)) {
                Properties properties = new Properties();
                properties.load(new FileInputStream(customConfigFile));
                value = properties.getProperty(key);
            }
        }
        if (StringUtils.isBlank(value)) {
            Properties properties = new Properties();
            properties.load(OzTrackUtils.class.getResourceAsStream("/org/oztrack/conf/application.properties"));
            value = properties.getProperty(key);
        }
        return value;
    }
}