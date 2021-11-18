package nu.mine.mosher.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import lombok.*;

import java.net.URL;
import java.nio.charset.*;
import java.util.*;

@WebServlet("/d1/d2/*")
public class PlayServlet extends HttpServlet {
    private static final boolean ALLOW_PUBLIC_DEBUG_FLAG = true;
    private static final Charset DEFAULT_RESPONSE_CHARSET = StandardCharsets.UTF_8;

    @Override
    @SneakyThrows
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        val ctx = req.getServletContext();
        ctx.log("PlayServlet: begin");






        val safePathInfo = Optional.ofNullable(req.getPathInfo()).orElse("");
        val segs = Arrays.stream(safePathInfo.split(FileUtilities.SLASH, -1)).filter(s -> !s.isBlank()).toList();
        val trailingSlash = safePathInfo.endsWith(FileUtilities.SLASH);
        val pathIsInvalid = segs.stream().anyMatch(FileUtilities::invalidName);


        val roundtrip = FileUtilities.buildPath(segs, trailingSlash);
        val roundtripWithoutTrailingSlash = FileUtilities.buildPath(segs, false);

        val dir =
            Optional.ofNullable(ctx.getResourcePaths(roundtrip));

        val res =
            dir.isPresent() ?
                Optional.<URL>empty() :
                Optional.ofNullable(ctx.getResource(roundtrip));




        val debug = ALLOW_PUBLIC_DEBUG_FLAG && Optional.ofNullable(req.getParameter("debug")).isPresent();
        if (pathIsInvalid && !debug) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URI: invalid character");
            return;
        }



        resp.setContentType("text/plain");
        resp.setCharacterEncoding(DEFAULT_RESPONSE_CHARSET.name());

        val out = resp.getWriter();



        out.println("-".repeat(64));

        out.println("path info (raw):");
        out.println("    "+Optional.ofNullable(req.getPathInfo()).orElse("<<NULL>>"));
        out.println("-".repeat(64));

        out.println("parsed "+segs.size()+" path segments: <good> or [invalid]:");
        segs.forEach(s -> out.println("    "+(FileUtilities.invalidName(s) ? "["+s+"]" : "<"+s+">")));
        out.println("-".repeat(64));

        out.println("constructed path of webapp resource:");
        out.println("    "+roundtrip);
        out.println("-".repeat(64));

        if (FileUtilities.isInternalInformation(roundtrip)) {
            out.println("Detected INF request; ignoring.");
            out.println("-".repeat(64));
            // TODO: NOT FOUND
        } else {
            if (dir.isPresent()) {
                out.println("Found directory resources:");
                dir.get().forEach(r -> out.println("    " + r));
                out.println("-".repeat(64));

                if (trailingSlash) {
                    out.println("Path has trailing slash; good.");
                    // TODO: send directory listing page OK
                } else {
                    out.println("PATH DOES NOT HAVE TRAILING SLASH");
                    out.println("-".repeat(64));
                    out.println("would redirect to:");
                    out.println("    "+roundtrip+(roundtrip.equals("/") ? "" : FileUtilities.SLASH));
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
                    // It should never file a (non-directory) file when the path ends with a slash.
                    out.println("PATH HAS TRAILING SLASH");
                    out.println("-".repeat(64));
                    out.println("would redirect to:");
                    out.println("    "+ roundtripWithoutTrailingSlash);
                    // TODO: ??? can't happen
                }
                out.println("-".repeat(64));
            } else {
                out.println("RESOURCE NOT FOUND");
                out.println("-".repeat(64));

                if (trailingSlash) {
                    val altRes = Optional.ofNullable(ctx.getResource(roundtripWithoutTrailingSlash));
                    if (altRes.isPresent()) {
                        out.println("Found file resource:");
                        out.println("    " + altRes.get());
                        out.println("-".repeat(64));
                        out.println("would redirect to:");
                        out.println("    "+roundtripWithoutTrailingSlash);
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

        out.flush();
        out.close();

        ctx.log("PlayServlet: end");
    }
}
