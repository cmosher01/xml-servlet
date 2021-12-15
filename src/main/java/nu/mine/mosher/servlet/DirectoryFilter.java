package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DirectoryFilter extends HttpFilter {
    private String attrOut;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);
        this.attrOut = ServletUtilities.requireParam(config, "attrOut");
    }

    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);

        if (urlPath.invalid()) {
            log.warn("Detected invalid requested path: {}", urlPath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            log.trace("filter exit");
            return;
        }

        val dir = ServletUtilities.listDirectory(ctx, urlPath);

        if (dir.isPresent()) {
            log.warn("Building directory listing into DOM: resourcePaths(\"{}\") --> {}", urlPath, this.attrOut);
            request.setAttribute(this.attrOut, XmlUtilities.convertToXml(dir.get()));
        } else {
            log.trace("filter forward");
            super.doFilter(request, response, chain);
            log.trace("filter return");
        }
        log.trace("filter exit");
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrOut = null;
        super.destroy();
    }
}
