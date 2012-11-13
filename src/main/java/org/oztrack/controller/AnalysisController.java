package org.oztrack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.PositionFix;
import org.oztrack.error.RServeInterfaceException;
import org.oztrack.util.RServeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AnalysisController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("analysis")
    public void initAnalysisBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("analysis")
    public Analysis getAnalysis(@PathVariable(value="analysisId") Long analysisId) {
        return analysisDao.getAnalysisById(analysisId);
    }

    @RequestMapping(value="/projects/{projectId}/analysis/{analysisId}", method=RequestMethod.GET, produces="application/vnd.google-earth.kml+xml")
    @PreAuthorize("#analysis.project.global or hasPermission(#analysis.project, 'read')")
    public void handleKMLQuery(
        Authentication authentication,
        HttpServletResponse response,
        @ModelAttribute("analysis") Analysis analysis
    ) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(analysis.toSearchQuery());
        RServeInterface rServeInterface = new RServeInterface();
        File kmlFile = null;
        try {
            kmlFile = rServeInterface.createKml(analysis, positionFixList);
        }
        catch (RServeInterfaceException e) {
            logger.error("RServeInterface exception", e);
            response.setStatus(500);
            return;
        }
        String filename = analysis.getAnalysisType().name().toLowerCase(Locale.ENGLISH) + ".kml";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/vnd.google-earth.kml+xml");
        response.setCharacterEncoding("UTF-8");
        FileInputStream kmlInputStream = null;
        try {
            kmlInputStream = new FileInputStream(kmlFile);
            IOUtils.copy(kmlInputStream, response.getOutputStream());
            kmlFile.delete();
        }
        catch (IOException e) {
            response.setStatus(500);
            return;
        }
        finally {
            IOUtils.closeQuietly(kmlInputStream);
        }
    }
}