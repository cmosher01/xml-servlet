package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MediaType;

import java.net.*;
import java.util.*;
import java.util.stream.*;

@Slf4j
public final class ServletUtilities {
    @NonNull
    public static UrlPath pathInfo(@NonNull final HttpServletRequest request) {
        val safePathInfo = Optional.ofNullable(request.getPathInfo()).orElse("");
        return new UrlPath(safePathInfo);
    }

    @NonNull
    public static Optional<List<DirectoryEntry>> listDirectory(@NonNull final ServletContext ctx, @NonNull final UrlPath urlPath) {
        var dir = Optional.<List<DirectoryEntry>>empty();

        val u = ServletUtilities.getResource(ctx, urlPath.toString());
        if (u.isEmpty()) {
            log.warn("While trying to list directory, could not find resource: {}", urlPath);
        }

        if (u.isPresent() && urlPath.slashTrailing()) {
            var rs = ServletUtilities.getResourcePaths(ctx, urlPath.toString());
            if (rs.isEmpty()) {
                rs = Optional.of(Set.of());
            }

            dir = Optional.of(buildDir(rs.get(), urlPath));
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
    private static List<DirectoryEntry> buildDir(@NonNull final Collection<String> paths, @NonNull final UrlPath urlPath) {
        val entries =
            paths.stream()
                .map(UrlPath::new)
                .filter(p -> !p.invalid())
                .map(e -> format(e, urlPath))
                .map(DirectoryEntry::create)
                .sorted();

        return Stream.concat(Stream.of(DirectoryEntry.up()), entries).toList();
    }

    @NonNull
    private static String format(@NonNull final UrlPath entry, @NonNull final UrlPath urlPath) {
        val prefix = urlPath.toString();
        val absolute = entry.toString();

        String ret= absolute;
        if (absolute.startsWith(prefix)) {
            ret = absolute.substring(prefix.length());
        }

        return ret;
    }

    @SneakyThrows
    public static Optional<URL> getResource(final ServletContext ctx, final String path) {
        val ctxOverride = Optional.ofNullable(ctx.getContext("/srv"));
        if (ctxOverride.isPresent()) {
            val override = Optional.ofNullable(ctxOverride.get().getResource(path));
            if (override.isPresent()) {
                log.info("Found overriding resource: {}", override.get().toExternalForm());
                return override;
            }
        }

        log.info("Could not find override for resource: {}", path);
        return Optional.ofNullable(ctx.getResource(path));
    }

    public static Optional<Set<String>> getResourcePaths(final ServletContext ctx, final String path) {
        val ctxOverride = Optional.ofNullable(ctx.getContext("/srv"));
        if (ctxOverride.isPresent()) {
            val override = Optional.ofNullable(ctxOverride.get().getResourcePaths(path));
            if (override.isPresent()) {
                log.info("Found overriding directory of: {}", path);
                return override;
            }
        }

        log.info("Could not find override for directory resource: {}", path);
        return Optional.ofNullable(ctx.getResourcePaths(path));
    }
}
