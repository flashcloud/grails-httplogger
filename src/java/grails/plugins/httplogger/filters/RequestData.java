package grails.plugins.httplogger.filters;

import grails.plugins.httplogger.HttpLogger;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public class RequestData {
    
    private final HttpServletRequest request;

    public RequestData(HttpServletRequest request) {
        this.request = request;
    }

    public Long getRequestNumber() {
        return (Long) request.getAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE);
    }
    
    void setRequestNumber(long requestNumber) {
        request.setAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE, requestNumber);
    }

    public Long getElapsedTimeMillis() {
        return getEndTimeMillis() - getStartTimeMillis();
    }

    public Long getStartTimeMillis() {
        return (Long) request.getAttribute(HttpLogger.START_TIME_ATTRIBUTE);
    }

    void setStartTimeMillis(long startTimeMillis) {
        request.setAttribute(HttpLogger.START_TIME_ATTRIBUTE, startTimeMillis);
    }

    public Long getEndTimeMillis() {
        return (Long) request.getAttribute(HttpLogger.END_TIME_ATTRIBUTE);
    }

    void setEndTimeMillis(long endTimeMillis) {
        request.setAttribute(HttpLogger.END_TIME_ATTRIBUTE, endTimeMillis);
    }

    public String getUrlWithQueryString() {
        return request.getRequestURL().toString() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }

    public String getHeadersAsString(String... headerNames) {
        StringBuilder headersBuilder = new StringBuilder();
        headersBuilder.append('[');
        String delimiter = "";
        for (String name : headerNames) {
            String headerValue = request.getHeader(name);
            headersBuilder.append(delimiter).append(name).append(": ").append(headerValue);
            delimiter = ", ";
        }
        headersBuilder.append(']');
        return headersBuilder.toString();
    }

    public String getController() {
        return (String) request.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE);
    }

    public String getAction() {
        return (String) request.getAttribute(GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE);
    }

    public String getParameterMapAsString() {
        Map<String, String[]> params = request.getParameterMap();

        StringBuilder parametersBuilder = new StringBuilder();
        parametersBuilder.append('[');
        String delimiter = "";
        for(Map.Entry<String, String[]> entry : params.entrySet()) {
            String parameterValues = ArrayUtils.toString(entry.getValue());
            parametersBuilder.append(delimiter).append('\'').append(entry.getKey()).append("':'").append(parameterValues).append('\'');
            delimiter = ", ";
        }
        parametersBuilder.append(']');
        return parametersBuilder.toString();
    }
}
