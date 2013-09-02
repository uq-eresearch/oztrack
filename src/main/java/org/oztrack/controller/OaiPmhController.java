package org.oztrack.controller;

import java.io.PrintWriter;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

@Controller
public class OaiPmhController {
    private static class OaiPmhErrorView extends AbstractView {
        private final String code;
        private final String message;

        public OaiPmhErrorView(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        protected void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            FastDateFormat utcDateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");
            GregorianCalendar currentUtcDateTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            PrintWriter out = response.getWriter();
            out.append("<OAI-PMH\n");
            out.append("  xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n");
            out.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            out.append("  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n");
            out.append("  <responseDate>" + StringEscapeUtils.escapeXml(utcDateTimeFormat.format(currentUtcDateTime)) + "</responseDate>\n");
            out.append("  <request>" + StringEscapeUtils.escapeXml(request.getRequestURL().toString()) + "</request>\n");
            out.append("  <error code=\"" + StringEscapeUtils.escapeXml(code) + "\">" + StringEscapeUtils.escapeXml(message) + "</error>\n");
            out.append("</OAI-PMH>\n");
        }
    }

    @RequestMapping(value="/oai", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public View handleRequest(HttpServletRequest request) {
        String[] verbs = request.getParameterValues("verb");
        if (verbs == null) {
            return new OaiPmhErrorView("badVerb", "Verb argument is missing.");
        }
        if (verbs.length > 1) {
            return new OaiPmhErrorView("badVerb", "Verb argument is repeated.");
        }
        String verb = verbs[0];
        if (verb.equals("Identify")) {
            // Possible errors:
            // - badArgument
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListMetadataFormats")) {
            // Possible errors:
            // - badArgument
            // - idDoesNotExist
            // - noMetadataFormats
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListSets")) {
            // Possible errors:
            // - badArgument
            // - badResumptionToken
            // - noSetHierarchy
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListIdentifiers")) {
            // Possible errors:
            // - badArgument
            // - badResumptionToken
            // - cannotDisseminateFormat
            // - noRecordsMatch
            // - noSetHierarchy
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("ListRecords")) {
            // Possible errors:
            // - badArgument
            // - badResumptionToken
            // - cannotDisseminateFormat
            // - noRecordsMatch
            // - noSetHierarchy
            throw new UnsupportedOperationException();
        }
        else if (verb.equals("GetRecord")) {
            // Possible errors:
            // - badArgument
            // - cannotDisseminateFormat
            // - idDoesNotExist
            throw new UnsupportedOperationException();
        }
        else {
            return new OaiPmhErrorView("badVerb", "Verb argument is not a legal OAI-PMH verb.");
        }
    }
}
