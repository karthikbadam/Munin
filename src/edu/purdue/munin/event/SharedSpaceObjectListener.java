/* ------------------------------------------------------------------
 * SharedSpaceObjectListener.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.event;

import edu.purdue.munin.space.SharedObject;

public interface SharedSpaceObjectListener {
	public void objectCreated(SharedObject so);
	public void objectDeleted(SharedObject so);
}
