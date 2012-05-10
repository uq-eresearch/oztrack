package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import au.edu.uq.itee.maenad.dataaccess.Page;

public class SearchFormController extends SimpleFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        Long projectId = null;
        if (request.getParameter("project_id") != null) {
            projectId = Long.parseLong(request.getParameter("project_id"));
        }
        Project project = null;
        if (projectId != null) {
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            project = projectDao.getProjectById(projectId);
            projectDao.refresh(project);
        }
        
        SearchQuery searchQuery = (SearchQuery) command;
        searchQuery.setProject(project);
        request.getSession().setAttribute("searchQuery", searchQuery);

        return showFormInternal(request, response, errors);
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(List.class, "animalList", new CustomCollectionEditor(List.class) {
            @Override
            protected Object convertElement(Object element) {
                String animalId = (String) element;
                Animal animal = new Animal();
                animal.setId(Long.valueOf(animalId));
                return animal;
            }
        });
        super.initBinder(request, binder);
    }
    
    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        // If we have a SearchQuery instance in the session from a previously posted form,
        // replace the default SearchQuery instance in the model so we show previous values in the form.
        SearchQuery searchQuery = (SearchQuery) request.getSession().getAttribute("searchQuery");
        if (searchQuery != null) {
            return searchQuery;
        }
        return super.formBackingObject(request);
    }
    
    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
        return showFormInternal(request, response, errors);
    }
    
    private ModelAndView showFormInternal(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
        Long projectId = null;
        if (request.getParameter("project_id") != null) {
            projectId = Long.parseLong(request.getParameter("project_id"));
        }
        Project project = null;
        if (projectId != null) {
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            project = projectDao.getProjectById(projectId);
            projectDao.refresh(project);
        }
        
        AnimalDao animalDao = OzTrackApplication.getApplicationContext().getDaoManager().getAnimalDao();
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        SearchQuery searchQuery = (SearchQuery) errors.getModel().get("searchQuery");
        searchQuery.setProject(project);

        // for pagination
        int offset=0;
        int nbrObjectsPerPage=30;
        int totalCount=0;
        int nbrObjectsThisPage=0;

        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }

        ModelAndView modelAndView = new ModelAndView(getFormView(), errors.getModel());
        modelAndView.addObject("project", project);
        modelAndView.addObject("searchQuery", searchQuery);
        modelAndView.addObject("projectAnimalsList", projectAnimalsList);

        switch (project.getProjectType()) {
             case GPS:
                 PositionFixDao positionFixDao = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao();
                 Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery, offset, nbrObjectsPerPage);
                 nbrObjectsThisPage = positionFixPage.getObjects().size();
                 totalCount = positionFixPage.getCount();
                 modelAndView.addObject("positionFixList", positionFixPage.getObjects());
                 break;
             default:
                 break;
        }

        modelAndView.addObject("offset", offset);
        modelAndView.addObject("nbrObjectsPerPage", nbrObjectsPerPage);
        modelAndView.addObject("nbrObjectsThisPage", nbrObjectsThisPage);
        modelAndView.addObject("totalCount", totalCount);

        return modelAndView;
    }
}