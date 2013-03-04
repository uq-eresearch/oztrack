package org.oztrack.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.pool.ObjectPool;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RServeSettingsController {
    @Autowired
    private ObjectPool<RConnection> rServeConnectionPool;

    @RequestMapping(value="/settings/rserve", method=RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String handleRequest(Model model) {
        model.addAttribute("rServeConnectionPool", rServeConnectionPool);
        return "rserve-settings-form";
    }

    @RequestMapping(value="/settings/rserve", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processSubmit(
        Model model,
        @RequestParam(value="force", defaultValue="false") Boolean force
    ) throws Exception {
        String signal = force ? "KILL" : "TERM";
        Process process = Runtime.getRuntime().exec("killall --verbose --signal " + signal + " --regexp Rserve");
        String err = IOUtils.toString(process.getErrorStream());
        String out = IOUtils.toString(process.getInputStream());
        process.waitFor();
        model.addAttribute("rServeConnectionPool", rServeConnectionPool);
        model.addAttribute("err", err);
        model.addAttribute("out", out);
        return "rserve-settings-form";
    }
}