package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

@Slf4j
public class LogInfoFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final ServletRequest request, @NonNull final ServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        val ctx = Objects.requireNonNull(request.getServletContext());

        log.info("-------- servlet context information --------");
        log.info("servletContextName: {}", s(ctx.getServletContextName()));
        log.info("effectiveVersion: {}.{}", s(ctx.getEffectiveMajorVersion()), s(ctx.getEffectiveMinorVersion()));
        log.info("version: {}.{}", s(ctx.getMajorVersion()), s(ctx.getMinorVersion()));
        log.info("requestCharacterEncoding: {}", s(ctx.getRequestCharacterEncoding()));
        log.info("responseCharacterEncoding: {}", s(ctx.getResponseCharacterEncoding()));
        log.info("serverInfo: {}", s(ctx.getServerInfo()));
        log.info("contextPath: {}", s(ctx.getContextPath()));
        log.info("resource(\"/\"): {}", s(ctx.getResource("/").toExternalForm()));
        log.info("realPath(\"/\"): {}", s(ctx.getRealPath("/")));

        val attributeNames = ctx.getAttributeNames();
        if (Objects.isNull(attributeNames)) {
            log.info("attributeNames: <<NULL>>");
        } else {
            val r = Collections.list(attributeNames);
            if (r.isEmpty()) {
                log.info("attributeNames: <<NONE>>");
            } else {
                r.forEach(n -> log.info("attribute: {} = {}", s(n), s(ctx.getAttribute(n))));
            }
        }

        val initParameters = ctx.getInitParameterNames();
        if (Objects.isNull(initParameters)) {
            log.info("initParameters: <<NULL>>");
        } else {
            val r = Collections.list(initParameters);
            if (r.isEmpty()) {
                log.info("initParameters: <<NONE>>");
            } else {
                r.forEach(n -> log.info("initParameter: {} = {}", s(n), s(ctx.getInitParameter(n))));
            }
        }

        if (request instanceof HttpServletRequest hreq) {
            log.info("-------- HTTP request information --------");
            logRequestInfo(hreq);
        }

        log.info("-------- end of information report --------");

        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");
        log.trace("filter exit");
    }

    private static void logRequestInfo(@NonNull final HttpServletRequest request) throws MalformedURLException {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val e = request.getHeaderNames();
        if (Objects.isNull(e)) {
            log.info("headerNames: <<NULL>>");
        } else {
            val r = Collections.list(e);
            if (r.isEmpty()) {
                log.info("headerNames: <<NONE>>");
            } else {
                r.forEach(n -> log.info("header: {} = {}", s(n), s(request.getHeader(n))));
            }
        }

        log.info("requestURL: {}", s(request.getRequestURL()));

        log.info("scheme: {}", s(request.getScheme()));
        log.info("serverName: {}", s(request.getServerName()));
        log.info("serverPort: {}", s(request.getServerPort()));
        log.info("requestURI: {}", s(request.getRequestURI()));

        log.info("contextPath: {}", s(request.getContextPath()));
        log.info("servletPath: {}", s(request.getServletPath()));

        val path = request.getPathInfo();
        log.info("pathInfo: {}", s(path));
        if (Objects.nonNull(path)) {
            log.info("resource: {}", s(ctx.getResource(path)));
            val optPaths = Optional.ofNullable(ctx.getResourcePaths(path));
            if (optPaths.isPresent()) {
                log.info("resourcePaths: (size: {})", optPaths.get().size());
            } else {
                log.info("resourcePaths: <<NULL>>");
            }
        }

        log.info("pathTranslated: {}", s(request.getPathTranslated()));

        log.info("remoteHost: {}", s(request.getRemoteHost()));
        log.info("remoteAddr: {}", s(request.getRemoteAddr()));
        log.info("remotePort: {}", s(request.getRemotePort()));
        log.info("remoteUser: {}", s(request.getRemoteUser()));
        log.info("localName: {}", s(request.getLocalName()));
        log.info("localAddr: {}", s(request.getLocalAddr()));
        log.info("localPort: {}", s(request.getLocalPort()));

        log.info("contentType: {}", s(request.getContentType()));
        log.info("encoding: {}", s(request.getCharacterEncoding()));

        log.info("queryString: {}", s(request.getQueryString()));

        val parameterMap = request.getParameterMap();
        if (Objects.isNull(parameterMap)) {
            log.info("query parameterMap: <<NULL>>");
        } else if (parameterMap.isEmpty()) {
            log.info("query parameterMap: <<NONE>>");
        } else {
            parameterMap.entrySet().forEach(LogInfoFilter::logQueryParams);
        }

        val cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            log.info("cookies: <<NULL>>");
        } else if (cookies.length <= 0) {
            log.info("cookies: <<NONE>>");
        } else {
            Arrays.stream(cookies).forEach(c -> log.info("cookie: {} / {} = {}", s(c.getPath()), s(c.getName()), s(c.getValue())));
        }

        val attributeNames = request.getAttributeNames();
        if (Objects.isNull(attributeNames)) {
            log.info("attributeNames: <<NULL>>");
        } else {
            val r = Collections.list(attributeNames);
            if (r.isEmpty()) {
                log.info("attributeNames: <<NONE>>");
            } else {
                r.forEach(n -> log.info("attribute: {} = {}", s(n), s(request.getAttribute(n))));
            }
        }
    }

    private static void logQueryParams(final Map.Entry<String, String[]> entry) {
        if (Objects.isNull(entry)) {
            log.info("query parameter: <<NULL>>");
            return;
        }

        val name = entry.getKey();
        val values = entry.getValue();
        if (Objects.isNull(values)) {
            log.info("query parameter: {} = {}", s(name), "<<NULL>>");
        } else if (values.length <= 0) {
            log.info("query parameter: {} = {}", s(name), "<<NONE>>");
        } else {
            Arrays.stream(values).forEach(value -> log.info("query parameter: {} = {}", s(name), s(value)));
        }
    }

    @NonNull
    private static String s(final Object anyObjectOrNull) {
        if (Objects.isNull(anyObjectOrNull)) {
            return "<<NULL>>";
        }

        var s = anyObjectOrNull.toString();

        if (s.isEmpty()) {
            return "<<EMPTY>>";
        }
        if (s.isBlank()) {
            return String.format("<<WS:%d>>", s.length());
        }

        return s;
    }
}
