package org.oztrack.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.SightingDao;
import org.oztrack.data.model.Sighting;
import org.oztrack.validator.SightingFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SightingFormController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private SightingDao sightingDao;
    
    @ModelAttribute("sighting")
    public Sighting getSighting() {
        return new Sighting();
    }

    @RequestMapping(value="/sighting", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public String showForm() throws Exception {
        return "sighting";
    }
    
    @RequestMapping(value="/sighting", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public String onSubmit(
        Model model,
        @ModelAttribute(value="sighting") Sighting sighting,
        BindingResult bindingResult
    ) throws Exception {
        new SightingFormValidator().validate(sighting, bindingResult);
        if (bindingResult.hasErrors()) {
            return "sighting";
        }
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

        return "sightingsuccess";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        CustomDateEditor editor = new CustomDateEditor(sdf, true);
        binder.registerCustomEditor(Date.class,editor);
    }
}