package org.oztrack.controller;

import au.edu.uq.itee.maenad.dataaccess.Page;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.direct.JdbcQuery;
import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.ProjectType;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 11:24 AM
  */
public class SearchFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        SearchQuery searchQuery = (SearchQuery) command;
        request.getSession().setAttribute("searchQuery", searchQuery);

        List<AcousticDetection> acousticDetections = queryAcousticDetections(searchQuery);

        ModelAndView modelAndView = showForm(request, response, errors);
        modelAndView.addObject("acousticDetectionsList", acousticDetections);

        return modelAndView;

    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class,editor);

        super.initBinder(request, binder);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        return super.formBackingObject(request);    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {

        // find the project, add a searchQuery command object
        Project project =  (Project) request.getSession().getAttribute("project");
        SearchQuery searchQuery = (SearchQuery) request.getSession().getAttribute("searchQuery");
        if (searchQuery == null) {
            searchQuery = new SearchQuery();
            searchQuery.setProject(project);
        }
        // for pagination
        int offset=0;
        int nbrObjectsPerPage=30;
        int totalCount=0;
        int nbrObjectsThisPage=0;

        if (request.getParameter("offset") != null) {
            offset = Integer.parseInt(request.getParameter("offset"));
        }

        ModelAndView modelAndView = new ModelAndView("searchform");
        modelAndView.addObject("searchQuery",searchQuery); // empty searchQuery


        /*
        AcousticDetectionDao acousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getAcousticDetectionDao();
        Page<AcousticDetection> acousticDetectionsPage = acousticDetectionDao.getPage(offset,nbrObjectsPerPage);
        List<AcousticDetection> acousticDetectionsList = acousticDetectionsPage.getObjects();
        nbrObjectsThisPage = acousticDetectionsList.size();

        totalCount=acousticDetectionDao.getTotalCount();


        modelAndView.addObject("acousticDetectionsList", acousticDetectionsList);
        modelAndView.addObject("offset", offset);
        modelAndView.addObject("nbrObjectsPerPage", nbrObjectsPerPage);
        modelAndView.addObject("nbrObjectsThisPage", nbrObjectsThisPage);
        modelAndView.addObject("totalCount", totalCount);
        */

        switch (project.getProjectType()) {
             case PASSIVE_ACOUSTIC:
                 //modelAndView.addObject("acousticDetectionsList", queryAcousticDetections(searchQuery));
                 break;
             case GPS:
                 PositionFixDao positionFixDao = OzTrackApplication.getApplicationContext().getDaoManager().getPositionFixDao();
                 Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery,offset, nbrObjectsPerPage);
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
        //return super.showForm(request, response, errors);    //To change body of overridden methods use File | Settings | File Templates.

    }


    public List<AcousticDetection> queryAcousticDetections(SearchQuery searchQuery) {

        JdbcQuery jdbcQuery = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery();
        List<AcousticDetection> acousticDetections = jdbcQuery.queryAcousticDetections2(searchQuery);
        return acousticDetections;
    }

    /*
    public List<PositionFix> getAllPositionFixResults(SearchQuery searchQuery) {

        JdbcQuery jdbcQuery = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcQuery();
        List<PositionFix> positionFixes = jdbcQuery.queryProjectPositionFixes(searchQuery);
        return positionFixes;
    }

    public List<PositionFix> getPagePositionFixResults(SearchQuery searchQuery) {

    }
    */











}
