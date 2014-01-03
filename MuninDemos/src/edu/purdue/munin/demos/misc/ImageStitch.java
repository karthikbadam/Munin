/* ------------------------------------------------------------------
 * ImageStitch.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.misc;

import javax.swing.JFileChooser;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.piccolo.SceneGraph;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.munin.util.FileLib;

@PluginImplementation
public class ImageStitch implements ServiceFactory {
    
	public class ImageStitchService implements Service {
		
		private Platform platform;
		private SharedObject root;
        
        public ImageStitchService(Platform platform, Surface surface) throws Exception {
        	this.platform = platform;
    		this.root = platform.getRoot(surface);
        }
        
        public void start() {
        	JFileChooser jfc = new JFileChooser(".");
        	if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        	SharedObject image = platform.getSpace().createObject();
	        	SceneGraph.createImage(image, FileLib.getRelativePath(jfc.getSelectedFile()));
	        	SceneGraph.addChild(root, image);
	        	image.commit();
        	}
        }

		public void stop() {}

		public ServiceFactory getFactory() {
			return ImageStitch.this;
		}
    }
    
	public String getName() {
		return "ImageStitch";
	}
	
	public int getVersion() {
		return 1;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			ImageStitchService service = new ImageStitchService(platform, surface);
			return service;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}
}
