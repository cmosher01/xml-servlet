package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SlashRedirectFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);
        var s = urlPath.toString();
        if (s.isBlank()) {
            s = FileUtilities.SLASH;
        }
        val optRes = Optional.ofNullable(ctx.getResource(s));

        if (optRes.isPresent() && optRes.get().toExternalForm().endsWith(FileUtilities.SLASH) && !urlPath.slashTrailing()) {
            val pre = new UrlPath(Optional.ofNullable(request.getAttribute("nu.mine.mosher.xml.pathPrefix")).orElse("").toString());

            val seg = urlPath.isRoot() ? pre.segmentLeaf() : urlPath.segmentLeaf();

            val redir = seg+FileUtilities.SLASH;
            log.info("301 Moved Permanently / Location: {}", redir);

            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", redir);
        } else {
            log.trace("filter forward");
            super.doFilter(request, response, chain);
            log.trace("filter return");
        }
        log.trace("filter exit");
    }
}
