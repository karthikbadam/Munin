package edu.purdue.munin.demos.tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.purdue.munin.data.SharedArray;
import edu.purdue.munin.event.SharedObjectChangeListener;
import edu.purdue.munin.event.SharedSpaceObjectListener;
import edu.purdue.munin.peer.Peer;
import edu.purdue.munin.space.SharedObject;

public class TetrisControl implements SharedObjectChangeListener, SharedSpaceObjectListener {

	public static final String 		CHANNEL_NAME 		= "MuninTetris";
    
	private static final int 		FIELD_WIDTH 		= 16;
	private static final int 		FIELD_HEIGHT 		= 16;

	private static final int 		BLOCK_SIZE 			= 4;
	
	private class TetrisLogic implements Runnable {
		private int blockX = 0, blockY = FIELD_HEIGHT;
		private TetrisBlock currBlock;
//		private TetrisField field;
		public void run() {
			try {
				while (true) {
					synchronized (this) {
						
						// First make sure that we really should update
						if (running == false) {
							wait();
						}
						
						// Do we need to spawn a new block?
						if (blockY >= FIELD_HEIGHT) {
							
							// Initialize the block
							currBlock = TetrisLib.getRandomBlock();
							blockX = FIELD_WIDTH / 2;
							blockY = - BLOCK_SIZE;

							sobj.putInt("blockX", blockX);
							
							// Update block
							updateBlock();
							sobj.put("blockColor", currBlock.getColor());
						}
						
						// Update one step
						blockY++;
						sobj.putInt("blockY", blockY);
						
						// Commit changes 
						sobj.commit();

						// Sleep for a while
						wait(1000);
					}
				}
			}
			catch (InterruptedException e) {}
		}
		
		private void updateBlock() {
			SharedArray block = sobj.getArray("block");
			for (int i = 0; i < BLOCK_SIZE * BLOCK_SIZE; i++) {
				block.set(i, currBlock.cellAt(i));
			}
		}
		
		public synchronized void move(int delta) {
			if (!running) return;
			blockX += delta;
			if (blockX < 0) blockX = 0;
			if (blockX > FIELD_WIDTH - BLOCK_SIZE) blockX = FIELD_WIDTH - BLOCK_SIZE;
			sobj.putInt("blockX", blockX);
			sobj.commit();
		}
		
		public synchronized void rotateLeft() {
			if (!running) return;
			currBlock.rotateLeft();
			updateBlock();
			sobj.commit();
		}

		public synchronized void rotateRight() {
			if (!running) return;
			currBlock.rotateRight();
			updateBlock();
			sobj.commit();
		}
	}

	private JFrame frame;
    private Peer peer = new Peer(CHANNEL_NAME);

	private boolean running = false;
	private SharedObject sobj = null;
	private TetrisLogic logic = new TetrisLogic();
	
    public TetrisControl() throws Exception { 

    	// Create the frame
		frame = new JFrame("Munin Tetris");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(300, 64));
		
		// Create the canvas
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));
		
		JButton gameButton = new JButton("Play");
		gameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				if (running) {
					pause();
				}
				else {
					play();
				}
				button.setText(running ? "Pause" : "Play");
			}
		});
		controlPanel.add(gameButton);
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pause();
				System.err.println("Game reset.");
			}			
		});
		controlPanel.add(resetButton);
		
		JTextField keyPanel = new JTextField();
		keyPanel.setEditable(false);
		keyPanel.setPreferredSize(new Dimension(100, 32));
		keyPanel.setFocusable(true);
		keyPanel.setBackground(Color.white);
		keyPanel.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				System.err.println("key");
				switch (e.getKeyCode()) {
				
				case KeyEvent.VK_LEFT:
					logic.move(-1);
					System.err.println("left");
					break;

				case KeyEvent.VK_RIGHT:
					logic.move(1);
					System.err.println("right");
					break;

				case KeyEvent.VK_UP:
					logic.rotateLeft();
					System.err.println("up");
					break;

				case KeyEvent.VK_DOWN:
					logic.rotateRight();
					System.err.println("down");
					break;

				case KeyEvent.VK_SPACE:
					System.err.println("space");
					break;
					
				default: break;
				}
			}
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
		});
		controlPanel.add(keyPanel);
		
		// Prepare to show the frame
		frame.getContentPane().add(controlPanel);
		frame.pack();
		
		// Add input handlers to the frame
		
		// Handle frame closing
		frame.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent event) {
				peer.disconnect();
			}
		});
		
		// Make sure we listen to space events
		peer.getSpace().addObjectListener(this);
		
		// Run the logic
		new Thread(logic).start();
    }
    
    private void pause() {
    	if (!running) return;
    	synchronized (logic) {
    		logic.notify();
	    	running = false;    	
	    	System.err.println("Game paused.");
    	}
    }
    
    private void play() {
    	if (running) return;
    	synchronized (logic) {
    		logic.notify();
        	running = true;
        	System.err.println("Game unpaused.");
    	}
    }

    private void start() throws Exception {

    	// Start it all up
    	peer.connect();
    	
    	// Create the data object
    	peer.getSpace().createObject();
    	        
        // Show the user interface 
    	frame.setVisible(true);
    }
    
	public void objectChanged(SharedObject so, Collection<String> keys) {
//		System.err.println("object changed:");
//		SharedObject obj = peer.getSpace().getObject(id);
//		for (String key : keys) {
//			System.err.println(" - " + key + " : " + obj.get(key));
//		}
	}

	public void objectCreated(SharedObject so) { //SpaceEvent event) {

		sobj = so; //peer.getSpace().getObject(event.getId());
		sobj.addChangeListener(this);
		
		SharedArray field = new SharedArray();
		field.fill(FIELD_WIDTH * FIELD_HEIGHT, new Integer(0));
		sobj.put("field", field);

		sobj.putInt("fieldWidth", FIELD_WIDTH);
		sobj.putInt("fieldHeight", FIELD_HEIGHT);

		SharedArray block = new SharedArray();
		block.fill(BLOCK_SIZE * BLOCK_SIZE, new Integer(0));
		sobj.put("block", block);
		
		sobj.putInt("blockX", -1);
		sobj.putInt("blockY", -1);

		sobj.put("blockColor", Color.black);
		sobj.put("blockSize", BLOCK_SIZE);
		
		sobj.commit();
	}

	public void objectDeleted(SharedObject so) {}

	public static final void main(String[] args) {
    	try {
    		new TetrisControl().start();
    	}
    	catch (Exception e) { 
    		e.printStackTrace();
    	}
    }
}
