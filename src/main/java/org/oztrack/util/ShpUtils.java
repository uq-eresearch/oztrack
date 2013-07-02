package org.oztrack.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;

public class ShpUtils {
    public static void writeShpZip(
        SimpleFeatureCollection featureCollection,
        String baseFileName,
        OutputStream out
    )
    throws Exception {
        File tmpFile = File.createTempFile(baseFileName + "-", null);
        File tmpDir = new File(tmpFile.getPath() + "dir");
        try {
            tmpDir.mkdir();
            File shpFile = new File(tmpDir, baseFileName + ".shp");
            writeShp(featureCollection, shpFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(out);
            try {
                for (String fileURL: new ShpFiles(shpFile).getFileNames().values()) {
                    File file = new File(URI.create(fileURL));
                    if (file.exists()) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOutputStream.putNextEntry(zipEntry);
                        IOUtils.copy(new FileInputStream(file), zipOutputStream);
                        zipOutputStream.closeEntry();
                    }
                }
            }
            finally {
                zipOutputStream.close();
            }
        }
        finally {
            try {FileUtils.deleteDirectory(tmpDir);} catch (Exception e) {};
            try {tmpFile.delete();} catch (Exception e) {};
        }
    }

    private static void writeShp(
        SimpleFeatureCollection featureCollection,
        File shpFile
    )
    throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("url", shpFile.toURI().toURL());
        params.put("charset", Charset.forName("UTF-8"));
        ShapefileDataStore dataStore = (ShapefileDataStore) (new ShapefileDataStoreFactory()).createNewDataStore(params);
        dataStore.createSchema(featureCollection.getSchema());
        Transaction transaction = new DefaultTransaction();
        String typeName = dataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
        SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
        featureStore.setTransaction(transaction);
        try {
            featureStore.addFeatures(featureCollection);
            transaction.commit();
        }
        catch (Exception e) {
            transaction.rollback();
            throw e;
        }
        finally {
            transaction.close();
        }
    }
}