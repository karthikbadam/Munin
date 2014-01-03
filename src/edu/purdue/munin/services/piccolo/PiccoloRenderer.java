/* ------------------------------------------------------------------
 * PiccoloRenderer.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services.piccolo;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.services.DisplayService;
import edu.purdue.munin.services.RenderingService;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedObject;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

@PluginImplementation
public class PiccoloRenderer implements ServiceFactory {

	private HashMap<File, Image> images = new HashMap<File, Image>();

	public String getName() {
		return "PiccoloRenderer";
	}

	public int getVersion() {
		return 0;
	}

	public String getType() {
		return ServiceFactory.TYPE_RENDERING;
	}

	public Service create(Platform platform, Surface surface) {
		return new Renderer();
	}
	
	private Image loadImageResource(String file) {
		File f = new File(file);
		if (images.containsKey(f)) return images.get(f);
		try {
			BufferedImage img = ImageIO.read(f);
			images.put(f, img);
			return img;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public class Renderer implements RenderingService {
	
		public Renderer() {
		}
		
		public ServiceFactory getFactory() {
			return PiccoloRenderer.this;
		}
	
		public void start() {
			// do nothing
		}
	
		public void stop() {
			// do nothing
		}
		int number_of_objects = 1;
		long time_before_rendering = 0L;
		long time_after_rendering = 0L;
		int NUMBER_OF_OBJECTS = 10000;
		
		private void handleShape(SharedObject so, PiccoloDisplayService.Display pds, Collection<String> changes) {
			if (number_of_objects == NUMBER_OF_OBJECTS) 
				time_before_rendering =  System.currentTimeMillis();
			// If there is a representation and no geometry changes, we don't have to do anything
			if (pds.hasRepresentation(so.getId()) && !SceneGraph.hasGeometryChanges(changes))
				return;
			
			// Create the Piccolo shape
			PPath shape = null;
			if (SceneGraph.isRect(so)) {
				shape = SceneGraph.getRect(so);
			}
			else if (SceneGraph.isCircle(so)) {
				shape = SceneGraph.getCircle(so);
			}
			else if (SceneGraph.isEllipse(so)) {
				shape = SceneGraph.getEllipse(so);
			}
			else if (SceneGraph.isLine(so)) {
				shape = SceneGraph.getLine(so);
			}
			else if (SceneGraph.isPolyLine(so)) {
				shape = SceneGraph.getPolyLine(so);
			}
			else if (SceneGraph.isPolygon(so)) {
				shape = SceneGraph.getPolygon(so);
			}
	
			// If no shape was created, this was not renderable
			if (shape == null) return;
			
			// Set basic visual appearance
			shape.setStrokePaint(Color.black);
			shape.setPaint(null);
			
			// Copy state if the shape already existed
			if (pds.hasRepresentation(so.getId())) {
				PNode oldNode = pds.getRepresentation(so.getId());
				shape.setTransform(oldNode.getTransform());
				shape.setPaint(oldNode.getPaint());
				if (oldNode instanceof PPath) shape.setStrokePaint(((PPath) oldNode).getStrokePaint());
			}
	
			// Register new representation
			pds.registerRepresentation(so.getId(), shape);
			if (number_of_objects == NUMBER_OF_OBJECTS) { 
				time_after_rendering =  System.currentTimeMillis();
				System.out.println("time before rendering: "+time_before_rendering);
				System.out.println("time after rendering: "+time_after_rendering);
			}
			number_of_objects++;
		}
		
		private void handleText(SharedObject so, PiccoloDisplayService.Display pds, Collection<String> changes) {
			
			// Is there a representation? Or did the text change?
			if (pds.hasRepresentation(so.getId()) && !changes.contains(SceneGraph.TEXT_TEXT_ATTR)) return;
			
			// Get the text representation
			PText text = SceneGraph.getText(so);
			
			// Copy state if the text already existed
			if (pds.hasRepresentation(so.getId())) {
				PNode oldNode = pds.getRepresentation(so.getId());
				text.setTransform(oldNode.getTransform());
				text.setPaint(oldNode.getPaint());
				if (oldNode instanceof PText) text.setTextPaint(((PText) oldNode).getTextPaint());
			}			
			
			// Register the text
			pds.registerRepresentation(so.getId(), text);
		}
		
		private void handleImage(SharedObject so, PiccoloDisplayService.Display pds, Collection<String> changes) {

			// Is there a representation? Or did the image change?
			if (pds.hasRepresentation(so.getId()) && !changes.contains(SceneGraph.IMAGE_FILE_ATTR)) return;

			// Get image data
			String filename = SceneGraph.getImageFilename(so);
			Image image = loadImageResource(filename);

			// Create the appropriate object 
			PNode node = null;
			if (image == null) {
				node = PPath.createRectangle(0, 0, 100, 100);
				node.setPaint(Color.white);
			}
			else {
				node = new PImage(image);
			}
			
			// Copy state if the image already existed
			if (pds.hasRepresentation(so.getId())) {
				PNode oldNode = pds.getRepresentation(so.getId());
				node.setTransform(oldNode.getTransform());
			}
			
			// Register the image
			pds.registerRepresentation(so.getId(), node);
		}
		
		private void handleGroup(SharedObject so, PiccoloDisplayService.Display pds, Collection<String> changes) {
			pds.registerRepresentation(so.getId(), new PNode());
		}	
	
		public boolean nodeChanged(SharedObject so, DisplayService ds, Collection<String> changes) {
			
			// Sanity check: make sure this is a Piccolo display service
			if (!(ds instanceof PiccoloDisplayService.Display)) return false;
			PiccoloDisplayService.Display pds = (PiccoloDisplayService.Display) ds;
	
			// Check node type
			if (!SceneGraph.isNode(so)) return false;
			
			// Create the various scene graph types we can handle
			if (SceneGraph.isShape(so)) handleShape(so, pds, changes);
			else if (SceneGraph.isText(so)) handleText(so, pds, changes);
			else if (SceneGraph.isImage(so)) handleImage(so, pds, changes);
			else if (SceneGraph.isGroup(so)) handleGroup(so, pds, changes);
			else return false;
	
			// Get the representation (groups)
			PNode node = pds.getRepresentation(so.getId());
			
			// Check the changes, one at a time
			for (String key : changes) {
				if (SceneGraph.isStrokeColor(key)) {
					if (node instanceof PPath) {
						((PPath) node).setStrokePaint(SceneGraph.getStrokeColor(so));
					}
				}
				else if (SceneGraph.isFillColor(key)) {
					if (node instanceof PText) {
						((PText) node).setTextPaint(SceneGraph.getFillColor(so));
					}
					node.setPaint(SceneGraph.getFillColor(so));
				}
				else if (SceneGraph.isTransform(key)) {
					node.setTransform(SceneGraph.getTransform(so));
				}
				else if (SceneGraph.isParent(key)) {
					
					// Remove from old parent
					if (node.getParent() != null) {
						node.getParent().removeChild(node);
					}
					
					// Get the new parent
					PNode parent = pds.getRepresentation(SceneGraph.getParent(so));
					
					// Add the node to the new parent
					parent.addChild(node);
				}
			}
			return true;
		}
		
		public void nodeRemoved(SharedObject so, DisplayService ds) {
	
			// Sanity check: make sure this is a Piccolo display service
			if (!(ds instanceof PiccoloDisplayService.Display)) return;
			PiccoloDisplayService.Display pds = (PiccoloDisplayService.Display) ds;
	
			// Check node type
			if (!SceneGraph.isNode(so)) return;
	
			// Remove the representation
			pds.unregisterRepresentation(so.getId());
		}
	}
}
