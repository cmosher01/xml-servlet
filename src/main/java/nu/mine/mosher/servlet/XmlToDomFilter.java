package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.util.*;

public class XmlToDomFilter extends HttpFilter {
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
        ctx.log("XmlToDomFilter for "+request.getPathInfo());

        val contentType = Optional.ofNullable(response.getContentType()).orElse("text/plain");

        val mediaType = MediaType.parse(contentType);
        ctx.log("XmlToDomFilter media type: "+mediaType);
        ctx.log("XmlToDomFilter media subtype: "+mediaType.getSubtype());

        val urlPath = ServletUtilities.pathInfo(request);
        val u = Optional.ofNullable(ctx.getResource(urlPath.toString()));

        if (XmlFilterUtilities.isXmlContentType(mediaType) && u.isPresent()) {
            try (val in = TikaInputStream.get(u.get())) {
                val result = new DOMResult();
                XmlUtilities.getTransformerFactory().newTransformer().transform(new StreamSource(in), result);
                request.setAttribute(this.attrOut, result.getNode());
                response.setContentType("application/xml");
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
