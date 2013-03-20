package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.AnimalDetectionsKMLView;
import org.oztrack.view.AnimalTrajectoryKMLView;
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
import org.springframework.web.servlet.View;

@Controller
public class AnimalKMLController {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("animal")
    public void initAnimalBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "fromDate",
            "toDate"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(isoDateFormat, true));
    }

    @ModelAttribute("animal")
    public Animal getAnimal(@RequestParam(value="animalId") Long animalId) {
        return animalDao.getAnimalById(animalId);
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery() {
        SearchQuery searchQuery = new SearchQuery();
        return searchQuery;
    }

    @RequestMapping(value="/detections", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'read')")
    public View getDetectionsView(
        @ModelAttribute(value="animal") Animal animal,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery
    ) throws Exception {
        searchQuery.setProject(animal.getProject());
        searchQuery.setAnimalIds(Arrays.asList(animal.getId()));
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        return new AnimalDetectionsKMLView(configuration, animal, positionFixList);
    }

    @RequestMapping(value="/trajectory", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'read')")
    public View getTrajectoryView(
        @ModelAttribute(value="animal") Animal animal,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery
    ) throws Exception {
        searchQuery.setProject(animal.getProject());
        searchQuery.setAnimalIds(Arrays.asList(animal.getId()));
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        return new AnimalTrajectoryKMLView(configuration, animal, positionFixList);
    }
}
