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

    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        String dataSpaceUrl = OzTrackApplication.getApplicationContext().getDataSpaceUrl();
        String agentURL = dataSpaceUrl + "agents/" + project.getDataSpaceAgent().getDataSpaceAgentURI();
        String collectionURL = dataSpaceUrl + "collections/" + project.getDataSpaceURI();

        String json = "{ \"dataSpaceAgentURL\" : \"" + agentURL + "\""
                    + ",\"dataSpaceAgentUpdateDate\" : \"" + dateTimeFormat.format(project.getDataSpaceAgent().getDataSpaceAgentUpdateDate()) + "\""
                    + ",\"dataSpaceCollectionURL\" : \"" + collectionURL + "\""
                    + ",\"dataSpaceUpdateDate\" : \"" + dateTimeFormat.format(project.getDataSpaceUpdateDate()) + "\""
                    + ",\"errorMessage\" : \"" + errorMessage + "\""
                    + "}";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}