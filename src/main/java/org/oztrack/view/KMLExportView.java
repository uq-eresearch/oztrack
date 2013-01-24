package org.oztrack.view;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.springframework.web.servlet.view.AbstractView;

public class KMLExportView extends AbstractView{
    private final SimpleDateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private final Animal animal;
    private final List<PositionFix> positionFixList;

    public KMLExportView(Project project, Animal animal, List<PositionFix> positionFixList) {
        this.animal = animal;
        this.positionFixList = positionFixList;
    }

    @Override
    protected void renderMergedOutputModel(
        @SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        String fileName = "animal.kml";
        response.setHeader("Content-Disposition", "attachment; filename=\""+ fileName + "\"");
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.append("<kml xmlns=\"http://earth.google.com/kml/2.2\">");
        writer.append("<Document>");
        Matcher matcher = Pattern.compile("^#(..)(..)(..)$").matcher(animal.getColour());
        if (matcher.matches()) {
            String kmlBaseColour = matcher.group(3) + matcher.group(2) + matcher.group(1);
            String kmlIconColour = "cc" + kmlBaseColour; // 80% opacity
            writer.append("<Style id=\"animal-" + animal.getId() + "\">");
            writer.append("  <IconStyle>");
            writer.append("    <color>" + kmlIconColour + "</color>");
            writer.append("    <scale>0.8</scale>");
            writer.append("    <Icon>");
            writer.append("        <href>http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png</href>");
            writer.append("    </Icon>");
            writer.append("  </IconStyle>");
            writer.append("</Style>");
        }
        writer.append("<Folder>");
        writer.append("<name>" + animal.getId() + "</name>");
        for(PositionFix positionFix : positionFixList) {
            writer.append("<Placemark>");
            writer.append("  <styleUrl>#animal-" + animal.getId() + "</styleUrl>");
            writer.append("  <description>" + dateTimeFormat.format(positionFix.getDetectionTime()) + "</description>");
            writer.append("  <Point>");
            writer.append("    <coordinates>" + positionFix.getLocationGeometry().getX() + "," + positionFix.getLocationGeometry().getY() + "</coordinates>");
            writer.append("  </Point>");
            writer.append("  <TimeStamp>");
            writer.append("    <when>" + isoDateTimeFormat.format(positionFix.getDetectionTime()) + "</when>");
            writer.append("  </TimeStamp>");
            writer.append("</Placemark>");
        }
        writer.append("</Folder>");
        writer.append("</Document>");
        writer.append("</kml>");
    }
}