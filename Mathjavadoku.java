import java.util.*;

public class Mathjavadoku {

	private int[][] solution;
	private char[][] allocation;
	private char nextChar;
	private int size;
	private ArrayList<SubPuzzle> subpuzzles;

	private final int ONE = 0;
	private final int TWO_HORIZONTAL = 1;
	private final int TWO_VERTICAL = 2;
	private final int THREE_HORIZONTAL = 3;
	private final int THREE_VERTICAL = 4;
	private final int TRIANGLE_UPPER_LEFT = 5;
	private final int TRIANGLE_UPPER_RIGHT = 6;
	private final int TRIANGLE_LOWER_LEFT = 7;
	private final int TRIANGLE_LOWER_RIGHT = 8;
	private final int SQUARE = 9;
	private final int ZIG_ZAG_HORIZONTAL_NEG = 10;
	private final int ZIG_ZAG_HORIZONTAL_POS = 11;
	private final int ZIG_ZAG_VERTICAL_POS = 12;
	private final int ZIG_ZAG_VERTICAL_NEG = 13;

	public Mathjavadoku(int size) {
		nextChar = 'A';
		this.size = size;
		setSolution();
		setAllocation();
		setSubpuzzles();
	}
	
	public void setSolution() {
		solution = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				solution[i][j] = (i + j) % size + 1;
			}
		}
		for (int i = 0; i < 100; i++) {
			int random = (int)(Math.random()*2);
			switch (random) {
				case 0: switchRandomRow(); break;
				case 1: switchRandomColumn(); break;
			}
		}
	}

	public void switchRandomRow() {
		int i = (int) (Math.random()*solution.length);
		int j = (int) (Math.random()*solution.length);
		int[] temp = solution[j];
		solution[j] = solution[i];
		solution[i] = temp;
	}

	public void switchRandomColumn() {
		int i = (int) (Math.random()*solution.length);
		int j = (int) (Math.random()*solution.length);
		int[] temp = new int[solution.length];
		for (int k = 0; k < solution.length; k++) {
			temp[k] = solution[k][j];
			solution[k][j] = solution[k][i];
			solution[k][i] = temp[k];
		}
	}
	
	public void setAllocation() {
		allocation = new char[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				allocation[i][j] = ' ';
			}
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (allocation[i][j] == ' ') {
					// check for types of regions that will work
					ArrayList<Integer> possibles = new ArrayList<Integer>();
					for (int k = 0; k <= 13; k++) {
						if (isValid(i, j, k)) {
							possibles.add(k);
						}
					}
					// determine a random region and apply it
					int random = (int)(Math.random()*possibles.size());
					add(i, j, possibles.get(random));
				} 
			}
		}
	}

	private boolean isValid(int i, int j, int k) {
		boolean result = false;
		switch (k) {
			case ONE: 
				result = true; 
				break;
			case TWO_HORIZONTAL: 
				if (j < size - 1 && allocation[i][j+1] == ' ') {
					result = true;
				}
				break;
			case TWO_VERTICAL:
				if (i < size - 1 && allocation[i+1][j] == ' ') {
					result = true;
				}
				break;
			case THREE_HORIZONTAL:
				if (j < size - 2 && allocation[i][j+1] == ' ' && allocation[i][j+2] == ' ') {
					result = true;
				}
				break;
			case THREE_VERTICAL:
				if (i < size - 2 && allocation[i+1][j] == ' ' && allocation[i+2][j] == ' ') {
					result = true;
				}
				break;
			case TRIANGLE_UPPER_LEFT:
				if (i < size - 1 && j < size - 1 && allocation[i+1][j] == ' ' && allocation[i][j+1] == ' ') {
					result = true;
				}
				break;
			case TRIANGLE_UPPER_RIGHT:
				if (i < size - 1 && j < size - 1 && allocation[i+1][j] == ' ' && allocation[i+1][j+1] == ' ') {
					result = true;
				}
				break;
			case TRIANGLE_LOWER_LEFT:
				if (i < size - 1 && j < size - 1 && allocation[i+1][j] == ' ' && allocation[i+1][j+1] == ' ') {
					result = true;
				}
				break;
			case TRIANGLE_LOWER_RIGHT:
				if (i < size - 1 && j > 0 && allocation[i+1][j-1] == ' ' && allocation[i+1][j] == ' ') {
					result = true;
				}
				break;
			case SQUARE: 
				if (i < size - 1 && j < size - 1 && allocation[i][j+1] == ' ' && allocation[i+1][j] == ' ' && allocation[i+1][j+1] == ' ') {
					result = true;
				}
				break;
			case ZIG_ZAG_HORIZONTAL_NEG:
				if (i < size - 1 && j < size - 2 && allocation[i][j+1] == ' ' && allocation[i+1][j+1] == ' ' && allocation[i+1][j+2] == ' ') {
					result = true;
				}
				break;
			case ZIG_ZAG_HORIZONTAL_POS:
				if (i < size - 1 && j > 0 && j < size - 1 && allocation[i][j+1] == ' ' && allocation[i+1][j-1] == ' ' && allocation[i+1][j] == ' ') {
					result = true;
				}
				break;
			case ZIG_ZAG_VERTICAL_NEG:
				if (i < size - 2 && j < size - 1 &&	allocation[i+1][j] == ' ' && allocation[i+1][j+1] == ' ' && allocation[i+2][j+1] == ' ') {
					result = true;
				}
				break;
			case ZIG_ZAG_VERTICAL_POS: 
				if (i < size - 2 && j > 0 && allocation[i+1][j] == ' ' && allocation[i+1][j-1] == ' ' && allocation[i+2][j-1] == ' ') {
					result = true;
				}
				break;
		}
		return result;
	}
	
	private void add(int i, int j, int random) {
		allocation[i][j] = nextChar;
		switch (random) {
			case ONE: 
				break;
			case TWO_HORIZONTAL:
				allocation[i][j+1] = nextChar;
				break;
			case TWO_VERTICAL:
				allocation[i+1][j] = nextChar;
				break;
			case THREE_HORIZONTAL:
				allocation[i][j+1] = nextChar;
				allocation[i][j+2] = nextChar;
				break;
			case THREE_VERTICAL:
				allocation[i+1][j] = nextChar;
				allocation[i+2][j] = nextChar;
				break;
			case TRIANGLE_UPPER_LEFT: 
				allocation[i+1][j] = nextChar;
				allocation[i][j+1] = nextChar;
				break;
			case TRIANGLE_UPPER_RIGHT:
				allocation[i+1][j] = nextChar;
				allocation[i+1][j+1] = nextChar;
				break;
			case TRIANGLE_LOWER_LEFT:
				allocation[i+1][j] = nextChar;
				allocation[i+1][j+1] = nextChar;
				break;
			case TRIANGLE_LOWER_RIGHT:
				allocation[i+1][j] = nextChar;
				allocation[i+1][j-1] = nextChar;
				break;
			case SQUARE:
				allocation[i+1][j] = nextChar;
				allocation[i][j+1] = nextChar;
				allocation[i+1][j+1] = nextChar;
				break;
			case ZIG_ZAG_HORIZONTAL_NEG:
				allocation[i][j+1] = nextChar;
				allocation[i+1][j+1] = nextChar;
				allocation[i+1][j+2] = nextChar;
				break;
			case ZIG_ZAG_HORIZONTAL_POS:
				allocation[i][j+1] = nextChar;
				allocation[i+1][j-1] = nextChar;
				allocation[i+1][j] = nextChar;
				break;
			case ZIG_ZAG_VERTICAL_NEG:
				allocation[i+1][j] = nextChar;
				allocation[i+1][j+1] = nextChar;
				allocation[i+2][j+1] = nextChar;
				break;
			case ZIG_ZAG_VERTICAL_POS:
				allocation[i+1][j] = nextChar;
				allocation[i+1][j-1] = nextChar; 
				allocation[i+2][j-1] = nextChar;
				break;
		}
		nextChar++;
		if (nextChar == 91) {
			nextChar = (char) 97;
		}
	}

	public void setSubpuzzles() {
		subpuzzles = new ArrayList<SubPuzzle>();
		for (char ch = 'A'; ch < nextChar; ch++) {
			if (ch == 91) ch = (char)97;
			subpuzzles.add(new SubPuzzle(ch, size, allocation, solution));
		}
	}
	
	public int[][] getSolution() {return solution;}
	public char[][] getAllocation() {return allocation;}
	public ArrayList<SubPuzzle> getSubPuzzles() {return subpuzzles;}
	
	public void displaySolution() {
		for (int[] row : solution) {
			for (int element : row) {
				System.out.print(element + " ");
			}
			System.out.println();
		}
	}
	
	public void displayAllocation() {
		for (char[] row : allocation) {
			for (char element : row) {
				System.out.print(element + " ");
			}
			System.out.println();
		}
	}
	
	public void displaySubpuzzles() {
		for (SubPuzzle puzzle : subpuzzles) {
			System.out.println(puzzle);
		}
	}
}
