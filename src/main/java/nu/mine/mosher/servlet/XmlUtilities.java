package nu.mine.mosher.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.*;
import org.w3c.dom.Node;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;

public final class XmlUtilities {
    public static final String XHTML_NAMESPACE = "http://www.w3.org/1999/xhtml";

    public static TransformerFactory getTransformerFactory() {
        return TransformerFactory.newInstance("net.sf.saxon.jaxp.SaxonTransformerFactory", XmlUtilities.class.getClassLoader());
    }

    public static Node convertToXml(@NonNull final Object object) throws JsonProcessingException, TransformerException {
        val xml = new XmlMapper().writeValueAsBytes(object);

        val result = new DOMResult();
        getTransformerFactory().newTransformer().transform(new StreamSource(new ByteArrayInputStream(xml)), result);
        return result.getNode();
    }
}
