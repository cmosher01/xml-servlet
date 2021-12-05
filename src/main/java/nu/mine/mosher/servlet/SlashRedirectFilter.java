package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SlashRedirectFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val urlPath = ServletUtilities.pathInfo(request);
        ctx.log("SlashRedirectFilter: "+urlPath);

        if (ServletUtilities.isDirectory(ctx, urlPath) && !urlPath.slashTrailing()) {
//                final var redir = (roundtrip.equals("/") ? FileUtilities.getLeafSegment(request.getServletPath()) : FileUtilities.getLeafSegment(roundtrip)) + FileUtilities.SLASH;
//                ctx.log("Redirecting to: "+redir);
//                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
//                response.setHeader("Location", redir);

            val pre = new UrlPath(Optional.ofNullable(request.getAttribute("nu.mine.mosher.xml.pathPrefix")).orElse("").toString());

            val seg = urlPath.isRoot() ? pre.segmentLeaf() : urlPath.segmentLeaf();

            val redir = seg+FileUtilities.SLASH;
            ctx.log("Redirecting to: "+redir);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", redir);
//        } else if () {
        } else {


            super.doFilter(request, response, chain);
        }
    }
}
