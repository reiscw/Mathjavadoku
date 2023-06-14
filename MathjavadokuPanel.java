import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.font.*;

public class MathjavadokuPanel extends JPanel {

	private Mathjavadoku mathjavadoku;
	private JButton[][] buttons;
	private JButton autoSimplifyButton;
	private JButton newGameButton;
    private JButton quitButton;
    private JButton undoButton;
    private JLabel filledProgress;
    private JLabel singledProgress;
    
	private int size;
	private Color currentColor;
	private ArrayList<State> states;
	private boolean lastActionUndo;

	private static final int BUTTON_SIZE = 80;
	private static final int FONT_SIZE = 16;

	public MathjavadokuPanel(int size) {
		this.size = size;
		lastActionUndo = false;
		mathjavadoku = new Mathjavadoku(size);
		buttons = new JButton[size][size];
		states = new ArrayList<State>();
		currentColor = getRandomColor();
		// adjust size and set layout
        setPreferredSize(new Dimension ((size+1)*BUTTON_SIZE+200, (size+1)*BUTTON_SIZE));
        setLayout(null);
        // setup buttons
		buttonSetup();
	}
	
	public void buttonSetup() {
		for (SubPuzzle subpuzzle : mathjavadoku.getSubPuzzles()) {
			ArrayList<MathjavadokuLocation> locations = subpuzzle.getLocations();
			Collections.sort(locations);
			JButton[] subpuzzleButtons = new JButton[locations.size()];
			for (int i = 0; i < subpuzzleButtons.length; i++) {
				subpuzzleButtons[i] = new JButton();
				// handle the first tile separately
				if (i == 0) {
					subpuzzleButtons[i].setLayout(new BorderLayout());
					JLabel label = new JLabel(subpuzzle.instruction());
					subpuzzleButtons[i].add(BorderLayout.NORTH,label);
				}
				subpuzzleButtons[i].setBackground(currentColor);
				subpuzzleButtons[i].setOpaque(true);
				subpuzzleButtons[i].setBorderPainted(false);
				subpuzzleButtons[i].setFont(new Font("Arial", Font.BOLD, FONT_SIZE));  
				int row = locations.get(i).row;
				int col = locations.get(i).col;
				subpuzzleButtons[i].addActionListener(e -> {
					try {
						click(row, col);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				});    
				buttons[row][col] = subpuzzleButtons[i];
			}
			currentColor = getRandomColor();
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				buttons[i][j].setBounds(50 + j*BUTTON_SIZE, 50+i*BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
				add(buttons[i][j]);
			}
		}
		autoSimplifyButton = new JButton("Simplify");
		newGameButton = new JButton("New Game");
		quitButton = new JButton("Quit");
		undoButton = new JButton("Undo");
		filledProgress = new JLabel("Filled: 0/" + (size*size));
		singledProgress = new JLabel("Singled: 0/" + (size*size));
		autoSimplifyButton.setBounds((size+1)*BUTTON_SIZE, 50, 150, 20);
		newGameButton.setBounds((size+1)*BUTTON_SIZE, 90, 150, 20);
		quitButton.setBounds((size+1)*BUTTON_SIZE, 130, 150, 20);
		undoButton.setBounds((size+1)*BUTTON_SIZE, 170, 150, 20);
		filledProgress.setBounds((size+1)*BUTTON_SIZE, 210, 150, 20);
		singledProgress.setBounds((size+1)*BUTTON_SIZE, 250, 150, 20);
		add(autoSimplifyButton);
		add(newGameButton);
		add(quitButton);
		add(undoButton);
		add(filledProgress);
		add(singledProgress);
		addState();
		
		autoSimplifyButton.addActionListener(e -> {
			try {
				autoUpdate();
			} catch (Exception exc) {
				exc.printStackTrace();
			}			
		});
		
		newGameButton.addActionListener(e -> {
			try {
				reset();
			} catch (Exception exc) {
				exc.printStackTrace();
			}			
		});
		
		quitButton.addActionListener(e -> {
			try {
				System.exit(0);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
		
		undoButton.addActionListener(e -> {
			try {
				undo();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}
	
	public void click(int row, int col) {
		// check if the current square is empty for autofill
		boolean empty = buttons[row][col].getText().length() == 0;
		// check if the current square is a single
		// locate the proper subpuzzle
		ArrayList<SubPuzzle> subPuzzles = mathjavadoku.getSubPuzzles();
		int i = 0;
		for (i = 0; i < subPuzzles.size(); i++ ) {
			if (subPuzzles.get(i).containsLocation(row, col)) {
				break;
			}
		}		
		// check if the subpuzzle is a single
		boolean single = subPuzzles.get(i).getLocations().size() == 1;
		lastActionUndo = false;
		JTextField candidateEntry = new JTextField();
		Object[] message = {"Enter your solution or candididates: ", candidateEntry};
		int result = JOptionPane.showConfirmDialog(null,  message, "Update Location", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			buttons[row][col].setText(candidateEntry.getText());
			addState();
			if (empty && !single) {
				String speedUpMessage = "Do you want to apply this candidate list to the rest of the subupuzzle?";
				result = JOptionPane.showConfirmDialog(null,  speedUpMessage, "Fill the rest of the subpuzzle?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					// locate the proper subpuzzle
					for (i = 0; i < subPuzzles.size(); i++ ) {
						if (subPuzzles.get(i).containsLocation(row, col)) {
							break;
						}
					}
					// update the locations of the proper subpuzzle
					for (MathjavadokuLocation loc : subPuzzles.get(i).getLocations()) {
						int r = loc.getRow();
						int c = loc.getCol();
						if (buttons[r][c].getText().length() == 0) {
							buttons[r][c].setText(candidateEntry.getText());
						}
					}
				}
			}
		}
		updateProgress(); 
		if (check()) {
			String successMessage = "Would you like to play again?";
			result = JOptionPane.showConfirmDialog(null,  successMessage, "Congratulations!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				reset();
			} else {
				System.exit(0);
			}
		}
	}
	
	public void addState() {
		String[][] stateArray = new String[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				stateArray[i][j] = buttons[i][j].getText();
			}
		}
		states.add(new State(stateArray));
	}
	
	public boolean check() {
		// check for all positions having length 1
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (buttons[i][j].getText().length() != 1) {
					return false;
				}
			}
		}
		// check for all rows and all columns having all numbers
		for (int i = 0; i < size; i++) {
			ArrayList<String> row = new ArrayList<String>();
			ArrayList<String> col = new ArrayList<String>();
			for (int j = 0; j < size; j++) {
				row.add("" + buttons[i][j].getText());
				col.add("" + buttons[j][i].getText());
			}
			for (int k = 0; k < size; k++) {
				if (!row.contains("" + (k+1)) || !col.contains("" + (k+1))) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void autoUpdate() {
		lastActionUndo = false;
		boolean finished = false;
		while (!finished) {
			// check columns
			boolean columnChange = false;
			for (int col = 0; col < size; col++) {
				ArrayList<String> singles = new ArrayList<String>();
				for (int row = 0; row < size; row++) {
					if (buttons[row][col].getText().length() == 1) {
						singles.add(buttons[row][col].getText());
					}
				}
				for (int row = 0; row < size; row++) {
					for (String single : singles) {
						String text = buttons[row][col].getText();
						if (text.equals(single)) continue;
						int loc = text.indexOf(single);
						if (loc != -1) {
							text = text.replace(single, "");
							buttons[row][col].setText(text);
							columnChange = true;
						}
					}
				}
			}
			// check rows
			boolean rowChange = false;
			for (int row = 0; row < size; row++) {
				ArrayList<String> singles = new ArrayList<String>();
				for (int col = 0; col < size; col++) {
					if (buttons[row][col].getText().length() == 1) {
						singles.add(buttons[row][col].getText());
					}
				}
				for (int col = 0; col < size; col++) {
					for (String single : singles) {
						String text = buttons[row][col].getText();
						if (text.equals(single)) continue;
						int loc = text.indexOf(single);
						if (loc != -1) {
							text = text.replace(single, "");
							buttons[row][col].setText(text);
							rowChange = true;
						}
					}
				}
			}
			if (!columnChange && !rowChange) {
				finished = true;
				addState();
			}
		}
		updateProgress();
		if (check()) {
			String successMessage = "Would you like to play again?";
			int result = JOptionPane.showConfirmDialog(null,  successMessage, "Congratulations!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.YES_OPTION) {
				reset();
			} else {
				System.exit(0);
			}
		}
	}
	
	public void undo() {
		if ((!lastActionUndo && states.size() > 1) || (states.size() > 0)) {
			if (!lastActionUndo) {
				states.remove(states.size() - 1);
			}
			State removed = states.remove(states.size() - 1);
			String[][] array = removed.getState();
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					buttons[i][j].setText(array[i][j]);
				}
			}
			setVisible(true);
			revalidate();
			repaint();        
		}
		updateProgress();
		lastActionUndo = true;
	}
	
	public void reset() {
		lastActionUndo = false;
		states = new ArrayList<State>();
		removeAll();
		mathjavadoku = new Mathjavadoku(size);
		buttons = new JButton[size][size];
		currentColor = Color.CYAN;
		filledProgress.setText("Filled: 0/" + (size*size));
		singledProgress.setText("Singled: 0/" + (size*size));
		buttonSetup();
		setVisible(true);
		revalidate();
		repaint();        
	}
	
	public Color getRandomColor() {
		int r = (int)(Math.random()*156)+100;
		int g = (int)(Math.random()*156)+100;
		int b = (int)(Math.random()*156)+100;
		return new Color(r, g, b);
	}
	
	public void updateProgress() {
		int single = 0;
		int filled = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (buttons[i][j].getText().length() == 1) {
					single++;
					filled++;
				} else if (buttons[i][j].getText().length() > 1) {
					filled++;
				}
			}
		}
		filledProgress.setText("Filled: " + filled + "/" + (size*size));
		singledProgress.setText("Singled: " + single + "/" + (size*size));
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Mathjavadoku 2.0 by Christopher Reis");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField sizeEntry = new JTextField();
		Object[] message = {"Enter your desired puzzle size: ", sizeEntry};
		int result = JOptionPane.showConfirmDialog(null,  message, "Choose your Mathjavadoku size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		int size = 0;
		if (result == JOptionPane.OK_OPTION) {
			try {
				size = Integer.parseInt(sizeEntry.getText());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (size < 3 || size > 9) {
				System.exit(0);
			}
			MathjavadokuPanel panel = new MathjavadokuPanel(size);
			frame.getContentPane().add(panel);
			frame.pack();
			frame.setVisible(true);
		}  else {
			System.exit(0);
		}
    }
}
