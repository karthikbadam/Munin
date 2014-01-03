/* ------------------------------------------------------------------
 * Assembly.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.assembly;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.purdue.munin.io.AbstractXMLReader;
import edu.purdue.munin.io.Settings;
import edu.purdue.munin.io.SettingsFactory;
import edu.purdue.munin.services.ServiceFactory;

/**
 * An Assembly is a collection of services that are executed to start a 
 * distributed application in a Munin space; in other words, they are the 
 * Munin version of applications.  Because the application may be
 * asymmetric and have different behavior on different devices, an
 * assembly consists of multiple nodes, each with their own set of 
 * services to run.
 * 	
 * @author elm
 */
public class Assembly implements Settings {
	
	public static class AssemblyFactory implements SettingsFactory<Assembly> {
		public Assembly create() {
			return new Assembly();
		}
	}
	
	private class AssemblyReader extends AbstractXMLReader {
		private Node current = null;
		private StringBuffer charBuffer = new StringBuffer();
		public AssemblyReader(String name) throws IOException, FileNotFoundException {
			super(name);
		}
		public void startElement(String namespaceURI, String localName,
                String qName, Attributes atts) throws SAXException {
			if (qName.equals("node")) {
				if (atts.getValue("id") == null) {
					System.err.println("no node id specified, ignoring element.");
					return;
				}
				current = new Node(atts.getValue("id"), atts.getValue("name"));
				nodes.add(current);
			}
			else if (qName.equals("run-service")) {				
				if (atts.getValue("name") == null) {
					System.err.println("no service name specified, ignoring element.");
					return;
				}
				current.addRunService(atts.getValue("name"), atts.getValue("type"));
			}
			else if (qName.equals("desc") || qName.equals("name")) {
				charBuffer = new StringBuffer();
			}
	    }
	    public void characters(char[] ch, int start, int length) throws SAXException {
	    	if (charBuffer == null) return;
	    	for (int i = start; i < length; i++) { 
	    		charBuffer.append(ch[i]);
	    	}
	    }
	    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
	    	if (qName.equals("desc")) {
	    		if (current == null) Assembly.this.setDescription(charBuffer.toString());
	    		else current.setDescription(charBuffer.toString());
			}
			else if (qName.equals("name")) {
				Assembly.this.setName(charBuffer.toString());
			}
			charBuffer = null;
	    }
	}

	private String name, filename;
	private String desc;
	private ArrayList<Node> nodes = new ArrayList<Node>();
	
	public Assembly() {
		// empty
	}

	public boolean load(File file) {
		try {
			filename = file.getName();
			AssemblyReader reader = new AssemblyReader(file.getAbsolutePath());
			return reader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String toString() {
		return "" + getName() + " (" + filename + ")";
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return desc;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String desc) {
		this.desc = desc;
	}
	public void clear() {
		nodes.clear();
	}
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}
	public void resolve(Collection<ServiceFactory> services) {
		for (Node node : nodes) {
			node.resolve(services);
		}
	}
	public int getChildCount() {
		return nodes.size();
	}
	public Node getChild(int index) {
		return nodes.get(index);
	}
	public int getIndexOfChild(Object child) {
		return nodes.indexOf(child);
	}	
}
