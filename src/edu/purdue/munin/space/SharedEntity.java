/* ------------------------------------------------------------------
 * SharedEntity.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created 2012-03-17 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.space;

import java.util.UUID;

import edu.purdue.munin.data.SharedArray;
import edu.purdue.munin.data.SharedObjectState;

public interface SharedEntity {

	public UUID getId();
	public SharedObjectState getState();
	public SharedSpace getSpace();

	public void put(String key, Object value);	
	public void putDouble(String key, double value);
	public void putInt(String key, int value);
	public void putBoolean(String key, boolean value);

	public Object get(String key); 
	public String getString(String key);
	public double getDouble(String key);
	public int getInt(String key);
	public boolean getBoolean(String key);
	public SharedArray getArray(String key);
	
	public boolean has(String key);
	public boolean hasValue(String key, Object value);
	
//	public void markChanged(String key);
}
