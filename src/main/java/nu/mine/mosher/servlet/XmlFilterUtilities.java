package nu.mine.mosher.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.val;
import org.w3c.dom.Node;

import java.util.Objects;
import java.util.Optional;

public class XmlFilterUtilities {
    public static final String ATTR_DOM = "nu.mine.mosher.servlet.xml-org.w3c.dom.Node";

    public static Optional<Node> dom(@NonNull final HttpServletRequest req) {
        val ctx = Objects.requireNonNull(req.getServletContext());

        val optDom = Optional.ofNullable(req.getAttribute(ATTR_DOM));
        if (optDom.isEmpty()) {
            ctx.log("Could not find attribute \""+ATTR_DOM+"\".");
            return Optional.empty();
        }

        ctx.log("Found attribute \""+ATTR_DOM+"\" of type: "+optDom.get().getClass().getName());

        if (!(optDom.get() instanceof Node node)) {
            ctx.log("Attribute is not of class "+Node.class.getName()+".");
            return Optional.empty();
        }

        return Optional.of(node);
    }
}
