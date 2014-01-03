/* ------------------------------------------------------------------
 * SpaceConfig.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.config;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import edu.purdue.munin.io.AbstractXMLReader;
import edu.purdue.munin.io.Settings;
import edu.purdue.munin.io.SettingsFactory;

public class SpaceConfig implements Settings {	

	public static class SpaceConfigFactory implements SettingsFactory<SpaceConfig> {
		public SpaceConfig create() {
			return new SpaceConfig();
		}		
	}

	private class SpaceReader extends AbstractXMLReader {
		private StringBuffer charBuffer = new StringBuffer();
		private Stack<Surface> surfaceStack = new Stack<Surface>();
		public SpaceReader(String name) throws IOException, FileNotFoundException {
			super(name);
//			surfaceStack.add(new Surface("root", "root"));
		}
		public void startElement(String namespaceURI, String localName,
                String qName, Attributes atts) throws SAXException {
			if (qName.equals("surface")) {
				if (atts.getValue("id") == null) {
					System.err.println("no space id specified, ignoring element.");
					return;
				}
				Surface parentSurface = surfaceStack.isEmpty() ? null : surfaceStack.peek();
				String surfaceId = parentSurface != null ? parentSurface.getFullId() + "." + atts.getValue("id") : atts.getValue("id");	
				if (spaceMap.contains(surfaceId)) {
					System.err.println("surface with id '" + surfaceId + "' already exists, ignoring element.");
					return;
				}
				Surface surface = new Surface(atts.getValue("id"), atts.getValue("name"));
				if (parentSurface != null) {
					parentSurface.addChild(surface);
				}
				else {
					root = surface;
				}
				surfaceStack.add(surface);
				spaceMap.put(surface.getFullId(), surface);
			}
			else if (qName.equals("world-geometry")) {
				if (surfaceStack.isEmpty()) return;
				Surface currSurface = surfaceStack.peek();
				if (atts.getValue("x") == null || atts.getValue("y") == null || atts.getValue("width") == null || atts.getValue("height") == null) return;
				double x = Double.parseDouble(atts.getValue("x"));
				double y = Double.parseDouble(atts.getValue("y"));
				double w = Double.parseDouble(atts.getValue("width"));
				double h = Double.parseDouble(atts.getValue("height"));
				currSurface.setWorldGeometry(new Rectangle2D.Double(x, y, w, h));
			}
			else if (qName.equals("screen-geometry")) {
				if (surfaceStack.isEmpty()) return;
				Surface currSurface = surfaceStack.peek();
				if (atts.getValue("x") == null || atts.getValue("y") == null || atts.getValue("width") == null || atts.getValue("height") == null) return;
				double x = Double.parseDouble(atts.getValue("x"));
				double y = Double.parseDouble(atts.getValue("y"));
				double w = Double.parseDouble(atts.getValue("width"));
				double h = Double.parseDouble(atts.getValue("height"));
				currSurface.setScreenGeometry(new Rectangle2D.Double(x, y, w, h));
			}
			else if (qName.equals("full-screen")) {
				if (surfaceStack.isEmpty()) return;
				Surface currSurface = surfaceStack.peek();
				currSurface.setFullscreen(true);
			}
			else if (qName.equals("windowed")) {
				if (surfaceStack.isEmpty()) return;
				Surface currSurface = surfaceStack.peek();
				currSurface.setFullscreen(false);
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
	    	if (qName.equals("surface")) {
	    		surfaceStack.pop();
	    	}
			else if (qName.equals("desc")) {
				SpaceConfig.this.setDescription(charBuffer.toString());
			}
			else if (qName.equals("name")) {
				SpaceConfig.this.setName(charBuffer.toString());
			}
			charBuffer = null;
	    }
	}
	
	private String name, filename;
	private String description;
	private Surface root = new Surface("root", "root space");
	private Hashtable<String, Surface> spaceMap = new Hashtable<String, Surface>();

	public SpaceConfig() {
		// empty
	}
	
	public boolean load(File file) {
		try {
			filename = file.getName();
			SpaceReader reader = new SpaceReader(file.getAbsolutePath());
			return reader.load();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Surface getRoot() {
		return root;
	}
	
	public Surface findSurface(String fullId) {
		return root.find(fullId);
	}
	
	public String toString() {
		return "" + getName() + " (" + filename + ")";
	}
}
