/* ------------------------------------------------------------------
 * Service.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services;

public interface Service {
	public ServiceFactory getFactory();
	public void start();
	public void stop();
}
