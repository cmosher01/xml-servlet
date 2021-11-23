package nu.mine.mosher.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Element;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nu.mine.mosher.servlet.FileUtilities.segs;
import static nu.mine.mosher.servlet.XmlUtils.XHTML_NAMESPACE;
import static nu.mine.mosher.servlet.XmlUtils.e;
import static nu.mine.mosher.servlet.XmlUtils.t;

@WebServlet("/d1/d2/*")
public class PlayServlet extends HttpServlet {
    private static final boolean ALLOW_PUBLIC_DEBUG_FLAG = true;
//    private static final Charset DEFAULT_RESPONSE_CHARSET = StandardCharsets.UTF_8;



    @Override
    @SneakyThrows
    protected void doGet(@NonNull final HttpServletRequest req, @NonNull final HttpServletResponse resp) {
        val ctx = req.getServletContext();

        logXmlInfo(ctx);

        val debug = ALLOW_PUBLIC_DEBUG_FLAG && Optional.ofNullable(req.getParameter("debug")).isPresent();

        val safePathInfo = Optional.ofNullable(req.getPathInfo()).orElse("");
        val trailingSlash = safePathInfo.endsWith(FileUtilities.SLASH);
        val pathIsInvalid = segs(safePathInfo).anyMatch(FileUtilities::invalidName);

        val roundtrip = FileUtilities.buildPath(segs(safePathInfo).toList(), trailingSlash);
        val roundtripWithoutTrailingSlash = FileUtilities.buildPath(segs(safePathInfo).toList(), false);



        if (roundtripWithoutTrailingSlash.equals("/health")) {
            return; // OK
        }

        if (pathIsInvalid && !debug) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URI: invalid character");
            return;
        }

        req.setAttribute(
            "nu.mine.mosher.xml.pathPrefix",
            buildPrefixPath(Optional.ofNullable(req.getHeader("x-forwarded-prefix")), Optional.ofNullable(req.getServletPath())));

//        resp.setContentType("application/xhtml+xml");
//        val doc = buildPage();
//        req.setAttribute(SerializeDomFilter.ATTR_DOM, doc);

        ////////////////////////////////////
        //if (false)
        //{
            ////////////////////////////////////

            var dir = Optional.<Set<String>>empty();
            var res = Optional.<URL>empty();
            {
                val u = Optional.ofNullable(ctx.getResource(roundtrip));
                if (u.isPresent()) {
                    val p = Path.of(u.get().toURI());

                    ctx.log("Resource is present. URL: " + u.get().toExternalForm());
                    ctx.log("                     URI: " + u.get().toURI().toASCIIString());
                    ctx.log("                    Path: " + p);
                    ctx.log("                  exists: " + Files.exists(p));
                    ctx.log("             isDirectory: " + Files.isDirectory(p));
                    ctx.log("           isRegularFile: " + Files.isRegularFile(p));
                    ctx.log("                    size: " + Files.size(p));
                    ctx.log("        lastModifiedTime: " + Files.getLastModifiedTime(p));

                    if (Files.isDirectory(p)) {
                        dir = Optional.ofNullable(ctx.getResourcePaths(roundtrip));
                        if (dir.isEmpty()) {
                            dir = Optional.of(Set.of());
                        }
                    } else if (Files.isRegularFile(p)) {
                        res = u;
                    } else {
                        ctx.log("Resource exists, but cannot determine if it is a file or a directory.");
                    }
                } else {
                    ctx.log("Resource not found.");
                }
            }



//            resp.setContentType("text/html");
//            resp.setCharacterEncoding(DEFAULT_RESPONSE_CHARSET.name());

//            val out = resp.getWriter();


//            printDebugHeader(Optional.ofNullable(req.getPathInfo()).orElse("{NULL}"), out);

//            out.println("-".repeat(64));
//
//            out.println("path info (raw):");
//            out.println("    " + Optional.ofNullable(req.getPathInfo()).orElse("{NULL}"));
//            out.println("-".repeat(64));
//
//            out.println("parsed " + segs.size() + " path segments: {good} or [invalid]:");
//            segs.forEach(s -> out.println("    " + (FileUtilities.invalidName(s) ? "[" + s + "]" : "{" + s + "}")));
//            out.println("-".repeat(64));
//
//            out.println("constructed path of webapp resource:");
//            out.println("    " + roundtrip);
//            out.println("-".repeat(64));

//            out.println("-".repeat(64));


            if (FileUtilities.isInternalInformation(roundtrip)) {
//                out.println("Detected INF request; ignoring.");
//                out.println("-".repeat(64));
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                if (dir.isPresent()) {

                    if (trailingSlash) {
//                        out.println("Path has trailing slash; good.");
                        val body = buildPage();
                        req.setAttribute(XmlFilterUtilities.ATTR_DOM, body.getOwnerDocument());
                        val ul = e(body, "ul");
//                        out.println("    " + link("/", "website home"));
//                        val liHomeWebsite = e(ul, "li");
//                        xlink(liHomeWebsite, "/", "website home");

//                        val pathFwdPrefix = Optional.ofNullable(req.getHeader("x-forwarded-prefix"));
//                        val pathServlet = Optional.ofNullable(req.getServletPath());
//                        if (pathFwdPrefix.isPresent()) {
//                           out.println("    " + link(pathFwdPrefix.get(), "webapp home"));
//                            val liHomeWebapp = e(ul, "li");
//                            xlink(liHomeWebapp, pathFwdPrefix.get(), "webapp home");
//                            if (pathServlet.isPresent()) {
//                               out.println("    " + link(pathFwdPrefix.get() + FileUtilities.forceRelative(pathServlet.get()) + FileUtilities.SLASH, "servlet home"));
//                                val liHomeServlet = e(ul, "li");
//                                xlink(liHomeServlet, pathFwdPrefix.get() + FileUtilities.forceRelative(pathServlet.get()) + FileUtilities.SLASH, "servlet home");
//                            }
//                        }

                        val liHome = e(ul, "li");
                        xlink(liHome, FileUtilities.SLASH, "home");

//                        if (dir.isPresent()) {
//                            out.println("    " + link("../", "up"));
                            val liUp = e(ul, "li");
                            xlink(liUp, "../", "up");
//                        } else {
//                            out.println("    " + link("./", "up"));
//                            val liUp = e(ul, "li");
//                            xlink(liUp, "./", "up");
//                        }

                        if (dir.get().stream().anyMatch(e -> filterDirectoryEntry(e, Path.of(roundtrip)))) {
//                             dir.get().stream().filter(e -> filterDirectoryEntry(e, Path.of(roundtrip))).map(e -> convertDirectoryEntry(e, Path.of(roundtrip))).forEach(r -> out.println("    " + r));
                            dir.get().stream().filter(e -> filterDirectoryEntry(e, Path.of(roundtrip))).forEach(e -> convertDirectoryEntryX(ul, e, Path.of(roundtrip)));
                        } else {
//                            out.println("This directory is empty.");
                            val li = e(ul, "li");
                            t(li, "[This directory is empty.]");
                        }
//                    out.println("-".repeat(64));



                    } else {
//                        out.println("PATH DOES NOT HAVE TRAILING SLASH");
//                        out.println("-".repeat(64));
//                        out.println("would redirect to:");
                        final var redir = (roundtrip.equals("/") ? FileUtilities.getLeafSegment(req.getServletPath()) : FileUtilities.getLeafSegment(roundtrip)) + FileUtilities.SLASH;
                        ctx.log("Redirecting to: "+redir);
//                        out.println("    " + link(redir));
                        resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                        resp.setHeader("Location", redir);
                    }
//                    out.println("-".repeat(64));
                } else if (res.isPresent()) {
//                    out.println("Found file resource:");
//                    out.println("    " + res.get());
//                    out.println("-".repeat(64));

                    if (!trailingSlash) {
//                        out.println("Path does not have trailing slash; good.");
                        send(res.get(), req, resp);
                    } else {
                        // Can't happen.
                        // It should never find a (non-directory) file when the path ends with a slash.
//                        out.println("PATH HAS TRAILING SLASH");
//                        out.println("-".repeat(64));
                        //                    out.println("would redirect to:");
                        //                    out.println("    "+ roundtripWithoutTrailingSlash);
                        // TODO: ??? can't happen
                    }
//                    out.println("-".repeat(64));
                } else {
//                    out.println("RESOURCE NOT FOUND");
//                    out.println("-".repeat(64));

                    if (trailingSlash) {
//                        out.println("looking for alternate resource:");
//                        out.println("    " + roundtripWithoutTrailingSlash);
//                        out.println("-".repeat(64));
                        val altRes = Optional.ofNullable(ctx.getResource(roundtripWithoutTrailingSlash));
                        if (altRes.isPresent()) {
//                            out.println("Found file resource:");
//                            out.println("    " + altRes.get());
//                            out.println("-".repeat(64));
//                            out.println("would redirect to:");
                            final var redir = "../" + FileUtilities.getLeafSegment(roundtripWithoutTrailingSlash);
                            ctx.log("Redirecting to: "+redir);
//                            out.println("    " + link(redir));
                            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                            resp.setHeader("Location", redir);
                        } else {
//                            out.println("RESOURCE NOT FOUND");
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                        }
//                        out.println("-".repeat(64));
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }

//            printDebugFooter(out);
//
//            out.flush();
//            out.close();



        ////////////////////////////////////////////////////
        //}
        ////////////////////////////////////////////////////
    }

    private static void logXmlInfo(ServletContext ctx) throws ParserConfigurationException, TransformerConfigurationException {
        val document = XmlUtils.getDocumentBuilderFactory().newDocumentBuilder().newDocument();
        ctx.log("XML DOM: "+document.getClass());
        val transformer = XmlUtils.getTransformerFactory().newTransformer();
        ctx.log("XML XSLT: "+transformer.getClass());
    }

    private static String buildPrefixPath(@NonNull final Optional<String> forwarded, final Optional<String> servlet) {
        final Stream<String> s1;
        if (forwarded.isPresent()) {
            s1 = segs(forwarded.get());
        } else {
            s1 = Stream.empty();
        }

        final Stream<String> s2;
        if (servlet.isPresent()) {
            s2 = segs(servlet.get());
        } else {
            s2 = Stream.empty();
        }

        return Stream.concat(s1, s2).collect(Collectors.joining(FileUtilities.SLASH, FileUtilities.SLASH, ""));
    }

    private static void send(@NonNull final URL res, @NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response) throws IOException, TransformerException {
        val ctx = request.getServletContext();

        val metatika = new Metadata();
        metatika.set(TikaCoreProperties.RESOURCE_NAME_KEY, res.getPath());

        val in = TikaInputStream.get(res, metatika);

        val contentType = TikaConfig.getDefaultConfig().getDetector().detect(in, metatika);
        ctx.log("Detected content type: "+contentType);
        val characterEncoding = TikaConfig.getDefaultConfig().getEncodingDetector().detect(in, metatika);
        ctx.log("Detected character encoding: "+characterEncoding.name());

        if (contentType.equals(MediaType.TEXT_HTML)) {
            val document = Jsoup.parse(in, characterEncoding.name(), res.toExternalForm());
            document.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml);
            document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
            document.outputSettings().charset(StandardCharsets.UTF_8);
            document.outputSettings().prettyPrint(true);
            document.getElementsByTag("html").first().attr("xmlns", XHTML_NAMESPACE);
            request.setAttribute(XmlFilterUtilities.ATTR_DOM, W3CDom.convert(document));
        } else if (isXmlContentType(contentType)) {
            val result = new DOMResult();
            XmlUtils.getTransformerFactory().newTransformer().transform(new StreamSource(in), result);
            request.setAttribute(XmlFilterUtilities.ATTR_DOM, result.getNode());
        } else {
            response.setContentType(contentType.toString());
            response.setCharacterEncoding(characterEncoding.name());
            try (val inr = new InputStreamReader(in, characterEncoding); val out = response.getWriter()) {
                inr.transferTo(out);
            }
        }
    }

    private static boolean isXmlContentType(@NonNull final MediaType contentType) {
        return
            contentType.toString().equals("text/xml") ||
            contentType.equals(MediaType.APPLICATION_XML) ||
            contentType.getSubtype().endsWith("+xml");
    }

    private static boolean filterDirectoryEntry(@NonNull final String entry, @NonNull final Path cwd) {
        if (FileUtilities.isInternalInformation(entry)) {
            return false;
        }

        val pathOrig = Path.of(entry);
        val pathRel = cwd.relativize(pathOrig);
        var n = pathRel.toString();
        if (n.startsWith(".")) {
            return false;
        }

        return true;
    }

    @NonNull
    private static void convertDirectoryEntryX(@NonNull final Element parent, @NonNull final String entry, @NonNull final Path cwd) {
        val slash = entry.endsWith(FileUtilities.SLASH);
        val pathOrig = Path.of(entry);
        val pathRel = cwd.relativize(pathOrig);

        var ret = pathRel.toString();
        if (slash) {
            ret += FileUtilities.SLASH;
        }

        val li = e(parent, "li");
        xlink(li, ret, ret);
    }

    private static void xlink(@NonNull final Element parent, @NonNull final String path, @NonNull final String display) {
        val a = e(parent, "a");
        a.setAttribute("href", path);
        t(a, display);
    }

    @SneakyThrows
    private static Element buildPage() {
        val doc = XmlUtils.getDocumentBuilderFactory().newDocumentBuilder().newDocument();

        val html = e(doc, "html");

        val head = e(html, "head");

        val title = e(head, "title");
        title.setTextContent("TEST");

        val css = e(head, "link");
        css.setAttribute("rel", "stylesheet");
        css.setAttribute("href", "/style.css");

        return e(html, "body");
    }
}
