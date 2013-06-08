package grails.plugins.httplogger;

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
public interface RequestMatcher {
    boolean matches(MultiReadHttpServletRequest requestWrapper);
}
