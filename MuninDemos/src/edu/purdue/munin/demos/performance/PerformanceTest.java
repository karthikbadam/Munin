package edu.purdue.munin.demos.performance;

import java.awt.Color;
import java.util.ArrayList;

import edu.purdue.munin.Platform;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.munin.services.piccolo.SceneGraph;

public class PerformanceTest implements Service{
	private Platform platform;
	private ServiceFactory factory;

	private double sx, sy;
	private SharedObject root, node;
	ArrayList<SharedObject> nodes = new ArrayList<SharedObject>();
	int NUMBER_OF_OBJECTS = 10000;
	
	PerformanceTest(Platform platform, Surface surface, ServiceFactory factory) throws Exception {
		this.platform = platform;
		this.factory = factory;
		//this.platform.getSpace().addEventListener(this);
		this.root = platform.getRoot(surface);
		

		// Create the new element
		for (int i = 0; i < NUMBER_OF_OBJECTS; i++ ) {
			nodes.add(platform.getSpace().createObject());
			double width = Math.random() * 1000;
			double height = Math.random() * 800;
			SceneGraph.createRectangle(nodes.get(i), 0, 0, width, height);
			SceneGraph.setFillColor(nodes.get(i), SceneGraph.getRandomColor());
			SceneGraph.setStrokeColor(nodes.get(i), Color.black);
			double sx = Math.random() * 800;
			double sy = Math.random() * 800;
			
			SceneGraph.setTranslationMatrix(nodes.get(i), sx, sy);
			SceneGraph.addChild(root, nodes.get(i));
			
		}
		
		long millisecs = 0L;
		for (int i = 0; i < NUMBER_OF_OBJECTS; i++ ) {
			if (i == 0) {
				millisecs =  System.currentTimeMillis();  
			}
			nodes.get(i).commit();
		}
		System.out.println("First object sent at "+millisecs+" millisecs");
		
	}

	public ServiceFactory getFactory() {
		return factory;
	}

	public void start() {
	}

	public void stop() {
	}
}
