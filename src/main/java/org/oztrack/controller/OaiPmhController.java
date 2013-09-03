package org.oztrack.controller;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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

// Implements the Open Archives Initiative Protocol for Metadata Harvesting (OAI-PMH)
// Protocol Version 2.0 of 2002-06-14; Document Version 2008-12-07T20:42:00Z
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm
@Controller
public class OaiPmhController {
    @Autowired
    private OzTrackConfiguration configuration;

    // Implements the OAI-PMH XML Response Format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#XMLResponse
    private static abstract class OaiPmhView extends AbstractView {
        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            // Encoding of the XML must use the UTF-8 representation of Unicode.
            response.setCharacterEncoding("UTF-8");

            PrintWriter out = response.getWriter();

            // The first tag output is an XML declaration where the version is always 1.0 and the encoding is always UTF-8.
            out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            // The root element has the name OAI-PMH and must define the namespace URI and schema URL for OAI-PMH.
            out.append("<OAI-PMH\n");
            out.append("  xmlns=\"http://www.openarchives.org/OAI/2.0/\"\n");
            out.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            out.append("  xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">\n");

            // The first child of the root element is the responseDate element, indicating the UTC datetime that the response was sent.
            GregorianCalendar currentUtcDateTime = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            FastDateFormat utcDateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String responseDate = utcDateTimeFormat.format(currentUtcDateTime);
            out.append("  <responseDate>" + StringEscapeUtils.escapeXml(responseDate) + "</responseDate>\n");

            // The second child of the root element is the request element, indicating the protocol request that generated this response.
            // The content of the request element must always be the base URL of the protocol request;
            // The attribute values must correspond to the key=value pairs of protocol request.
            String baseUrl = request.getRequestURL().toString();
            StringBuilder parameterAttributes = new StringBuilder();
            if (shouldIncludeRequestArguments()) {
                for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
                    parameterAttributes.append(" ").append(parameterEntry.getKey());
                    parameterAttributes.append("=\"").append(StringUtils.join(parameterEntry.getValue(), ",")).append("\"");
                }
            }
            out.append("  <request" + parameterAttributes.toString() + ">" + StringEscapeUtils.escapeXml(baseUrl) + "</request>\n");

            // The third child of the root element is either:
            // an error element that must be used in case of an error or exception condition;
            // an element with the same name as the verb of the respective OAI-PMH request.
            renderVerbElement(model, request, response);

            out.append("</OAI-PMH>\n");
        }

        protected boolean shouldIncludeRequestArguments() {
            // In cases where the request that generated this response did not result in an error or exception condition,
            // the attributes and attribute values of the request element must match the key=value pairs of the protocol request.
            return true;
        }

        protected abstract void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
    }

    // Implements Error and Exception Conditions response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
    private static class OaiPmhErrorView extends OaiPmhView {
        private final String code;
        private final String message;

        public OaiPmhErrorView(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        protected boolean shouldIncludeRequestArguments() {
            // In cases where the request that generated this response resulted in a badVerb or badArgument error condition,
            // the repository must return the base URL of the protocol request only. Attributes must not be provided in these cases.
            // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#XMLResponse
            return !code.equals("badVerb") && !code.equals("badArgument");
        }

        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            PrintWriter out = response.getWriter();
            // In event of an error or exception condition, repositories must include one or more error elements in the response.
            // Each error element must have a code attribute and may have a string value to provide information to a human reader.
            // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
            out.append("  <error code=\"" + StringEscapeUtils.escapeXml(code) + "\">");
            if (message != null) {
                out.append(StringEscapeUtils.escapeXml(message));
            }
            out.append("</error>\n");
        }
    }

    // Implements Identify verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
    private static class OaiPmhIdentifyView extends OaiPmhView {
        private final String repositoryName;
        private final String adminEmail;

        public OaiPmhIdentifyView(String repositoryName, String adminEmail) {
            this.repositoryName = repositoryName;
            this.adminEmail = adminEmail;
        }

        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            String baseUrl = request.getRequestURL().toString();
            String earliestDatestamp = "1970-01-01T00:00:00Z"; // TODO: Query from database
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

    // Implements ListMetadataFormats verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
    private static class OaiPmhListMetadataFormatsView extends OaiPmhView {
        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <ListMetadataFormats>\n");
            out.append("    <metadataFormat>\n");
            out.append("      <metadataPrefix>oai_dc</metadataPrefix>\n");
            out.append("      <schema>http://www.openarchives.org/OAI/2.0/oai_dc.xsd</schema>\n");
            out.append("      <metadataNamespace>http://www.openarchives.org/OAI/2.0/oai_dc/</metadataNamespace>\n");
            out.append("    </metadataFormat>\n");
            out.append("    <metadataFormat>\n");
            out.append("      <metadataPrefix>rif</metadataPrefix>\n");
            out.append("      <schema>http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd</schema>\n");
            out.append("      <metadataNamespace>http://ands.org.au/standards/rif-cs/registryObjects</metadataNamespace>\n");
            out.append("    </metadataFormat>\n");
            out.append("  </ListMetadataFormats>\n");
        }
    }

    // Implements ListSets verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets
    private static class OaiPmhListSetsView extends OaiPmhView {
        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <ListSets>\n");
            out.append("  </ListSets>\n");
        }
    }

    // Implements ListIdentifiers and ListRecords verb response formats
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
    private static class OaiPmhListIdentifiersOrListRecordsView extends OaiPmhView {
        private final String verb;

        public OaiPmhListIdentifiersOrListRecordsView(String verb) {
            this.verb = verb;
        }

        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <" + verb + ">\n");
            out.append("  </" + verb + ">\n");
        }
    }

    // Implements GetRecord verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
    private static class OaiPmhGetRecordView extends OaiPmhView {
        @Override
        protected void renderVerbElement(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            PrintWriter out = response.getWriter();
            out.append("  <GetRecord>\n");
            out.append("  </GetRecord>\n");
        }
    }

    // Implements OAI-PMH Protocol Requests and Responses
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ProtocolMessages
    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET)
    @PreAuthorize("permitAll")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!configuration.isOaiPmhEnabled()) {
            response.setStatus(404);
            return null;
        }

        // Return badVerb error code if verb argument is missing or repeated.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String[] verbs = request.getParameterValues("verb");
        if (verbs == null) {
            return new OaiPmhErrorView("badVerb", "verb argument is missing.");
        }
        if (verbs.length > 1) {
            return new OaiPmhErrorView("badVerb", "verb argument is repeated.");
        }
        String verb = request.getParameter("verb");

        // Return badArgument error code if request includes a repeated argument.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
            if (parameterEntry.getValue().length > 1) {
                return new OaiPmhErrorView("badArgument", parameterEntry.getKey() + " argument is repeated.");
            }
        }

        if (verb.equals("Identify")) {
            return handleIdentifyRequest(request);
        }
        else if (verb.equals("ListMetadataFormats")) {
            return handleListMetadataFormatsRequest(request);
        }
        else if (verb.equals("ListSets")) {
            return handleListSetsRequest(request);
        }
        else if (verb.equals("ListIdentifiers") || verb.equals("ListRecords")) {
            return handleListIdentifiersOrListRecordsRequest(request, verb);
        }
        else if (verb.equals("GetRecord")) {
            return handleGetRecordRequest(request);
        }
        else {
            // Return badVerb error code if verb argument is not a legal OAI-PMH verb.
            // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
            return new OaiPmhErrorView("badVerb", "Verb argument is not a legal OAI-PMH verb.");
        }
    }

    // Implements Identify verb request handling
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
    private View handleIdentifyRequest(HttpServletRequest request) {
        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            return new OaiPmhErrorView("badArgument", "Request includes illegal arguments.");
        }

        return new OaiPmhIdentifyView(
            configuration.getOaiPmhRepositoryName(),
            configuration.getOaiPmhAdminEmail()
        );
    }

    // Implements ListMetadataFormats verb request handling
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
    private View handleListMetadataFormatsRequest(HttpServletRequest request) {
        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            return new OaiPmhErrorView("badArgument", "Request includes illegal arguments.");
        }

        @SuppressWarnings("unused")
        String identifier = request.getParameter("identifier");

        // TODO: Query for records matching identifier
        // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)
        // TODO: Check for noMetadataFormats error (no metadata formats available for identified item)

        return new OaiPmhListMetadataFormatsView();
    }

    // Implements ListSets verb request handling
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets
    private View handleListSetsRequest(HttpServletRequest request) {
        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "resumptionToken"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            return new OaiPmhErrorView("badArgument", "Request includes illegal arguments.");
        }

        @SuppressWarnings("unused")
        String resumptionToken = request.getParameter("resumptionToken");

        // TODO: Check for badResumptionToken (resumptionToken is invalid or expired)
        // TODO: Check for noSetHierarchy (repository does not support sets)

        return new OaiPmhListSetsView();
    }

    // Implements ListIdentifiers and ListRecords verb request handling
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
    private View handleListIdentifiersOrListRecordsRequest(HttpServletRequest request, String verb) {
        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        // Return badArgument error code if exclusive resumptionToken argument mixed with others.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ProtocolMessages
        String resumptionToken = request.getParameter("resumptionToken");
        HashSet<String> legalArguments = (resumptionToken != null)
            ? new HashSet<String>(Arrays.asList("verb", "resumptionToken"))
            : new HashSet<String>(Arrays.asList("verb", "from", "until", "metadataPrefix", "set"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            return new OaiPmhErrorView("badArgument", "Request includes illegal arguments.");
        }

        // TODO: Check for badResumptionToken (resumptionToken is invalid or expired)

        // Dates and times are uniformly encoded using ISO8601 and are expressed in UTC throughout the protocol.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Dates
        // Return badArgument error code if values for arguments have an illegal syntax.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        @SuppressWarnings("unused")
        Date fromUtcDateTime = null;
        String fromUtcDateTimeString = request.getParameter("from");
        if (fromUtcDateTimeString != null) {
            try {
                fromUtcDateTime = utcDateTimeFormat.parse(fromUtcDateTimeString);
            }
            catch (ParseException e) {
                return new OaiPmhErrorView("badArgument", "from argument is invalid datetime.");
            }
        }
        @SuppressWarnings("unused")
        Date toUtcDateTime = null;
        String toUtcDateTimeString = request.getParameter("to");
        if (toUtcDateTimeString != null) {
            try {
                toUtcDateTime = utcDateTimeFormat.parse(toUtcDateTimeString);
            }
            catch (ParseException e) {
                return new OaiPmhErrorView("badArgument", "to argument is invalid datetime.");
            }
        }

        // Return badArgument error code if request is missing required arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String metadataPrefix = request.getParameter("metadataPrefix");
        if (metadataPrefix == null) {
            return new OaiPmhErrorView("badArgument", "metadataPrefix argument is missing.");
        }

        // Return cannotDisseminateFormat error code if metadataPrefix argument not supported by the repository.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
        HashSet<String> supportedMetadataPrefixes = new HashSet<String>(Arrays.asList("oai_dc", "rif"));
        if (!supportedMetadataPrefixes.contains(metadataPrefix)) {
            return new OaiPmhErrorView("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        @SuppressWarnings("unused")
        String set = request.getParameter("set");

        // TODO: Check for noSetHierarchy (repository does not support sets)
        // TODO: Query for records matching from/until/set parameters
        // TODO: Check for noRecordsMatch error (combination of from/until/set results no records)

        return new OaiPmhListIdentifiersOrListRecordsView(verb);
    }

    // Implements GetRecord verb request handling
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
    private View handleGetRecordRequest(HttpServletRequest request) {
        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier", "metadataPrefix"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            return new OaiPmhErrorView("badArgument", "Request includes illegal arguments.");
        }

        // Return badArgument error code if request is missing required arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String identifier = request.getParameter("identifier");
        if (identifier == null) {
            return new OaiPmhErrorView("badArgument", "identifier argument is missing.");
        }
        String metadataPrefix = request.getParameter("metadataPrefix");
        if (metadataPrefix == null) {
            return new OaiPmhErrorView("badArgument", "metadataPrefix argument is missing.");
        }

        // Return cannotDisseminateFormat error code if metadataPrefix argument not supported by identified item.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
        HashSet<String> supportedMetadataPrefixes = new HashSet<String>(Arrays.asList("oai_dc", "rif"));
        if (!supportedMetadataPrefixes.contains(metadataPrefix)) {
            return new OaiPmhErrorView("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        // TODO: Query for record matching identifier
        // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)

        return new OaiPmhGetRecordView();
    }
}
