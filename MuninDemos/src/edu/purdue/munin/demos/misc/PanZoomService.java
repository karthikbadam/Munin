package edu.purdue.munin.demos.misc;

import java.awt.geom.AffineTransform;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.services.piccolo.SceneGraph;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;

@PluginImplementation
public class PanZoomService implements ServiceFactory {

	private enum Mode { Zoom, Pan };

	public String getName() {
		return "ZoomPanner";
	}

	public int getVersion() {
		return 0;
	}

	public String getType() {
		return TYPE_APP;
	}

	public Service create(Platform platform, Surface surface) {
		return new ZoomPanner(platform, surface);
	}

	public class ZoomPanner implements Service, SharedSpaceEventListener {
		
		private Mode mode = Mode.Pan;
		private SharedObject root;
		private AffineTransform transform; 
		private double sx, sy;
		
		public ZoomPanner(Platform platform, Surface surface) {
			platform.getSpace().addEventListener(this);
			root = platform.getRoot(surface);
		}
		
		public void eventReceived(SharedEvent event) {
			
			// Only respond to mouse events
			if (!event.hasValue("input", "mouse")) return;
			
			if (event.has("button1") && event.getBoolean("button1") == true) {
				transform = SceneGraph.getTransform(root);
				sx = event.getDouble("x");
				sy = event.getDouble("y");
				mode = Mode.Pan;
			}
			else if (event.has("button3") && event.getBoolean("button3") == true) {
				transform = SceneGraph.getTransform(root);
				sx = event.getDouble("x");
				mode = Mode.Zoom;
			}
			else if (!event.has("button1") || !event.has("button3")) {
				if (mode == Mode.Pan) { 
					double deltaX = event.getDouble("x") - sx;
					double deltaY = event.getDouble("y") - sy;
					sx = event.getDouble("x");
					sy = event.getDouble("y");
					transform.translate(deltaX, deltaY);
					SceneGraph.setTransform(root, transform);
					root.commit();
				}
				else if (mode == Mode.Zoom){
					if (transform.getScaleX() < 0.1 || transform.getScaleY() < 0.1) return;
					double deltaX = event.getDouble("x") - sx;
					double scaleDelta = (1.0 + (0.001 * deltaX));
					sx = event.getDouble("x");
					transform.scale(scaleDelta, scaleDelta);
					SceneGraph.setTransform(root, transform);
					root.commit();
				}
			}
		}

		public ServiceFactory getFactory() {
			return PanZoomService.this;
		}

		public void start() {}
		public void stop() {}
		
	}
}
