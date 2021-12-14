package nu.mine.mosher.servlet;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;

import java.util.*;

@Slf4j
public class XmlFilterUtilities {
    @NonNull
    public static Optional<Node> dom(@NonNull final HttpServletRequest request, @NonNull final String attr) {
        val optDom = Optional.ofNullable(request.getAttribute(attr));
        if (optDom.isEmpty()) {
            log.info("Could not find DOM attribute: {}", attr);
            return Optional.empty();
        }

        log.info("Found attribute {} of type: {}", attr, optDom.get().getClass());

        if (!(optDom.get() instanceof Node node)) {
            log.error("DOM attribute is of incorrect type; must be of type: {}", Node.class);
            return Optional.empty();
        }

        return Optional.of(node);
    }
}
