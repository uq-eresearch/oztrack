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
import java.util.HashSet;

import org.apache.commons.io.IOUtils;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.oztrack.error.FileProcessingException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class OzTrackUtil {
    public static void removeDuplicateLinesFromFile(String fileName) throws FileProcessingException {
        File inFile = new File(fileName);
        File outFile = new File(fileName + ".dedup");
        
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
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
	        dataInputStream = new DataInputStream(fileInputStream);
	        reader = new BufferedReader(new InputStreamReader(dataInputStream));
	
	        try {
	            writer = new BufferedWriter(new FileWriter(outFile));
	        }
	        catch (IOException e) {
	            throw new FileProcessingException("Couldn't create new file", e);
	        }
	        
	        try {
	        	String headers = reader.readLine();
	            writer.write(headers);
	            writer.newLine();
	
	            HashSet<String> hashSet = new HashSet<String>(10000);
	            String strLine;
	            while ((strLine = reader.readLine()) != null) {
	                if (!hashSet.contains(strLine)) {
	                  hashSet.add(strLine);
	                  writer.write(strLine);
	                  writer.newLine();
	                }
	            }
	            hashSet.clear();
	            reader.close();
	            writer.close();
	
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
        finally {
        	IOUtils.closeQuietly(writer);	
        	IOUtils.closeQuietly(reader);
        	IOUtils.closeQuietly(dataInputStream);
        	IOUtils.closeQuietly(fileInputStream);
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