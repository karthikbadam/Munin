package edu.purdue.munin.services;

import java.util.Collection;

import edu.purdue.munin.space.SharedObject;

public interface RenderingService extends Service {
	public boolean nodeChanged(SharedObject so, DisplayService ds, Collection<String> changes);
	public void nodeRemoved(SharedObject so, DisplayService ds);
}
