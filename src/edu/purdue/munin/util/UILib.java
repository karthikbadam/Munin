package edu.purdue.munin.util;

import java.awt.Frame;

import javax.swing.UIManager;

public class UILib {

	public static void setPlatformLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setFullscreen(Frame frame, boolean fullscreen) {
		if (fullscreen) {
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(true);
			frame.setResizable(false);
			frame.setVisible(true);
		}		
		else {
			frame.setVisible(false);
			frame.dispose();
			frame.setUndecorated(false);
			frame.setResizable(true);
			frame.setVisible(true);
		}
	}
	
	public static void loadDemoProperties() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		/*
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("munindemos.properties"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.setProperties(properties);
		*/
	}

}
