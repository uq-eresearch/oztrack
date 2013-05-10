package org.oztrack.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrajectoriesController {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="projectId") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{projectId}/trajectories", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("hasPermission(#project, 'read')")
    public void handleJSON(
        HttpServletResponse response,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString
    ) throws IOException, JSONException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "Thu, 01 Jan 1970 00:00:00 GMT");

        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = StringUtils.isNotBlank(fromDateString) ? isoDateFormat.parse(fromDateString) : null;
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        try {
            toDate = StringUtils.isNotBlank(toDateString) ? isoDateFormat.parse(toDateString) : null;
        }
        catch (ParseException e) {
            logger.error("Invalid toDate", e);
        }

        Map<Long, Double> animalDistances = positionFixDao.getAnimalDistances(project, fromDate, toDate);
        Map<Long, Range<Date>> animalStartEndDates = positionFixDao.getAnimalStartEndDates(project, fromDate, toDate);

        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        for (Animal animal : project.getAnimals()) {
            out.key(animal.getId().toString());
            out.object();
            out.key("animalId").value(animal.getId());
            if (animalDistances.containsKey(animal.getId())) {
                out.key("distance").value(animalDistances.get(animal.getId()));
            }
            if (animalStartEndDates.containsKey(animal.getId())) {
                Range<Date> startEndDates = animalStartEndDates.get(animal.getId());
                if (startEndDates.getMinimum() != null) {
                    out.key("startDate").value(isoDateFormat.format(startEndDates.getMinimum()));
                }
                if (startEndDates.getMaximum() != null) {
                    out.key("endDate").value(isoDateFormat.format(startEndDates.getMaximum()));
                }
            }
            out.endObject();
        }
        out.endObject();
    }
}
