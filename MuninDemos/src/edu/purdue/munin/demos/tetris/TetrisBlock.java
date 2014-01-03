package edu.purdue.munin.demos.tetris;

import java.awt.Color;

public class TetrisBlock {	
	private int block[];
	private int size;
	private Color color;
	
	public TetrisBlock(int block[], int size, Color color) {
		this.block = block.clone();
		this.size = size;
		this.color = color;
	}
	
	public int minX() { 
		for (int x = 0; x < size; x++) { 
			if (!emptyColumn(x, block, size)) return x;
		}
		return size;
	}
	
	public int maxX() { 
		for (int x = size - 1; x >= 0; x--) {
			if (!emptyColumn(x, block, size)) return x;
		}
		return 0;
	}
	
	public int getCell(int x, int y) {
		return cellAt(block, size, x, y);
	}
	
	public int[] getData() {
		return block;
	}
	
	public int cellAt(int index) { 
		return block[index];
	}
		
	public void normalize() {
		normalize(block, size);
	}
	
	public void rotateLeft() {
		block = rotateLeft(block, size);
	}

	public void rotateRight() {
		block = rotateRight(block, size);
	}
	
	public String toString() {
		StringBuffer sbuf = new StringBuffer();
		for (int y = 0; y < size; y++) {
			sbuf.append("|");
			for (int x = 0; x < size; x++) {
				sbuf.append("" + getCell(x, y));
			}
			sbuf.append("\n");
		}
		return sbuf.toString();
	}
	
	public static int index(int size, int x, int y) {
		return y * size + x;
	}

	public static int cellAt(int block[], int size, int x, int y) {
		return block[index(size, x, y)];
	}

	private static boolean emptyRow(int row, int block[], int size) {
		for (int i = 0; i < size; i++) { 
			if (cellAt(block, size, i, row) != 0) return false; 
		}
		return true;
	}
	
	private static boolean emptyColumn(int col, int block[], int size) {
		for (int i = 0; i < size; i++) { 
			if (cellAt(block, size, col, i) != 0) return false; 
		}
		return true;
	}
	
	public static void shiftUp(int block[], int size) {
		int[] topRow = new int[size];
		for (int x = 0; x < size; x++) {
			topRow[x] = block[index(size, x, 0)];
		}		
		for (int y = 1; y < size; y++) {
			for (int x = 0; x < size; x++) {
				block[index(size, x, y - 1)] = block[index(size, x, y)];
			}
		}
		for (int x = 0; x < size; x++) {
			block[index(size, x, size - 1)] = topRow[x];
		}
	}

	public static void shiftLeft(int block[], int size) {
		int[] leftColumn = new int[size];
		for (int y = 0; y < size; y++) {
			leftColumn[y] = block[index(size, 0, y)];
		}		
		for (int x = 1; x < size; x++) {
			for (int y = 0; y < size; y++) {
				block[index(size, x - 1, y)] = block[index(size, x, y)];
			}
		}
		for (int y = 0; y < size; y++) {
			block[index(size, size - 1, y)] = leftColumn[y];
		}
	}
	
	public static void normalize(int block[], int size) {
		while (emptyRow(0, block, size)) {
			shiftUp(block, size);
		}
		while (emptyColumn(0, block, size)) {
			shiftLeft(block, size);
		}
	}
	
	public static int[] ihm(int size) {
		int block[] = new int [size * size];
		for (int i = 0; i < size; i++) { 
			block[index(size, size - i - 1, i)] = 1;
		}
		return block;
	}
	
	public static int[] rotateLeft(int block[], int size) {
		int newBlock[] = new int [size * size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				newBlock[index(size, size - y - 1, x)] = block[index(size, x, y)];
			}
		}
		return newBlock;
	}

	public static int[] rotateRight(int block[], int size) {
		int newBlock[] = new int [size * size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				newBlock[index(size, y, size - x - 1)] = block[index(size, x, y)];
			}
		}
		return newBlock;
	}
	
	public static int[] flipHorizontal(int block[], int size) {
		int newBlock[] = new int [size * size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				newBlock[index(size, x, y)] = block[index(size, size - x - 1, y)];
			}
		}
		return newBlock;
	}

	public static int[] flipVertical(int block[], int size) {
		int newBlock[] = new int [size * size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				newBlock[index(size, x, y)] = block[index(size, x, size - y - 1)];
			}
		}
		return newBlock;
	}
	
	public Color getColor() { 
		return color;
	}
}