package grails.plugins.httplogger;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public class AntPathRequestMatcher implements RequestMatcher {

    private List<String> includeUrls;
    private List<String> excludeUrls;

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean matches(MultiReadHttpServletRequest requestWrapper) {
        String path = requestWrapper.getServletPath();
        if (includeUrls.isEmpty()) {
            return !matchesAny(excludeUrls, path);
        } else {
            return matchesAny(includeUrls, path);
        }
    }

    private boolean matchesAny(List<String> excludeUrls, String path) {
        for (String excludeUrl : excludeUrls) {
            if (pathMatcher.match(excludeUrl, path)) {
                return true;
            }
        }
        return false;
    }

    public void setIncludeUrls(List<String> includeUrls) {
        this.includeUrls = includeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }
}
