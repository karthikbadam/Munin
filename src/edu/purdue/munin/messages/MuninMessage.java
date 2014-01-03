/* ------------------------------------------------------------------
 * MuninMessage.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.messages;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MuninMessage implements Serializable, Iterable<String> {	
	private static final long serialVersionUID = 1L;
	
	public enum Type {
		CHANGE,
		DELETE,
		EVENT,
	};
	
	private UUID id;
	private Type type;
	private String name;
	private Map<String, Object> state = new HashMap<String, Object>();
	
	public MuninMessage(Type type, UUID id) { 
		this(".", type, id);
	}

	public MuninMessage(String name, Type type, UUID id) { 
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	public void put(String key, Object value) { 
		state.put(key, value);
	}
	
	public Object get(String key) {
		return state.get(key);
	}
	
	public UUID getId() {
		return id;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean hasChanges() {
		return !state.isEmpty();
	}

	public boolean has(String key) {
		return state.containsKey(key);
	}

	public Collection<String> keys() {
		return state.keySet();
	}
	
	public String toString() { 
		return name + "(" + type.toString() + ")";
	}

	public Iterator<String> iterator() {
		return state.keySet().iterator();
	}
	
	public String getName() {
		return this.name;
	}
}
