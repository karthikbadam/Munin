/* ------------------------------------------------------------------
 * MouseStitchPad.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.mousestitch;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;

@PluginImplementation
public class MouseStitchPad implements ServiceFactory {
    
	private static final double VIEWPORT_WIDTH = 400;
	
	public class PadInstance implements Service {
	    private JFrame frame;
	    private JMouseStitchPadCanvas canvas;
	
	    public PadInstance(Platform platform, Surface surface) { 

	    	// Create the frame
    		frame = new JFrame("Munin MouseStitch");
    		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    		// Create the canvas
			canvas = new JMouseStitchPadCanvas(platform);
			Rectangle2D box = platform.getSpaceConfig().getRoot().getBoundingBox();
			double aspect = box.getHeight() / box.getWidth();
			System.err.println(" - " + VIEWPORT_WIDTH * aspect + " box " + box);
			canvas.setPreferredSize(new Dimension((int) VIEWPORT_WIDTH, (int) (VIEWPORT_WIDTH * aspect)));
			frame.getContentPane().add(canvas);
			
			// Prepare to show the frame
			frame.pack();
	    }
	    
	    public void start() {
        
	        // Show the user interface 
	    	frame.setVisible(true);
	    }

		public ServiceFactory getFactory() {
			return MouseStitchPad.this;
		}

		public void stop() {
			frame.dispose();
		}
	}

	public String getName() {
		return "MouseStitchPad";
	}

	public int getVersion() {
		return 1;
	}

	public Service create(Platform platform, Surface surface) {
		PadInstance service = new PadInstance(platform, surface);
		return service;
	}

	public void stop(Service instance) {
		instance.stop();
	}

	public String getType() {
		return ServiceFactory.TYPE_INPUT;
	}
}