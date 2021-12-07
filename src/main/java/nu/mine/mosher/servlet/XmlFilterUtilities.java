package nu.mine.mosher.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import org.w3c.dom.Node;

import java.util.*;

public class XmlFilterUtilities {

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
}
