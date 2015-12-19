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

/**
 * @author Shaylen Pastakia
 *<br>
 * Get an Ingredient from the User, with the addition of buttons for copying fractions
 * <br>
 * Based on Manabu: main.CreateOrOpenPanel
 */
public class IngredientsDialog extends JDialog implements KeyListener{

	private static final long serialVersionUID = -2415235021335302898L;
	private JPanel panel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	JTextField ingredient = new JTextField();
	private JPanel okPanel = new JPanel();
	
	private JLabel label = new JLabel("Please enter the quantity and name of the new Ingredient");
	
	private JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	
	private JButton half = new JButton("\u00BD");
	private JButton third = new JButton("\u2153");
	private JButton twoThird = new JButton("\u2154");
	private JButton quart = new JButton("\u00BC");
	private JButton threeQuart = new JButton("\u00BE");
	private JCheckBox header = new JCheckBox("Sub-Heading");
	
	private String title = "Enter the Quantity and Name of Ingredient";
	
	public String ingredientText = "";
	
	public IngredientsDialog(){
		super();
		
		new BoxLayout(panel, BoxLayout.PAGE_AXIS);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		initialise();
		this.setContentPane((panel));
		setMinimumSize(new Dimension(400, 190));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void initialise(){

		ingredient.setColumns(25);
		ingredient.addKeyListener(this);;
		
		new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS);
		buttonPanel.add(half);
		buttonPanel.add(third);
		buttonPanel.add(twoThird);
		buttonPanel.add(quart);
		buttonPanel.add(threeQuart);
		
		int max = buttonPanel.getComponentCount();
		for (int i = 0; i < max; i++){
			buttonPanel.getComponent(i).setFont(new Font("Times New Roman", Font.PLAIN, 18));
			((JButton) buttonPanel.getComponent(i)).addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ingredient.setText(ingredient.getText() + e.getActionCommand() + " ");
					ingredient.requestFocusInWindow();
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
