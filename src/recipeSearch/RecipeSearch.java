package recipeSearch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import main.Module;
import main.ShokuhinMain;

public class RecipeSearch extends Module {
	private static final long serialVersionUID = -8627032843621798303L;

	//Do not adjust Indents! Represents Nesting of Panels
	JPanel firstPanel = new JPanel();
		JPanel titlePanel = new JPanel();
			JPanel textPanel = new JPanel();
			JPanel controlPanel = new JPanel();
				JPanel searchPanel = new JPanel();
					JPanel searchSelector = new JPanel();
				JPanel goPanel = new JPanel();
				
				
	JPanel advancedPanel = new JPanel();
		JPanel advTextPanel = new JPanel();
		
	//firstPanel fields
	JTextField titleText = new JTextField(100);
	JButton searchButton = new JButton("Search");
	JRadioButton simpleRadio = new JRadioButton("Title Search");
	JRadioButton advancedRadio = new JRadioButton("Advanced Search");
	
	//advancedPanel fields
	JTextField ingredientsText = new JTextField(100);
	JTextField tagsText = new JTextField(100);
	
	public RecipeSearch(ShokuhinMain m) {
		super(m, "Recipe Search");
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		createTopGui();
		createBottomGui();
		
		firstPanel.add(titlePanel);
		firstPanel.add(advancedPanel);
		firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.PAGE_AXIS));
		add(firstPanel);
	}
	
	public void createTopGui(){
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		
		titlePanel.setBorder(BorderFactory.createTitledBorder("Recipe Search"));
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
		titlePanel.setMaximumSize(new Dimension(9999, 200));
		
		textPanel.setBorder(BorderFactory.createTitledBorder("Recipe Title"));
		
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		
		searchSelector.setBorder(BorderFactory.createTitledBorder("Search Type"));
		searchSelector.setLayout(new BoxLayout(searchSelector, BoxLayout.PAGE_AXIS));
		
		//Add listeners to Radio Buttons
		simpleRadio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isAdvanced(false);
			}
		});
		
		advancedRadio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isAdvanced(true);
			}
		});
		
		//Group Radio Controls together
		ButtonGroup group = new ButtonGroup();
		group.add(simpleRadio);
		group.add(advancedRadio);
		simpleRadio.setSelected(true);
		
		//Format the Search Button
		searchButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		
		//Add all panels together
		goPanel.add(searchButton);
		textPanel.add(titleText);
		titlePanel.add(textPanel);
		titlePanel.add(controlPanel);
		controlPanel.add(searchPanel);
		controlPanel.add(goPanel);
		searchPanel.add(searchSelector);
		searchSelector.add(simpleRadio);
		searchSelector.add(advancedRadio);
	}
	
	public void createBottomGui(){
		advancedPanel.setBorder(BorderFactory.createTitledBorder("Advanced Search"));
		advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.PAGE_AXIS));
		
		
		
		advancedPanel.add(advTextPanel);
	}
	
	public void isAdvanced(boolean adv){
		if (adv)
			for (Component c : advancedPanel.getComponents())
				c.setEnabled(false);
		else
			for (Component c : advancedPanel.getComponents())
				c.setEnabled(true);
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Search");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

}
