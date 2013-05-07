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
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Tomasz Kalkosiński <tomasz.kalkosinski@gmail.com>
 */
public class LogGrailsUrlsInfoFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Long requestNumber = (Long) servletRequest.getAttribute(HttpLogger.REQUEST_NUMBER_ATTRIBUTE);
        // this filter has urlPattern '/*' so I need to determine if this request is marked with requestNumber
        if (requestNumber != null) {
            String controllerName = (String) servletRequest.getAttribute(GrailsApplicationAttributes.CONTROLLER_NAME_ATTRIBUTE);
            String actionName = (String) servletRequest.getAttribute(GrailsApplicationAttributes.ACTION_NAME_ATTRIBUTE);
            Map<String, String[]> params = servletRequest.getParameterMap();
            String paramsString = "";

            if (params.size() > 0) {
                String delimiter = "";
                StringBuilder values = new StringBuilder();
                for(Map.Entry<String, String[]> entry : params.entrySet()) {
                    values.append(delimiter).append('\'').append(entry.getKey()).append("':'").append(ArrayUtils.toString(entry.getValue())).append('\'');
                    delimiter = ", ";
                }
                paramsString = values.toString();
            }

            if (logger.isInfoEnabled()) {
                logger.info("<< #" + requestNumber + " dispatched to " + controllerName + '/' + actionName + " with parsed params [" + paramsString + "].");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
