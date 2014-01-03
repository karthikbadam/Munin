package edu.purdue.munin.launcher;

import edu.purdue.munin.assembly.Assembly;
import edu.purdue.munin.assembly.Node;

public class AssemblyTreeModel extends SettingsTreeModel<Assembly> {
    private Assembly assembly = new Assembly();
    public void setSettings(Assembly assembly) {
    	Assembly oldRoot = this.assembly;
    	this.assembly = assembly;
    	fireTreeStructureChanged(oldRoot);
    }
    public Assembly getSettings() {
    	return assembly;
    }
	public Object getChild(Object parent, int index) {
		if (parent instanceof Assembly) {
			return assembly.getChild(index);
		}
		Node node = (Node) parent;
		return node.getChild(index);
	}
	public int getChildCount(Object parent) {
		if (parent instanceof Assembly) {
			return assembly.getChildCount();
		}
		Node node = (Node) parent;
		return node.getChildCount();
	}
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof Assembly) {
			return assembly.getIndexOfChild((Node) child);
		}
		Node node = (Node) parent;
		return node.getIndexOfChild(child);
	}
	public Object getRoot() {
		return assembly;
	}
	public boolean isLeaf(Object node) {
		if (node instanceof Assembly) return false;
		if (node instanceof Node) return false;
		return true;
	}
}
