/* ------------------------------------------------------------------
 * JMouseStitchPadCanvas.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.mousestitch;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.HashMap;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.space.SharedEvent;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

public class JMouseStitchPadCanvas extends PCanvas implements SharedSpaceEventListener {
	private static final long serialVersionUID = 1L;
	
	private static final int CURSOR_SIZE = 5;

	private Platform platform;
	private Surface rootSurface;
	private PText coords;
	private double canvasWidth, canvasHeight;
	private DecimalFormat df = new DecimalFormat("0.00");
	private double mx, my, wx, wy;
	
	private HashMap<String, PPath> pointers = new HashMap<String, PPath>();
	
	private class InputHandler extends PBasicInputEventHandler {
		public void mousePressed(PInputEvent e) {
			updateMousePosition(e.getPosition().getX(), e.getPosition().getY());
			SharedEvent mouse = platform.getSpace().createEvent();
			mouse.putString("input", "mouse");
			mouse.putString("surface", rootSurface.getFullId());
			mouse.putDouble("x", wx);  
			mouse.putDouble("y", wy); 
			mouse.putBoolean("button" + e.getButton(), true);
			mouse.send();

			e.setHandled(true);
		}

		public void mouseDragged(PInputEvent e) {
			updateMousePosition(e.getPosition().getX(), e.getPosition().getY());
			SharedEvent mouse = platform.getSpace().createEvent();
			mouse.putString("input", "mouse");
			mouse.putString("surface", rootSurface.getFullId());
			mouse.putDouble("x", wx);  
			mouse.putDouble("y", wy); 
			mouse.send();

			e.setHandled(true);
		}
		public void mouseReleased(PInputEvent e) {
			updateMousePosition(e.getPosition().getX(), e.getPosition().getY());
			SharedEvent mouse = platform.getSpace().createEvent();
			mouse.putString("input", "mouse");
			mouse.putString("surface", rootSurface.getFullId());
			mouse.putDouble("x", wx);  
			mouse.putDouble("y", wy); 
			mouse.putBoolean("button" + e.getButton(), false);
			mouse.send();

			e.setHandled(true);
		}
	}
	
	public JMouseStitchPadCanvas(Platform platform) {
	
		this.platform = platform;
		this.rootSurface = platform.getSpaceConfig().getRoot();
		
		// Set up shared state
		platform.getSpace().addEventListener(this);
				
		// Disable default navigation handlers
		setPanEventHandler(null);
		setZoomEventHandler(null);
		
		// Add the input handler
		addInputEventListener(new InputHandler());
		
		// Handle canvas resize
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				updateCanvas();
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
	}
	
	private PNode createSurfaceHierarchy(Surface node, double scaleX, double scaleY) {
		if (node.getChildCount() == 0) {
			Rectangle2D world = node.getWorldGeometry();
			PPath rect = PPath.createRectangle((float) (world.getX() * scaleX), (float) (world.getY() * scaleY),
					(float) (world.getWidth() * scaleX), (float) (world.getHeight() * scaleY));
			PText label = new PText(node.getId());
			label.setOffset((float) (world.getX() * scaleX), (float) (world.getY() * scaleY));
			rect.addChild(label);
			return rect;
		}
		PNode group = new PNode();
		for (Surface child : node) {
			group.addChild(createSurfaceHierarchy(child, scaleX, scaleY));
		}
		return group;
	}
	
	private void updateCanvas() {
		
		// Get rid of the old graphic
		getLayer().removeAllChildren();
		
		// Calculate global bounding box and aspect ratio
		Rectangle2D box = rootSurface.getBoundingBox();
		double aspect = box.getHeight() / box.getWidth();
		
		// Calculate canvas dimensions
		canvasWidth = getWidth();
		canvasHeight = aspect * getWidth();
		if (canvasHeight > getHeight()) {
			canvasWidth = getHeight() / aspect;
			canvasHeight = getHeight();
		}

		// Now add the rectangles for the boxes
		PNode hierarchy = createSurfaceHierarchy(rootSurface, canvasWidth / box.getWidth(), canvasHeight / box.getHeight());
		getLayer().addChild(hierarchy);

		// Add back all the pointers
		for (PPath pointer : pointers.values()) {
			getLayer().addChild(pointer);
		}
		
		// Update status text
		coords = new PText("mouse: (x: " + df.format(this.mx) + ", y: " + df.format(this.my) + 
				"), world: (x: " + df.format(this.wx) + ", y: " + df.format(this.wy) + ")");
		coords.setOffset(0, getHeight() - coords.getHeight());
		getLayer().addChild(coords);
		
		// Force repaint
		repaint();
	}
	
	private void updateMousePosition(double x, double y) {
		this.mx = x;
		this.my = y;
		this.wx = mx / canvasWidth * rootSurface.getBoundingBox().getWidth();
		this.wy = my / canvasHeight * rootSurface.getBoundingBox().getHeight();

		if (coords != null) {
			coords.setText("mouse: (x: " + df.format(this.mx) + ", y: " + df.format(this.my) + 
					"), world: (x: " + df.format(this.wx) + ", y: " + df.format(this.wy) + ")");
		}
	}

	public void eventReceived(SharedEvent event) {
		if (event.hasValue("input", "mouse")) {
			
			boolean hasButton = false;
			boolean buttonValue = false;
			
			for (int i = 1; i <= 3; i++) {
				if (event.has("button" + i)) {
					hasButton = true;
					buttonValue = event.getBoolean("button" + i);
					break;
				}
			}
			
			double x = event.getDouble("x") * canvasWidth / rootSurface.getBoundingBox().getWidth();
			double y = event.getDouble("y") * canvasHeight / rootSurface.getBoundingBox().getHeight();
			
			if (hasButton && buttonValue) {
				PPath pointer = PPath.createEllipse((float) -CURSOR_SIZE / 2.0f, (float) -CURSOR_SIZE / 2.0f, CURSOR_SIZE, CURSOR_SIZE);
				pointer.setOffset(x, y);
				pointer.setPaint(Color.yellow);
				pointers.put(rootSurface.getFullId(), pointer);
				getLayer().addChild(pointer);			
			}
			else if (!hasButton) {
				PPath pointer = pointers.get(rootSurface.getFullId());
				if (pointer != null) {
					pointer.setOffset(x, y);
				}
			}
			else if (hasButton && !buttonValue) {
				PPath pointer = pointers.get(rootSurface.getFullId());
				if (pointer != null) {
					getLayer().removeChild(pointer);
					pointers.remove(rootSurface.getFullId());
				}
			}
			
			// Force repaint
			repaint();
		}
	}
}
