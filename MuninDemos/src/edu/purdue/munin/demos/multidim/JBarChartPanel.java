/* ------------------------------------------------------------------
 * JBarChartPanel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 17, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;

public class JBarChartPanel extends JPanel implements VisPanel {
	private static final long serialVersionUID = 1L;
	
	private JComboBox dataBox, labelBox;
	
	public JBarChartPanel(Table table) {
		
		setLayout(new GridLayout(0, 1));
		
		JPanel dataPanel = new JPanel();
		dataBox = new JComboBox(new TableColumnListModel(table));
		dataBox.setPreferredSize(new Dimension(300, 32));
		dataPanel.add(new JLabel("Data: "));
		dataPanel.add(dataBox);

		JPanel labelPanel = new JPanel();
		labelBox = new JComboBox(new TableColumnListModel(table));
		labelBox.setPreferredSize(new Dimension(300, 32));
		labelPanel.add(new JLabel("Label: "));
		labelPanel.add(labelBox);

		add(dataPanel);
		add(labelPanel);
	}
	
	public String getTabTitle() {
		return "Bar Chart";
	}
	
	public VisPanel.VisType getType() {
		return VisPanel.VisType.BarChart;
	}
	
	public List<Column> getMapping() {
		ArrayList<Column> mappings = new ArrayList<Column>();
		mappings.add((Column) dataBox.getSelectedItem());
		mappings.add((Column) labelBox.getSelectedItem());
		return mappings;
	}
	
	public void refresh() {
		((TableColumnListModel) dataBox.getModel()).refresh();
		((TableColumnListModel) labelBox.getModel()).refresh();
	}
}
