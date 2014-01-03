/* ------------------------------------------------------------------
 * ScribbleService.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.misc;

import java.awt.Color;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.piccolo.SceneGraph;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;

@PluginImplementation
public class ScribbleService implements ServiceFactory {

	public class Scribble implements Service, SharedSpaceEventListener {
		
		private boolean active = false;
		private double prevX, prevY;
		private Platform platform;
		private SharedObject root;
        
        public Scribble(Platform platform, Surface surface) throws Exception {
        	this.platform = platform;
    		this.root = platform.getRoot(surface);
    		this.platform.getSpace().addEventListener(this);
        }
        
        public void start() {}
		public void stop() {}

		public ServiceFactory getFactory() {
			return ScribbleService.this;
		}

		public void eventReceived(SharedEvent event) {
			if (event.hasValue("input", "mouse")) {
				if (event.has("button3") && event.getBoolean("button3") == true) {
					prevX = event.getDouble("x");
					prevY = event.getDouble("y");
					active = true;
				}
				else if (!event.has("button3") && active) {
					SharedObject line = platform.getSpace().createObject();
					SceneGraph.createLine(line, prevX, prevY, event.getDouble("x"),  event.getDouble("y"));
					SceneGraph.setStrokeColor(line, Color.red);
					SceneGraph.setStrokeWidth(line, 2.0);
					SceneGraph.addChild(root, line);
					line.commit();
					prevX = event.getDouble("x");
					prevY = event.getDouble("y");
				}
				else if (event.has("button3") && event.getBoolean("button3") == false) {
					active = false;
				}
			}
		}
    }
    
	public String getName() {
		return "Scribble";
	}
	
	public int getVersion() {
		return 1;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			Scribble service = new Scribble(platform, surface);
			return service;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}
}
