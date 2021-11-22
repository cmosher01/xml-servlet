package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.net.URL;
import java.util.*;

public class XsltFilter extends HttpFilter {
    private static final String PARAM_PATH_XSLT = "xsltPath";
    private URL urlXslt;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);

        val ctx = Objects.requireNonNull(config.getServletContext());

        // Inspired by "Java and XSLT" by Eric M. Burke
        // xsltPath should be something like "/WEB-INF/xslt/a.xslt"
        val path = Optional.ofNullable(config.getInitParameter(PARAM_PATH_XSLT)).orElse("");
        if (path.isBlank()) {
            throw new ServletException("Cannot find init-param: "+PARAM_PATH_XSLT);
        }

        this.urlXslt = config.getServletContext().getResource(path);

        ctx.log("Found XSLT at: "+this.urlXslt);
    }

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest req, @NonNull final HttpServletResponse res, @NonNull final FilterChain chain) {
        super.doFilter(req, res, chain);
        val ctx = Objects.requireNonNull(req.getServletContext());

        val optNode = XmlFilterUtilities.dom(req);
        if (optNode.isPresent()) {
            ctx.log("Will process XSLT: "+this.urlXslt);
            val transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(this.urlXslt.toExternalForm()));
            val result = new DOMResult();
            req.getAttributeNames().asIterator().forEachRemaining(n -> transformer.setParameter(n, req.getAttribute(n).toString()));
            transformer.transform(new DOMSource(optNode.get()), result);
            req.setAttribute(XmlFilterUtilities.ATTR_DOM, result.getNode());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        this.urlXslt = null;
    }
}
