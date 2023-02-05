public class MathjavadokuLocation implements Comparable {

	protected int row;
	protected int col;
	protected int value;
	
	public MathjavadokuLocation(int row, int col, int value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}
	
	public String toString() {
		return "[" + row + ", " + col + "]:" + value;
	}
	
	public int compareTo(Object o) {
		MathjavadokuLocation other = (MathjavadokuLocation) o;
		if (row < other.row) {
			return -1;
		} else if (row > other.row) {
			return 1;
		} else {
			if (col < other.col) {
				return -1;
			} else {
				return 1;
			}
		}
	}
}
