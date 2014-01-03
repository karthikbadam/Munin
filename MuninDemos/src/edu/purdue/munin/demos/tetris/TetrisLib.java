package edu.purdue.munin.demos.tetris;

import java.awt.Color;
import java.util.Random;

public class TetrisLib {

	private static final int 	blockSize = 4;
	private static final Color 	blockColors[] = { Color.cyan, Color.blue, Color.orange, Color.magenta, Color.red, Color.green, Color.yellow };
	public static final int 	blockData[][] = {
		{
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 0, 0,
			0, 1, 0, 0
		},
		{
			0, 2, 2, 0,
			0, 2, 0, 0,
			0, 2, 0, 0,
			0, 0, 0, 0			
		},
		{
			0, 3, 3, 0,
			0, 0, 3, 0,
			0, 0, 3, 0,
			0, 0, 0, 0			
		},
		{
			0, 0, 4, 0,
			0, 4, 4, 0,
			0, 0, 4, 0,
			0, 0, 0, 0			
		},
		{
			0, 0, 5, 0,
			0, 5, 5, 0,
			0, 5, 0, 0,
			0, 0, 0, 0			
		},
		{
			0, 6, 0, 0,
			0, 6, 6, 0,
			0, 0, 6, 0,
			0, 0, 0, 0			
		},
		{
			0, 0, 0, 0,
			0, 7, 7, 0,
			0, 7, 7, 0,
			0, 0, 0, 0			
		},	
	};
	
	private static Random rnd = new Random();

	public static int getBlockCount() { 
		return blockData.length;
	}
	
	public static TetrisBlock getBlock(int block) {
		return new TetrisBlock(blockData[block], blockSize, blockColors[block]);
	}
	
	public static TetrisBlock getRandomBlock() {
		return getBlock(rnd.nextInt(getBlockCount()));
	}
}
