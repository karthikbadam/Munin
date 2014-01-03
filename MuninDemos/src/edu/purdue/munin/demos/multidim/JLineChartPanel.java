/* ------------------------------------------------------------------
 * JLineChartPanel.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 24, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;

public class JLineChartPanel extends JPanel implements VisPanel {
	private static final long serialVersionUID = 1L;
	
	private JComboBox dataBox;
	private JList mappingTable;
	private DefaultListModel listModel = new DefaultListModel();
	
	public JLineChartPanel(Table table) {
		
		setLayout(new GridLayout(0, 1));
		
		// Data selection panel
		JPanel dataPanel = new JPanel();
		dataBox = new JComboBox(new TableColumnListModel(table));
		dataBox.setPreferredSize(new Dimension(300, 32));
		dataPanel.add(new JLabel("Data: "));
		dataPanel.add(dataBox);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listModel.addElement(dataBox.getSelectedItem());
			}
		});
		dataPanel.add(addButton);
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listModel.remove(mappingTable.getSelectedIndex());
			}
		});
		dataPanel.add(deleteButton);
		add(dataPanel);
		
		// Create mapping table
		mappingTable = new JList(listModel);
		add(mappingTable);
	}
	
	public String getTabTitle() {
		return "Line Chart";
	}
	
	public VisPanel.VisType getType() {
		return VisPanel.VisType.LineChart;
	}
	
	public List<Column> getMapping() {
		ArrayList<Column> mappings = new ArrayList<Column>();
		for (int i = 0; i < listModel.getSize(); i++) {
			mappings.add((Column) listModel.get(i));		
		}
		return mappings;
	}
	
	public void refresh() {
		((TableColumnListModel) dataBox.getModel()).refresh();
		listModel.clear();
	}
}
