/* ------------------------------------------------------------------
 * SharedObjectChangeListener.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.event;

import java.util.Collection;

import edu.purdue.munin.space.SharedObject;

public interface SharedObjectChangeListener {
	public void objectChanged(SharedObject so, Collection<String> keys);
}
