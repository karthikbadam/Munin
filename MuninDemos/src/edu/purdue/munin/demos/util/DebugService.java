/* ------------------------------------------------------------------
 * DebugService.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.util;

import java.util.Collection;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedObjectChangeListener;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.peer.Peer;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;

@PluginImplementation
public class DebugService implements ServiceFactory {

	public class Debugger implements Service, SharedSpaceObjectListener, SharedObjectChangeListener, SharedSpaceEventListener {

		private Peer peer;
		
		public Debugger(Platform platform) throws Exception {
        	peer = new Peer(platform.getChannelName());
    		peer.getSpace().addObjectListener(this);
		}

		public void objectChanged(SharedObject so, Collection<String> keys) {
			System.err.println("* Object changed: " + so.getId() + " ------------");
			for (String key : keys) {
				System.err.println("  - " + key + " = " + so.get(key));
			}
		}

		public ServiceFactory getFactory() {
			return DebugService.this;
		}

        public void start() {
        	try {
        		peer.connect();
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        }

		public void stop() {
			peer.disconnect();
		}

		public void objectCreated(SharedObject so) {
			System.err.println("* Object added: " + so.getId() + " ------------");
			so.addChangeListener(this);
		}

		public void objectDeleted(SharedObject so) {
			System.err.println("* Object deleted: " + so.getId() + " ------------");
			so.removeChangeListener(this);
		}

		@Override
		public void eventReceived(SharedEvent event) {
			System.err.println("* Event received:");
			for (String key : event) {
				System.err.println("  - " + key + " = " + event.get(key));
			}
		}		
	}
	
	public String getName() {
		return "DebugService";
	}

	public int getVersion() {
		return 0;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			return new Debugger(platform);
		}
		catch (Exception e) {}
		return null;
	}
}
