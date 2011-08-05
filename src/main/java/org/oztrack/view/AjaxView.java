package org.oztrack.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 3/08/11
 * Time: 2:36 PM
 */
public class AjaxView extends AbstractView {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (model != null) {
            logger.debug("Resolving ajax request view - " + model);
        }

        String projectId = request.getParameter("projectId");
        if (projectId != null) {
            logger.debug("Ajax View for projectId: " + projectId);
        }
        else {
            logger.debug("Ajax View no projectId");
        }

        File kmlFile = new File("D:\\oztrack_related\\points.kml");
        FileInputStream fin = new FileInputStream(kmlFile);
        byte kmlContent[] = new byte[(int) kmlFile.length()];
        fin.read(kmlContent);

        response.setContentType("text/xml");
        response.getOutputStream().write(kmlContent);

    }
}
