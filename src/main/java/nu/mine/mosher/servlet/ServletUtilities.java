package nu.mine.mosher.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public final class ServletUtilities {
    @NonNull
    public static UrlPath pathInfo(@NonNull final HttpServletRequest request) {
        val safePathInfo = Optional.ofNullable(request.getPathInfo()).orElse("");
        return new UrlPath(safePathInfo);
    }

    public static boolean isFile(@NonNull final ServletContext ctx, @NonNull final UrlPath urlPath) throws MalformedURLException, URISyntaxException {
        if (urlPath.isRoot()) {
            return false;
        }

        val r = Optional.ofNullable(ctx.getResource(urlPath.toString()));
        if (r.isEmpty()) {
            return false;
        }

        return Files.isRegularFile(Path.of(r.get().toURI()));
    }

    public static boolean isDirectory(@NonNull final ServletContext ctx, @NonNull final UrlPath urlPath) throws MalformedURLException, URISyntaxException {
        if (urlPath.isRoot()) {
            return true;
        }
        val r = Optional.ofNullable(ctx.getResource(urlPath.toString()));
        if (r.isEmpty()) {
            return false;
        }

        return Files.isDirectory(Path.of(r.get().toURI()));
    }

    @NonNull
    public static Optional<List<DirectoryEntry>> listDirectory(@NonNull final ServletContext ctx, @NonNull final UrlPath urlPath) throws MalformedURLException, URISyntaxException {
        var dir = Optional.<List<DirectoryEntry>>empty();

        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));
        if (u.isPresent()) {
            if (Files.isDirectory(Path.of(u.get().toURI()))) {
                var rs = Optional.ofNullable(ctx.getResourcePaths(urlPath.toString()));
                if (rs.isEmpty()) {
                    rs = Optional.of(Set.of());
                }

                dir = Optional.of(buildDir(rs.get(), urlPath));
            }
        }

        return dir;
    }

    private static List<DirectoryEntry> buildDir(@NonNull final Collection<String> paths, @NonNull final UrlPath urlPath) {
        val entries =
            paths
            .stream()
            .map(UrlPath::new)
            .filter(p -> !p.invalid())
            .map(e -> format(e, urlPath))
            .map(DirectoryEntry::create)
            .sorted();

        return Stream.concat(Stream.of(DirectoryEntry.up()), entries).toList();
    }

    @NonNull
    private static String format(@NonNull final UrlPath entry, @NonNull final UrlPath urlPath) {
        val pathRel = Path.of(urlPath.toString()).relativize(Path.of(entry.toString()));

        var ret = pathRel.toString();

        if (entry.slashTrailing()) {
            ret += FileUtilities.SLASH;
        }

        return ret;
    }
}
