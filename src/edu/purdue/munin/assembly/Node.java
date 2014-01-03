package edu.purdue.munin.assembly;

import java.util.ArrayList;
import java.util.Collection;

import edu.purdue.munin.services.ServiceFactory;

/**
 * Nodes are run configurations of services in an Assembly for a
 * particular type of device.  For example, a computer in an LCD
 * display wall will be executing different services than a mobile
 * device connected to the Munin space. Nodes capture the different
 * node configurations possible. 
 * 
 * @author elm
 *
 */
public class Node {
	
	public static class RunService {
		private String name, type;
		private ServiceFactory service = null;
		public RunService(String name, String type) {
			this.name = name;
			this.type = type;
		}
		public void resolve(Collection<ServiceFactory> services) {
			for (ServiceFactory sf : services) {
				if (sf.getName().equals(name)) {
					service = sf;
					return;
				}
			}
		}
		public ServiceFactory getService() {
			return service;
		}
		public String getName() {
			return this.name;
		}
		public String getType() {
			return this.type;
		}
		public String toString() {
			return "" + getName() + " (" + type + ") [" + (service != null ? "resolved" : "unresolved") + "]";
		}		
	}
	
	private String id, name, desc;
	private ArrayList<RunService> runList = new ArrayList<RunService>();
	
	public Node(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return desc;
	}
	public void setDescription(String desc) {
		this.desc = desc;
	}	
	public void clear() {
		runList.clear();
	}
	public void addRunService(String name, String type) {
		runList.add(new RunService(name, type));
	}
	public boolean isResolved() {
		for (RunService rs : runList) {
			if (rs.getService() == null) return false;
		}
		return true;
	}
	public void resolve(Collection<ServiceFactory> services) {
		for (RunService rs : runList) {
			rs.resolve(services);
		}
	}
	public String toString() {
		return "" + getName() + " (" + id + ")";
	}
	public int getChildCount() {
		return runList.size();
	}
	public RunService getChild(int index) {
		return runList.get(index);
	}
	public int getIndexOfChild(Object child) {
		return runList.indexOf(child);
	}		
}
