/* ------------------------------------------------------------------
 * VectorEditorFactory.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.vector;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;

@PluginImplementation
public class VectorEditorFactory implements ServiceFactory {

	public String getName() {
		return "VectorEditor";
	}

	public int getVersion() {
		return 1;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			return new VectorEditor(platform, surface, this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
}
