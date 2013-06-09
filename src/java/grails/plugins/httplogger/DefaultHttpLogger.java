package grails.plugins.httplogger;

import grails.plugins.httplogger.filters.RequestData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public class DefaultHttpLogger implements HttpLogger {

    protected final Log logger = LogFactory.getLog(getClass());

    private String[] headersToLog;
    
    @Override
    public void logBeforeRequest(MultiReadHttpServletRequest requestWrapper, RequestData requestData) throws IOException {
        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = requestData.getRequestNumber();
        String method = requestWrapper.getMethod();
        String urlWithQueryString = requestData.getUrlWithQueryString();
        String headers = requestData.getHeadersAsString(headersToLog);

        logger.info("<< #" + requestNumber + ' ' + method + ' ' + urlWithQueryString);
        logger.info("<< #" + requestNumber + ' ' + "headers " + headers);
        if ("POST".equalsIgnoreCase(method)) {
            logger.info("<< #" + requestNumber + ' ' + "body: '" + requestWrapper.getCopiedInput() + "'");
        }
    }

    @Override
    public void logBeforeForwardOrError(MultiReadHttpServletRequest requestWrapper, RequestData requestData) {
        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = requestData.getRequestNumber();
        String controllerName = requestData.getController();
        String actionName = requestData.getAction();
        String parameterMapAsString = requestData.getParameterMapAsString();

        logger.info("<< #" + requestNumber + " dispatched to " + controllerName + '/' + actionName + " with parsed params " + parameterMapAsString + ".");
    }

    @Override
    public void logAfterResponse(MultiReadHttpServletRequest requestWrapper, MultiReadHttpServletResponse responseWrapper, RequestData requestData) throws IOException {
        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = requestData.getRequestNumber();
        Long elapsedTime = requestData.getElapsedTimeMillis();

        logger.info(">> #" + requestNumber + " returned " + responseWrapper.getStatus() + ", took " + elapsedTime + " ms.");
        logger.info(">> #" + requestNumber + " responded with '" + responseWrapper.getCopiedOutput() + "'");
    }

    public void setHeadersToLog(String headersToLog) {
        this.headersToLog = StringUtils.tokenizeToStringArray(headersToLog, ",");
    }
}
