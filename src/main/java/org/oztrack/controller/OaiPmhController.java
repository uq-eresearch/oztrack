package org.oztrack.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
    private static class Namespace {
        public final String nsPrefix;
        public final String nsUri;
        public final String xsdUri;
        public Namespace(String nsPrefix, String nsUri, String xsdUri) {
            this.nsPrefix = nsPrefix;
            this.nsUri = nsUri;
            this.xsdUri = xsdUri;
        }
    }
    private static Namespace XSI = new Namespace(
        "xsi",
        "http://www.w3.org/2001/XMLSchema-instance",
        null
    );
    private static Namespace OAI_PMH = new Namespace(
        null,
        "http://www.openarchives.org/OAI/2.0/",
        "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"
    );

    private static class MetadataFormat extends Namespace {
        private MetadataFormat(String nsPrefix, String nsUri, String xsdUri) {
            super(nsPrefix, nsUri, xsdUri);
        }
    }
    private static MetadataFormat OAI_DC = new MetadataFormat(
        "oai_dc",
        "http://www.openarchives.org/OAI/2.0/oai_dc/",
        "http://www.openarchives.org/OAI/2.0/oai_dc.xsd"
    );
    private static MetadataFormat RIF_CS = new MetadataFormat(
        "rif",
        "http://ands.org.au/standards/rif-cs/registryObjects",
        "http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd"
    );
    private static List<MetadataFormat> supportedMetadataFormats = new ArrayList<MetadataFormat>(Arrays.asList(
        OAI_DC,
        RIF_CS
    ));

    @Autowired
    private OzTrackConfiguration configuration;

    // Implements the OAI-PMH XML Response Format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#XMLResponse
    private static abstract class OaiPmhView extends AbstractView {
        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            // Encoding of the XML must use the UTF-8 representation of Unicode.
            response.setCharacterEncoding("UTF-8");

            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter out = xmlOutputFactory.createXMLStreamWriter(response.getWriter());

            // The first tag output is an XML declaration where the version is always 1.0 and the encoding is always UTF-8.
            out.writeStartDocument("UTF-8", "1.0");

            // The root element has the name OAI-PMH and must define the namespace URI and schema URL for OAI-PMH.
            out.writeStartElement("OAI-PMH");
            out.setDefaultNamespace(OAI_PMH.nsUri);
            out.writeDefaultNamespace(OAI_PMH.nsUri);
            out.setPrefix(XSI.nsPrefix, XSI.nsUri);
            out.writeNamespace(XSI.nsPrefix, XSI.nsUri);
            out.writeAttribute(XSI.nsUri, "schemaLocation", OAI_PMH.nsUri + " " + OAI_PMH.xsdUri);

            // The first child of the root element is the responseDate element, indicating the UTC datetime that the response was sent.
            SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcDateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String responseDate = utcDateTimeFormat.format(new Date());
            writeSimpleElement(out, "responseDate", responseDate);

            // The second child of the root element is the request element, indicating the protocol request that generated this response.
            // The content of the request element must always be the base URL of the protocol request;
            // The attribute values must correspond to the key=value pairs of protocol request.
            String baseUrl = request.getRequestURL().toString();
            out.writeStartElement("request");
            if (shouldIncludeRequestArguments()) {
                for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
                    out.writeAttribute(parameterEntry.getKey(), parameterEntry.getValue()[0]);
                }
            }
            out.writeCharacters(baseUrl);
            out.writeEndElement();

            // The third child of the root element is either:
            // an error element that must be used in case of an error or exception condition;
            // an element with the same name as the verb of the respective OAI-PMH request.
            writeMainElement(out);

            out.writeEndElement();
            out.writeEndDocument();
        }

        protected boolean shouldIncludeRequestArguments() {
            // In cases where the request that generated this response did not result in an error or exception condition,
            // the attributes and attribute values of the request element must match the key=value pairs of the protocol request.
            return true;
        }

        protected abstract void writeMainElement(XMLStreamWriter out) throws XMLStreamException;
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
            return !(code.equals("badVerb") || code.equals("badArgument"));
        }

        @Override
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            // In event of an error or exception condition, repositories must include one or more error elements in the response.
            // Each error element must have a code attribute and may have a string value to provide information to a human reader.
            // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
            out.writeStartElement("error");
            out.writeAttribute("code", code);
            if (message != null) {
                out.writeCharacters(message);
            }
            out.writeEndElement();
        }
    }

    // Implements Identify verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
    private static class OaiPmhIdentifyView extends OaiPmhView {
        private final String repositoryName;
        private final String baseUrl;
        private final String adminEmail;

        public OaiPmhIdentifyView(String repositoryName, String baseUrl, String adminEmail) {
            this.repositoryName = repositoryName;
            this.baseUrl = baseUrl;
            this.adminEmail = adminEmail;
        }

        @Override
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            String earliestDatestamp = "1970-01-01T00:00:00Z"; // TODO: Query from database
            out.writeStartElement("Identify");
            writeSimpleElement(out, "repositoryName", repositoryName);
            writeSimpleElement(out, "baseURL", baseUrl);
            writeSimpleElement(out, "protocolVersion", "2.0");
            writeSimpleElement(out, "adminEmail", adminEmail);
            writeSimpleElement(out, "earliestDatestamp", earliestDatestamp);
            writeSimpleElement(out, "deletedRecord", "transient");
            writeSimpleElement(out, "granularity", "YYYY-MM-DDThh:mm:ssZ");
            out.writeEndElement();
        }
    }

    // Implements ListMetadataFormats verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
    private static class OaiPmhListMetadataFormatsView extends OaiPmhView {
        @Override
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            out.writeStartElement("ListMetadataFormats");
            for (MetadataFormat metadataFormat : supportedMetadataFormats) {
                out.writeStartElement("metadataFormat");
                writeSimpleElement(out, "metadataPrefix", metadataFormat.nsPrefix);
                writeSimpleElement(out, "schema", metadataFormat.xsdUri);
                writeSimpleElement(out, "metadataNamespace", metadataFormat.nsUri);
                out.writeEndElement();
            }
            out.writeEndElement();
        }
    }

    // Implements ListSets verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListSets
    private static class OaiPmhListSetsView extends OaiPmhView {
        @Override
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            out.writeEmptyElement("ListSets");
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
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            out.writeEmptyElement(verb);
        }
    }

    // Implements GetRecord verb response format
    // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
    private static class OaiPmhGetRecordView extends OaiPmhView {
        @Override
        protected void writeMainElement(XMLStreamWriter out) throws XMLStreamException {
            out.writeEmptyElement("GetRecord");
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
            configuration.getBaseUrl() + "/oai-pmh",
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
        HashSet<String> supportedMetadataPrefixes = new HashSet<String>();
        for (MetadataFormat metadataFormat : supportedMetadataFormats) {
            supportedMetadataPrefixes.add(metadataFormat.nsPrefix);
        }
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
        HashSet<String> supportedMetadataPrefixes = new HashSet<String>();
        for (MetadataFormat metadataFormat : supportedMetadataFormats) {
            supportedMetadataPrefixes.add(metadataFormat.nsPrefix);
        }
        if (!supportedMetadataPrefixes.contains(metadataPrefix)) {
            return new OaiPmhErrorView("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        // TODO: Query for record matching identifier
        // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)

        return new OaiPmhGetRecordView();
    }

    private static void writeSimpleElement(XMLStreamWriter out, String localName, String text) throws XMLStreamException {
        out.writeStartElement(localName);
        out.writeCharacters(text);
        out.writeEndElement();
    }
}
