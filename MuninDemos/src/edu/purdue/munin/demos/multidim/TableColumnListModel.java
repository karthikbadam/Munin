/* ------------------------------------------------------------------
 * TableColumnListModel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 16, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import javax.swing.DefaultComboBoxModel;

import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;

public class TableColumnListModel extends DefaultComboBoxModel {
	private static final long serialVersionUID = 1L;
	private Table table;
	private Column current = null;
	public TableColumnListModel(Table table) {
		this.table = table;
	}
	public Object getElementAt(int index) {
		return table.getColumnAt(index);
	}
	public int getSize() {
		return table.getColumnCount();
	}
	public Object getSelectedItem() {
		return current;
	}
	public void setSelectedItem(Object current) {
		this.current = (Column) current;
	}
	public void refresh() {
		super.fireContentsChanged(this, 0, getSize());
	}
}
