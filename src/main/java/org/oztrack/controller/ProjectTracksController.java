package org.oztrack.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONWriter;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.PositionFixStats;
import org.oztrack.data.model.types.TrajectoryStats;
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
public class ProjectTracksController {
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

    @ModelAttribute("fromDate")
    public Date getFromDate(@RequestParam(value="fromDate", required=false) String fromDateString) {
        Date fromDate = null;
        try {
            fromDate = StringUtils.isNotBlank(fromDateString) ? isoDateFormat.parse(fromDateString) : null;
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        return fromDate;
    }

    @ModelAttribute("toDate")
    public Date getToDate(@RequestParam(value="toDate", required=false) String toDateString) {
        Date toDate = null;
        try {
            toDate = StringUtils.isNotBlank(toDateString) ? isoDateFormat.parse(toDateString) : null;
        }
        catch (ParseException e) {
            logger.error("Invalid toDate", e);
        }
        return toDate;
    }

    @RequestMapping(value="/projects/{projectId}/detections", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("hasPermission(#project, 'read')")
    public void handleDetectionsJSON(
        HttpServletResponse response,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="fromDate") Date fromDate,
        @ModelAttribute(value="toDate") Date toDate
    ) throws IOException, JSONException {
        Map<Long, PositionFixStats> animalPositionFixStats = positionFixDao.getAnimalPositionFixStats(project, fromDate, toDate);
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        for (Animal animal : project.getAnimals()) {
            out.key(animal.getId().toString());
            out.object();
            out.key("animalId").value(animal.getId());
            if (animalPositionFixStats.containsKey(animal.getId())) {
                PositionFixStats stats = animalPositionFixStats.get(animal.getId());
                out.key("startDate").value(isoDateFormat.format(stats.getStartDate()));
                out.key("endDate").value(isoDateFormat.format(stats.getEndDate()));
                out.key("count").value(stats.getCount());
                out.key("dailyMean").value(stats.getDailyMean());
                out.key("dailyMax").value(stats.getDailyMax());
            }
            out.endObject();
        }
        out.endObject();
    }

    @RequestMapping(value="/projects/{projectId}/trajectories", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("hasPermission(#project, 'read')")
    public void handleTrajectoriesJSON(
        HttpServletResponse response,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="fromDate") Date fromDate,
        @ModelAttribute(value="toDate") Date toDate
    ) throws IOException, JSONException {
        Map<Long, TrajectoryStats> animalTrajectoryStats = positionFixDao.getAnimalTrajectoryStats(project, fromDate, toDate);
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        for (Animal animal : project.getAnimals()) {
            out.key(animal.getId().toString());
            out.object();
            out.key("animalId").value(animal.getId());
            if (animalTrajectoryStats.containsKey(animal.getId())) {
                TrajectoryStats stats = animalTrajectoryStats.get(animal.getId());
                out.key("startDate").value(isoDateFormat.format(stats.getStartDate()));
                out.key("endDate").value(isoDateFormat.format(stats.getEndDate()));
                out.key("distance").value(stats.getDistance());
                out.key("meanStepDistance").value(stats.getMeanStepDistance());
                out.key("meanStepSpeed").value(stats.getMeanStepSpeed());
            }
            out.endObject();
        }
        out.endObject();
    }
}
