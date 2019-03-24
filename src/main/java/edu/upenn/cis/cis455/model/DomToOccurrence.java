package edu.upenn.cis.cis455.model;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.upenn.cis.cis455.model.OccurrenceEvent.Type;

/**
 * This is simple demo code to convert between events and trees.
 * @author ZacharyIves
 *
 */
public class DomToOccurrence {
    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    /**
     * Convert a DOM document into a series of OccurrenceEvents
     * 
     * @param dom
     * @return
     */
    public static List<OccurrenceEvent> getOccurrenceEvents(Node dom) {
    	List<OccurrenceEvent> ret = new ArrayList<>();
		addNextOccurrenceEvent(dom, ret);
		return ret;
	}
    
    private static void addNextOccurrenceEvent(Node dom, List<OccurrenceEvent> output) {
    	if (dom.getNodeType() == Node.DOCUMENT_NODE) {
    		// We'll skip through and 
    	} else if (dom.getNodeType() == Node.ELEMENT_NODE) {
			output.add(new OccurrenceEvent(OccurrenceEvent.Type.Open, dom.getNodeName()));
    	} else if (dom.getNodeType() == Node.TEXT_NODE) {
			output.add(new OccurrenceEvent(OccurrenceEvent.Type.Text, dom.getTextContent()));
    	}
    	
    	// Iterate through children
		Node child = dom.getFirstChild();
		while (child != null) {
			addNextOccurrenceEvent(child, output);
			child = child.getNextSibling();
		}
		
		if (dom.getNodeType() == Node.ELEMENT_NODE) {
			output.add(new OccurrenceEvent(OccurrenceEvent.Type.Close, dom.getNodeName()));
		}
    }
	
    /**
     * Parse an XML document using JAX, getting an org.w3c.dom Document
     * 
     * @param text
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
	public static Document getDomNode(String text) throws ParserConfigurationException, SAXException, IOException {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(text));
	    return builder.parse(is);
	}

	/**
	 * Write XML as a string
	 * From https://stackoverflow.com/questions/2567416/xml-document-to-string/2567443
	 * 
	 * @param doc
	 * @return
	 */
	public static String xmlToString(Document doc) {
	    try {
	        StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}
	/**
	 * Given a stream (sequence) of OccurrenceEvents, create a DOM document.
	 * Expects the Open/Close elements to be matched!
	 * 
	 * @param occurrences
	 * @return
	 * @throws ParserConfigurationException
	 */
	public Document getDomFromOccurrences(List<OccurrenceEvent> occurrences) throws ParserConfigurationException {
	    DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Node node = doc;
		
		for (OccurrenceEvent occur: occurrences) {
			if (occur.getType() == Type.Open)
				node = node.appendChild(doc.createElement(occur.getValue()));
			else if (occur.getType() == Type.Close)
				node = node.getParentNode();
			else if (occur.getType() == Type.Text)
				node.appendChild(doc.createTextNode(occur.getValue()));
			else
				throw new UnsupportedOperationException();
		}
		
		return doc;
	}
	
	
	/**
	 * Processes one occurrence event at a time until we have closed all
	 * open elements, then gets a DOM document.
	 * 
	 * Returns null on each call, until the document is complete.
	 */
	int count = 0;
	List<OccurrenceEvent> occurrences = new ArrayList<>(); 
	public Document processOccurrence(OccurrenceEvent occur) throws ParserConfigurationException {
		if (occur.getType() == Type.Open)
			count++;
		if (occur.getType() == Type.Close)
			count--;
		
		occurrences.add(occur);
		
		if (count == 0) {
			Document doc = getDomFromOccurrences(occurrences);
			
			return doc; 
		} else
			return null;
	}
	

	/**
	 * Very simple test driver
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		DomToOccurrence converter = new DomToOccurrence();

		try {
			// Some test cases
			Document doc = getDomNode("<a>v1<b/>v2</a>");
			
			System.out.println(xmlToString(doc));
			List<OccurrenceEvent> events = converter.getOccurrenceEvents(doc);
			System.out.println(events.toString());
			doc = converter.getDomFromOccurrences(events); 
			System.out.println(xmlToString(doc));
			
			////////////////
			doc = getDomNode("<a>t<b>a<c>abc</c>more<d/></b></a>");
			
			System.out.println(xmlToString(doc));
			events = converter.getOccurrenceEvents(doc);
			System.out.println(events.toString());
			doc = converter.getDomFromOccurrences(events); 
			System.out.println(xmlToString(doc));
			
			//////////////////////
			doc = getDomNode("<a r='abc'>t<b s='def'>a<c u='why'>abc</c><d/>more</b></a>");
			
			System.out.println(xmlToString(doc));
			events = converter.getOccurrenceEvents(doc);
			System.out.println(events.toString());
			doc = converter.getDomFromOccurrences(events); 
			System.out.println(xmlToString(doc));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
