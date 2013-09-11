package org.oztrack.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhException;
import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.view.OaiPmhListIdentifiersOrListRecordsView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements ListIdentifiers and ListRecords verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
@Controller
public class OaiPmhListIdentifiersOrListRecordsController extends OaiPmhController {
    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET, params={"verb=ListIdentifiers"})
    public View handleListIdentifiersRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        return handleRequest(request, response);
    }

    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET, params={"verb=ListRecords"})
    public View handleListRecordsRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        return handleRequest(request, response);
    }

    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        String verb = request.getParameter("verb");

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        // Return badArgument error code if exclusive resumptionToken argument mixed with others.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ProtocolMessages
        String resumptionToken = request.getParameter("resumptionToken");
        HashSet<String> legalArguments = (resumptionToken != null)
            ? new HashSet<String>(Arrays.asList("verb", "resumptionToken"))
            : new HashSet<String>(Arrays.asList("verb", "from", "until", "metadataPrefix", "set"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
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
                throw new OaiPmhException("badArgument", "from argument is invalid datetime.");
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
                throw new OaiPmhException("badArgument", "to argument is invalid datetime.");
            }
        }

        // Return badArgument error code if request is missing required arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String metadataPrefix = request.getParameter("metadataPrefix");
        if (metadataPrefix == null) {
            throw new OaiPmhException("badArgument", "metadataPrefix argument is missing.");
        }

        // Return cannotDisseminateFormat error code if metadataPrefix argument not supported by the repository.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListIdentifiers
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListRecords
        OaiPmhMetadataFormat metadataFormat = null;
        for (OaiPmhMetadataFormat supportedMetadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            if (supportedMetadataFormat.nsPrefix.equals(metadataPrefix)) {
                metadataFormat = supportedMetadataFormat;
            }
        }
        if (metadataFormat == null) {
            throw new OaiPmhException("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        @SuppressWarnings("unused")
        String set = request.getParameter("set");

        // TODO: Check for noSetHierarchy (repository does not support sets)
        // TODO: Query for records matching from/until/set parameters
        // TODO: Check for noRecordsMatch error (combination of from/until/set results no records)

        return new OaiPmhListIdentifiersOrListRecordsView(verb, metadataFormat);
    }
}
