package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class HtmlToXhtmlFilter extends HttpFilter {
    private String attrOut;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);
        this.attrOut = ServletUtilities.requireParam(config, "attrOut");
    }

    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val contentType = Optional.ofNullable(response.getContentType()).orElse("text/plain");
        val characterEncoding = Optional.ofNullable(response.getCharacterEncoding()).orElse("");

        val mediaType = MediaType.parse(contentType);

        val urlPath = ServletUtilities.pathInfo(request);
        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));

        if (mediaType.getSubtype().equals("html") && u.isPresent()) {
            val urlResource = u.get();
            try (val in = TikaInputStream.get(urlResource)) {
                log.info("Converting HTML into DOM: {} --> {}", urlPath, this.attrOut);
                request.setAttribute(this.attrOut, jsoup(characterEncoding, urlResource, in));
                response.setContentType("application/xhtml+xml");
            }
        } else {
            super.doFilter(request, response, chain);
        }
    }

    private static org.w3c.dom.Document jsoup(@NonNull final String characterEncoding, @NonNull final URL urlResource, @NonNull final TikaInputStream in) throws IOException {
        val jsoup = Jsoup.parse(in, characterEncoding, urlResource.toExternalForm());
        configureJsoup(jsoup);
        return W3CDom.convert(jsoup);
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrOut = null;
        super.destroy();
    }



    private static void configureJsoup(@NonNull final Document jsoup) {
        jsoup.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        jsoup.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        jsoup.outputSettings().charset(StandardCharsets.UTF_8);
        jsoup.outputSettings().prettyPrint(true);

        val html = Optional.ofNullable(jsoup.getElementsByTag("html").first());
        html.ifPresent(element -> element.attr("xmlns", XmlUtilities.XHTML_NAMESPACE));
    }
}
