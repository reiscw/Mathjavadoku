import java.util.*;

public class SubPuzzle {

	private char letter;
	private char operation;
	private int value;
	private ArrayList<MathjavadokuLocation> locations;
	
	public SubPuzzle(char letter, int size, char[][] allocation, int[][] solution) {
		this.letter = letter;
		locations = new ArrayList<MathjavadokuLocation>();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (allocation[i][j] == letter) {
					locations.add(new MathjavadokuLocation(i, j, solution[i][j]));
				}
			}
		}
		if (locations.size() == 1) {
			operation = ' ';
		} else {
			int operators = 8;
			// determine if subtraction is possible
			if (locations.size() == 2) {
				operators = 9;
			}
			// determine if division is possible
			if (locations.size() == 2 && (locations.get(0).value % locations.get(1).value == 0 || locations.get(1).value % locations.get(0).value == 0)) {
				operators = 10;
			}
			int random = (int)(Math.random()*operators);
			switch (random) {
				case 0: operation = '+'; break;
				case 1: operation = '*'; break;
				case 2: operation = '*'; break;
				case 3: operation = '*'; break;
				case 4: operation = '*'; break;
				case 5: operation = '*'; break;
				case 6: operation = '*'; break;
				case 7: operation = '*'; break;
				case 8: operation = '-'; break;
				case 9: operation = '/'; break;
			} 
		}
		switch (operation) {
			case ' ':
					value = locations.get(0).value;
			case '+':
				value = 0;
				for (MathjavadokuLocation loc : locations) {
					value = value + loc.value;
				}
				break;
			case '-':
				value = (int)Math.abs(locations.get(0).value - locations.get(1).value);
				break;
			case '*':
				value = 1;
				for (MathjavadokuLocation loc : locations) {
					value = value * loc.value;
				}
				break;
			case '/':
				if (locations.get(0).value % locations.get(1).value == 0) {
					value = locations.get(0).value / locations.get(1).value;
				} else {
					value = locations.get(1).value / locations.get(0).value;
				}
				break;
		}
	}
	
	public ArrayList<MathjavadokuLocation> getLocations() {return locations;}
	
	public String toString() {
		String result = "" + letter + "" + operation + value;
		for (MathjavadokuLocation loc : locations) {
			result = result + "\n" + loc.toString();
		}
		return result;
	}
	
	public String instruction() {
		return "" + operation + "" + value;
	}
	
	public boolean containsLocation(int r, int c) {
		for (MathjavadokuLocation loc : locations) {
			if (loc.getRow() == r && loc.getCol() == c) {
				return true;
			}
		}
		return false;
	}
}
