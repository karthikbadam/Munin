/* ------------------------------------------------------------------
 * SharedArray.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created June 2011 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class SharedArray implements Iterable<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Object> data = new ArrayList<Object>();
	private SharedArrayDelta delta = new SharedArrayDelta();
	
	public SharedArray() {}

	public Iterator<Object> iterator() {
		return data.iterator();
	}
	
	public int size() {
		return data.size();
	}
	
	public void clear() {
		fill(0);
	}
	
	public void add(Object value) {
		delta.add(value);
	}
	
	public void set(int index, Object value) {
		delta.set(index, value);
	}
	
	public void fill(int size, Object fill) {
		delta.fill(size, fill);
	}
	
	public void fill(int size) {
		fill(size, null);
	}
	
	public boolean isChanged() {
		return delta.size() != 0;
	}
	
	public Object get(int index) {
		return data.get(index);
	}
	
	public void clearChanged() {
		this.delta = new SharedArrayDelta();
	}
	
	public SharedArrayDelta getDelta() {
		return delta;
	}
	
	public void update(SharedArrayDelta delta) {
		delta.update(this.data);
		this.delta.setPosition(this.data.size());
	}
	
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("[");
		boolean first = true;
		for (Object item : data) {
			if (first) first = false;
			else sbuf.append(", ");
			sbuf.append(item.toString());
		}
		sbuf.append("]");
		return sbuf.toString();
	}
}
