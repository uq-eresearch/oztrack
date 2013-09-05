package org.oztrack.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.view.OaiPmhErrorView;
import org.oztrack.view.OaiPmhGetRecordView;
import org.oztrack.view.OaiPmhIdentifyView;
import org.oztrack.view.OaiPmhListIdentifiersOrListRecordsView;
import org.oztrack.view.OaiPmhListMetadataFormatsView;
import org.oztrack.view.OaiPmhListSetsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements the Open Archives Initiative Protocol for Metadata Harvesting (OAI-PMH)
// Protocol Version 2.0 of 2002-06-14; Document Version 2008-12-07T20:42:00Z
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm
@Controller
public class OaiPmhController {
    @Autowired
    private OzTrackConfiguration configuration;

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
        for (OaiPmhMetadataFormat metadataFormat : OaiPmhConstants.supportedMetadataFormats) {
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
        for (OaiPmhMetadataFormat metadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            supportedMetadataPrefixes.add(metadataFormat.nsPrefix);
        }
        if (!supportedMetadataPrefixes.contains(metadataPrefix)) {
            return new OaiPmhErrorView("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        // TODO: Query for record matching identifier
        // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)

        return new OaiPmhGetRecordView();
    }
}
