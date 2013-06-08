/**
 * Copyright 2013 TouK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.httplogger.filters;
import grails.plugins.httplogger.HttpLogger;
import grails.plugins.httplogger.MultiReadHttpServletRequest;
import grails.plugins.httplogger.MultiReadHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
public class LogOutputResponseFilter extends HttpLoggerFilter {

    @Override
    protected void logResponse(MultiReadHttpServletRequest requestWrapper, MultiReadHttpServletResponse responseWrapper) throws IOException, ServletException {
        responseWrapper.flushBuffer();

        if (!logger.isInfoEnabled()) {
            return;
        }

        Long requestNumber = (Long) requestWrapper.getAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE);
        Long startTime = (Long) requestWrapper.getAttribute(HttpLogger.START_TIME_ATTRIBUTE);
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info(">> #" + requestNumber + " returned " + responseWrapper.getStatus() + ", took " + elapsedTime + " ms.");
        logger.info(">> #" + requestNumber + " responded with '" + responseWrapper.getCopiedOutput() + "'");
    }
}
