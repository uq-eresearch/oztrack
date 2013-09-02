package org.oztrack.controller;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.oztrack.app.OzTrackConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

@Controller
public class OaiPmhController {
    @Autowired
    private OzTrackConfiguration configuration;

    private static abstract class OaiPmhView extends AbstractView {
        private final boolean includeRequestArguments;

        public OaiPmhView() {
            this(true);
        }

        public OaiPmhView(boolean includeRequestArguments) {
            this.includeRequestArguments = includeRequestArguments;
        }

        @Override
        protected void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            FastDateFormat utcDateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");
            GregorianCalendar currentUtcDateTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            String responseDate = utcDateTimeFormat.format(currentUtcDateTime);
            String baseUrl = request.getRequestURL().toString();
            PrintWriter out = response.getWriter();
            out.append("<OAI-PMH\n");
            out.append("  xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n");
            out.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            out.append("  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n");
            out.append("  <responseDate>" + StringEscapeUtils.escapeXml(responseDate) + "</responseDate>\n");
            StringBuilder parameterAttributes = new StringBuilder();
            if (includeRequestArguments) {
                for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
                    parameterAttributes.append(" ").append(parameterEntry.getKey());
                    parameterAttributes.append("=\"").append(StringUtils.join(parameterEntry.getValue(), ",")).append("\"");
                }
            }
            out.append("  <request" + parameterAttributes.toString() + ">" + StringEscapeUtils.escapeXml(baseUrl) + "</request>\n");
            renderVerbElement(model, request, response);
            out.append("</OAI-PMH>\n");
        }

        protected abstract void renderVerbElement(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception;
    }

    private static class OaiPmhErrorView extends OaiPmhView {
        private final String code;
        private final String message;

        public OaiPmhErrorView(String code, String message) {
            super(!code.equals("badVerb") && !code.equals("badArgument"));
            this.code = code;
            this.message = message;
        }

        @Override
        protected void renderVerbElement(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <error code=\"" + StringEscapeUtils.escapeXml(code) + "\">" + StringEscapeUtils.escapeXml(message) + "</error>\n");
        }
    }

    private static class OaiPmhIdentifyView extends OaiPmhView {
        private final String repositoryName;
        private final String adminEmail;

        public OaiPmhIdentifyView(String repositoryName, String adminEmail) {
            this.repositoryName = repositoryName;
            this.adminEmail = adminEmail;
        }

        @Override
        protected void renderVerbElement(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            String baseUrl = request.getRequestURL().toString();
            String earliestDatestamp = "1990-02-01T00:00:00Z"; // TODO: Query from database
            PrintWriter out = response.getWriter();
            out.append("  <Identify>\n");
            out.append("    <repositoryName>" + StringEscapeUtils.escapeXml(repositoryName) + "</repositoryName>\n");
            out.append("    <baseURL>" + StringEscapeUtils.escapeXml(baseUrl) + "</baseURL>\n");
            out.append("    <protocolVersion>2.0</protocolVersion>\n");
            out.append("    <adminEmail>" + StringEscapeUtils.escapeXml(adminEmail) + "</adminEmail>\n");
            out.append("    <earliestDatestamp>" + StringEscapeUtils.escapeXml(earliestDatestamp) + "</earliestDatestamp>\n");
            out.append("    <deletedRecord>transient</deletedRecord>\n");
            out.append("    <granularity>YYYY-MM-DDThh:mm:ssZ</granularity>\n");
            out.append("  </Identify>\n");
        }
    }

    private static class OaiPmhListMetadataFormatsView extends OaiPmhView {
        @Override
        protected void renderVerbElement(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
        ) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <ListMetadataFormats>\n");
            out.append("    <metadataFormat>\n");
            out.append("      <metadataPrefix>rif</metadataPrefix>\n");
            out.append("      <schema>http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd</schema>\n");
            out.append("      <metadataNamespace>http://ands.org.au/standards/rif-cs/registryObjects</metadataNamespace>\n");
            out.append("    </metadataFormat>\n");
            out.append("  </ListMetadataFormats>\n");
        }
    }

    @RequestMapping(value="/oai", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!configuration.isOaiPmhEnabled()) {
            response.setStatus(404);
            return null;
        }
        String[] verbs = request.getParameterValues("verb");
        if (verbs == null) {
            return new OaiPmhErrorView("badVerb", "Verb argument is missing.");
        }
        if (verbs.length > 1) {
            return new OaiPmhErrorView("badVerb", "Verb argument is repeated.");
        }
        String verb = verbs[0];
        if (verb.equals("Identify")) {
            HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb"));
            OaiPmhView illegalArgumentsErrorView = checkForIllegalArguments(request, legalArguments);
            if (illegalArgumentsErrorView != null) {
                return illegalArgumentsErrorView;
            }
            return new OaiPmhIdentifyView(
                configuration.getOaiPmhRepositoryName(),
                configuration.getOaiPmhAdminEmail()
            );
        }
        else if (verb.equals("ListMetadataFormats")) {
            HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier"));
            OaiPmhView illegalArgumentsErrorView = checkForIllegalArguments(request, legalArguments);
            if (illegalArgumentsErrorView != null) {
                return illegalArgumentsErrorView;
            }
            String[] identifiers = request.getParameterValues("identifier");
            if ((identifiers != null) && (identifiers.length > 1)) {
                return new OaiPmhErrorView("badArgument", "Identifier argument is repeated.");
            }
            @SuppressWarnings("unused")
            String identifier = (identifiers != null) ? identifiers[0] : null;
            // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)
            // TODO: Check for noMetadataFormats error (no metadata formats available for identified item)
            return new OaiPmhListMetadataFormatsView();
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

    private OaiPmhView checkForIllegalArguments(HttpServletRequest request, HashSet<String> legalArguments) {
        for (Enumeration<String> arguments = request.getParameterNames(); arguments.hasMoreElements(); ) {
            String argument = arguments.nextElement();
            if (!legalArguments.contains(argument)) {
                return new OaiPmhErrorView("badArgument", "Request includes illegal argument \'" + argument + "\'.");
            }
        }
        return null;
    }
}
