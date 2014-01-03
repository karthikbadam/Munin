/* ------------------------------------------------------------------
 * AbstractSharedEntity.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created 2012-03-17 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.space;

import java.util.Iterator;
import java.util.UUID;

import edu.purdue.munin.data.SharedArray;
import edu.purdue.munin.data.SharedObjectState;

public abstract class AbstractSharedEntity implements SharedEntity, Iterable<String> {
	
	private SharedObjectState objectState;
	private SharedSpace space;
	
	public AbstractSharedEntity(UUID id, SharedSpace state) {
		this.objectState = new SharedObjectState(id);
		this.space = state;
	}

	public AbstractSharedEntity(SharedObjectState objectState, SharedSpace state) {
		this.objectState = objectState;
		this.space = state;
	}

	public UUID getId() { 
		return objectState.getId();
	}
	
	public SharedSpace getSpace() {
		return space;
	}
	
	public void put(String key, Object value) {
		synchronized (objectState) {
			objectState.put(key, value);
		}
	}
	
	public void putString(String key, String value) {
		put(key, value);
	}
	
	public void putDouble(String key, double value) {
		put(key, new Double(value));
	}
	
	public void putInt(String key, int value) {
		put(key, new Integer(value));
	}
	
	public void putBoolean(String key, boolean value) {
		put(key, new Boolean(value));
	}

	public void update(String key, Object value) {
		synchronized (objectState) {
			if (get(key) != null && get(key).equals(value)) return;
			objectState.put(key, value);
		}
	}
	
	public Object get(String key) { 
		return objectState.get(key);
	}
	
	public String getString(String key) {
		if (!has(key)) return null;
		return get(key).toString();
	}
	
	public double getDouble(String key) {
		if (!has(key)) return 0;
		return ((Double) get(key)).doubleValue();
	}
	
	public int getInt(String key) {
		if (!has(key)) return 0;
		return ((Integer) get(key)).intValue();
	}
	
	public boolean getBoolean(String key) {
		if (!has(key)) return false;
		return ((Boolean) get(key)).booleanValue();
	}
	
	public SharedArray getArray(String key) {
		if (!has(key)) return null;
		return (SharedArray) get(key);
	}
	
	public boolean has(String key) { 
		return objectState.contains(key);
	}

	public boolean hasValue(String key, Object value) {
		if (!objectState.contains(key)) return false;
//		Object o = objectState.get(key);
//		boolean t = o.equals(value);
		return objectState.get(key).equals(value);
	}

	public Iterator<String> iterator() {
		return objectState.keys().iterator();
	}
	
	public SharedObjectState getState() {
		return objectState;
	}
}
