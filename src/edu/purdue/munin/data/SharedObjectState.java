/* ------------------------------------------------------------------
 * SharedObjectState.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created June 2011 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SharedObjectState implements Serializable { 
	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public SharedObjectState(UUID id) {
		this.id = id;
	}
	
	public UUID getId() { 
		return id;
	}
	
	public Collection<String> keys() { 
		return map.keySet();
	}

	public Collection<Object> values() { 
		return map.values();
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public void put(String key, Object value) {
		if (value instanceof SharedArrayDelta) {
			SharedArrayDelta delta = (SharedArrayDelta) value;
			// FIXME: type may change!
			if (!map.containsKey(key)) {
				map.put(key, new SharedArray());
			}
			SharedArray array = (SharedArray) map.get(key);
			array.update(delta);
		}
		else {
			map.put(key, value);
		}
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
}
