package org.oztrack.controller;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.util.OaiPmhException;
import org.oztrack.view.OaiPmhIdentifyView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements Identify verb request handling
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Identify
@Controller
public class OaiPmhIdentifyController extends OaiPmhController {
    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET, params="verb=Identify")
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);

        // Return badArgument error code if request includes illegal arguments.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        HashSet<String> legalArguments = new HashSet<String>(Arrays.asList("verb"));
        if (!legalArguments.containsAll(request.getParameterMap().keySet())) {
            throw new OaiPmhException("badArgument", "Request includes illegal arguments.");
        }

        return new OaiPmhIdentifyView(
            configuration.getBaseUrl() + "/oai-pmh",
            configuration.getOaiPmhConfiguration().getOaiPmhServiceTitle(),
            configuration.getOaiPmhConfiguration().getOaiPmhServiceAdminEmail()
        );
    }
}
