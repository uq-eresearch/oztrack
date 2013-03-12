package org.oztrack.view;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.RedirectView;

// Override behaviour of default RedirectView resolver.
// In RedirectView, don't expose model attributes or path variables.
public class RedirectViewResolver extends AbstractCachingViewResolver {
    private String viewNamePrefix = "redirect:";

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        if (viewName.startsWith(viewNamePrefix)) {
            String viewNameSuffix = viewName.substring(viewNamePrefix.length());
            RedirectView redirectView = new RedirectView(viewNameSuffix, true);
            redirectView.setExposeModelAttributes(false);
            redirectView.setExposePathVariables(false);
            return redirectView;
        }
        return null;
    }
}