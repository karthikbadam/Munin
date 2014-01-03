/* ------------------------------------------------------------------
 * JScatterPlotPanel.java
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

public class JScatterPlotPanel extends JPanel implements VisPanel {
	private static final long serialVersionUID = 1L;
	
	private JComboBox xAxisBox, yAxisBox, colorBox;
	
	public JScatterPlotPanel(Table table) {
		
		setLayout(new GridLayout(0, 1));
		
		JPanel xPanel = new JPanel();
		xAxisBox = new JComboBox(new TableColumnListModel(table));
		xAxisBox.setPreferredSize(new Dimension(300, 32));
		xPanel.add(new JLabel("X Axis: "));
		xPanel.add(xAxisBox);
		
		JPanel yPanel = new JPanel();
		yAxisBox = new JComboBox(new TableColumnListModel(table));
		yAxisBox.setPreferredSize(new Dimension(300, 32));
		yPanel.add(new JLabel("Y Axis: "));
		yPanel.add(yAxisBox);

		JPanel colorPanel = new JPanel();
		colorBox = new JComboBox(new TableColumnListModel(table));
		colorBox.setPreferredSize(new Dimension(300, 32));
		colorPanel.add(new JLabel("Color: "));
		colorPanel.add(colorBox);
		
		add(xPanel);
		add(yPanel);
		add(colorPanel);
	}
	
	public String getTabTitle() {
		return "Scatterplot";
	}
	
	public VisPanel.VisType getType() {
		return VisPanel.VisType.ScatterPlot;
	}
	
	public List<Column> getMapping() {
		ArrayList<Column> mappings = new ArrayList<Column>();
		mappings.add((Column) xAxisBox.getSelectedItem());
		mappings.add((Column) yAxisBox.getSelectedItem());
		mappings.add((Column) colorBox.getSelectedItem());
		return mappings;
	}
	
	public void refresh() {
		((TableColumnListModel) xAxisBox.getModel()).refresh();
		((TableColumnListModel) yAxisBox.getModel()).refresh();
		((TableColumnListModel) colorBox.getModel()).refresh();
	}
}
