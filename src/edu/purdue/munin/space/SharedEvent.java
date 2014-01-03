/* ------------------------------------------------------------------
 * SharedEvent.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.space;

import java.util.UUID;

import org.jgroups.Address;

public class SharedEvent extends AbstractSharedEntity {
	private Address source;
	
	public SharedEvent(UUID id, SharedSpace space, Address source) {
		super(id, space);
		this.source = source;
	}
	
	public Address getSource() {
		return source;
	}
	
	public void send() {
		synchronized(getState()) {
			getSpace().sendEvent(getId(), getState().getMap());
		}
	}
}
