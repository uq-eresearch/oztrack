package org.oztrack.controller;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.poi.util.IOUtils;
import org.oztrack.util.HttpClientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProxyController {
    @RequestMapping(value="/proxy/portal.tern.org.au/ternapi/search", method=RequestMethod.GET)
    public void handleTernSearch(HttpServletRequest outerRequest, HttpServletResponse outerResponse) throws IOException {
        handle("http://portal.tern.org.au/ternapi/search", outerRequest, outerResponse);
    }

    @RequestMapping(value="/proxy/bie.ala.org.au/search", method=RequestMethod.GET)
    public void handleAlaSearch(HttpServletRequest outerRequest, HttpServletResponse outerResponse) throws IOException {
        handle("http://bie.ala.org.au/search.json", outerRequest, outerResponse);
    }

    @RequestMapping(value="/proxy/bie.ala.org.au/search/auto.json", method=RequestMethod.GET)
    public void handleAlaSearchAuto(HttpServletRequest outerRequest, HttpServletResponse outerResponse) throws IOException {
        handle("http://bie.ala.org.au/search/auto.json", outerRequest, outerResponse);
    }

    private void handle(String baseUrl, HttpServletRequest request, HttpServletResponse response) throws IOException, ClientProtocolException {
        DefaultHttpClient client = HttpClientUtils.createDefaultHttpClient();
        HttpGet innerRequest = new HttpGet(URI.create(baseUrl + ((request.getQueryString() != null) ? ("?" + request.getQueryString()) : "")));
        HttpResponse innerResponse = client.execute(innerRequest);
        response.setStatus(innerResponse.getStatusLine().getStatusCode());
        if (innerResponse.getEntity() != null) {
            IOUtils.copy(innerResponse.getEntity().getContent(), response.getOutputStream());
        }
    }
}
