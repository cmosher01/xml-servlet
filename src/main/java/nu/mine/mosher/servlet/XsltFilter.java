package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.util.*;

@Slf4j
public class XsltFilter extends HttpFilter {
    private URL urlXslt;
    private String attrIn;
    private String attrOut;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);

        val ctx = Objects.requireNonNull(config.getServletContext());

        // Inspired by "Java and XSLT" by Eric M. Burke
        // xsltPath should be something like "/WEB-INF/xslt/a.xslt"
        val path = ServletUtilities.requireParam(config, "xsltPath");
        this.urlXslt = ctx.getResource(path);
        if (Objects.isNull(this.urlXslt)) {
            throw new ServletException("Cannot find XSLT file: "+path);
        }
        log.info("XSLT path: {}", this.urlXslt);

        this.attrIn = ServletUtilities.requireParam(config, "attrIn");
        this.attrOut = ServletUtilities.requireParam(config, "attrOut");
    }

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        super.doFilter(request, response, chain);

        val optNode = XmlFilterUtilities.dom(request, this.attrIn);
        if (optNode.isPresent()) {
            log.info("Processing XSLT transform: {} --> {} --> {}", this.attrIn, this.urlXslt, this.attrOut);
            val transformer = XmlUtilities.getTransformerFactory().newTransformer(new StreamSource(this.urlXslt.toExternalForm()));
            val result = new DOMResult();
            request.getAttributeNames().asIterator().forEachRemaining(n -> transformer.setParameter(n, request.getAttribute(n).toString()));
            transformer.transform(new DOMSource(optNode.get()), result);
            request.setAttribute(this.attrOut, result.getNode());
        }
    }

    @Override
    public void destroy() {
        this.attrOut = null;
        this.attrIn = null;
        this.urlXslt = null;

        super.destroy();
    }
}
