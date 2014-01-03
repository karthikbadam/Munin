/* ------------------------------------------------------------------
 * JMultiDimFrame.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created March 16, 2013 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.demos.multidim;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.purdue.pivotlib.data.BasicTable;
import edu.purdue.pivotlib.data.Column;
import edu.purdue.pivotlib.data.Table;
import edu.purdue.pivotlib.io.TableReader;

public class JMultiDimFrame {
	
	private JFrame frame;
	private Table table = new BasicTable();
	private JTabbedPane tabbedPane;
	
	private JScatterPlotPanel scatterPlotPanel;
	private JParCoordPlotPanel parCoordPlotPanel;
	private JBarChartPanel barChartPanel;
	private JLineChartPanel lineChartPanel;
		
	public JMultiDimFrame() {
		
		// Create the frame
		frame = new JFrame("MultiDim - Munin Multidimensional Visualization Demo");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Set the layout
		frame.setLayout(new BorderLayout());
		
		// Create the button panel
		JPanel buttonPanel = new JPanel();
		frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
		
		// Need an open button 
		JButton openButton = new JButton("Open...");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(".");
				if (jfc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					table.clear();
					TableReader reader = new TableReader(jfc.getSelectedFile(), table);
					reader.load();
					
					scatterPlotPanel.refresh();
					parCoordPlotPanel.refresh();
					barChartPanel.refresh();
					lineChartPanel.refresh();
				}
			}
		});
		buttonPanel.add(openButton);
		
		// Create the tabbed pane
		tabbedPane = new JTabbedPane();
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		// Add all of the tabs
		scatterPlotPanel = new JScatterPlotPanel(table);
		tabbedPane.addTab(scatterPlotPanel.getTabTitle(), scatterPlotPanel);

		barChartPanel = new JBarChartPanel(table);
		tabbedPane.addTab(barChartPanel.getTabTitle(), barChartPanel);

		lineChartPanel = new JLineChartPanel(table);
		tabbedPane.addTab(lineChartPanel.getTabTitle(), lineChartPanel);

		parCoordPlotPanel = new JParCoordPlotPanel(table);
		tabbedPane.addTab(parCoordPlotPanel.getTabTitle(), parCoordPlotPanel);

		// Prepare to show the frame
		frame.pack();		
	}
	
	public void show() {
		frame.setVisible(true);
	}
	
	public VisPanel getSelectedPanel() {
		return (VisPanel) tabbedPane.getSelectedComponent();
	}
	
	public VisPanel.VisType getSelectedType() {
		if (table == null) return VisPanel.VisType.None;
		return getSelectedPanel().getType();
	}
	
	public Table getTable() {
		return table;
	}
	
	public List<Column> getSelectedMapping() {
		if (table == null) return null;
		return getSelectedPanel().getMapping();
	}
}
