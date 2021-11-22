package nu.mine.mosher.servlet;

import lombok.*;
import org.w3c.dom.*;

public final class XmlUtils {
    public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    public static Element e(@NonNull final Node parent, @NonNull final String tag) {
        val x = getDoc(parent).createElementNS(XHTML_NAMESPACE, tag);
        parent.appendChild(x);
        return x;
    }

    public static Text t(@NonNull final Node parent, @NonNull final String text) {
        val x = getDoc(parent).createTextNode(text);
        parent.appendChild(x);
        return x;
    }

    private static Document getDoc(@NonNull final Node parent) {
        return (parent instanceof Document doc) ? doc : parent.getOwnerDocument();
    }
}
