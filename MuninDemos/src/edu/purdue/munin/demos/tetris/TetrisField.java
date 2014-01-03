package edu.purdue.munin.demos.tetris;

public class TetrisField {

	private int[] data;
	private int dimX, dimY;
	
	public TetrisField(int dimX, int dimY) {
		this.dimX = dimX;
		this.dimY = dimY;
		this.data = new int [dimX * dimY];
	}
	
	public int cellAt(int x, int y) {
		return cellAt(data, dimX, dimY, x, y);
	}
	
	public static int cellAt(int data[], int dimX, int dimY, int x, int y) {
		return data[index(dimX, dimY, x, y)];
	}
	
	public static int index(int dimX, int dimY, int x, int y) {
		return y * dimX + x;
	}
	
//	public 
	
	public int[] getData() {
		return data;
	}
}
