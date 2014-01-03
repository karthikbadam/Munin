/* ------------------------------------------------------------------
 * ServiceFactory.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.services;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import net.xeoh.plugins.base.Plugin;

/**
 * The ServiceFactory interface is an example of an Abstract Factory
 * for creating new Munin services in plugins loaded at run-time.
 * The intent of the interface is that new plugins can be created
 * without the Munin core system needing to know exactly which class
 * of service is being constructed.
 * 
 * @@@@ This interface will be updated to allow multiple services to
 * be created from the same factory. 
 * 
 * @author elm
 */
public interface ServiceFactory extends Plugin {
	
	public static final String TYPE_DISPLAY 	= "display";
	public static final String TYPE_INPUT 		= "input";
	public static final String TYPE_RENDERING 	= "rendering";
	public static final String TYPE_COMPUTATION = "computation";
	public static final String TYPE_APP 		= "app";
	public static final String TYPE_SIMULATION 	= "simulation";
	
	public String getName();
	public int getVersion();
	public String getType();
	public Service create(Platform platform, Surface surface);
}
