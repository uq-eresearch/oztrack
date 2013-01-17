package org.oztrack.view;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

public class JavaViewResolver extends AbstractCachingViewResolver {
    protected final Log logger = LogFactory.getLog(getClass());

    private String viewPrefix;

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        logger.debug("viewName : " + viewName);
        View view = null;
        if (viewName.startsWith(this.viewPrefix)) {
            String suffix = viewName.substring(this.viewPrefix.length());
            String viewClassName = suffix.substring(0,1).toUpperCase() + suffix.substring(1);
            Class<?> viewClass = Class.forName("org.oztrack.view." + viewClassName + "View");
            view = (View) viewClass.getConstructor().newInstance();
        }
        return view;
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    public void setViewPrefix(String viewPrefix) {
        this.viewPrefix = viewPrefix;
    }
}
