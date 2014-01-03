package edu.purdue.munin.launcher;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import edu.purdue.munin.services.ServiceFactory;

public class JServiceTable extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String columnNames[] = { "Name", "Type", "Version" };
	
	private class PluginTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public int getColumnCount() {
			return columnNames.length;
		}
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}
		public int getRowCount() {
			if (services == null) return 0;
			return services.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (services == null) return null;
			ServiceFactory plugin = (ServiceFactory) services.toArray()[rowIndex];
			switch (columnIndex) {
			case 0: 
				return plugin.getName();
			case 1:
				return plugin.getType();
			case 2:
				return plugin.getVersion();
			default: return "";
			}
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
	
	private JTable table;
	private Collection<ServiceFactory> services;
	
	public JServiceTable() {
		table = new JTable(new PluginTableModel());
		JScrollPane tablePane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(200, 250));
		tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tablePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		add(tablePane);
		setBorder(BorderFactory.createTitledBorder("Available Services"));
	}
	
	public void setServices(Collection<ServiceFactory> services) {
		this.services = services;
	}
	
	public ServiceFactory getService(String name) {
		for (ServiceFactory service : services) {
			if (service.getName().equals(name)) return service;
		}
		return null;
	}
	
	public ServiceFactory getSelectedService() {
		if (table.getSelectedRow() == -1) return null;
		return getService(table.getSelectedRow());
	}
	
	public ServiceFactory getService(int row) {
		return (ServiceFactory) services.toArray()[row];
	}
}
