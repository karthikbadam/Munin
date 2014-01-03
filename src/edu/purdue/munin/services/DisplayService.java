package edu.purdue.munin.services;

import edu.purdue.munin.space.SharedObject;

public interface DisplayService extends Service {
	public void bind(RenderingService renderer);
	public void unbind(RenderingService renderer);
	public void render(SharedObject node);
}
