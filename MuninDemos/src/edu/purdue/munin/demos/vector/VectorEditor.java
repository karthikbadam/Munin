/* ------------------------------------------------------------------
 * VectorEditor.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.vector;

import java.awt.Color;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.services.piccolo.SceneGraph;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;

public class VectorEditor implements Service, SharedSpaceEventListener {	

	private Platform platform;
	private ServiceFactory factory;

	private double sx, sy;
	private SharedObject root, node;
	
	VectorEditor(Platform platform, Surface surface, ServiceFactory factory) throws Exception {
		this.platform = platform;
		this.factory = factory;
		this.platform.getSpace().addEventListener(this);
		this.root = platform.getRoot(surface);
	}

	public ServiceFactory getFactory() {
		return factory;
	}

	public void start() {
	}

	public void stop() {
	}
	
	public void eventReceived(SharedEvent event) {
		if (event.hasValue("input", "mouse")) {
			if (event.has("button1") && event.getBoolean("button1") == true) {

				// Get start point
				sx = event.getDouble("x");
				sy = event.getDouble("y");
				
				// Create the new element
				node = platform.getSpace().createObject();
				SceneGraph.createRectangle(node, 0, 0, 1, 1);
				SceneGraph.setFillColor(node, SceneGraph.getRandomColor());
				SceneGraph.setStrokeColor(node, Color.black);
				SceneGraph.setTranslationMatrix(node, sx, sy);
				SceneGraph.addChild(root, node);
				node.commit();
			}
			else if (!event.has("button1") && node != null) {
				
				// Determine the rubber box dimensions
				double x = sx;
				double y = sy;
				double diffX = event.getDouble("x") - sx;
				double diffY = event.getDouble("y") - sy;
				if (diffX < 0) {
					x = sx + diffX;
					diffX = -diffX;
				}
				if (diffY < 0) {
					y = sy + diffY;
					diffY = -diffY;
				}
				
				// Update the node
				SceneGraph.setTranslationMatrix(node, x, y);
				SceneGraph.createRectangle(node, 0, 0, diffX, diffY);
				node.commit();
			}
		}
	}
	
}
