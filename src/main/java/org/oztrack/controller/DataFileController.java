package org.oztrack.controller;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONWriter;
import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.DataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DataFileController {
    @Autowired
    private DataFileDao dataFileDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("dataFile")
    public void initDataFileBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("dataFile")
    public DataFile getDataFile(@PathVariable(value="id") Long dataFileId) {
        return dataFileDao.getDataFileById(dataFileId);
    }

    @RequestMapping(value="/datafiles/{id}", method=RequestMethod.GET, produces="text/html")
    @PreAuthorize("hasPermission(#dataFile.project, 'write')")
    public String getHtmlView(Model model, @ModelAttribute(value="dataFile") DataFile dataFile) throws Exception {
        model.addAttribute("dataFileDetectionCount", dataFileDao.getDetectionCount(dataFile, false));
        return "datafile";
    }

    @RequestMapping(value="/datafiles/{id}", method=RequestMethod.GET, produces="application/json")
    @PreAuthorize("hasPermission(#dataFile.project, 'write')")
    public void getJsonView(
        HttpServletResponse response,
        @ModelAttribute(value="dataFile") DataFile dataFile
    ) throws Exception {
        JSONWriter out = new JSONWriter(response.getWriter());
        out.object();
        out.key("id").value(dataFile.getId());
        out.key("status").value(dataFile.getStatus());
        out.key("statusMessage").value(dataFile.getStatusMessage());
        out.key("numPositionFixes").value(dataFileDao.getDetectionCount(dataFile, false));
        out.endObject();
    }

    @RequestMapping(value="/datafiles/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#dataFile.project, 'write')")
    public void processDelete(@ModelAttribute(value="dataFile") DataFile dataFile, HttpServletResponse response) {
        dataFileDao.delete(dataFile);
        positionFixDao.renumberPositionFixes(dataFile.getProject());
        response.setStatus(204);
    }
}
