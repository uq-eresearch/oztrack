package org.oztrack.controller;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.util.OaiPmhConstants;
import org.oztrack.util.OaiPmhException;
import org.oztrack.view.OaiPmhListMetadataFormatsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements ListMetadataFormats verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ListMetadataFormats
@Controller
public class OaiPmhListMetadataFormatsController extends OaiPmhController {
    @Autowired
    OaiPmhRecordDao recordDao;

    @RequestMapping(value="/oai-pmh", method={RequestMethod.GET, RequestMethod.POST}, produces="text/xml", params="verb=ListMetadataFormats")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb", "identifier"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
        }

        String identifier = request.getParameter("identifier");
        if (identifier != null) {
            OaiPmhRecord record = recordDao.getRecordByOaiPmhRecordIdentifier(identifier);
            if (record == null) {
                throw new OaiPmhException("idDoesNotExist", "identifier argument unknown or illegal in this repository.");
            }
        }

        return new OaiPmhListMetadataFormatsView(OaiPmhConstants.supportedMetadataFormats);
    }
}
