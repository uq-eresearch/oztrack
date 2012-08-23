package org.oztrack.view;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.oztrack.data.model.SearchQuery;
import org.springframework.web.servlet.view.AbstractView;

public class KMLMapQueryView extends AbstractView {
    private File kmlFile;
    private SearchQuery searchQuery;

    public KMLMapQueryView(File kmlFile, SearchQuery searchQuery) {
        this.kmlFile = kmlFile;
        this.searchQuery = searchQuery;
    }

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        if (searchQuery.getProject() == null) {
            return;
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(kmlFile);
            byte kmlContent[] = new byte[(int) kmlFile.length()];
            fileInputStream.read(kmlContent);

            String filename = searchQuery.getMapQueryType().name().toLowerCase(Locale.ENGLISH) + ".kml";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/xml");
            response.getOutputStream().write(kmlContent);
            kmlFile.delete();
        }
        finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }
}