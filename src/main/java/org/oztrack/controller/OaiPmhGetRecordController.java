package org.oztrack.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhException;
import org.oztrack.util.OaiPmhMetadataFormat;
import org.oztrack.view.OaiPmhGetRecordView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements GetRecord verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
@Controller
public class OaiPmhGetRecordController extends OaiPmhController {
    @RequestMapping(value="/oai-pmh", method={RequestMethod.GET, RequestMethod.POST}, produces="text/xml", params="verb=GetRecord")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier", "metadataPrefix"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
        }

        // Return badArgument error code if request is missing required arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String identifier = request.getParameter("identifier");
        if (identifier == null) {
            throw new OaiPmhException("badArgument", "identifier argument is missing.");
        }
        String metadataPrefix = request.getParameter("metadataPrefix");
        if (metadataPrefix == null) {
            throw new OaiPmhException("badArgument", "metadataPrefix argument is missing.");
        }

        // Return cannotDisseminateFormat error code if metadataPrefix argument not supported by identified item.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#GetRecord
        HashSet<String> supportedMetadataPrefixes = new HashSet<String>();
        for (OaiPmhMetadataFormat metadataFormat : OaiPmhConstants.supportedMetadataFormats) {
            supportedMetadataPrefixes.add(metadataFormat.nsPrefix);
        }
        if (!supportedMetadataPrefixes.contains(metadataPrefix)) {
            throw new OaiPmhException("cannotDisseminateFormat", "metadataPrefix argument is not supported by the repository.");
        }

        // TODO: Query for record matching identifier
        // TODO: Check for idDoesNotExist error (identifier argument unknown or illegal in this repository)
        String oaiPmhRecordIdentifierPrefix = configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix();
        String localIdentifier = identifier.substring(oaiPmhRecordIdentifierPrefix.length());
        if (localIdentifier.equals("service")) {
        }
        else if (localIdentifier.equals("oai-pmh")) {
        }
        else if (localIdentifier.equals("collection")) {
        }
        else if (localIdentifier.equals("data-manager")) {
        }
        else {
            Matcher matcher = Pattern.compile("^([a-z-]+)/([0-9]+)$").matcher(localIdentifier);
            if (!matcher.matches()) {
                throw new OaiPmhException("idDoesNotExist", "identifier argument unknown or illegal in this repository.");
            }
            String recordType = matcher.group(1);
            Long recordId = Long.valueOf(matcher.group(2));
            if (recordType.equals("projects")) {
            }
            else if (recordType.equals("people")) {
            }
            else if (recordType.equals("institutions")) {
            }
            else {
                throw new OaiPmhException("idDoesNotExist", "identifier argument unknown or illegal in this repository.");
            }
        }

        return new OaiPmhGetRecordView();
    }
}
