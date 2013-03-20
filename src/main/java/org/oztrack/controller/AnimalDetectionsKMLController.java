package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
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
public class AnimalDetectionsKMLController {
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

    @ModelAttribute("animal")
    public Animal getAnimal(@RequestParam(value="animalId") Long animalId) {
        return animalDao.getAnimalById(animalId);
    }

    @RequestMapping(value="/exportKML", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#animal.project, 'read')")
    public View handleRequest(
        @ModelAttribute(value="animal") Animal animal,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(animal.getProject());
        searchQuery.setAnimalIds(Arrays.asList(animal.getId()));
        if (fromDateString != null) {
            searchQuery.setFromDate(isoDateFormat.parse(fromDateString));
        }
        if (toDateString != null) {
            searchQuery.setToDate(isoDateFormat.parse(toDateString));
        }
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        return new AnimalDetectionsKMLView(
            configuration,
            animal,
            searchQuery.getFromDate(),
            searchQuery.getToDate(),
            positionFixList
        );
    }
}
