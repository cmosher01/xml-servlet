package nu.mine.mosher.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class HtmlToXhtmlFilter extends HttpFilter {
    private String attrOut;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);
        this.attrOut = XmlFilterUtilities.requireParam(config, "attrOut");
    }

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        val ctx = Objects.requireNonNull(request.getServletContext());
        ctx.log("HtmlToXhtmlFilter for "+request.getPathInfo());

        val contentType = Optional.ofNullable(response.getContentType()).orElse("text/plain");
        val characterEncoding = Optional.ofNullable(response.getCharacterEncoding()).orElse("");

        val mediaType = MediaType.parse(contentType);
        ctx.log("HtmlToXhtmlFilter media type: "+mediaType);
        ctx.log("HtmlToXhtmlFilter media subtype: "+mediaType.getSubtype());

        val urlPath = ServletUtilities.pathInfo(request);
        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));

        if (mediaType.getSubtype().equals("html") && u.isPresent()) {
            val urlResource = u.get();
            try (val in = TikaInputStream.get(urlResource)) {
                val jsoup = Jsoup.parse(in, characterEncoding, urlResource.toExternalForm());
                configureJsoup(jsoup);
                request.setAttribute(this.attrOut, W3CDom.convert(jsoup));
                response.setContentType("application/xhtml+xml");
            }
        } else {
            super.doFilter(request, response, chain);
        }
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
        jsoup.getElementsByTag("html").first().attr("xmlns", XmlUtilities.XHTML_NAMESPACE);
    }
}
