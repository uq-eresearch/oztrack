package org.oztrack.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 3/08/11
 * Time: 2:33 PM
 */
public class AjaxViewResolver extends AbstractCachingViewResolver {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    private String ajaxPrefix;
    private View ajaxView;

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        logger.debug("viewName : " + viewName);
        View view = null;
        if (viewName.startsWith(this.ajaxPrefix)) {
            view = ajaxView;
        }
        return view;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getAjaxPrefix() {
        return ajaxPrefix;
    }

    public void setAjaxPrefix(String ajaxPrefix) {
        this.ajaxPrefix = ajaxPrefix;
    }

    public View getAjaxView() {
        return ajaxView;
    }

    public void setAjaxView(View ajaxView) {
        this.ajaxView = ajaxView;
    }

}
