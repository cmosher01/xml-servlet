package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.*;
import java.util.*;


/**
 * Given a DOM Node object (in a request attribute named by the attrIn parameter), this filter serializes
 * it to the response. In this case, it sets the response's character encoding
 * to UTF-8, and closes the response's output stream.
 *
 * If the content type of the response is not already set, then it sets it to "application/xhtml+xml".
 *
 *  Otherwise (if the attribute does not exist, or is of the wrong datatype),
 * then the response's output stream is unaffected.
 */
@Slf4j
public class SerializeDomFilter extends HttpFilter {
    private static final Charset CHARSET_RESPONSE = StandardCharsets.UTF_8;
    private String attrIn;

    @Override
    @SneakyThrows
    public void init(@NonNull final FilterConfig config) {
        super.init(config);
        this.attrIn = ServletUtilities.requireParam(config, "attrIn");
    }

    @Override
    @SneakyThrows
    public void doFilter(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain chain) {
        log.trace("filter entry");
        log.trace("filter forward");
        super.doFilter(request, response, chain);
        log.trace("filter return");

        val optNode = XmlFilterUtilities.dom(request, this.attrIn);
        if (optNode.isPresent()) {
            log.info("Serializing DOM to XML: {} --> response", this.attrIn);
            sendXml(optNode.get(), response);
        }
        log.trace("filter exit");
    }

    @Override
    @SneakyThrows
    public void destroy() {
        this.attrIn = null;
        super.destroy();
    }



    private static void sendXml(@NonNull final Node node, @NonNull final HttpServletResponse response) throws IOException, TransformerException {
        if (Optional.ofNullable(response.getContentType()).orElse("").isBlank()) {
            response.setContentType("application/xhtml+xml");
        }
        response.setCharacterEncoding(CHARSET_RESPONSE.name());
        try (val out = response.getOutputStream()) {
            serialize(node, out);
        }
    }

    private static void serialize(@NonNull final Node node, @NonNull final OutputStream out) throws TransformerException {
        val tf = XmlUtilities.getTransformerFactory().newTransformer();

        tf.setOutputProperty(OutputKeys.METHOD, "xml");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        tf.setOutputProperty(OutputKeys.ENCODING, CHARSET_RESPONSE.name());
        tf.setOutputProperty(OutputKeys.INDENT, "no");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        tf.transform(new DOMSource(node), new StreamResult(out));
    }
}
