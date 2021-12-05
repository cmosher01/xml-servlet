package nu.mine.mosher.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

public final class XmlUtilities {
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

//    public static DocumentBuilderFactory getDocumentBuilderFactory() {
//        return DocumentBuilderFactory.newNSInstance("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl", XmlUtilities.class.getClassLoader());
//    }

    public static TransformerFactory getTransformerFactory() {
        return TransformerFactory.newInstance("net.sf.saxon.jaxp.SaxonTransformerFactory", XmlUtilities.class.getClassLoader());
    }

    public static Node convertToXml(@NonNull final Object object) throws JsonProcessingException, TransformerException {
        val sxml = new XmlMapper().writeValueAsBytes(object);
        val rlt = new DOMResult();
        getTransformerFactory().newTransformer().transform(new StreamSource(new ByteArrayInputStream(sxml)), rlt);
        return rlt.getNode();
    }
}
