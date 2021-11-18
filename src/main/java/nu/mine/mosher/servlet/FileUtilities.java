package nu.mine.mosher.servlet;

import lombok.*;

import java.nio.file.*;
import java.util.Collection;
import java.util.regex.Pattern;

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

    private static final Pattern INF = Pattern.compile("(?i)^/[^/]*-INF(/.*|)$");

    public static boolean isInternalInformation(@NonNull final String pathResource) {
        return INF.matcher(pathResource).matches();
    }
}
