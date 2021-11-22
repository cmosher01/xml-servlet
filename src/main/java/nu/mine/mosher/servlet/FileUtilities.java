package nu.mine.mosher.servlet;

import lombok.*;

import java.nio.file.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class FileUtilities {
    public static final Path HOME = getHome();

    @SneakyThrows
    private static Path getHome() {
        return Paths.get(System.getProperty("user.home", "/srv")).toAbsolutePath().normalize().toRealPath();
    }

    public static final String SLASH = "/";

    private static final Pattern SAFE = Pattern.compile("^[a-zA-Z0-9][-._a-zA-Z0-9]{0,254}$");

    public static boolean invalidName(@NonNull final String s) {
        if (s.startsWith(".") || s.endsWith(".")) {
            return true;
        }

        if (!SAFE.matcher(s).matches()) {
            return true;
        }

        return false;
    }

    @NonNull
    public static String buildPath(@NonNull final Collection<String> segs, final boolean trailingSlash) {
        // Handle the only special case, where we have no segments.
        // Always return "/", regardless of whether they want a trailing slash or not.
        if (segs.isEmpty()) {
            return SLASH;
        }

        // build path, with a leading slash, and without a trailing slash
        var path = SLASH + String.join(SLASH, segs);

        // conditionally add a trailing slash
        if (trailingSlash) {
            path += SLASH;
        }

        return path;
    }

    private static final Pattern INF = Pattern.compile("(?i)^[^/]+-INF$");

    public static boolean isInternalInformation(@NonNull final String pathResource) {
        return segs(pathResource).anyMatch(s -> INF.matcher(s).matches());
    }

    public static Stream<String> segs(@NonNull final String path) {
        return Arrays.stream(path.split(SLASH, -1)).filter(s -> !s.isBlank());
    }

    @NonNull
    public static String getLeafSegment(@NonNull final String path) {
        val segs = segs(path).toList();
        if (segs.size() == 0) {
            // TODO what to do here?
            return "";
        }
        return segs.get(segs.size()-1);
    }

    @NonNull
    public static String forceRelative(@NonNull String path) {
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }
}
