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
    private JTextField status;	
	private int size;
	private Color currentColor;

	private static final int BUTTON_SIZE = 80;
	private static final int FONT_SIZE = 16;

	public MathjavadokuPanel(int size) {
		this.size = size;
		mathjavadoku = new Mathjavadoku(size);
		buttons = new JButton[size][size];
		currentColor = getRandomColor();
		// adjust size and set layout
        setPreferredSize(new Dimension ((size+1)*BUTTON_SIZE+200, (size+1)*BUTTON_SIZE));
        setLayout(null);
        // setup buttons
		buttonSetup();
		// setup fields
		fieldSetup();
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
		autoSimplifyButton.setBounds((size+1)*BUTTON_SIZE, 50, 150, 20);
		newGameButton.setBounds((size+1)*BUTTON_SIZE, 90, 150, 20);
		quitButton.setBounds((size+1)*BUTTON_SIZE, 130, 150, 20);
		add(autoSimplifyButton);
		add(newGameButton);
		add(quitButton);
		
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
	}
	
	public void fieldSetup() {
		status = new JTextField("Puzzle not yet solved.");
		status.setEditable(false);
		status.setBounds((size+1)*BUTTON_SIZE, 170, 150, 20);
		add(status);
	}
	
	public void click(int row, int col) {
		JTextField candidateEntry = new JTextField();
		Object[] message = {"Enter your solution or candididates: ", candidateEntry};
		int result = JOptionPane.showConfirmDialog(null,  message, "Update Location", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result != JOptionPane.OK_CANCEL_OPTION) {
			buttons[row][col].setText(candidateEntry.getText());
		} 
		if (check()) {
			String successMessage = "Would you like to play again? (Choose OK for yes, Cancel for no)";
			result = JOptionPane.showConfirmDialog(null,  successMessage, "Congratulations!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				reset();
			} else {
				System.exit(0);
			}
		}
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
		status.setText("Congratulations!");
		return true;
	}
	
	public void autoUpdate() {
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
			}
		}
		if (check()) {
			String successMessage = "Would you like to play again? (Choose OK for yes, Cancel for no)";
			int result = JOptionPane.showConfirmDialog(null,  successMessage, "Congratulations!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				reset();
			} else {
				System.exit(0);
			}
		}
	}
	
	public void reset() {
		removeAll();
		mathjavadoku = new Mathjavadoku(size);
		buttons = new JButton[size][size];
		currentColor = Color.CYAN;
		buttonSetup();
		fieldSetup();
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
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Mathjavadoku 1.4 by Christopher Reis");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextField sizeEntry = new JTextField();
		Object[] message = {"Enter your desired puzzle size: ", sizeEntry};
		int result = JOptionPane.showConfirmDialog(null,  message, "Choose your Mathjavadoku size", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		int size = 0;
		if (result != JOptionPane.OK_CANCEL_OPTION) {
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
