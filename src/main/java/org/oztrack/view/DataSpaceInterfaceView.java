package org.oztrack.view;

import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.view.AbstractView;

public class DataSpaceInterfaceView extends AbstractView {
    protected final Log logger = LogFactory.getLog(getClass());

    public DataSpaceInterfaceView() {
    }

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        Project project = (Project) model.get("project");
        String errorMessage = (String) model.get("errorMessage");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();

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