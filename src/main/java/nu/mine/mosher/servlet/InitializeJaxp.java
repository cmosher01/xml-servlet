package nu.mine.mosher.servlet;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebListener;

import javax.xml.parsers.*;
import javax.xml.transform.TransformerFactory;

@WebListener
public class InitializeJaxp implements ServletContextListener {
    @Override
    public void contextInitialized(final ServletContextEvent unused) {
        System.setProperty(DocumentBuilderFactory.class.getName(), "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        System.setProperty(SAXParserFactory.class.getName(), "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        System.setProperty(TransformerFactory.class.getName(), "net.sf.saxon.TransformerFactoryImpl");
    }
}
