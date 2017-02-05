package recipeEditor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import generalResources.GuiMethods;
import main.ShokuhinMain;

/**
 * @author Shaylen Pastakia
 *<br>
 * Get an Ingredient from the User, with the addition of buttons for copying fractions
 * <br>
 * Based on Manabu: main.CreateOrOpenPanel
 */
public class IngredientsDialog extends JDialog implements KeyListener{

	private static final long serialVersionUID = -2415235021335302898L;
	private JPanel panel = GuiMethods.getStyledJPanel();//new JPanel();
	private JPanel buttonPanel = new JPanel();
	JTextField ingredient = new JTextField();
	private JPanel okPanel = new JPanel();
	
	private JLabel label = GuiMethods.getStyledJLabel("Enter the quantity and name of the new Ingredient", 14);
	
	private JButton okButton = GuiMethods.getStyledJButton("OK", 20);
	JButton cancelButton = GuiMethods.getStyledJButton("Cancel", 20);
	
	private JButton half = GuiMethods.getStyledJButton("\u00BD", 20);
	private JButton third = GuiMethods.getStyledJButton("\u2153", 20);
	private JButton twoThird = GuiMethods.getStyledJButton("\u2154", 20);
	private JButton quart = GuiMethods.getStyledJButton("\u00BC", 20);
	private JButton threeQuart = GuiMethods.getStyledJButton("\u00BE", 20);
	private JCheckBox header = new JCheckBox("Sub-Heading");
	
	private String title = "Enter the Quantity and Name of Ingredient";
	
	public String ingredientText = "";
	
	public IngredientsDialog(){
		super();
		initialise();
	}
	
	public IngredientsDialog(String edit){
		super();
		
		ingredientText = edit;
		fillDialog();
		initialise();
	}
	
	public void initialise(){
		
		new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setTitle(title);

		ingredient.setColumns(25);
		ingredient.addKeyListener(this);
		
		new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS);
		buttonPanel.add(half);
		buttonPanel.add(third);
		buttonPanel.add(twoThird);
		buttonPanel.add(quart);
		buttonPanel.add(threeQuart);
		buttonPanel.setOpaque(false);
		okPanel.setOpaque(false);
		header.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));
		header.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		
		int max = buttonPanel.getComponentCount();
		for (int i = 0; i < max; i++){
			buttonPanel.getComponent(i).setFont(new Font("Times New Roman", Font.PLAIN, 24));
			((JButton) buttonPanel.getComponent(i)).addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int i = ingredient.getCaretPosition();
					String temp = ingredient.getText();
					temp = temp.substring(0, i) + e.getActionCommand() + " " + temp.substring(i, temp.length());
					ingredient.setText(temp);
					ingredient.requestFocusInWindow();
					ingredient.setCaretPosition(i+2);
				}
			});
		}
		
		buttonPanel.add(header);
		new BoxLayout(okPanel, BoxLayout.LINE_AXIS);
		okPanel.add(cancelButton);
		okPanel.add(okButton);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (header.isSelected()){
					ingredientText = "<html><b>" + ingredient.getText() + "</b></html>";
				} else {
					ingredientText = ingredient.getText();
				}
				dispose();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ingredientText = "";
				dispose();
			}
		});
		
		ingredient.requestFocus();
		panel.add(label);
		panel.add(ingredient);
		panel.add(buttonPanel);
		panel.add(new JSeparator(SwingConstants.VERTICAL));
		panel.add(okPanel);
		
		this.setContentPane((panel));
		setMinimumSize(new Dimension(400, 220));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void fillDialog(){
		if (ingredientText.startsWith("<html><b>")){
			ingredientText = ingredientText.replaceAll("<html><b>|</b></html>", "");
			header.setSelected(true);
		}
		ingredient.setText(ingredientText);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (arg0.getKeyChar() == KeyEvent.VK_ENTER)
			okButton.doClick();
	}
}
