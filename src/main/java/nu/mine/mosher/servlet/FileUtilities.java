package nu.mine.mosher.servlet;

import lombok.*;

import java.nio.file.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtilities {
//    public static final Path HOME = getHome();
//
//    @SneakyThrows
//    private static Path getHome() {
//        return Paths.get(System.getProperty("user.home", "/srv")).toAbsolutePath().normalize().toRealPath();
//    }

    public static final String SLASH = "/";

    private static final Pattern SAFE = Pattern.compile("^[a-zA-Z0-9][-._a-zA-Z0-9]{0,254}$");

    public static boolean invalidName(@NonNull final String pathSegment) {
        if (pathSegment.startsWith(".") || pathSegment.endsWith(".")) {
            return true;
        }

        if (!SAFE.matcher(pathSegment).matches()) {
            return true;
        }

        return false;
    }

    @NonNull
    public static String buildPath(@NonNull final Collection<String> segs, boolean slashLeading, final boolean slashTrailing) {
        if (segs.isEmpty()) {
            if (slashLeading || slashTrailing) {
                return SLASH;
            }
            return "";
        }

        return segs.stream().collect(Collectors.joining(SLASH, slashLeading ? SLASH : "", slashTrailing ? SLASH : ""));
    }

    private static final Pattern INF = Pattern.compile("(?i)^[^/]+-INF$");

//    public static boolean hidden(@NonNull final String path) {
//        return segs(path.toString()).anyMatch(FileUtilities::hidden);
//    }

    public static boolean hidden(@NonNull final String pathSegment) {
        if (INF.matcher(pathSegment).matches()) {
            // entry is a *-INF directory, so hide it
            return true;
        }

        if (pathSegment.startsWith(".")) {
            // hidden file (starts with a period)
            return true;
        }

        return false;
    }


    public static Stream<String> segs(@NonNull final String path) {
        return Arrays.stream(path.split(SLASH, -1)).map(String::trim).filter(s -> !s.isBlank());
    }

//    @NonNull
//    public static String forceRelative(@NonNull String path) {
//        while (path.startsWith("/")) {
//            path = path.substring(1);
//        }
//        return path;
//    }
}
