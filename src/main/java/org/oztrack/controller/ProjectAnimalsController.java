package org.oztrack.controller;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 23/05/11
 * Time: 9:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProjectAnimalsController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

    Project project =  (Project) httpServletRequest.getSession().getAttribute("project");
    String errors = "";
    AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
    List<Animal> animalList = animalDao.getAnimalsByProjectId(project.getId());

    if (project == null) {
        errors = "No project, sorry.";
    }

    ModelAndView modelAndView = new ModelAndView("projectanimals");
    modelAndView.addObject("animalList", animalList);
    modelAndView.addObject("errors", errors);
    modelAndView.addObject("project", project);
    return modelAndView;

    }
}
