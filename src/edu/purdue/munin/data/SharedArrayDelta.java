/* ------------------------------------------------------------------
 * SharedArrayDelta.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created June 2011 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SharedArrayDelta implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int position;
	private ArrayList<Integer> indices = new ArrayList<Integer>();
	private ArrayList<Object> data = new ArrayList<Object>();
	
	public SharedArrayDelta() {
		this.position = 0;
	}

	public SharedArrayDelta(int position) {
		this.position = position;
	}
	
	public void set(int index, Object value) {
		indices.add(index);
		data.add(value);
	}
	
	public void fill(int size) {
		fill(size, null);
	}
	
	public void fill(int size, Object fill) {
		indices.clear();
		data.clear();
		while (this.position < size) {
			add(fill);
		}
	}
	
	public void add(Object value) {
		indices.add(position++);
		data.add(value);
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void merge() {
		// empty (for now)
	}
	
	public int size() {
		return data.size();
	}
	
	public void clearChanged() {
		indices.clear();
		data.clear();
	}
	
	public void update(ArrayList<Object> source) {

		// Step through the list of updates
		for (int i = 0; i < data.size(); i++) { 
			
			// Retrieve the values
			int index = indices.get(i);
			Object value = data.get(i);
			
			// Ensure that the source is big enough
			while (source.size() <= index) {
				source.add(null);
			}
			
			// Update the value
			source.set(index, value);
		}
	}
}
