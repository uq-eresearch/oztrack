package org.oztrack.view;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.view.AbstractView;

public class DataSpaceInterfaceView extends AbstractView{

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.info("renderMergedOutputModel");
		
    	Project project;
		
		project = (Project) model.get("project");
	    project.setDataSpaceUpdateDate(new Date());
	    
	    String test = "hello";
	    
	    String json = "{ dataspaceURI : " + test  // + project.getDataSpaceURI() 
	    			+ " , dataspaceUpdateDate : " + project.getDataSpaceUpdateDate() + "}";	
	            
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();

		
	}
	
	
	

}
