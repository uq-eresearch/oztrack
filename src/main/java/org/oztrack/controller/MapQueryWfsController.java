package org.oztrack.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalStartEndFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapQueryWfsController {
    private final Logger logger = Logger.getLogger(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("searchQuery")
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields();
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="animalIds", required=false) List<Long> animalIds,
        @RequestParam(value="includeDeleted", required=false) Boolean includeDeleted
    ) {
        SearchQuery searchQuery = new SearchQuery();
        if (projectId != null) {
            searchQuery.setProject(projectDao.getProjectById(projectId));
        }
        try {
            searchQuery.setFromDate(StringUtils.isNotBlank(fromDateString) ? isoDateFormat.parse(fromDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        try {
            searchQuery.setToDate(StringUtils.isNotBlank(toDateString) ? isoDateFormat.parse(toDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid toDate", e);
        }
        searchQuery.setAnimalIds(animalIds);
        searchQuery.setIncludeDeleted(includeDeleted);
        return searchQuery;
    }

    @RequestMapping(value="/wfs/detections", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public void getDetections(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, HttpServletResponse response) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        Boolean includeDeleted = (searchQuery.getIncludeDeleted() != null) && searchQuery.getIncludeDeleted().booleanValue();
        SimpleFeatureCollection featureCollection = new AnimalDetectionsFeatureBuilder(positionFixList, includeDeleted).buildFeatureCollection();
        WfsControllerUtils.writeWfsResponse(response, featureCollection);
    }

    @RequestMapping(value="/wfs/trajectory", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public void getTrajectory(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, HttpServletResponse response) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        SimpleFeatureCollection featureCollection = new AnimalTrajectoryFeatureBuilder(positionFixList).buildFeatureCollection();
        WfsControllerUtils.writeWfsResponse(response, featureCollection);
    }

    @RequestMapping(value="/wfs/start-end", method=RequestMethod.POST)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public void getStartEnd(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, HttpServletResponse response) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        SimpleFeatureCollection featureCollection = new AnimalStartEndFeatureBuilder(positionFixList).buildFeatureCollection();
        WfsControllerUtils.writeWfsResponse(response, featureCollection);
    }
}