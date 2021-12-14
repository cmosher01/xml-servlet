package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MediaType;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.util.*;

@Slf4j
public class XmlToDomFilter extends HttpFilter {
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
        val mediaType = MediaType.parse(contentType);

        val urlPath = ServletUtilities.pathInfo(request);
        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));

        if (ServletUtilities.isXmlContentType(mediaType) && u.isPresent()) {
            try (val in = TikaInputStream.get(u.get())) {
                log.info("Parsing XML to DOM: {} --> {}", urlPath, this.attrOut);
                val result = new DOMResult();
                XmlUtilities.getTransformerFactory().newTransformer().transform(new StreamSource(in), result);
                request.setAttribute(this.attrOut, result.getNode());
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
}
