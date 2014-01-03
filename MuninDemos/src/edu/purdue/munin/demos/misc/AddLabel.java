/* ------------------------------------------------------------------
 * AddLabel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.misc;

import javax.swing.JOptionPane;

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
public class AddLabel implements ServiceFactory {

	public class RandomTextService implements Service, SharedSpaceEventListener {
		
		private Platform platform;
		private SharedObject root, node;
        
        public RandomTextService(Platform platform, Surface surface) throws Exception {
        	this.platform = platform;
    		this.root = platform.getRoot(surface);
    		this.platform.getSpace().addEventListener(this);
        }
        
        public void start() {}
		public void stop() {}

		public ServiceFactory getFactory() {
			return AddLabel.this;
		}

		public void eventReceived(SharedEvent event) {
			if (event.hasValue("input", "mouse")) {
				if (event.has("button3") && event.getBoolean("button3") == true) {
					node = platform.getSpace().createObject();
					SceneGraph.createText(node, 0, 0, "label");
					SceneGraph.setTranslationMatrix(node, event.getDouble("x"), event.getDouble("y"));
					SceneGraph.addChild(root, node);
					node.commit();
				}
				else if (!event.has("button3") && node != null) {
					SceneGraph.setTranslationMatrix(node, event.getDouble("x"), event.getDouble("y"));
					node.commit();
				}
				else if (event.has("button3") && event.getBoolean("button3") == false) {
					String label = JOptionPane.showInputDialog("Label:");
					SceneGraph.createText(node, 0, 0, label);
					node.commit();
				}
			}
		}
    }
    
	public String getName() {
		return "AddLabel";
	}
	
	public int getVersion() {
		return 1;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			RandomTextService service = new RandomTextService(platform, surface);
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
