package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.model.AcousticDetection;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import au.edu.uq.itee.maenad.dataaccess.Page;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 11/05/11
 * Time: 1:21 PM
 */
public class SearchAcousticController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        int offset=0;
        int nbrObjectsPerPage=30;
        int totalCount=0;
        int nbrObjectsThisPage=0;

        if (httpServletRequest.getParameter("offset") != null) {
            offset = Integer.parseInt(httpServletRequest.getParameter("offset"));
        }

        AcousticDetectionDao acousticDetectionDao = OzTrackApplication.getApplicationContext().getDaoManager().getAcousticDetectionDao();
        Page<AcousticDetection> acousticDetectionsPage = acousticDetectionDao.getPage(offset,nbrObjectsPerPage);
        List<AcousticDetection> acousticDetectionsList = acousticDetectionsPage.getObjects();
        nbrObjectsThisPage = acousticDetectionsList.size();

        totalCount=acousticDetectionDao.getTotalCount();


        ModelAndView modelAndView = new ModelAndView("searchacoustic");
        modelAndView.addObject("acousticDetectionsList", acousticDetectionsList);
        modelAndView.addObject("offset", offset);
        modelAndView.addObject("nbrObjectsPerPage", nbrObjectsPerPage);
        modelAndView.addObject("nbrObjectsThisPage", nbrObjectsThisPage);
        modelAndView.addObject("totalCount", totalCount);
        return modelAndView;
    }
      /**
     * Retrieves "page" of objects of the type managed.
     *
     * @param offset object index to start from (zero based)
     * @param limit maximum number of objects per page (will only be less on last page)
     *
     * @return instance of <code>Page</code> capturing object sublist and position information
     */
    //Page<T> getPage(int offset, int limit);


}
