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
public class TeiDetectorFilter extends HttpFilter {
    private String attrIn;
    private String attrOut;
    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);
        this.attrIn = ServletUtilities.requireParam(config, "attrIn");
        this.attrOut = ServletUtilities.requireParam(config, "attrOut");
    }

    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");
        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");

        val contentType = Optional.ofNullable(response.getContentType()).orElse("text/plain");
        val mediaType = MediaType.parse(contentType);

        if (mediaType.getBaseType().toString().equals("application/tei+xml")) {
            log.info("Copying TEI DOM: {} --> {}", this.attrIn, this.attrOut);
            request.setAttribute(this.attrOut, request.getAttribute(this.attrIn));
            response.setContentType("application/xhtml+xml");
        }

        log.trace("filter exit");
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrOut = null;
        this.attrIn = null;
        super.destroy();
    }
}
