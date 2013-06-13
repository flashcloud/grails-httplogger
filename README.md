# grails-httplogger

Grails plugin for logging HTTP traffic.

It logs:

* request information (url, headers, cookies, method, body),
* grails dispatch information (controller, action, parameters),
* response information (elapsed time and body).
* **whatever else you want!** (see Configuration section)

It is mostly useful for logging your REST traffic. Full HTTP web pages can be huge to log and generally waste your space.

## Installation

Add the following to your `BuildConfig.groovy`

```
runtime ":httplogger:1.0"
```

And be sure to enable logging for `grails.plugins.httlogger` at INFO level in `Config.groovy`:

```
info 'grails.plugins.httplogger'
```

## Profit!

Without any further configuration, all reqeusts are logged. Sample output below:

```
17:16:00,331 INFO  filters.LogRawRequestInfoFilter  - << #1 GET http://localhost:8080/riddle/rest/index?username=admin&search=foo
17:16:00,340 INFO  filters.LogRawRequestInfoFilter  - << #1 headers Cookie: 'JSESSIONID=DF4EA5725AC4A4990281BD96963739B0; splashShown1.6=1', Accept-Language: 'en-US,en;q=0.8,pl;q=0.6', X-MyHeader: 'null'
17:16:00,342 INFO  filters.LogGrailsUrlsInfoFilter  - << #1 dispatched to rest/index with parsed params [username:[admin], search:[foo]].
17:16:00,731 INFO  filters.LogOutputResponseFilter  - >> #1 returned 200, took 405 ms.
17:16:00,745 INFO  filters.LogOutputResponseFilter  - >> #1 responded with '{count:0}'
```

```
17:18:55,799 INFO  filters.LogRawRequestInfoFilter  - << #2 POST http://localhost:8080/riddle/rest/login
17:18:55,799 INFO  filters.LogRawRequestInfoFilter  - << #2 headers Cookie: 'JSESSIONID=DF4EA5725AC4A4990281BD96963739B0; splashShown1.6=1', Accept-Language: 'en-US,en;q=0.8,pl;q=0.6', X-MyHeader: 'null'
17:18:55,800 INFO  filters.LogRawRequestInfoFilter  - << #2 body: 'username=admin&password=password'
17:18:55,801 INFO  filters.LogOutputResponseFilter  - >> #2 returned 404, took 3 ms.
17:18:55,802 INFO  filters.LogOutputResponseFilter  - >> #2 responded with ''
```

Logging all the requests and responses is quite a lot of logs, so you might want to configure the plugin before running it on production :).


## Configuration

You can configure the plugin in your `Config.groovy` like this:

```groovy

grails.plugins.httplogger.enabled = false
//should you wish to disable it temporarily or based on environment

grails.plugins.httplogger.headers = 'Cookie, Accept-Language, X-MyHeader' 
//list of headers to log by the default HttpLogger implementation

grails.plugins.httplogger.includeUrls = ['/rest/**', '/**/*.interesting']
grails.plugins.httplogger.excludeUrls = ['/css/**', '/**/*.js']
// - lists of Ant-style patterns to be included/excluded by the default RequestMatcher implementation
//   (AntPatternRequestMatcher)
// - includes take precedence over excludes
// - if none of them are given - all requests are logged
```

You can even completely override how the requests are matched and what and how is logged, by providing your own implementations of two simple interfaces. See the following sections.

### Customising loggable requests matching

Just implement `grails.plugins.httplogger.RequestMatcher` interface and expose it as a bean named `loggableRequestMatcher`:

```groovy
//somewhere in resources.groovy
loggableRequestMatcher(MethodBasedRequestMatcher) {
    method = 'POST'
}
```

You might want your custom implementations to be written is Java, for performance and type-safety reasons (BTW: most of the plugin is Java):

```java
public class MethodBasedRequestMatcher implements RequestMatcher {
    
    String method;

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean matches(MultiReadHttpServletRequest requestWrapper) {
        return method.equalsIgnoreCase(requestWrapper.getMethod());
    }
}
```

But a Groovy class will do as well:

```groovy
class MethodBasedRequestMatcher implements RequestMatcher {

    String method

    @Override
    boolean matches(MultiReadHttpServletRequest requestWrapper) {
        method.equalsIgnoreCase(requestWrapper.method)
    }
}
```


### Customising log messages

Just implement the `grails.plugins.httplogger.HttpLogger` interface and expose it as a bean named `httpLogger`.

See the default implementation (`grails.plugins.httplogger.DefaultHttpLogger`) for details.

Notice how we log request and response body using our MultiRead wrappers' `getCopied[Input/Output]` methods (these were one of the 'tricky' parts of the plugin ;)).

Be sure to adjust your logging configuration in config groovy.


## Kudos

Many kudos to [Marek Maj](https://github.com/MarekMaj) for his `MultiReadHttpServletResponse` class.
