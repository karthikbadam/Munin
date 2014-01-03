package edu.purdue.munin.io;

import java.io.File;

public interface Settings {
	public boolean load(File file);
	public String getName();
	public String getDescription();
	public void setName(String name);
	public void setDescription(String desc);
}
