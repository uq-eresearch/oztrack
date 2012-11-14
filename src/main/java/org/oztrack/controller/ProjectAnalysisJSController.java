package org.oztrack.controller;

import org.oztrack.data.model.types.AnalysisType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectAnalysisJSController {
    @RequestMapping(value="/js/project-analysis.js", method=RequestMethod.GET, produces="text/javascript")
    @PreAuthorize("permitAll")
    public String getView(Model model) {
        model.addAttribute("analysisTypeList", AnalysisType.values());
        return "project-analysis.js";
    }
}