package org.oztrack.controller;

import java.util.List;

import org.oztrack.data.access.AcousticDetectionDao;
import org.oztrack.data.access.Page;
import org.oztrack.data.model.AcousticDetection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchAcousticController {
    @Autowired
    AcousticDetectionDao acousticDetectionDao;
    
    @RequestMapping(value="/searchacoustic", method=RequestMethod.GET)
    public String handleRequest(
        Model model,
        @RequestParam(value="offset", defaultValue="0") int offset
    ) throws Exception {
        int nbrObjectsPerPage=30;

        Page<AcousticDetection> acousticDetectionsPage = acousticDetectionDao.getPage(offset, nbrObjectsPerPage);
        List<AcousticDetection> acousticDetectionsList = acousticDetectionsPage.getObjects();
        
        int nbrObjectsThisPage = acousticDetectionsList.size();
        int totalCount = acousticDetectionDao.getTotalCount();

        model.addAttribute("acousticDetectionsList", acousticDetectionsList);
        model.addAttribute("offset", offset);
        model.addAttribute("nbrObjectsPerPage", nbrObjectsPerPage);
        model.addAttribute("nbrObjectsThisPage", nbrObjectsThisPage);
        model.addAttribute("totalCount", totalCount);
        return "searchacoustic";
    }
}