/* ------------------------------------------------------------------
 * SharedSpace.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import edu.purdue.munin.data.SharedObjectState;
import edu.purdue.munin.event.SharedObjectChangeListener;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.messages.MuninMessage;

public class SharedSpace extends ReceiverAdapter implements Iterable<UUID> {
	
	private JChannel channel; 
	private ArrayList<SharedSpaceObjectListener> listeners = new ArrayList<SharedSpaceObjectListener>();
	private ArrayList<SharedSpaceEventListener> eventListeners = new ArrayList<SharedSpaceEventListener>();
	private Map<UUID, SharedObject> state = new HashMap<UUID, SharedObject>();
	private Map<UUID, ArrayList<SharedObjectChangeListener>> objectListeners = new HashMap<UUID, ArrayList<SharedObjectChangeListener>>();
	private Map<UUID, SharedObject> pool = new HashMap<UUID, SharedObject>();
	
	public SharedSpace(JChannel channel) {
		this.channel = channel;
	}
	
	public void addObjectListener(SharedSpaceObjectListener listener) {
		listeners.add(listener);
	}
	
	public void removeObjectListener(SharedSpaceObjectListener listener) {
		listeners.remove(listener);
	}

	public void addEventListener(SharedSpaceEventListener listener) {
		eventListeners.add(listener);
	}

	public void removeEventListener(SharedSpaceEventListener listener) {
		eventListeners.remove(listener);
	}
	
    public void receive(Message msg) {
        byte[] buffer = msg.getRawBuffer();
        try {
        	SharedObject sobj;
        	boolean objectCreated = false;
            MuninMessage message = (MuninMessage) Util.objectFromByteBuffer(buffer);
            switch (message.getType()) { 
            	
            case EVENT:
            	SharedEvent event = new SharedEvent(message.getId(), this, msg.getSrc());
            	for (String key : message) {
            		event.update(key, message.get(key));
            	}
            	fireEventReceived(event);
            	break;
            	
            case CHANGE:
            	sobj = state.get(message.getId());
            	if (sobj == null) {
            		sobj = pool.get(message.getId());
            		if (sobj == null) {
            			sobj = new SharedObject(message.getId(), this);
            		}
                	synchronized (state) {
                		state.put(message.getId(), sobj);
                	}
                	objectCreated = true;
            	}
            	sobj = state.get(message.getId());
            	for (String key : message) {
            		sobj.update(key, message.get(key));
            	}
            	if (objectCreated) fireObjectCreated(sobj, msg.getSrc());
            	if (message.hasChanges()) fireObjectChanged(sobj, message.keys());
            	break;
            
            case DELETE:
            	if (!state.containsKey(message.getId())) return;
            	sobj = state.get(message.getId());
            	state.remove(message.getId());
            	objectListeners.remove(message.getId());
            	fireObjectDeleted(sobj, msg.getSrc());
            	break;
            	
            }
        }
        catch (Exception e) { 
        	e.printStackTrace();
        }
    }
    
    public Iterator<UUID> iterator() {
    	return state.keySet().iterator();
    }

    public byte[] getState() {
        try {
        	ArrayList<SharedObjectState> objectState = new ArrayList<SharedObjectState>();
        	for (SharedObject sobj : state.values()) { 
        		objectState.add(sobj.getState());
        	}
            synchronized (state) {
            	return Util.objectToByteBuffer(objectState);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	public void setState(byte[] newState) {
    	try {
        	ArrayList<SharedObjectState> fullState = (ArrayList<SharedObjectState>) Util.objectFromByteBuffer(newState);
        	synchronized (state) { 
            	for (SharedObjectState currState : fullState) {
            		SharedObject sobj = new SharedObject(currState, this);
            		state.put(sobj.getId(), sobj);
            		fireObjectCreated(sobj, null);
            		fireObjectChanged(sobj, currState.keys());
            	}
        	}
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    void sendChange(UUID id, Map<String, Object> data) { 
		try { 
			MuninMessage message = new MuninMessage(MuninMessage.Type.CHANGE, id);
			for (String key : data.keySet()) {
				message.put(key, data.get(key));
			}
			channel.send(null, null, Util.objectToByteBuffer(message));
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
    }

    void sendEvent(UUID id, Map<String, Object> data) { 
		try { 
			MuninMessage message = new MuninMessage(MuninMessage.Type.EVENT, id);
			for (String key : data.keySet()) {
				message.put(key, data.get(key));
			}
			channel.send(null, null, Util.objectToByteBuffer(message));
		}
		catch (Exception e) { 
			e.printStackTrace();
		}    	
    }
    
    public void deleteObject(UUID id) {
		try { 
			MuninMessage message = new MuninMessage(MuninMessage.Type.DELETE, id);
			channel.send(null, null, Util.objectToByteBuffer(message));
		}
		catch (Exception e) { 
			e.printStackTrace();
		}    	
    }

    public SharedEvent createEvent() {
		return new SharedEvent(UUID.randomUUID(), this, channel.getAddress());
	}

	public SharedObject createObject() {
		SharedObject sobj = new SharedObject(UUID.randomUUID(), this);
		pool.put(sobj.getId(), sobj);
		return sobj;
	}

	public SharedObject createObjectAndCommit() {
		SharedObject sobj = createObject();
		sobj.commit();
		return sobj;
	}

    public void viewAccepted(View newView) {
        System.out.println("** view: " + newView);
    }

	public void addChangeListener(UUID id, SharedObjectChangeListener listener) {
		
		// Is there even an entry for this object?
		if (!objectListeners.containsKey(id)) { 
			ArrayList<SharedObjectChangeListener> ol = new ArrayList<SharedObjectChangeListener>();
			objectListeners.put(id, ol);
		}
		
		// Add the listener
		ArrayList<SharedObjectChangeListener> ol = objectListeners.get(id);
		ol.add(listener);
	}
	
	public boolean hasChangeListener(UUID id, SharedObjectChangeListener listener) {
		if (!objectListeners.containsKey(id)) return false;
		return objectListeners.get(id).contains(listener);
	}
	
	public void removeChangeListener(UUID id, SharedObjectChangeListener listener) {
		if (!objectListeners.containsKey(id)) return;
		objectListeners.get(id).remove(listener);
	}
	
	public SharedObject getObject(UUID id) { 
		return state.get(id);
	}

	private void fireEventReceived(SharedEvent event) {
		for (SharedSpaceEventListener l : eventListeners) {
			l.eventReceived(event);
		}
	}
	
	private void fireObjectCreated(SharedObject sobj, Address source) {
		for (SharedSpaceObjectListener l : listeners) { 
			l.objectCreated(sobj);
		}
	}

	private void fireObjectDeleted(SharedObject sobj, Address source) {
		for (SharedSpaceObjectListener l : listeners) { 
			l.objectDeleted(sobj);
		}
	}
	
	private void fireObjectChanged(SharedObject sobj, Collection<String> keys) {
		ArrayList<SharedObjectChangeListener> ol = objectListeners.get(sobj.getId());
		if (ol == null) return;
		for (SharedObjectChangeListener l : ol) { 
			l.objectChanged(sobj, keys);
		}
	}
}