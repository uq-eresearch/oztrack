package org.oztrack.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.util.RServeInterface;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 3/08/11
 * Time: 2:36 PM
 */
public class KMLMapQueryView extends AbstractView {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {

        SearchQuery searchQuery;
        File kmlFile = null;

        if (model != null) {
            logger.debug("Resolving ajax request view ");
            searchQuery = (SearchQuery) model.get("searchQuery");

            if (searchQuery.getProject() != null) {
                RServeInterface r = new RServeInterface();
                kmlFile = r.createKml(searchQuery);

            }
        }

        //write out the kml
        FileInputStream fin = new FileInputStream(kmlFile);
        byte kmlContent[] = new byte[(int) kmlFile.length()];
        fin.read(kmlContent);

        response.setContentType("text/xml");
        response.getOutputStream().write(kmlContent);
        kmlFile.delete();

    }
}
