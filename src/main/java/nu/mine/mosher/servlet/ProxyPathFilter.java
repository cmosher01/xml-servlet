package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.*;

import static nu.mine.mosher.servlet.FileUtilities.segs;

@Slf4j
public class ProxyPathFilter extends HttpFilter {
    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");

        val prefix = buildPrefixPath(request);

        log.info("Setting attribute nu.mine.mosher.xml.pathPrefix: {}", prefix);
        request.setAttribute("nu.mine.mosher.xml.pathPrefix", prefix);

        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");
        log.trace("filter exit");
    }



    // Builds prefix (path to servlet root). For example:
    // "/forwarded1/forwarded2/servlet1/servlet2"
    // TODO check what happens when both are empty, do we get "/" or "", and does it work?
    private static String buildPrefixPath(@NonNull final HttpServletRequest request) {
        final Stream<String> s1;
        val forwarded = Optional.ofNullable(request.getHeader("x-forwarded-prefix"));
        if (forwarded.isPresent()) {
            log.info("Detected x-forwarded-prefix header: {}", forwarded.get());
            s1 = segs(forwarded.get());
        } else {
            s1 = Stream.empty();
        }

        final Stream<String> s2;
        val servlet = Optional.ofNullable(request.getServletPath());
        if (servlet.isPresent()) {
            log.info("Detected servlet path: {}", servlet.get());
            s2 = segs(servlet.get());
        } else {
            s2 = Stream.empty();
        }

        return Stream.concat(s1, s2).collect(Collectors.joining(FileUtilities.SLASH, FileUtilities.SLASH, ""));
    }
}
