package org.oztrack.view;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

public class JavaViewResolver extends AbstractCachingViewResolver {
    private String viewNamePrefix;

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        if (viewName.startsWith(this.viewNamePrefix)) {
            String viewNameSuffix = viewName.substring(this.viewNamePrefix.length());
            String viewClassName = viewNameSuffix.substring(0,1).toUpperCase() + viewNameSuffix.substring(1);
            Class<?> viewClass = Class.forName("org.oztrack.view." + viewClassName + "View");
            View view = (View) viewClass.getConstructor().newInstance();
            return view;
        }
        return null;
    }

    public String getViewNamePrefix() {
        return viewNamePrefix;
    }

    public void setViewNamePrefix(String viewNamePrefix) {
        this.viewNamePrefix = viewNamePrefix;
    }
}
