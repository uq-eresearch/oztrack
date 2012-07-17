package org.oztrack.view;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 3/08/11
 * Time: 2:33 PM
 */
public class JavaViewResolver extends AbstractCachingViewResolver {

    /** Logger for this class and subclasses */
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
        return view;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    public void setViewPrefix(String viewPrefix) {
        this.viewPrefix = viewPrefix;
    }

}
