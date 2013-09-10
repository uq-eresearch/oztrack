package org.oztrack.controller;

import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.util.OaiPmhException;
import org.oztrack.view.OaiPmhErrorView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

// Implements the Open Archives Initiative Protocol for Metadata Harvesting (OAI-PMH)
// Protocol Version 2.0 of 2002-06-14; Document Version 2008-12-07T20:42:00Z
// http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm
@Controller
public abstract class OaiPmhController {
    @Autowired
    protected OzTrackConfiguration configuration;

    @ExceptionHandler(OaiPmhException.class)
    public View handleIOException(OaiPmhException e) {
        return new OaiPmhErrorView(e.getCode(), e.getMessage());
    }

    public void preHandleRequest(HttpServletRequest request, HttpServletResponse response) throws OaiPmhException {
        if (!configuration.isOaiPmhEnabled()) {
            throw new RuntimeException("OAI-PMH not enabled");
        }

        // Return badVerb error code if verb argument is repeated.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        String[] verbArguments = request.getParameterValues("verb");
        if ((verbArguments != null) && (verbArguments.length > 1)) {
            throw new OaiPmhException("badVerb", "verb argument is repeated.");
        }

        // Return badArgument error code if request includes a repeated argument.
        // http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#ErrorConditions
        for (Entry<String, String[]> parameterEntry : request.getParameterMap().entrySet()) {
            if (parameterEntry.getValue().length > 1) {
                throw new OaiPmhException("badArgument", parameterEntry.getKey() + " argument is repeated.");
            }
        }
    }
}
