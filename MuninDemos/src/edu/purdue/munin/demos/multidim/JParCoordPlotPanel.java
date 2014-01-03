/* ------------------------------------------------------------------
 * JScatterPlotPanel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 17, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;

public class JParCoordPlotPanel extends JPanel implements VisPanel {
	private static final long serialVersionUID = 1L;
	
	private JComboBox colorBox;
	
	public JParCoordPlotPanel(Table table) {
		JPanel colorPanel = new JPanel();
		colorBox = new JComboBox(new TableColumnListModel(table));
		colorBox.setPreferredSize(new Dimension(300, 32));
		colorPanel.add(new JLabel("Color: "));
		colorPanel.add(colorBox);		
		add(colorPanel);
	}
	
	public String getTabTitle() {
		return "Parallel Coordinates";
	}
	
	public VisPanel.VisType getType() {
		return VisPanel.VisType.ParallelCoords;
	}
	
	public List<Column> getMapping() {
		ArrayList<Column> mappings = new ArrayList<Column>();
		mappings.add((Column) colorBox.getSelectedItem());
		return mappings;
	}
	
	public void refresh() {
		((TableColumnListModel) colorBox.getModel()).refresh();
	}
}
