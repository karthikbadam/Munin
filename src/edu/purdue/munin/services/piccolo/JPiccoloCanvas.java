/* ------------------------------------------------------------------
 * JPiccoloCanvas.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services.piccolo;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.purdue.munin.config.Surface;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedSpace;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

public class JPiccoloCanvas extends PCanvas {
	private static final long serialVersionUID = 1L;

	private Surface surface;
	private SharedSpace space;
	
	public JPiccoloCanvas(Surface surface, SharedSpace space) {
		this.surface = surface;
		this.space = space;
				
		// Disable default navigation handlers
		setPanEventHandler(null);
		setZoomEventHandler(null);

		// Set window background
		setBackground(new Color(0.9f, 0.9f, 0.9f));
		
		// Handle direct mouse input
		addInputEventListener(new PBasicInputEventHandler() {
			public void mousePressed(PInputEvent e) {
				SharedEvent mouse = JPiccoloCanvas.this.space.createEvent();
				mouse.putString("input", "mouse");
				mouse.putString("surface", JPiccoloCanvas.this.surface.getFullId());
				mouse.putDouble("x", e.getPosition().getX()); 
				mouse.putDouble("y", e.getPosition().getY());
				mouse.putBoolean("button" + e.getButton(), true);
				mouse.send();
				e.setHandled(true);
			}
			public void mouseDragged(PInputEvent e) {
				SharedEvent mouse = JPiccoloCanvas.this.space.createEvent();
				mouse.putString("input", "mouse");
				mouse.putString("surface", JPiccoloCanvas.this.surface.getFullId());
				mouse.putDouble("x", e.getPosition().getX()); 
				mouse.putDouble("y", e.getPosition().getY());
				mouse.send();
				e.setHandled(true);
			}
			public void mouseReleased(PInputEvent e) {
				SharedEvent mouse = JPiccoloCanvas.this.space.createEvent();
				mouse.putString("input", "mouse");
				mouse.putString("surface", JPiccoloCanvas.this.surface.getFullId());
				mouse.putDouble("x", e.getPosition().getX()); 
				mouse.putDouble("y", e.getPosition().getY());
				mouse.putBoolean("button" + e.getButton(), false);
				mouse.send();				
				e.setHandled(true);
			}
		});
				
		// Change camera
		updateCamera();
	}

	public void start() {}
	
	private void updateCamera() {
		Rectangle2D world = surface.getWorldGeometry();
		getCamera().setViewOffset(-world.getX(), -world.getY());
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	public SharedSpace getSpace() {
		return space;
	}

}
