package edu.purdue.munin.launcher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.purdue.munin.io.Settings;
import edu.purdue.munin.io.SettingsManager;

public class JSettingsTable<E extends Settings> extends JPanel {
	private static final long serialVersionUID = 1L;
		
	private class SettingsListModel extends DefaultComboBoxModel {
		private static final long serialVersionUID = 1L;
		private E current = null; 
		public Object getElementAt(int index) {
			return scm.getItemAt(index);
		}
		public int getSize() {
			return scm.getCount();
		}
		public Object getSelectedItem() {
			return current;
		}
		@SuppressWarnings("unchecked")
		public void setSelectedItem(Object current) {
			E newConfig = (E) current;
			this.current = newConfig;
		}
	}

	private SettingsTreeModel<E> model;
	private SettingsManager<E> scm;
	private JTree tree;
	
	public JSettingsTable(SettingsManager<E> scm, SettingsTreeModel<E> model) {
		this.scm = scm;
		this.model = model;
		setLayout(new BorderLayout());
		
		JComboBox configList = new JComboBox(new SettingsListModel());
		configList.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				E settings = (E) cb.getSelectedItem();
				JSettingsTable.this.model.setSettings(settings);
				JSettingsTable.this.expandAll();
			}
		});
		add(configList, BorderLayout.PAGE_START);

		tree = new JTree(model);
		JScrollPane treePane = new JScrollPane(tree);
		treePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		treePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(treePane, BorderLayout.CENTER);
	}
	
	public void expandAll() {
		for (int i = 0; i < tree.getRowCount(); i++) {
	         tree.expandRow(i);
		}
	}

	public E getCurrentSettings() {
		return model.getSettings();
	}
	
	public Object getCurrentSelection() {
		TreePath path = tree.getSelectionPath();
		if (path == null) return null;
		return path.getLastPathComponent();
	}
	
	public void setName(String name) {
		setBorder(BorderFactory.createTitledBorder(name));		
	}
}
