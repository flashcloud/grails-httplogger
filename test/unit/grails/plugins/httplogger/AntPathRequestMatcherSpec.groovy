package grails.plugins.httplogger
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Specification

/**
 * @author Artur Gajowy <artur.gajowy@gmail.com>
 */
class AntPathRequestMatcherSpec extends Specification {
    
    def 'should match all paths when neither includes nor excludes are defined'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: [], excludeUrls: [])
        
        expect:
        matcher.matches(requestTo(anyPath))
        
        where:
        anyPath << ['/', '/foo', '/foo/bar.png']
    }

    def 'should match a path iff it is not excluded when there are no includes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: [], excludeUrls: ['/foo', '/**/bar.png'])

        expect:
        matcher.matches(requestTo('/'))
        matcher.matches(requestTo('/baz'))
        !matcher.matches(requestTo('/foo'))
        !matcher.matches(requestTo('/le/bar.png'))
    }
    
    def 'should match a path iff it is included when there are no excludes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: ['/foo', '/**/bar.png'], excludeUrls: [])

        expect:
        !matcher.matches(requestTo('/'))
        !matcher.matches(requestTo('/baz'))
        matcher.matches(requestTo('/foo'))
        matcher.matches(requestTo('/le/bar.png'))
    }
    
    def 'should match a path that matches both includes and excludes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: ['/foo/**/bar/baz*.png'], excludeUrls: ['/foo/**/bar/*.png'])

        expect:
        matcher.matches(requestTo('/foo/bar/baz.png'))
    }
    
    def 'should not match a path that matches nether includes nor excludes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: ['/foo/**/bar/baz*.png'], excludeUrls: ['/foo/**/bar/*.png'])

        expect:
        !matcher.matches(requestTo('/neither'))
    }
    
    def 'should not match a path that matches only excludes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: ['/foo/**/bar/*.png'], excludeUrls: ['/excluded'])

        expect:
        !matcher.matches(requestTo('/excluded'))
    }
    
    def 'should match a path that matches only includes'() {
        given:
        def matcher = new AntPathRequestMatcher(includeUrls: ['/foo/**/bar/*.png'], excludeUrls: ['/excluded'])

        expect:
        matcher.matches(requestTo('/foo/bar/baz.png'))
    }

    private MultiReadHttpServletRequest requestTo(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setServletPath(path)
        return new MultiReadHttpServletRequest(request)
    }
}

