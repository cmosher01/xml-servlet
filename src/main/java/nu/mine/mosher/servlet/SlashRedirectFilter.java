package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.*;

import java.util.*;

public class SlashRedirectFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);

        if (ServletUtilities.isDirectory(ctx, urlPath) && !urlPath.slashTrailing()) {
            val pre = new UrlPath(Optional.ofNullable(request.getAttribute("nu.mine.mosher.xml.pathPrefix")).orElse("").toString());

            val seg = urlPath.isRoot() ? pre.segmentLeaf() : urlPath.segmentLeaf();

            val redir = seg+FileUtilities.SLASH;
            ctx.log("SlashRedirectFilter redirecting to: "+redir);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", redir);
        } else {
            super.doFilter(request, response, chain);
        }
    }
}
