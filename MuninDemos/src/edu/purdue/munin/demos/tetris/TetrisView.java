package edu.purdue.munin.demos.tetris;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import edu.purdue.munin.peer.Peer;

public class TetrisView {

	private static final int 		VIEWPORT_WIDTH		= 400;
	private static final int 		VIEWPORT_HEIGHT		= 400;
//	private static final int		TILE_DIM			= 2;
    
    private JFrame frame;
//    private JTetrisView canvas;
    private Peer peer = new Peer(TetrisControl.CHANNEL_NAME);
    
    public TetrisView() throws Exception { 

    	// Create the frame
		frame = new JFrame("Munin Tetris");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
		
		// Create the canvas
//		canvas = new JTetrisView(0, 0, TILE_DIM, TILE_DIM, peer.getSpace());
//		frame.getContentPane().add(canvas);
		
		// Handle frame closing
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent event) {
				peer.disconnect();
			}
		});
				
		// Prepare to show the frame
		frame.pack();
    }

    private void start() throws Exception {

    	// Start it all up
    	peer.connect();
    	        
        // Show the user interface 
    	frame.setVisible(true);
    }
    
    public static final void main(String[] args) {
    	try {
    		new TetrisView().start();
    	}
    	catch (Exception e) { 
    		e.printStackTrace();
    	}
    }
}
