package org.oztrack.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.error.DataSpaceInterfaceException;
import org.oztrack.util.DataSpaceCollection;
import org.oztrack.util.DataSpaceInterface;
import org.springframework.web.servlet.view.AbstractView;

public class DataSpaceInterfaceView extends AbstractView{

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		HashMap<String, Object> projectActionMap = (HashMap<String, Object>) model.get("projectActionMap");
	    
		Project tempProject = (Project) projectActionMap.get("project");
		String action = (String) projectActionMap.get("action");
		
	    ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
	    Project project = projectDao.getProjectById(tempProject.getId());
	    String errorMessage = "";
	    
	    try {
	    	DataSpaceInterface dsi = new DataSpaceInterface();
	    	if (action.equals("publish")) {
		    	dsi.updateDataSpace(project);
	    	} else if (action.equals("delete")) {
	    		dsi.deleteFromDataSpace(project);
	    	}
	    	project = projectDao.getProjectById(project.getId());

	    } catch (DataSpaceInterfaceException e) {
	    	errorMessage = e.getMessage();
	    }
	    
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//("yyyy-MM-dd'T'HH:mm:ss'Z'");
	    String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL() ;
	    
	    String agentURL = dataSpaceURL + "agents/" + project.getDataSpaceAgent().getDataSpaceAgentURI();	    
		String collectionURL = dataSpaceURL + "collections/" + project.getDataSpaceURI();
	    
	    String json = "{ \"dataSpaceAgentURL\" : \"" + agentURL + "\""
	    			+ ",\"dataSpaceAgentUpdateDate\" : \"" + simpleDateFormat.format(project.getDataSpaceAgent().getDataSpaceAgentUpdateDate()) + "\""
	    			+ ",\"dataSpaceCollectionURL\" : \"" + collectionURL + "\""
	    			+ ",\"dataSpaceUpdateDate\" : \"" + simpleDateFormat.format(project.getDataSpaceUpdateDate()) + "\""
	    			+ ",\"errorMessage\" : \"" + errorMessage + "\""
	    			+ "}";	
	    
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();
		
	}

}
