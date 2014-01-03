/* ------------------------------------------------------------------
 * Platform.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 12, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import edu.purdue.munin.config.SpaceConfig;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.peer.Peer;
import edu.purdue.munin.services.DisplayService;
import edu.purdue.munin.services.RenderingService;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.munin.space.SharedSpace;

/**
 * The Platform class is the main controlling entity in Munin.  It
 * binds together the P2P peer, the shared space, all the services,
 * the space configuration, and all of the pre-defined shared state. 
 * 
 * @author elm
 */
public class Platform implements Iterable<Service>, SharedSpaceObjectListener {

	private Peer peer;
	private SpaceConfig sc;
	private Hashtable<String, SharedObject> roots = new Hashtable<String, SharedObject>();
	private ArrayList<Service> services = new ArrayList<Service>();
	private Hashtable<String, Service> lookup = new Hashtable<String, Service>();
	
	public Platform(String channel) throws Exception {
		peer = new Peer(channel);
		peer.getSpace().addObjectListener(this);
		peer.connect();
	}
	
	public String getChannelName() {
		return peer.getName();
	}

	public boolean isFirst() {
		return peer.isFirst();
	}
	
	public SharedObject getRoot(Surface surface) {
		if (surface == null) return null;
		if (roots.containsKey(surface.getFullId())) return roots.get(surface.getFullId());
		return getRoot(surface.getParent());
	}
	
	public void initSpace() {
		// Create scene graph nodes for each surface
		// FIXME: Current space config XML does not support more than one root surface
//		for (Surface surface : sc.getRoot()) {
		Surface surface = sc.getRoot();
			System.err.println("Creating root object for surface " + surface.getFullId());
			SharedObject root = peer.getSpace().createObject();
			root.put("sg.type", "root");
			root.put("sg.surfaceId", surface.getFullId());
			root.commit();
//		}
	}
	
	public void setSpaceConfig(SpaceConfig sc) {
		
		// Store the space configuration
		this.sc = sc;
		
		// FIXME: Store it into the shared space
		if (peer.isFirst()) initSpace();
	}
	
	public SpaceConfig getSpaceConfig() {
		return sc;
	}
	
	public Peer getPeer() {
		return peer;
	}
	
	public SharedSpace getSpace() {
		return peer.getSpace();
	}
	
	public void startService(Service service) {
		
		// Store the service
		ServiceFactory factory = service.getFactory();
		services.add(service);
		lookup.put(factory.getName(), service);
		
		// Renderers should be bound to display servers (only existing ones)
		if (factory.getType().equals(ServiceFactory.TYPE_RENDERING)) {
			bindRenderer((RenderingService) service);
		}

		// Finally: start the service
		System.err.println("Starting service '" + factory.getName() + "'...");
		service.start();
	}
	
	public void stopService(Service service) {
		if (!services.contains(service)) return;

		// Remove service from internal storage
		ServiceFactory factory = service.getFactory();
		services.remove(service);
		lookup.remove(factory.getName());

		// Renderers should be unbound from display servers
		if (factory.getType().equals(ServiceFactory.TYPE_RENDERING)) {
			unbindRenderer((RenderingService) service);
		}

		// Stop the service 
		System.err.println("Stopping service '" + factory.getName() + "'...");
		service.stop();
	}
	
	private void bindRenderer(RenderingService rs) {
		for (Service service : services) {
			if (service.getFactory().getType().equals(ServiceFactory.TYPE_DISPLAY)) {
				DisplayService ds = (DisplayService) service;
				ds.bind(rs);
				System.err.println("Binding renderer to display service...");
			}
		}		
	}
	
	private void unbindRenderer(RenderingService rs) {
		for (Service service : services) {
			if (service.getFactory().getType().equals(ServiceFactory.TYPE_DISPLAY)) {
				DisplayService ds = (DisplayService) service;
				ds.unbind(rs);
			}
		}		
	}
	
	public Iterator<Service> iterator() {
		return services.iterator();
	}
	
	public Service findService(String name) {
		return lookup.get(name);
	}
	
	public void shutdownAll() {
		for (Service s : services) {
			s.stop();
		}
		services.clear();
		lookup.clear();
	}

	public void objectCreated(SharedObject so) {
		if (!so.hasValue("sg.type", "root")) return;
		roots.put(so.getString("sg.surfaceId"), so);
	}

	public void objectDeleted(SharedObject so) {}
}
