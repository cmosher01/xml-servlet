package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.*;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Given a DOM Node object (in a request attribute), this filter serializes
 * it to the response. In this case, it sets the response's character encoding
 * to URF-8, and closes the response's output stream. If the content type of the
 * response is not already set, then it sets it to "application/xhtml_xml".
 *
 *  Otherwise (if the attribute does not exist, or is of the wrong datatype),
 * then the response's output stream is unaffected.
 */
public class SerializeDomFilter extends HttpFilter {
    private static final Charset CHARSET_RESPONSE = StandardCharsets.UTF_8;

    @Override
    @SneakyThrows
    protected void doFilter(@NonNull final HttpServletRequest req, @NonNull final HttpServletResponse res, @NonNull final FilterChain chain) {
        super.doFilter(req, res, chain);

        val ctx = Objects.requireNonNull(req.getServletContext());

        val optNode = XmlFilterUtilities.dom(req);

        if (optNode.isPresent()) {
            ctx.log("XML serialization beginning...");
            sendXml(optNode.get(), res);
            ctx.log("XML serialization complete.");
        }
    }

    private static void sendXml(@NonNull final Node node, @NonNull final HttpServletResponse res) throws IOException, TransformerException {
        if (Optional.ofNullable(res.getContentType()).orElse("").isBlank()) {
            res.setContentType("application/xhtml+xml");
        }
        res.setCharacterEncoding(CHARSET_RESPONSE.name());
        try (val out = res.getOutputStream()) {
            serialize(node, out);
        }
    }

    private static void serialize(@NonNull final Node node, @NonNull final OutputStream out) throws TransformerException {
        val tf = TransformerFactory.newInstance().newTransformer();

        tf.setOutputProperty(OutputKeys.METHOD, "xml");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        tf.setOutputProperty(OutputKeys.ENCODING, CHARSET_RESPONSE.name());
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        tf.transform(new DOMSource(node), new StreamResult(out));
    }
}
