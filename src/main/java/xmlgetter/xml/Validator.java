package xmlgetter.xml;
import java.io.IOException;
import java.io.*;
import java.net.URISyntaxException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
public class Validator
{
	private javax.xml.validation.Validator validator;
	public Validator(String s)
	{
		InputStream xsd = getClass().getResourceAsStream(s);
		//InputStream xsd = Validator.class.getResourceAsStream(s);
		try {
			System.out.println(Validator.class.getResource("").toURI());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Source schemaFile = new StreamSource(xsd);
		Schema schema;
		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't found schema file " + xsd);
		}
		validator = schema.newValidator();
	}
    public boolean validate(InputStream xml) throws IOException, XMLParseException, SAXException {
        try {
            Document document = createDocument(xml);
            validator.validate(new DOMSource(document));
            return true;
        } catch (SAXException e) {
        	e.printStackTrace();
            throw e;
        } catch (IOException e) {
            throw new IOException();
        }
    }

    private Document createDocument(InputStream xml) throws XMLParseException, IOException {
        DocumentBuilder parser;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XMLParseException();
        }
        Document document;
        try {
            document = parser.parse(xml);
        } catch (SAXException e) {
            throw new XMLParseException();
        } catch (IOException e) {
            throw new IOException();
        }
        return document;
    }
}
