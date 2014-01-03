/* ------------------------------------------------------------------
 * SharedObject.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.space;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.purdue.munin.data.SharedArray;
import edu.purdue.munin.data.SharedObjectState;
import edu.purdue.munin.event.SharedObjectChangeListener;

public class SharedObject extends AbstractSharedEntity {
	
	private Map<String, Object> changes = new HashMap<String, Object>();
	
	public SharedObject(UUID id, SharedSpace space) {
		super(id, space);
	}

	public SharedObject(SharedObjectState objectState, SharedSpace space) {
		super(objectState, space);
	}
	
	public void put(String key, Object value) {
		synchronized (changes) {
			if (value instanceof SharedArray) {
				SharedArray array = (SharedArray) value;
				value = array.getDelta();
				array.clearChanged();
			}
			changes.put(key, value);
		}
	}
	
	public void markChanged(String key) {
		synchronized (changes) {
			Object value = getState().get(key);			
			if (value instanceof SharedArray) {
				SharedArray array = (SharedArray) value;
				value = array.getDelta();
				array.clearChanged();
			}
			changes.put(key, value);
		}
	}	
	
	public void addChangeListener(SharedObjectChangeListener l) {
		getSpace().addChangeListener(getId(), l);
	}
	
	public boolean hasChangeListener(SharedObjectChangeListener l) {
		return getSpace().hasChangeListener(getId(), l);
	}
	
	public void removeChangeListener(SharedObjectChangeListener l) {
		getSpace().removeChangeListener(getId(), l);
	}
	
	public void rollback() {
		synchronized (changes) { 
			changes.clear();
		}
	}
	
	public void commit() {
		
		synchronized (changes) {
			
			// If there are no changes, do nothing
			if (changes.size() == 0) return;
						
			// Send the changes
			getSpace().sendChange(getState().getId(), changes);
			
			// Clear the changes
			changes.clear();
		}
	}
}
