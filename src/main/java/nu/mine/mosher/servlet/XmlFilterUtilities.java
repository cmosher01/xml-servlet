package nu.mine.mosher.servlet;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.apache.tika.mime.MediaType;
import org.w3c.dom.Node;

import java.nio.charset.Charset;
import java.util.*;

public class XmlFilterUtilities {
    public static final Charset DEFAULT_UNKNOWN_CHARSET = Charset.forName("windows-1252");

    @NonNull
    public static String requireParam(@NonNull final FilterConfig config, @NonNull final String paramName) {
        val s = Optional.ofNullable(config.getInitParameter(paramName)).orElse("");
        if (s.isBlank()) {
            throw new IllegalArgumentException("Missing init-param: "+paramName);
        }
        return s;
    }

    @NonNull
    public static Optional<Node> dom(@NonNull final HttpServletRequest request, @NonNull final String attr) {
        val ctx = Objects.requireNonNull(request.getServletContext());

        val optDom = Optional.ofNullable(request.getAttribute(attr));
        if (optDom.isEmpty()) {
            ctx.log("Could not find attribute \""+attr+"\".");
            return Optional.empty();
        }

        ctx.log("Found attribute \""+attr+"\" of type: "+optDom.get().getClass().getName());

        if (!(optDom.get() instanceof Node node)) {
            ctx.log("Attribute is not of class "+Node.class.getName()+".");
            return Optional.empty();
        }

        return Optional.of(node);
    }

    public static boolean isXmlContentType(@NonNull final MediaType contentType) {
        return
            contentType.getSubtype().equals("xml") ||
            contentType.getSubtype().endsWith("+xml");
    }
}
