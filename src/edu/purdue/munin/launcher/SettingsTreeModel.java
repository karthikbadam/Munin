/* ------------------------------------------------------------------
 * SettingsTreeModel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.launcher;

import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public abstract class SettingsTreeModel<E> implements TreeModel {
    private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
    public abstract void setSettings(E settings);
    public abstract E getSettings();
	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}
	public void valueForPathChanged(TreePath path, Object newValue) {
		// not used
	}
	protected void fireTreeStructureChanged(Object oldRoot) {
        TreeModelEvent e = new TreeModelEvent(this, new Object[] { oldRoot });
        for (TreeModelListener tml : treeModelListeners) {
            tml.treeStructureChanged(e);
        }
    }    
	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}
}
