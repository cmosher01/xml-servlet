package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.apache.tika.mime.MediaType;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class ServletUtilities {
    @NonNull
    public static UrlPath pathInfo(@NonNull final HttpServletRequest request) {
        val safePathInfo = Optional.ofNullable(request.getPathInfo()).orElse("");
        return new UrlPath(safePathInfo);
    }

    @NonNull
    public static Optional<List<DirectoryEntry>> listDirectory(@NonNull final ServletContext ctx, @NonNull final UrlPath urlPath) throws MalformedURLException, URISyntaxException {
        var dir = Optional.<List<DirectoryEntry>>empty();

        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));
        if (u.isPresent() && urlPath.slashTrailing()) {
            var rs = Optional.ofNullable(ctx.getResourcePaths(urlPath.toString()));
            if (rs.isEmpty()) {
                rs = Optional.of(Set.of());
            }

            dir = Optional.of(buildDir(rs.get(), urlPath, ctx));
        }

        return dir;
    }

    @NonNull
    public static String requireParam(@NonNull final FilterConfig config, @NonNull final String paramName) {
        val s = Optional.ofNullable(config.getInitParameter(paramName)).orElse("");
        if (s.isBlank()) {
            throw new IllegalArgumentException("Missing init-param: "+paramName);
        }
        return s;
    }

    public static boolean isXmlContentType(@NonNull final MediaType contentType) {
        val t = contentType.getSubtype();
        return
            t.equals("xml") ||
            t.endsWith("+xml");
    }



    @NonNull
    private static List<DirectoryEntry> buildDir(@NonNull final Collection<String> paths, @NonNull final UrlPath urlPath, @NonNull final ServletContext ctx) {
        val entries =
            paths
            .stream()
            .map(UrlPath::new)
            .filter(p -> !p.invalid())
            .map(e -> format(e, urlPath, ctx))
            .map(DirectoryEntry::create)
            .sorted();

        return Stream.concat(Stream.of(DirectoryEntry.up()), entries).toList();
    }

    @NonNull
    private static String format(@NonNull final UrlPath entry, @NonNull final UrlPath urlPath, @NonNull final ServletContext ctx) {
        val prefix = urlPath.toString();
        val absolute = entry.toString();

        String ret= absolute;
        if (absolute.startsWith(prefix)) {
            ret = absolute.substring(prefix.length());
        }
        ctx.log("listDirectory format: "+prefix+" + "+absolute+" --->> "+ret);

        return ret;
    }
}
