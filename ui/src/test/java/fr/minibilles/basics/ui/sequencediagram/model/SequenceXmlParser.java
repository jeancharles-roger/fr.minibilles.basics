package fr.minibilles.basics.ui.sequencediagram.model;

import fr.minibilles.basics.ui.sequencediagram.SequenceDiagramApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SequenceXmlParser extends DefaultHandler implements ContentHandler {

	private Sequence sequence;
	private Stack<SequenceItem> context;
	private HashMap<String, LifeLine> lines;
	
	/* protects the constructor */
	private SequenceXmlParser() {}
	
	@Override
	public void startDocument() throws SAXException {
		sequence = new Sequence();
		context = new Stack<SequenceItem>();
		lines = new HashMap<String, LifeLine>();
	}

	@Override
	public void endDocument() throws SAXException {
		context = null;
		lines = null;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (localName.equals("sequence") ) {
			
		} else if ( localName.equals("lifeline") ) {
			float x = getFloatValue("x", attributes, localName);
			String id = getStringValue("id", attributes, localName);
			LifeLine line = new LifeLine(sequence, null, x);
			sequence.addLine(line);
			context.push(line);
			lines.put(id, line);
			
		} else if ( localName.equals("message") ) {
			String source = getStringValue("source", attributes, localName);
			LifeLine sourceLine = getLine(source);
			if ( sourceLine == null ) throw new SAXException("Message's source '" + source + "' doesn't exist.");
			String target = getStringValue("target", attributes, localName);
			LifeLine targetLine = getLine(target);
			if ( targetLine == null ) throw new SAXException("Message's target '" + target + "' doesn't exist.");
			
			Message message = new Message(sequence, null, sourceLine, targetLine);
			sequence.addItem(message);
			context.push(message);

		} else if ( localName.equals("hline") ) {
			HorizontalLine hline = new HorizontalLine(sequence, null);
			sequence.addItem(hline);
			context.push(hline);
			
		} else if ( localName.equals("pause") ) {
			Pause pause = new Pause(sequence);
			sequence.addItem(pause);
			context.push(pause);
			
		}
	}

	private float getFloatValue(final String attribute, Attributes attributes, String elementName) throws SAXException {
		String valueString = getStringValue(attribute, attributes, elementName);
		float value = 0f; 
		try { 
			value = Float.parseFloat(valueString); 
		} catch (NumberFormatException e) { 
			throw new SAXException("Error element '" + elementName + "' x value, '" + valueString + "' isn't a float number." );
		}
		return value;
	}

	private String getStringValue(final String attribute, Attributes attributes, String elementName) throws SAXException {
		String valueString = attributes.getValue(attribute);
		if ( valueString == null ) throw new SAXException("Error element '" + elementName + "' has not x value." );
		return valueString;
	}
	
	private LifeLine getLine(String name) {
		if ( name == null ) return null;
		return lines.get(name);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if ( !context.isEmpty() ) {
			context.peek().setLabel(value.intern());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ( !localName.equals("sequence") ) {
			if ( context.isEmpty() ) {
				throw new SAXException("Ending non-started element '" + localName + "'.");
			} else {
				SequenceItem item = context.pop();
				if ( item.getLabel() == null ) item.setLabel("");
			}
		}
	}
	
	public Sequence getSequence() {
		return sequence;
	}
	
	/** Parses a stream as a XML described sequence and returns it. </p> */
	public static Sequence parseSequence(InputStream stream) throws SAXException, IOException {
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		final SequenceXmlParser handler = new SequenceXmlParser();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(stream));
		return handler.getSequence();
		
	}
	
	/** Parses a file as a XML described sequence and returns it. </p> */
	public static Sequence parseSequence(File file) throws SAXException, IOException {
		return parseSequence(new FileInputStream(file));
	}
	
	/** Parses a file as a XML described sequence and returns it. </p> */
	public static Sequence parseSequence(String filename) throws SAXException, IOException {
		return parseSequence(new File(filename));
	}
	
	public static void writeBigFile(String filename, int count) throws Exception {
		System.out.print("Generating file '" + filename + "' with " + count + " loops ... ");
		PrintWriter writer = new PrintWriter(filename);
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		writer.println("<sequence>");
		writer.println("<lifeline id=\"producer\" x=\"50\">Producer</lifeline>");
		writer.println("<lifeline id=\"bus\" x=\"250\">Bus</lifeline>");
		writer.println("<lifeline id=\"consumer\" x=\"450\">Consumer</lifeline>");
		
		for ( int i=0; i<count; i++ ) {
			writer.println("<hline>Produce 'a' then 'b'</hline>");
			writer.println("<message source=\"producer\" target=\"bus\">a</message>");
			writer.println("<message source=\"producer\" target=\"bus\">b</message>");
			writer.println("<message source=\"bus\" target=\"consumer\">a</message>");
			writer.println("<message source=\"consumer\" target=\"bus\">ok</message>");
			writer.println("<message source=\"bus\" target=\"consumer\">ok</message>");
			writer.println("<message source=\"bus\" target=\"consumer\">b</message>");
			writer.println("<message source=\"consumer\" target=\"bus\">ok</message>");
			writer.println("<message source=\"bus\" target=\"consumer\">ok</message>");
			writer.println("<pause/>");
		}
		writer.println("</sequence>");
		writer.close();
		System.out.println("done");
	}


	public static void main(String[] args) throws Exception {
		
		boolean generate = false;
		int count = 1000;
		String filename = null;
		
		for ( int i=0; i < args.length; i++ ) {
			if ( args[i].equals("-g") ) {
				try {
					generate = true;
					count = Integer.parseInt(args[++i]);
				} catch (Exception e) {
					System.out.println("-g needs an int for count (ex: -g 1000).");
					System.exit(1);
				}
			} else {
				filename = args[i];
				// stop read args
				break;
			}
		}
		
		if ( filename == null  ) {
			filename = generate ? "test/diagram/test_generated.msc" : "test/diagram/test1.msc";
		}
		
		if ( generate ) {
			writeBigFile(filename, count);
		} else {
			final Runtime runtime = Runtime.getRuntime();
			runtime.gc();
			long start = System.currentTimeMillis();
			long startTotalMem = runtime.totalMemory();
			long startFreeMem = runtime.freeMemory();

			Sequence sequence = parseSequence(filename);

			runtime.gc();
			long end = System.currentTimeMillis();
			long endTotalMem = runtime.totalMemory();
			long endFreeMem = runtime.freeMemory();
			long usedMem = (endTotalMem - endFreeMem) - (startTotalMem - startFreeMem);
			
			
			System.out.println("Parsed (" + filename + ": " + formatOctet(new File(filename).length()) + "): " + formatOctet(usedMem) + " and " + (end - start) + " ms.");

			SequenceDiagramApplication app = new SequenceDiagramApplication(sequence);
			app.start();
		}
		
	}
	
	private static String [] octetUnits = new String [] { "o", "ko", "mo", "go", "to" };
	//private static String [] octetUnits = new String [] { "o", "ko" };
	
	public static String formatOctet(long value) {
		int unit = 0;
		while ( value > 1024 && unit < octetUnits.length - 1) {
			value = value / 1024;
			unit++;
		}
		return Long.toString(value) + " " + octetUnits[unit];
	}


}
