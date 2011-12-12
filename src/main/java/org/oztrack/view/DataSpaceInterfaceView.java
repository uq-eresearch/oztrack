package org.oztrack.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.util.DataSpaceCollection;
import org.springframework.web.servlet.view.AbstractView;

public class DataSpaceInterfaceView extends AbstractView{

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
	protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		logger.info("renderMergedOutputModel");
		
		
		Project project = (Project) model.get("project");
	    //project.setDataSpaceUpdateDate(new Date());
	    
	    ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
	    Project updatedProject = projectDao.getProjectById(project.getId());
	    updatedProject.setDataSpaceUpdateDate(new Date());
	    updatedProject = projectDao.update(updatedProject);
	    
	    DataSpaceCollection dsi = new DataSpaceCollection(updatedProject);
	    String agentAtom = dsi.agentToAtom();
	    String collectionAtom = dsi.collectionToAtom();
	    
	    logger.info("**********************************************");
	    logger.info(agentAtom);
	    logger.info("**********************************************");
	    logger.info(collectionAtom);
	    logger.info("**********************************************");
		
/*	    String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL(); 
	    URL agentURL = new URL(dataSpaceURL+"agents");
	    URL collectionURL = new URL(dataSpaceURL+"collections");
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(OzTrackApplication.getApplicationContext().getServerProxyName(), 8080));
		
		// do the agent 
		URLConnection uc = agentURL.openConnection(proxy);
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/atom+xml");
		
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		logger.info("posting agent details to dataspace");
		pw.print(agentAtom);
		pw.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		logger.info("Dataspace Response: " + res);
		in.close();
*/
		
	    String json = "{ \"dataspaceURI\" : \"" + project.getDataSpaceURI() 
	    			+ "\" , \"dataspaceUpdateDate\" : \"" + project.getDataSpaceUpdateDate() + "\"}";	

//	    String json = "{ \"dataspaceURI\" : \"test\", \"dataspaceUpdateDate\" : \"test\" } " ;
	    
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		response.getWriter().flush();

		
	}
	
	
	

}
