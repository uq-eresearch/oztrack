package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.util.OaiPmhException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

// Implements OAI-PMH Protocol Requests and Responses
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ProtocolMessages
@Controller
public class OaiPmhIllegalVerbController extends OaiPmhController {
    @RequestMapping(value="/oai-pmh", method=RequestMethod.GET, produces="text/xml", params={
        "verb",
        "verb!=Identify",
        "verb!=ListMetadataFormats",
        "verb!=ListSets",
        "verb!=ListIdentifiers",
        "verb!=ListRecords",
        "verb!=GetRecord"
    })
    public View handleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        super.preHandleRequest(request, response);
        throw new OaiPmhException("badVerb", "verb argument is not a legal OAI-PMH verb.");
    }
}
