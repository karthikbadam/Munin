/* ------------------------------------------------------------------
 * JPeerTable.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.launcher;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.jgroups.Address;

public class JPeerTable extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String columnNames[] = { "Name", "UUID", "Address" };
	
	private class Peer {
		private UUID id;
		private String name;
		private Address address;
		public Peer(UUID id) {
			this.id = id;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setAddress(Address address) {
			this.address = address;
		}
		public UUID getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public Address getAddress() {
			return address;
		}
	}
	
	private class PeerTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public int getColumnCount() {
			return columnNames.length;
		}
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}
		public int getRowCount() {
			return peers.size();
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			Peer peer = (Peer) peers.get(rowIndex);
			switch (columnIndex) {
			case 0: 
				return peer.getName();
			case 1:
				return peer.getId();
			case 2:
				return peer.getAddress();
			default: return "";
			}
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}
	
	private JTable table;
	private PeerTableModel model = new PeerTableModel();
	private ArrayList<Peer> peers = new ArrayList<Peer>();
	private HashMap<UUID, Peer> peerLookup = new HashMap<UUID, Peer>();
	
	public JPeerTable() {
		table = new JTable(model);
		JScrollPane tablePane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(200, 250));
		tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		tablePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setFillsViewportHeight(true);
		add(tablePane);
		setBorder(BorderFactory.createTitledBorder("Connected Peers"));
	}
	
	public void updatePeer(UUID id, String name, Address address) {
		if (!peerLookup.containsKey(id)) return;
		Peer peer = peerLookup.get(id);
		peer.setName(name);
		peer.setAddress(address);
		model.fireTableDataChanged();
	}

	public void addPeer(UUID id) {
		Peer peer = new Peer(id);
		peers.add(peer);
		peerLookup.put(id, peer);
		model.fireTableDataChanged();
	}
	
	public void removePeer(UUID id) {
		if (!peerLookup.containsKey(id)) return;
		Peer peer = peerLookup.get(id);
		peers.remove(peer);
		peerLookup.remove(id);
		model.fireTableDataChanged();
	}
}
