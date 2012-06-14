package org.oztrack.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class DataSpaceInterfaceController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private UserDao userDao;
    
    @RequestMapping(value="/dataspace", method=RequestMethod.POST)
	public String handleRequest(
	    Model model,
	    @RequestParam(value="project", required=false) String projectId,
	    @RequestParam(value="username", required=false) String username,
	    @RequestParam(value="action", required=false) String action
    ) throws Exception {
        User currentUser = userDao.getByUsername(username);

        if (currentUser == null) {
        	return "login";
        }
        else if (projectId == null) {
    		return "projects";
    	}
    	else {
    		Project project = projectDao.getProjectById(Long.parseLong(projectId));
    		Map <String, Object> projectActionMap = new HashMap<String, Object>();
    		projectActionMap.put("project", project);
    		projectActionMap.put("action", action);
    		
    		logger.info("request for dataspace syndication by user: " + currentUser.getUsername() + " for project: " + project.getTitle());
    		
    		// TODO: DAO should not be passed to view layer.
    		model.addAttribute("projectDao", projectDao);
    		model.addAttribute("userDao", userDao);
    		model.addAttribute("projectActionMap", projectActionMap);
    		return "java_DataSpaceInterface";
    	}
	}
}