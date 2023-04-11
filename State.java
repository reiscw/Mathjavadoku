public class State {
	
	private String[][] state;
	
	public State(String[][] state) {
		this.state = state;
	}
	
	public String[][] getState() {
		return state;
	}
	
	public String toString() {
		String result = "";
		for (String[] row : state) {
			for (String element : row) {
				if (element != "") {
					result = result + " " + element;
				}
			}
		}
		return result;
	}
	
}
