package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.val;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class LogInfoFilter extends HttpFilter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        val ctx = req.getServletContext();
        ctx.log("=".repeat(64));



        val srv = ctx.getServerInfo();
        ctx.log("Server info: "+srv);

        val path = ctx.getContextPath();
        ctx.log("context path: "+path);

        val mimeHtml = ctx.getMimeType("index.html");
        ctx.log("Mime type of html: "+mimeHtml);

        val reses = ctx.getResourcePaths("/");
        reses.forEach(r -> ctx.log("Top level resource: "+r));

        val urlRoot = ctx.getResource("/");
        ctx.log("URL of root: "+urlRoot.toExternalForm());

        val pathRoot = ctx.getRealPath("/");
        ctx.log("real path of root: "+pathRoot);

        val encReq = ctx.getRequestCharacterEncoding();
        ctx.log("request encoding: "+encReq);
        val encRes = ctx.getResponseCharacterEncoding();
        ctx.log("response encoding: "+encRes);

        Collections.list(ctx.getAttributeNames()).forEach(
            a -> ctx.log("attr: "+a+" = "+ctx.getAttribute(a)));
        Collections.list(ctx.getInitParameterNames()).forEach(
            a -> ctx.log("init: "+a+" = "+ctx.getInitParameter(a)));



        if (req instanceof HttpServletRequest hreq) {
            ctx.log("-".repeat(32));
            logRequestInfo(hreq);
        }



        ctx.log("-".repeat(32));
        super.doFilter(req, res, chain);
        ctx.log("=".repeat(64));
    }

    private static void logRequestInfo(final HttpServletRequest request) throws MalformedURLException {
        val ctx = request.getServletContext();

        val e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            val header = e.nextElement();
            ctx.log("header: " + header +" = "+ request.getHeader(header));
        }

        val cookies = request.getCookies();
        if (Objects.isNull(cookies) || cookies.length <= 0) {
            ctx.log("The request had no cookies attached.");
        } else {
            Arrays.stream(cookies).forEach(
                c -> ctx.log("cookie: " + Optional.ofNullable(c.getPath()).orElse("[no-path]")+ " / " + c.getName() + " = " + c.getValue()));
        }

        ctx.log("requestURL: " + request.getRequestURL());

        ctx.log("scheme: " + request.getScheme());
        ctx.log("serverName: " + request.getServerName());
        ctx.log("serverPort: " + request.getServerPort());
        ctx.log("requestURI: " + request.getRequestURI());

        ctx.log("contextPath: " + request.getContextPath());
        ctx.log("servletPath: " + request.getServletPath());
        val path = request.getPathInfo();
        ctx.log("pathInfo: " + path);
        if (Objects.nonNull(path)) {
            val optRes = Optional.ofNullable(ctx.getResource(path));
            if (optRes.isPresent()) {
                ctx.log("resource URL is non-null: " + optRes.get().toExternalForm());
            } else {
                ctx.log("resource URL is null.");
            }

            val optPaths = Optional.ofNullable(ctx.getResourcePaths(path));
            if (optPaths.isPresent()) {
                ctx.log("set of resource paths is non-null, with size: " + optPaths.get().size());
            } else {
                ctx.log("set of resource paths is null.");
            }
        }

        ctx.log("queryString: " + request.getQueryString());

        ctx.log("pathTranslated: " + request.getPathTranslated());

        ctx.log("remoteHost: " + request.getRemoteHost());
        ctx.log("remoteAddr: " + request.getRemoteAddr());
        ctx.log("remotePort: " + request.getRemotePort());
        ctx.log("remoteUser: " + request.getRemoteUser());
        ctx.log("localName: " + request.getLocalName());
        ctx.log("localAddr: " + request.getLocalAddr());
        ctx.log("localPort: " + request.getLocalPort());

        ctx.log("contentType: " + request.getContentType());
        ctx.log("encoding: " + request.getCharacterEncoding());

        val parameters = request.getParameterMap();
        if (Objects.isNull(parameters) || parameters.isEmpty()) {
            ctx.log("The request had no query parameters.");
        } else {
            parameters.entrySet().forEach(p -> logQueryParams(ctx, p));
        }

        Collections.list(request.getAttributeNames()).forEach(
            a -> ctx.log("attr: "+a+" = "+request.getAttribute(a)));
    }

    private static void logQueryParams(ServletContext ctx, final Map.Entry<String, String[]> entry) {
        val name = entry.getKey();
        val values = entry.getValue();
        if (Objects.isNull(values)) {
            ctx.log("query parameter: " + name + " = <<NULL>>");
        } else if (values.length <= 0) {
            ctx.log("query parameter:" + name + " = <<EMPTY>>");
        } else {
            Arrays.stream(values).forEach(value -> ctx.log("query parameter: " + name + " = " + value));
        }
    }
}
