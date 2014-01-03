package edu.purdue.munin.launcher;

import edu.purdue.munin.config.SpaceConfig;
import edu.purdue.munin.config.Surface;

public class SpaceConfigTreeModel extends SettingsTreeModel<SpaceConfig> {
    private SpaceConfig config = new SpaceConfig();
    public void setSettings(SpaceConfig config) {
    	Surface oldRoot = this.config.getRoot();
    	this.config = config;
    	fireTreeStructureChanged(oldRoot);
    }
    public SpaceConfig getSettings() {
    	return config;
    }
	public Object getChild(Object parent, int index) {
		Surface surface = (Surface) parent;
		return surface.getChild(index);
	}
	public int getChildCount(Object parent) {
		Surface surface = (Surface) parent;
		return surface.getChildCount();
	}
	public int getIndexOfChild(Object parent, Object child) {
		Surface surface = (Surface) parent;
		return surface.getIndexOfChild((Surface) child);
	}
	public Object getRoot() {
		return config.getRoot();
	}
	public boolean isLeaf(Object node) {
		Surface surface = (Surface) node;
		return surface.getChildCount() == 0;
	}
}
