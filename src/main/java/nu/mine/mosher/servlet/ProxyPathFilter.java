package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nu.mine.mosher.servlet.FileUtilities.segs;

public class ProxyPathFilter extends HttpFilter {
    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val prefix = buildPrefixPath(request);

        ctx.log("Setting attribute nu.mine.mosher.xml.pathPrefix: "+prefix);
        request.setAttribute("nu.mine.mosher.xml.pathPrefix", prefix);

        super.doFilter(request, response, chain);
    }



    // Builds prefix (path to servlet root). For example:
    // "/forwarded1/forwarded2/servlet1/servlet2"
    // TODO check what happens when both are empty, do we get "/" or "", and does it work?
    private static String buildPrefixPath(@NonNull final HttpServletRequest request) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        final Stream<String> s1;
        val forwarded = Optional.ofNullable(request.getHeader("x-forwarded-prefix"));
        if (forwarded.isPresent()) {
            ctx.log("Detected x-forwarded-prefix header: "+forwarded.get());
            s1 = segs(forwarded.get());
        } else {
            s1 = Stream.empty();
        }

        final Stream<String> s2;
        val servlet = Optional.ofNullable(request.getServletPath());
        if (servlet.isPresent()) {
            ctx.log("Detected servlet path: "+servlet.get());
            s2 = segs(servlet.get());
        } else {
            s2 = Stream.empty();
        }

        return Stream.concat(s1, s2).collect(Collectors.joining(FileUtilities.SLASH, FileUtilities.SLASH, ""));
    }
}
