import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.font.*;

public class MathjavadokuPanel extends JPanel {

	private Mathjavadoku mathjavadoku;
	private JButton[][] buttons;
	private JButton newGameButton;
    private JButton quitButton;
    private JTextField status;
//  private JTextField errorDetected;
	
	private int size;
	private Color currentColor;
	
	public MathjavadokuPanel(int size) {
		this.size = size;
		mathjavadoku = new Mathjavadoku(size);
		buttons = new JButton[size][size];
		currentColor = Color.CYAN;

		// adjust size and set layout
        setPreferredSize(new Dimension ((size+1)*100+200, (size+1)*100));
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
				subpuzzleButtons[i].setFont(new Font("Arial", Font.BOLD, 20));  
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
			currentColor = nextColor();
		}
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				buttons[i][j].setBounds(50 + j*100, 50+i*100, 100, 100);
				add(buttons[i][j]);
			}
		}
		
		newGameButton = new JButton("New Game");
		quitButton = new JButton("Quit");
		newGameButton.setBounds((size+1)*100, 50, 150, 20);
		quitButton.setBounds((size+1)*100, 90, 150, 20);
		add(newGameButton);
		add(quitButton);
		
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
		status.setBounds((size+1)*100, 130, 150, 20);
		add(status);
/*		errorDetected = new JTextField("No errors detected.");
		errorDetected.setEditable(false);
		errorDetected.setBounds((size+1)*100, 170, 150, 20);
		add(errorDetected);*/
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
/*		// check if any elements are incorrect
		boolean noIncorrects = true;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				String cellText = buttons[i][j].getText();
				String solution = "" + mathjavadoku.getSolution()[i][j];
				if (cellText.length() == 1 && !cellText.equals(solution)) {
					noIncorrects = false;
				}
			}
		}
		if (noIncorrects) {
			errorDetected.setText("No errors detected.");
		} else {
			errorDetected.setText("Possible error detected!");
		}*/
		
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
	
	public Color nextColor() {
		if (currentColor.equals(Color.CYAN)) {
			return Color.GREEN;
		} else if (currentColor.equals(Color.GREEN)) {
			return Color.LIGHT_GRAY;
		} else if (currentColor.equals(Color.LIGHT_GRAY)) {
			return Color.MAGENTA;
		} else if (currentColor.equals(Color.MAGENTA)) {
			return Color.ORANGE;
		} else if (currentColor.equals(Color.ORANGE)) {
			return Color.PINK;
		} else if (currentColor.equals(Color.PINK)) {
			return Color.RED;
		} else if (currentColor.equals(Color.RED)) {
			return Color.WHITE;
		} else if (currentColor.equals(Color.WHITE)) {
			return Color.YELLOW;
		} else {
			return Color.CYAN;
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Mathjavadoku 1.1 by Christopher Reis");
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
