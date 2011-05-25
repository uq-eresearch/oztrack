package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.direct.JdbcAccess;
import org.oztrack.data.model.AcousticDetection;
import org.oztrack.data.model.SearchQuery;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
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

        JdbcAccess jdbcAccess = OzTrackApplication.getApplicationContext().getDaoManager().getJdbcAccess();
        String sql = buildQuery(searchQuery);

        List<AcousticDetection> acousticDetections = jdbcAccess.queryAcousticDetections2(sql);

        ModelAndView modelAndView = showForm(request, response, errors);
        modelAndView.addObject("acousticDetectionsList", acousticDetections);
        modelAndView.addObject("sql", sql);
        return modelAndView;
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class,editor);

        super.initBinder(request, binder);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /*@Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors) throws Exception {
        return super.showForm(request, response, errors);    //To change body of overridden methods use File | Settings | File Templates.

    } */



    protected String buildQuery(SearchQuery searchQuery) {

        String dateFormat = "'DD/MM/YYYY'";

        String select = "SELECT ad.id as acousticdetectionid "
                      + ", ad.detectionTime "
                      + ", ad.animal_id "
                      + ", ad.receiverdeployment_id "
                      + ", ad.datafile_id "
                      + ", ad.sensor1value "
                      + ", ad.sensor1units "
                      + ", a.id as animalid "
                      + ", a.projectanimalid "
                      + ", d.uploaddate as datafile_uploaddate"
                      + ", rd.originalid as receiverdeployment_originalid";

        String from = " FROM acousticdetection ad"
                    + ", animal a "
                    + ", datafile d "
                    + ", receiverdeployment rd ";

        String joinClause = " WHERE ad.animal_id=a.id"
                     + " AND ad.receiverdeployment_id=rd.id "
                     + " AND ad.datafile_id = d.id ";

        String where = "";

        if (searchQuery.getProjectAnimalId() != null) {
            where = where + " AND a.projectanimalid = '"
                          + searchQuery.getProjectAnimalId() + "'";

        }

/*        if (searchQuery.getToDate().length() == 0) {
        //if ((searchQuery.getToDate() != null) || !searchQuery.getToDate().isEmpty()){
            where = where + " AND ad.detectiontime <= to_date('"
                          + searchQuery.getToDate() + "',"
                          + dateFormat + ")";
        }

        if (searchQuery.getFromDate().length() == 0) {
        //if ((searchQuery.getFromDate() != null) || !searchQuery.getFromDate().isEmpty()) {
            where = where + " AND ad.detectiontime >= to_date('"
                          + searchQuery.getFromDate() + "',"
                          + dateFormat + ")";
       }
*/

        String sql = select + from + joinClause + where;

        logger.debug(sql);
        return sql;

    }




}
