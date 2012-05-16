package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.SearchQueryXLSView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

@Controller
public class SearchQueryExportController {
    @RequestMapping(value="/export", method=RequestMethod.GET)
    public View handleRequest(HttpSession session, Model model) throws Exception {
        SearchQuery searchQuery = (SearchQuery) session.getAttribute("searchQuery");
        model.addAttribute(SearchQueryXLSView.SEARCH_QUERY_KEY, searchQuery);
        return new SearchQueryXLSView();
    }
}
