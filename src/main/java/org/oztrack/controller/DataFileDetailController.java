package org.oztrack.controller;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 28/04/11
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataFileDetailController implements Controller {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        logger.debug("Parm datafile_id = " + httpServletRequest.getParameter("datafile_id"));
        String errorStr = null;

        DataFileDao dataFileDao = OzTrackApplication.getApplicationContext().getDaoManager().getDataFileDao();
        DataFile dataFile = dataFileDao.getDataFileById(Long.parseLong(httpServletRequest.getParameter("datafile_id")));
        dataFileDao.refresh(dataFile);

        if (dataFile == null) {
            errorStr = "Couldn't find anything on that file sorry";
        }
        

        ModelAndView modelAndView = new ModelAndView("datafiledetail");
        modelAndView.addObject("errorStr", errorStr);
        modelAndView.addObject("dataFile", dataFile);

        return modelAndView;
    }

}
