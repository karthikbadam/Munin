/* ------------------------------------------------------------------
 * Peer.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created October 2010 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.peer;

import java.io.File;

import org.jgroups.Address;
import org.jgroups.JChannel;

import edu.purdue.munin.space.SharedSpace;

public class Peer {

	private String name;
	private boolean first = true;
    private JChannel channel;
    private SharedSpace state;

    public Peer(String name) throws Exception {
    	this.name = name;
    	// "http://engineering.purdue.edu/~elm/projects/munin/munin.xml"
        channel = new JChannel(new File("munin.xml"));
        state = new SharedSpace(channel);
        channel.setReceiver(state);
    }
    
    public String getName() {
    	return name;
    }
    
    public void connect() throws Exception { 
        channel.connect(name);    	
        first = !channel.getState(null, 10000);        
    }
    
    public void flush() {
    	channel.startFlush(true);
    }
 
    public void disconnect() {
    	channel.startFlush(true);    	
		channel.disconnect();
    }
    
    public JChannel getChannel() { 
    	return channel;
    }

    public SharedSpace getSpace() { 
		return state; 
	}
    
    public Address getAddress() {
    	return channel.getAddress();
    }
    
    public boolean isFirst() { 
    	return first;
    }
}
