package grails.plugins.httplogger.filters;

import grails.plugins.httplogger.HttpLogger;
import grails.plugins.httplogger.MultiReadHttpServletRequest;
import grails.plugins.httplogger.MultiReadHttpServletResponse;
import grails.plugins.httplogger.RequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public abstract class HttpLoggerFilter extends GenericFilterBean {

    private RequestMatcher loggableRequestMatcher;
    private HttpLogger httpLogger;
    
    @Override
    protected void initFilterBean() throws ServletException {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        loggableRequestMatcher = context.getBean("loggableRequestMatcher", RequestMatcher.class);
        httpLogger = context.getBean("httpLogger", HttpLogger.class);
    }

    protected HttpLogger getHttpLogger() {
        return httpLogger;
    }

    @Override
    final public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest((HttpServletRequest) request);
        if (loggableRequestMatcher.matches(requestWrapper)) {
            MultiReadHttpServletResponse responseWrapper = new MultiReadHttpServletResponse((HttpServletResponse) response);
            logRequest(requestWrapper);
            chain.doFilter(requestWrapper, responseWrapper);
            logResponse(requestWrapper, responseWrapper);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

    protected void logRequest(MultiReadHttpServletRequest requestWrapper) throws IOException, ServletException {
    }

    protected void logResponse(MultiReadHttpServletRequest requestWrapper, MultiReadHttpServletResponse responseWrapper) throws IOException, ServletException {
    }
}
