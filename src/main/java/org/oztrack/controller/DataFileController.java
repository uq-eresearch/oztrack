package org.oztrack.controller;

import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.DataFileDao;
import org.oztrack.data.model.DataFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DataFileController {
    @Autowired
    private DataFileDao dataFileDao;

    @ModelAttribute("dataFile")
    public DataFile getDataFile(@PathVariable(value="id") Long dataFileId) {
        return dataFileDao.getDataFileById(dataFileId);
    }
    
    @RequestMapping(value="/datafiles/{id}", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#dataFile.project, 'read')")
    public String getView(@ModelAttribute(value="dataFile") DataFile dataFile) throws Exception {
        return "datafile";
    }

    @RequestMapping(value="/datafiles/{id}", method=RequestMethod.DELETE)
    @PreAuthorize("hasPermission(#dataFile.project, 'write')")
    public void processDelete(@ModelAttribute(value="dataFile") DataFile dataFile, HttpServletResponse response) {
        dataFileDao.delete(dataFile);
        response.setStatus(204);
    }
}
