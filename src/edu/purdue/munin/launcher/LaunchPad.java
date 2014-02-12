/* ------------------------------------------------------------------
 * LaunchPad.java
 * 
 * Part of the Munin Peer-to-Peer Multicast Framework.
 * Created Spring 2012 by Niklas Elmqvist.
 * ------------------------------------------------------------------
 */
package edu.purdue.munin.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jgroups.Address;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import net.xeoh.plugins.base.util.uri.ClassURI;

import edu.purdue.munin.Platform;
import edu.purdue.munin.assembly.Assembly;
import edu.purdue.munin.config.SpaceConfig;
import edu.purdue.munin.config.Surface;
import edu.purdue.munin.event.SharedSpaceEventListener;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.io.SettingsManager;
import edu.purdue.munin.services.Service;
import edu.purdue.munin.services.ServiceFactory;
import edu.purdue.munin.space.SharedEvent;
import edu.purdue.munin.space.SharedObject;
import edu.purdue.munin.space.SharedSpace;
import edu.purdue.munin.util.UILib;

/**
 * This is the Munin launcher which manages services, shared space,
 * assemblies, and space configuration across the entire Munin space.
 * 
 * @author elm
 */
public class LaunchPad {

	private static final String 	DEFAULT_CHANNEL_NAME 		= "edu.munin";
	private static final int 		VIEWPORT_WIDTH				= 1000;
	private static final int 		VIEWPORT_HEIGHT				= 400;

	private JFrame frame;
    private JServiceTable serviceTable;
    private JSettingsTable<SpaceConfig> configTable;
    private JSettingsTable<Assembly> assemblyTable;
    private JPeerTable peerTable;

    private PluginManager pm; 
    private PluginManagerUtil pmUtil;
    private SettingsManager<Assembly> am = new SettingsManager<Assembly>();
    private Platform platform;
    private SharedSpace space;
    private SharedObject peerObject;

    private class LaunchPadObjectListener implements SharedSpaceObjectListener {
		public void objectCreated(SharedObject so) {
			if (so.hasValue("peer", "name")) {
				peerTable.addPeer(so.getId());
				peerTable.updatePeer(so.getId(), so.getString("name"), (Address) so.get("address"));
			}
		}
		public void objectDeleted(SharedObject so) {
			if (so.hasValue("peer", "name")) {
				peerTable.removePeer(so.getId());
			}
		}
    }
    
    private class LaunchPadEventListener implements SharedSpaceEventListener {
		public void eventReceived(SharedEvent event) {
			if (!event.has("type")) return;
			System.err.println("event received : " + event.get("type"));
			if (event.get("type").equals("start")) {
				System.err.println("start service '" + event.getString("service") + "'");				
			}
			else if (event.get("type").equals("shutdown")) {
				platform.shutdownAll();
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		}
    }
    
    public LaunchPad(Platform platform) throws Exception {
    	
    	// Save the platform
    	this.platform = platform;
    	
    	// Create the frame
		frame = new JFrame("Munin LaunchPad - (c) 2012-2013 by Pivot Lab @ Purdue University");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
		
		// Create the plugin manager
		pm = PluginManagerFactory.createPluginManager();

		// Create the service table
		serviceTable = new JServiceTable();
		frame.getContentPane().add(serviceTable, BorderLayout.LINE_START);

		// Set the space configuration name
		JLabel label = new JLabel("Space configuration: " + platform.getSpaceConfig().getName() + " (" + platform.getSpaceConfig().getDescription() + ")");
		frame.getContentPane().add(label, BorderLayout.PAGE_START);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(2, 0));
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		// Add the config table
		SettingsManager<SpaceConfig> scm = new SettingsManager<SpaceConfig>();
		scm.add(platform.getSpaceConfig());
		configTable = new JSettingsTable<SpaceConfig>(scm, new SpaceConfigTreeModel());
		configTable.setName("Surfaces");
		centerPanel.add(configTable);
		
		// Add the assembly table
		assemblyTable = new JSettingsTable<Assembly>(am, new AssemblyTreeModel());
		assemblyTable.setName("Assemblies");
		centerPanel.add(assemblyTable);

		// Load all the resources for the first time
		refreshResources();

		// Create the peer table
		peerTable = new JPeerTable();
		frame.getContentPane().add(peerTable, BorderLayout.LINE_END);

		// Create a button panel
		JPanel buttonPanel = new JPanel();
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshResources();
			}
		});
		buttonPanel.add(refreshButton);
		
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServiceFactory factory = serviceTable.getSelectedService();
				if (factory == null) return;
				if (configTable.getCurrentSettings() == null) return;
				Surface currSurface = (Surface) configTable.getCurrentSelection();
				if (currSurface == null) return;
				Service service = factory.create(LaunchPad.this.platform, currSurface);
				LaunchPad.this.platform.startService(service);
			}
		});
		buttonPanel.add(startButton);
		
		JButton stopButton = new JButton("Stop");
		buttonPanel.add(stopButton);

		JButton shutdownButton = new JButton("Shutdown all");
		shutdownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SharedEvent event = space.createEvent();
				event.put("type", "shutdown");
				event.send();
//				LaunchPad.this.platform.getPeer().flush();
			}
		});
		buttonPanel.add(shutdownButton);
		
		frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		// Start listening to our space
		space = platform.getSpace();
		space.addObjectListener(new LaunchPadObjectListener());
		space.addEventListener(new LaunchPadEventListener());
		
		// Handle frame closing
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent event) {
				shutdown();
			}
		});		
		
		// Prepare to show the frame
		frame.pack();
    }
    
    private void refreshResources() {
    	
    	// Check for new plugins
		pm.addPluginsFrom(ClassURI.CLASSPATH);
		pmUtil = new PluginManagerUtil(pm);
		Collection<ServiceFactory> services = pmUtil.getPlugins(ServiceFactory.class);
		serviceTable.setServices(services);

		// Check for new assemblies 
		am.addSettingsFrom(new File("assembly/"), new Assembly.AssemblyFactory());
		
		// Resolve assembly dependencies
		for (Assembly assembly : am) {
			assembly.resolve(services);
		}
    }
    
    private void shutdown() {
		space.deleteObject(peerObject.getId());
		platform.getPeer().disconnect();
    }

    private void start() throws Exception {

    	// FIXME: make this part of peer?
    	peerObject = space.createObject();
    	peerObject.put("name", "peer");
		peerObject.put("address", platform.getPeer().getAddress());
		peerObject.commit();

        // Show the user interface 
    	frame.setVisible(true);
    }

	public static final void main(String[] args) {
		try {
			
			// Configure interface
        	UILib.setPlatformLookAndFeel();
        	UILib.loadDemoProperties();

        	// Show the dialog
    		// FIXME: Get this from command-line
        	JTextField textField = new JTextField(DEFAULT_CHANNEL_NAME, 30);
        	int option = JOptionPane.showConfirmDialog(null, textField, "Munin Space", JOptionPane.OK_CANCEL_OPTION);

        	// Do we proceed?
        	if (option != JOptionPane.OK_OPTION) return;
        	
    		// Create the platform
    		String channel = textField.getText();
    		Platform platform = new Platform(channel);
    		
    		// Load the space configurations
    		SettingsManager<SpaceConfig> scm = new SettingsManager<SpaceConfig>();
    		scm.addSettingsFrom(new File("configs/"), new SpaceConfig.SpaceConfigFactory());

    		// Select a space configuration
    		JComboBox jcb = new JComboBox(scm.getItems());
    		option = JOptionPane.showConfirmDialog(null, jcb, "Munin Space Configuration", JOptionPane.OK_CANCEL_OPTION);
    		
    		// Do we proceed?
        	if (option != JOptionPane.OK_OPTION) return;
        	platform.setSpaceConfig((SpaceConfig) jcb.getSelectedItem());

        	// Run the launch pad
    		LaunchPad pad = new LaunchPad(platform);
    		pad.start();
    	}
    	catch (Exception e) { 
    		e.printStackTrace();
    	}
	}
}
