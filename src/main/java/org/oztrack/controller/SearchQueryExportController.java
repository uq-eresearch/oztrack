package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.SearchQueryXLSView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

@Controller
public class SearchQueryExportController {
    @Autowired
    private PositionFixDao positionFixDao;
    
    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(HttpSession session) {
        return (SearchQuery) session.getAttribute("searchQuery");
    }

    @RequestMapping(value="/export", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public View handleRequest(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, Model model) throws Exception {
        model.addAttribute(SearchQueryXLSView.SEARCH_QUERY_KEY, searchQuery);
        return new SearchQueryXLSView(positionFixDao);
    }
}
