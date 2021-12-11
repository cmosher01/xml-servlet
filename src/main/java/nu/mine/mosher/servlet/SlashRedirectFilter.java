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
        var s = urlPath.toString();
        if (s.isBlank()) {
            s = FileUtilities.SLASH;
        }
        val optRes = Optional.ofNullable(ctx.getResource(s));

        if (optRes.isPresent() && optRes.get().toExternalForm().endsWith(FileUtilities.SLASH) && !urlPath.slashTrailing()) {
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
