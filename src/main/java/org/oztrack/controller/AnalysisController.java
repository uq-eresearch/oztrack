package org.oztrack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisParameter;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.AnalysisStatus;
import org.oztrack.view.AnalysisResultFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

@Controller
public class AnalysisController {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("analysis")
    public void initAnalysisBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("analysis")
    public Analysis getAnalysis(@PathVariable(value="analysisId") Long analysisId) {
        return analysisDao.getAnalysisById(analysisId);
    }

    private boolean hasPermission(Authentication authentication, HttpServletRequest request, Analysis analysis, String permission) {
        User currentUser = permissionEvaluator.getAuthenticatedUser(authentication);
        if ((currentUser != null) && (currentUser.getAdmin() != null) && currentUser.getAdmin()) {
            return true;
        }
        if (permission.equals("write")) {
            // Users with write access to the project have write access to all of its analyses
            if (permissionEvaluator.hasPermission(authentication, analysis.getProject(), "write")) {
                return true;
            }
        }
        else if (permission.equals("read")) {
            // Users with read access to the project have read access to all saved analyses
            if (permissionEvaluator.hasPermission(authentication, analysis.getProject(), "read") && analysis.isSaved()) {
                return true;
            }
            // Otherwise, only the creator of an analysis is able to view it
            if ((currentUser != null) && currentUser.equals(analysis.getCreateUser())) {
                return true;
            }
            HttpSession currentSession = request.getSession(false);
            if ((currentSession != null) && currentSession.getId().equals(analysis.getCreateSession())) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value="/projects/{projectId}/analyses/{analysisId}", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("permitAll")
    public void handleJSON(
        Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis
    ) throws IOException, JSONException {
        if (!hasPermission(authentication, request, analysis, "read")) {
            response.setStatus(403);
            return;
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        out.key("id").value(analysis.getId());
        out.key("params").object();
        out.key("queryType").value(analysis.getAnalysisType());
        out.key("hasAnimalFeatures").value(analysis.getAnalysisType().getReturnsAnimalFeatures());
        if (analysis.getFromDate() != null) {
            out.key("fromDate").value(isoDateFormat.format(analysis.getFromDate()));
        }
        if (analysis.getToDate() != null) {
            out.key("toDate").value(isoDateFormat.format(analysis.getToDate()));
        }
        out.key("animalIds").array();
        for (Animal animal : analysis.getAnimals()) {
            out.value(animal.getId());
        }
        out.endArray();
        out.key("animalNames").array();
        for (Animal animal : analysis.getAnimals()) {
            out.value(animal.getAnimalName());
        }
        out.endArray();
        for (AnalysisParameter parameter : analysis.getParameters()) {
            out.key(parameter.getName()).value(parameter.getValue());
        }
        out.endObject();
        out.key("status").value(analysis.getStatus().name());
        if (analysis.getMessage() != null) {
            out.key("message").value(analysis.getMessage());
        }
        if (analysis.getStatus() == AnalysisStatus.COMPLETE) {
            String resultBaseUrl = String.format("%s/projects/%d/analyses/%d/result", request.getContextPath(), analysis.getProject().getId(), analysis.getId());
            out.key("resultFiles").array();
            {
                out.object();
                out.key("title").value("KML");
                out.key("format").value("kml");
                out.key("url").value(resultBaseUrl + "?format=kml");
                out.endObject();
            }
            if (analysis.getProject().getCrosses180()) {
                out.object();
                out.key("title").value("KML (outline)");
                out.key("format").value("kml");
                out.key("url").value(resultBaseUrl + "?format=kml&fill=false");
                out.endObject();
            }
            if (!analysis.getResultFeatures().isEmpty()) {
                out.object();
                out.key("title").value("SHP");
                out.key("format").value("shp");
                out.key("url").value(resultBaseUrl + "?format=shp");
                out.endObject();
            }
            out.endArray();
        }
        if (analysis.getDescription() != null) {
            out.key("description").value(analysis.getDescription());
        }
        out.endObject();
    }

    @RequestMapping(value="/projects/{projectId}/analyses/{analysisId}/saved", method=RequestMethod.PUT, consumes="application/json")
    @PreAuthorize("permitAll")
    public void updateSaved(
        Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis,
        @RequestBody String savedString
    ) {
        if (!hasPermission(authentication, request, analysis, "write")) {
            response.setStatus(403);
            return;
        }
        analysis.setSaved(Boolean.valueOf(savedString));
        analysisDao.save(analysis);
        response.setStatus(204);
    }

    @RequestMapping(value="/projects/{projectId}/analyses/{analysisId}/description", method=RequestMethod.PUT, consumes="text/plain")
    @PreAuthorize("permitAll")
    public void updateDescription(
        Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis,
        @RequestBody String description
    ) {
        if (!hasPermission(authentication, request, analysis, "write")) {
            response.setStatus(403);
            return;
        }
        analysis.setDescription(description);
        analysisDao.save(analysis);
        response.setStatus(204);
    }

    @RequestMapping(
        value="/projects/{projectId}/analyses/{analysisId}/result",
        method=RequestMethod.GET,
        produces={
            "application/vnd.google-earth.kml+xml",
            "application/zip"
        }
    )
    @PreAuthorize("permitAll")
    public void handleResult(
        Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response,
        @ModelAttribute(value="analysis") Analysis analysis,
        @RequestParam(value="format", defaultValue="kml") String format,
        @RequestParam(value="fill", defaultValue="true") Boolean fill
    ) {
        if (!hasPermission(authentication, request, analysis, "read")) {
            response.setStatus(403);
            return;
        }
        if (analysis.getStatus() == AnalysisStatus.FAILED) {
            response.setStatus(500);
            writeResultError(response, analysis.getMessage());
            return;
        }
        if ((analysis.getStatus() == AnalysisStatus.NEW) || (analysis.getStatus() == AnalysisStatus.PROCESSING)) {
            response.setStatus(404);
            writeResultError(response, "Processing");
            return;
        }
        try {
            if (format.equals("kml")) {
                writeResultKml(response, analysis, fill);
            }
            else if (format.equals("shp")) {
                writeResultShp(response, analysis);
            }
        }
        catch (Exception e) {
            String msg = "Error writing analysis result";
            logger.error(msg, e);
            response.setStatus(500);
            writeResultError(response, msg);
            return;
        }
    }

    private void writeResultKml(
        HttpServletResponse response,
        Analysis analysis,
        Boolean fill
    )
    throws Exception {
        String fileName = "analysis-" + analysis.getId() + ".kml";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/vnd.google-earth.kml+xml");
        response.setCharacterEncoding("UTF-8");
        if (!analysis.getResultFeatures().isEmpty()) {
            Configuration freemarkerConfiguration = new Configuration();
            DefaultObjectWrapper objectWrapper = new DefaultObjectWrapper();
            objectWrapper.setExposeFields(true);
            freemarkerConfiguration.setObjectWrapper(objectWrapper);
            freemarkerConfiguration.setTemplateLoader(new ClassTemplateLoader(this.getClass(), "/org/oztrack/view"));
            Template template = freemarkerConfiguration.getTemplate("analysis.kml.ftl");
            Map<String, Object> datamodel = new HashMap<String, Object>();
            datamodel.put("baseUrl", this.configuration.getBaseUrl());
            datamodel.put("analysis", analysis);
            datamodel.put("fill", fill);
            template.process(datamodel, response.getWriter());
        }
        else {
            Reader kmlReader = null;
            Reader xslReader = null;
            try {
                kmlReader = new FileReader(analysis.getAbsoluteResultFilePath());
                xslReader = buildXslReader(analysis, fill);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslReader));
                transformer.transform(new StreamSource(kmlReader), new StreamResult(response.getOutputStream()));
            }
            finally {
                IOUtils.closeQuietly(kmlReader);
                IOUtils.closeQuietly(xslReader);
            }
        }
    }

    private Reader buildXslReader(Analysis analysis, Boolean fill) {
        StringBuilder xslBuilder = new StringBuilder();
        xslBuilder.append("<?xml version=\"1.0\" ?>");
        xslBuilder.append("<xsl:stylesheet");
        xslBuilder.append("  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"");
        xslBuilder.append("  xmlns:kml=\"http://www.opengis.net/kml/2.2\"");
        xslBuilder.append("  xmlns=\"http://www.opengis.net/kml/2.2\"");
        xslBuilder.append("  version=\"1.0\">");
        // We're only making small changes to the KML, so copy elements through by default
        xslBuilder.append("  <xsl:template match=\"@*|node()\">");
        xslBuilder.append("    <xsl:copy>");
        xslBuilder.append("      <xsl:apply-templates select=\"@*|node()\"/>");
        xslBuilder.append("    </xsl:copy>");
        xslBuilder.append("  </xsl:template>");
        // Remove existing description from Document since we set it below
        xslBuilder.append("  <xsl:template match=\"//kml:Document/kml:description\">");
        xslBuilder.append("    <!-- remove existing description -->");
        xslBuilder.append("  </xsl:template>");
        // Insert elements at start of Document
        xslBuilder.append("  <xsl:template match=\"//kml:Document\">");
        xslBuilder.append("    <xsl:copy>");
        xslBuilder.append("      <description>" + "Generated by OzTrack\n" + (configuration.getBaseUrl() + "/") + "</description>");
        if (analysis.getAnalysisType().getReturnsAnimalFeatures()) {
            for (Animal animal : analysis.getAnimals()) {
                // Convert CSS colour (RRGGBB) to KML colour (AABBGGRR)
                Matcher matcher = Pattern.compile("^#(..)(..)(..)$").matcher(animal.getColour());
                if (matcher.matches()) {
                    String kmlBaseColour = matcher.group(3) + matcher.group(2) + matcher.group(1);
                    String kmlLineColour = "cc" + kmlBaseColour; // 80% opacity
                    String kmlPolyColour = "7f" + kmlBaseColour; // 50% opacity
                    xslBuilder.append("      <Style id=\"animal-" + animal.getId() + "\">");
                    xslBuilder.append("        <LineStyle>");
                    xslBuilder.append("          <color>" + kmlLineColour + "</color>");
                    xslBuilder.append("          <width>2</width>");
                    xslBuilder.append("        </LineStyle>");
                    xslBuilder.append("        <PolyStyle>");
                    xslBuilder.append("          <color>" + kmlPolyColour + "</color>");
                    xslBuilder.append("          <fill>" + (fill ? "1" : "0") + "</fill>");
                    xslBuilder.append("          <outline>1</outline>");
                    xslBuilder.append("        </PolyStyle>");
                    xslBuilder.append("      </Style>");
                }
            }
        }
        xslBuilder.append("      <xsl:apply-templates select=\"@*|node()\"/>");
        xslBuilder.append("    </xsl:copy>");
        xslBuilder.append("  </xsl:template>");
        if (analysis.getAnalysisType().getReturnsAnimalFeatures()) {
            // Remove existing styles from animals
            xslBuilder.append("  <xsl:template match=\"//kml:Placemark[.//kml:SimpleData[@name='id']]/kml:Style\">");
            xslBuilder.append("    <!-- remove existing styles -->");
            xslBuilder.append("  </xsl:template>");
            xslBuilder.append("  <xsl:template match=\"//kml:Placemark[.//kml:SimpleData[@name='id']]/kml:styleUrl\">");
            xslBuilder.append("    <!-- remove existing styles -->");
            xslBuilder.append("  </xsl:template>");
            // Insert styleUrl elements referring to Style for each animal
            xslBuilder.append("  <xsl:template match=\"//kml:Placemark[.//kml:SimpleData[@name='id']]\">");
            xslBuilder.append("    <xsl:copy>");
            xslBuilder.append("      <styleUrl>#animal-<xsl:apply-templates select=\".//kml:SimpleData[@name='id']/text()\"/></styleUrl>");
            xslBuilder.append("      <xsl:apply-templates select=\"@*|node()\"/>");
            xslBuilder.append("    </xsl:copy>");
            xslBuilder.append("  </xsl:template>");
        }
        xslBuilder.append("</xsl:stylesheet>");
        return new StringReader(xslBuilder.toString());
    }

    private void writeResultShp(HttpServletResponse response, Analysis analysis) throws Exception {
        String fileName = "analysis-" + analysis.getId() + ".zip";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/zip");
        response.setCharacterEncoding("UTF-8");
        AnalysisResultFeatureBuilder featureBuilder = new AnalysisResultFeatureBuilder(analysis);
        SimpleFeatureCollection featureCollection = featureBuilder.buildFeatureCollection();
        HashMap<String, Object> params = new HashMap<String, Object>();
        File tmpFile = File.createTempFile("analysis-" + analysis.getId() + "-", null);
        File tmpDir = new File(tmpFile.getPath() + "dir");
        try {
            tmpDir.mkdir();
            File shpFile = new File(tmpDir, "analysis-" + analysis.getId() + ".shp");
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
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
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

    private static void writeResultError(HttpServletResponse response, String error) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<analysis-result-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        out.append("    <error>" + StringUtils.trim(error) + "</error>\n");
        out.append("</analysis-result-response>\n");
    }
}
