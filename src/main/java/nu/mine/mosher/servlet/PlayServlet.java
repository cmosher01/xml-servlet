package nu.mine.mosher.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;

import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@WebServlet("/d1/d2/*")
public class PlayServlet extends HttpServlet {
    private static final boolean ALLOW_PUBLIC_DEBUG_FLAG = true;
    private static final Charset DEFAULT_RESPONSE_CHARSET = StandardCharsets.UTF_8;



    @Override
    @SneakyThrows
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        val ctx = req.getServletContext();

        val debug = ALLOW_PUBLIC_DEBUG_FLAG && Optional.ofNullable(req.getParameter("debug")).isPresent();

        val safePathInfo = Optional.ofNullable(req.getPathInfo()).orElse("");
        val segs = Arrays.stream(safePathInfo.split(FileUtilities.SLASH, -1)).filter(s -> !s.isBlank()).toList();
        val trailingSlash = safePathInfo.endsWith(FileUtilities.SLASH);
        val pathIsInvalid = segs.stream().anyMatch(FileUtilities::invalidName);


        val roundtrip = FileUtilities.buildPath(segs, trailingSlash);
        val roundtripWithoutTrailingSlash = FileUtilities.buildPath(segs, false);



        if (roundtrip.equals("/health")) {
            return; // OK
        }

        if (pathIsInvalid && !debug) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URI: invalid character");
            return;
        }



        var dir = Optional.<Set<String>>empty();
        var res = Optional.<URL>empty();
        {
            val u = Optional.ofNullable(ctx.getResource(roundtrip));
            if (u.isPresent()) {
                val p = Path.of(u.get().toURI());

                ctx.log("Resource is present. URL: "+u.get().toExternalForm());
                ctx.log("                     URI: "+u.get().toURI().toASCIIString());
                ctx.log("                    Path: "+p);
                ctx.log("                  exists: "+Files.exists(p));
                ctx.log("             isDirectory: "+Files.isDirectory(p));
                ctx.log("           isRegularFile: "+Files.isRegularFile(p));
                ctx.log("                    size: "+Files.size(p));
                ctx.log("        lastModifiedTime: "+Files.getLastModifiedTime(p));

                if (Files.isDirectory(p)) {
                    dir = Optional.ofNullable(ctx.getResourcePaths(roundtrip));
                    if (dir.isEmpty()) {
                        dir = Optional.of(Set.of());
                    }
                } else if (Files.isRegularFile(p)) {
                    res = Optional.ofNullable(ctx.getResource(roundtrip));
                }
            } else {
                ctx.log("Resource not found.");
            }
        }



        resp.setContentType("text/html");
        resp.setCharacterEncoding(DEFAULT_RESPONSE_CHARSET.name());

        val out = resp.getWriter();


        printDebugHeader(Optional.ofNullable(req.getPathInfo()).orElse("{NULL}"), out);

        out.println("-".repeat(64));

        out.println("path info (raw):");
        out.println("    "+Optional.ofNullable(req.getPathInfo()).orElse("{NULL}"));
        out.println("-".repeat(64));

        out.println("parsed "+segs.size()+" path segments: {good} or [invalid]:");
        segs.forEach(s -> out.println("    "+(FileUtilities.invalidName(s) ? "["+s+"]" : "{"+s+"}")));
        out.println("-".repeat(64));

        out.println("constructed path of webapp resource:");
        out.println("    "+roundtrip);
        out.println("-".repeat(64));

        out.println("    " + link("/", "website home"));
        val pathFwdPrefix = Optional.ofNullable(req.getHeader("x-forwarded-prefix"));
        val pathServlet = Optional.ofNullable(req.getServletPath());
        if (pathFwdPrefix.isPresent()) {
            out.println("    " + link(pathFwdPrefix.get(), "webapp home"));
            if (pathServlet.isPresent()) {
                out.println("    " + link(pathFwdPrefix.get() + rel(pathServlet.get()) + FileUtilities.SLASH, "servlet home"));
            }
        }
        if (dir.isPresent()) {
            out.println("    " + link("../", "up"));
        } else {
            out.println("    " + link("./", "up"));
        }
        out.println("-".repeat(64));



        if (FileUtilities.isInternalInformation(roundtrip)) {
            out.println("Detected INF request; ignoring.");
            out.println("-".repeat(64));
            // TODO: NOT FOUND
        } else {
            if (dir.isPresent()) {
                if (dir.get().stream().anyMatch(e -> filterDirectoryEntry(e, Path.of(roundtrip)))) {
                    dir
                        .get()
                        .stream()
                        .filter(e -> filterDirectoryEntry(e, Path.of(roundtrip)))
                        .map(e -> convertDirectoryEntry(e, Path.of(roundtrip)))
                        .forEach(r -> out.println("    " + r));
                } else {
                    out.println("This directory is empty.");
                }
                out.println("-".repeat(64));

                if (trailingSlash) {
                    out.println("Path has trailing slash; good.");
                    // TODO: send directory listing page OK
                } else {
                    out.println("PATH DOES NOT HAVE TRAILING SLASH");
                    out.println("-".repeat(64));
                    out.println("would redirect to:");
                    out.println("    "+link(
                        (roundtrip.equals("/") ? getLeafPathSegment(req.getServletPath()) : getLeafPathSegment(roundtrip)) +
                        FileUtilities.SLASH));
                    // TODO: send REDIRECT
                }
                out.println("-".repeat(64));
            } else if (res.isPresent()) {
                out.println("Found file resource:");
                out.println("    " + res.get());
                out.println("-".repeat(64));

                if (!trailingSlash) {
                    out.println("Path does not have trailing slash; good.");
                    // TODO: send file! OK
                } else {
                    // Can't happen.
                    // It should never find a (non-directory) file when the path ends with a slash.
                    out.println("PATH HAS TRAILING SLASH");
                    out.println("-".repeat(64));
//                    out.println("would redirect to:");
//                    out.println("    "+ roundtripWithoutTrailingSlash);
                    // TODO: ??? can't happen
                }
                out.println("-".repeat(64));
            } else {
                out.println("RESOURCE NOT FOUND");
                out.println("-".repeat(64));

                if (trailingSlash) {
                    out.println("looking for alternate resource:");
                    out.println("    "+roundtripWithoutTrailingSlash);
                    out.println("-".repeat(64));
                    val altRes = Optional.ofNullable(ctx.getResource(roundtripWithoutTrailingSlash));
                    if (altRes.isPresent()) {
                        out.println("Found file resource:");
                        out.println("    " + altRes.get());
                        out.println("-".repeat(64));
                        out.println("would redirect to:");
                        out.println("    "+link("../" + getLeafPathSegment(roundtripWithoutTrailingSlash)));
                        // TODO: REDIRECT
                    } else {
                        out.println("RESOURCE NOT FOUND");
                        // TODO: NOT FOUND
                    }
                    out.println("-".repeat(64));
                } else {
                    // TODO: NOT FOUND
                }
            }
        }

        printDebugFooter(out);

        out.flush();
        out.close();
    }



    @NonNull
    private String getLeafPathSegment(@NonNull final String servletPath) {
        val segs = Arrays.stream(servletPath.split(FileUtilities.SLASH, -1)).filter(s -> !s.isBlank()).toList();
        if (segs.size() == 0) {
            // TODO what to do here?
            return "";
        }
        return segs.get(segs.size()-1);
    }

    @NonNull
    private String rel(@NonNull final String path) {
        if (!path.startsWith("/")) {
            return path;
        }
        return path.substring(1);
    }

    private void printDebugHeader(@NonNull final String title, @NonNull final PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("    <head>");
        out.println("        <meta charset=\"UTF-8\">");
        out.println("        <title>"+title+"</title>");
        out.println("    </head>");
        out.println("    <body>");
        out.println("        <pre>");
    }

    private void printDebugFooter(@NonNull final PrintWriter out) {
        out.println("        </pre>");
        out.println("    </body>");
        out.println("</html>");
    }

    private static boolean filterDirectoryEntry(@NonNull final String entry, @NonNull final Path cwd) {
        if (cwd.toString().equals(FileUtilities.SLASH)) {
            if (FileUtilities.isInternalInformation(entry)) {
                return false;
            }
        }
        val pathOrig = Path.of(entry);
        val pathRel = cwd.relativize(pathOrig);
        String n = pathRel.toString();
        if (n.startsWith(".")) {
            return false;
        }
        return true;
    }

    @NonNull
    private static String convertDirectoryEntry(@NonNull final String entry, @NonNull final Path cwd) {
        val slash = entry.endsWith(FileUtilities.SLASH);
        val pathOrig = Path.of(entry);
        val pathRel = cwd.relativize(pathOrig);
        String ret = pathRel.toString();
        if (slash) {
            ret += FileUtilities.SLASH;
        }

        return link(ret);
    }

    @NonNull
    private static String link(@NonNull final String ret) {
        return "<a href=\"" + ret +"\">" + ret + "</a>";
    }

    @NonNull
    private static String link(@NonNull final String ret, @NonNull final String text) {
        return "<a href=\"" + ret + "\">" + ret + "</a> [" + text + "]";
    }
}
