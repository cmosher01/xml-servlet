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
        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);

        if (urlPath.invalid()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        val dir = ServletUtilities.listDirectory(ctx, urlPath);

        if (dir.isPresent()) {
            log.info("Building into DOM: directory --> {}", this.attrOut);
            request.setAttribute(this.attrOut, XmlUtilities.convertToXml(dir.get()));
        } else {
            super.doFilter(request, response, chain);
        }
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrOut = null;
        super.destroy();
    }
}
