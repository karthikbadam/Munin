package edu.purdue.munin.demos.performance;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.demos.vector.VectorEditor;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import net.xeoh.plugins.base.annotations.PluginImplementation;


@PluginImplementation
public class PerformanceTestFactory implements ServiceFactory {

	public String getName() {
		return "PerformanceTest";
	}

	public int getVersion() {
		return 1;
	}

	public String getType() {
		return ServiceFactory.TYPE_APP;
	}

	public Service create(Platform platform, Surface surface) {
		try {
			return new PerformanceTest(platform, surface, this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
