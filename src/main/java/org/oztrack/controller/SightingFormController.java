package org.oztrack.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.SightingDao;
import org.oztrack.data.model.Sighting;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/06/11
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class SightingFormController extends SimpleFormController {

    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        Sighting sighting = (Sighting)command;
        SightingDao sightingDao = OzTrackApplication.getApplicationContext().getDaoManager().getSightingDao();
        sightingDao.save(sighting);

        // sort out the image file - need the id to sort out the file path
        // get the image and throw it on the filesystem
        MultipartFile file = sighting.getImageFile();
        String dataDir = OzTrackApplication.getApplicationContext().getDataDir();

        if ((dataDir == null) || (dataDir.isEmpty())) {
            logger.debug("dataDir property not set");
            dataDir = System.getProperty("user.home");
        } else {
            logger.debug("dataDir: " + dataDir);
        }

        // save the file to the data dir
        String filePath = dataDir + File.separator + "oztrack" + File.separator + "sightings" + File.separator
                         + "sighting_" + sighting.getId().toString() + "_" + file.getOriginalFilename();

        File saveFile = new File(filePath);
        saveFile.mkdirs();
        file.transferTo(saveFile);


        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        return modelAndView;

    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class,editor);

        super.initBinder(request, binder);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
