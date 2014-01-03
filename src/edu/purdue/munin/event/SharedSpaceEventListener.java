/* ------------------------------------------------------------------
 * SharedSpaceEventListener.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created 2012-03-17 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.event;

import edu.purdue.munin.space.SharedEvent;

public interface SharedSpaceEventListener {
	public void eventReceived(SharedEvent event);
}
