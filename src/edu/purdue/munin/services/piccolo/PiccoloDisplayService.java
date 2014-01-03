/* ------------------------------------------------------------------
 * PiccoloDisplayServer.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services.piccolo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedObjectChangeListener;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.services.DisplayService;
import edu.purdue.munin.services.RenderingService;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.munin.util.UILib;
import edu.umd.cs.piccolo.PNode;

@PluginImplementation
public class PiccoloDisplayService implements ServiceFactory {
	
	public String getName() {
		return "PiccoloDisplayService";
	}

	public int getVersion() {
		return 0;
	}

	public Service create(Platform platform, Surface surface) {
		Display service = new Display(platform, surface);
		return service;
	}

	public String getType() {
		return ServiceFactory.TYPE_DISPLAY;
	}
	
	public class Display implements SharedSpaceObjectListener, SharedObjectChangeListener, DisplayService {
		
		private JFrame frame;
		private Platform platform;
	    private Surface surface;
	    private JPiccoloCanvas canvas;
	    private SharedObject root;
	    
	    private Stroke stroke;
	    private Color fillColor;
	    private Color strokeColor;
	    
	    private HashMap<UUID, PNode> representation = new HashMap<UUID, PNode>();
	    
	    private ArrayList<RenderingService> renderers = new ArrayList<RenderingService>();
	    
	    public Display(Platform platform, Surface surface) {
	
	    	// Save configuration
	    	this.surface = surface;
	    	this.platform = platform;
	    	
	    	// Start listening to the space
	    	this.platform.getSpace().addObjectListener(this);
	    	
	    	// Create the frame
			frame = new JFrame("Munin Piccolo2D Display Service");
			frame.setResizable(false);
			Rectangle2D screen = surface.getScreenGeometry();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setLocation((int) screen.getX(), (int) screen.getY());
			if (surface.getFullscreen()) {
				UILib.setFullscreen(frame, true);
			}
	    	
			// Create the canvas
			canvas = new JPiccoloCanvas(surface, platform.getSpace());
			canvas.setPreferredSize(new Dimension((int) screen.getWidth(), (int) screen.getHeight()));
			frame.getContentPane().add(canvas);
	
	    	// Start listening to the scene graph
	    	// FIXME: Is there a risk that nodes are concurrently created here?
	    	root = this.platform.getRoot(surface);
	    	root.addChangeListener(this);
	    	SceneGraph.createGroup(root);
	    	SceneGraph.clearTransform(root);
	    	root.commit();
	    	
	    	// FIXME: Need to traverse space and listen to all existing scene graph nodes
	
			// Prepare to show the frame
			frame.pack();
	    }
	    
		public ServiceFactory getFactory() {
			return PiccoloDisplayService.this;
		}
	
		public void stop() {
			frame.dispose();
		}
		
		public void start() {
    		// Start the canvas
    		canvas.start();
    		    		
            // Show the user interface 
        	frame.setVisible(true);
		}
		
		public Surface getSurface() {
			return surface;
		}
		
		public PNode getRoot() {
			return canvas.getLayer();
		}
		
		public void repaint() {
			synchronized (canvas) {
				canvas.repaint();
			}
		}
	
		public void bind(RenderingService renderer) {
			renderers.add(renderer);
		}
	
		public void unbind(RenderingService renderer) {
			renderers.add(renderer);
		}
	
		public void objectChanged(SharedObject so, Collection<String> keys) {
			
			// Handle the node changes
			for (RenderingService renderer : renderers) {
				renderer.nodeChanged(so, this, keys);
			}
	
			// Force repaint (is it necessary?)
			repaint();
		}
		
		public Color getFillColor() {
			return fillColor;
		}
		
		public Color getStrokeColor() {
			return strokeColor;
		}
		
		public Stroke getStroke() {
			return stroke;
		}
		
		public PNode getRepresentation(UUID id) {
			
			// Special case for the root
			if (id.equals(root.getId())) return canvas.getLayer();
	
			// If there is no representation, create a temporary one...
			if (!representation.containsKey(id)) {
				representation.put(id, new PNode());
			}
			return representation.get(id);
		}
		
		public void unregisterRepresentation(UUID id) {
			PNode node = getRepresentation(id);
			synchronized (canvas) {
				if (node == null) return;
				if (node.getParent() != null) node.getParent().removeChild(node);
				representation.remove(id);
			}
		}
		
		public void registerRepresentation(UUID id, PNode node) {
	
			PNode parent = null;
	//		int index = 0;
			
			synchronized (canvas) {
			
				// Remove any earlier representations
				if (representation.containsKey(id)) {
					PNode old = representation.get(id);
					representation.remove(old);
					if (old.getParent() != null) {
						parent = old.getParent();
						parent.removeChild(old);
		//				parent = old.getParent();
		//				for (index = 0; index < parent.getChildrenCount(); index++) {
		//					if (parent.getChild(index).equals(old)) break;
		//				}
		//				if (index < parent.getChildrenCount()) parent.removeChild(index);
					}
				}
		
				// Store the new representation
				representation.put(id, node);
				
				// Insert in scene graph if we are replacing something
				if (parent != null) {
		//			parent.addChild(index, node);
					parent.addChild(node);
				}
			}
		}
		
		public boolean hasRepresentation(UUID id) {
			
			// Special case for the root
			if (id.equals(root.getId())) return true;
			
			// Otherwise, look up the representation table
			return representation.containsKey(id);
		}
		
		public void changeParent(PNode node, UUID parentId) {
			
			synchronized (canvas) {
				
				// Remove from old parent
				if (node.getParent() != null) {
					node.getParent().removeChild(node);
				}
				
				// Get the new parent
				PNode parent = getRepresentation(parentId);
			
				// 	Add the node to the new parent
				parent.addChild(node);
			}
		}
	
		public void render(SharedObject node) {}
	
		public void objectCreated(SharedObject so) {
	
			// Is this a scene graph node?
			// FIXME: This needs to take surface-specific scene graphs into account
			if (!SceneGraph.isNode(so)) return;
			
			// Listen to newly created scene graph nodes
			if (!so.hasChangeListener(this)) so.addChangeListener(this);
	
			// Force repaint
			repaint();
		}
		
		public void objectDeleted(SharedObject so) {
			
			// Is this a scene graph node?
			if (!SceneGraph.isNode(so)) return;
				
			// Notify all the renderers
			for (RenderingService renderer : renderers) {
				renderer.nodeRemoved(so, this);
			}
						
			// Force repaint
			repaint();
		}
	}
	
}
