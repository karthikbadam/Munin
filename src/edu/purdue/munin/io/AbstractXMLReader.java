/* ------------------------------------------------------------------
 * AbstractXMLReader.java
 * 
 * Created 2008-12-08 by Niklas Elmqvist <elm@purdue.edu>.
 * Based on code by Jean-Daniel Fekete from the IVTK (ivtk.sf.net).
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public abstract class AbstractXMLReader extends AbstractReader implements ContentHandler, EntityResolver {

	protected boolean firstTag;
    protected XMLReader reader;
    protected SAXParser parser;
    
    public AbstractXMLReader(InputStream in, String name) {
    	super(in, name);
    }

    public AbstractXMLReader(String name) throws IOException, FileNotFoundException {
    	super(name);
    }

    public boolean load() {
        firstTag = true;
        try {
            SAXParserFactory p = SAXParserFactory.newInstance();
            p.setFeature("http://xml.org/sax/features/validation", false);
            p.setValidating(false);
            parser = p.newSAXParser();
            reader = parser.getXMLReader();
            reader.setContentHandler(this);
            reader.setEntityResolver(this);
            reader.setDTDHandler(null);
            reader.setFeature("http://xml.org/sax/features/validation", false);
            InputSource source = new InputSource(getIn());
            if (getEncoding() != null) {
                source.setEncoding(getEncoding());
            }
            source.setSystemId(getName());
            reader.parse(source);
        }
        catch (FactoryConfigurationError e) {
        	e.printStackTrace();
            return false;
        }
        catch (SAXParseException e) {
        	e.printStackTrace();
            return false;
        }
        catch (RuntimeException e) {
        	e.printStackTrace();
            return false;
        }
        catch (Exception e) {
        	e.printStackTrace();
            return false;
        }
        catch (OutOfMemoryError e) {
        	e.printStackTrace();
        	return false;
        }
        finally {
            try {
                getIn().close();
            }
            catch (IOException e) {}
        }
        return true;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {}

    public void endDocument() throws SAXException {
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        File file = new File(systemId);

        if (file.exists()) {
            return new InputSource(file.getAbsolutePath());
        }
        
        File dir = new File(getName());
        file = new File(dir.getParentFile(), systemId);
        if (file.exists()) {
            return new InputSource(file.getAbsolutePath());
        }
        
        return null;
    }
}
