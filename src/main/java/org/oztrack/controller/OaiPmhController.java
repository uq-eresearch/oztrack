package org.oztrack.controller;

import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

@Controller
public class OaiPmhController {
    private static class OaiPmhErrorView extends AbstractView {
        private final String code;

        public OaiPmhErrorView(String code) {
            this.code = code;
        }

        @Override
        protected void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            String responseDate = DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new GregorianCalendar());
            String requestURL = request.getRequestURL().toString();
            PrintWriter out = response.getWriter();
            out.append("<OAI-PMH\n");
            out.append("  xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n");
            out.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            out.append("  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n");
            out.append("  <responseDate>" + StringEscapeUtils.escapeXml(responseDate) + "</responseDate>\n");
            out.append("  <request>" + StringEscapeUtils.escapeXml(requestURL) + "</request>\n");
            out.append("  <error code=\"" + StringEscapeUtils.escapeXml(code) + "\" />\n");
            out.append("</OAI-PMH>\n");
        }
    }

    @RequestMapping(value="/oai", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public View handleRequest(
        @RequestParam(value="verb") String verb
    ) {
        if (verb.equals("Identify")) {
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListMetadataFormats")) {
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListSets")) {
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListIdentifiers")) {
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListRecords")) {
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("GetRecord")) {
            throw new UnsupportedOperationException();
        }
        else {
            return new OaiPmhErrorView("badVerb");
        }
    }
}
