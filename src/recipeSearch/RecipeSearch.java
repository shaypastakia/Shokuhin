package recipeSearch;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

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
				JPanel ingredientsPanel = new JPanel();
				JPanel tagsPanel = new JPanel();
			JPanel othersPanel = new JPanel();
				JPanel firstRowPanel = new JPanel();
					JPanel coursesPanel = new JPanel();
					JPanel totalTimePanel = new JPanel();
				JPanel secondRowPanel = new JPanel();
					JPanel servingsPanel = new JPanel();
					JPanel ratingsPanel = new JPanel();
				
	//firstPanel fields
	JTextField titleText = new JTextField(80);
	JButton searchButton = new JButton("Search");
	JRadioButton simpleRadio = new JRadioButton("Title Search");
	JRadioButton advancedRadio = new JRadioButton("Advanced Search");
	
	//advancedPanel fields
	JTextField ingredientsText = new JTextField(100);
	JTextField tagsText = new JTextField(100);
	
	JCheckBox breakfastCheckBox = new JCheckBox("Breakfast");
	JCheckBox lunchCheckBox = new JCheckBox("Lunch");
	JCheckBox dinnerCheckBox = new JCheckBox("Dinner");
	JCheckBox dessertCheckBox = new JCheckBox("Dessert");
	JCheckBox snackCheckBox = new JCheckBox("Snack");
	JCheckBox generalCheckBox = new JCheckBox("General");
	
	JSpinner totalTime = new JSpinner();
	JSpinner servings = new JSpinner();
	
	JCheckBox oneCheckBox = new JCheckBox("1");
	JCheckBox twoCheckBox = new JCheckBox("2");
	JCheckBox threeCheckBox = new JCheckBox("3");
	JCheckBox fourCheckBox = new JCheckBox("4");
	JCheckBox fiveCheckBox = new JCheckBox("5");
	
	public RecipeSearch(ShokuhinMain m) {
		super(m, "Recipe Search");
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		createTopGui();
		createBottomGui();
		
		firstPanel.add(titlePanel);
		firstPanel.add(advancedPanel);
		firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.PAGE_AXIS));
		add(firstPanel);
		isAdvanced(false);
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
		textPanel.add(new JLabel("Enter Title here: "));
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
		//Set up the Layout and Border of the Advanced Search section
		advancedPanel.setBorder(BorderFactory.createTitledBorder("Advanced Search"));
		advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.PAGE_AXIS));
		
		//Prepare the first part of the section
		advTextPanel.setLayout(new BoxLayout(advTextPanel, BoxLayout.PAGE_AXIS));
		ingredientsPanel.setLayout(new BoxLayout(ingredientsPanel, BoxLayout.LINE_AXIS));
		tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.LINE_AXIS));
		tagsText.setMaximumSize(new Dimension(9999, 40));
		ingredientsText.setMaximumSize(new Dimension(9999, 40));
		
		ingredientsPanel.add(new JLabel("Enter Ingredients here (Separated with Commas): "));
		ingredientsPanel.add(ingredientsText);
		tagsPanel.add(new JLabel("Enter Tags here (Separated with Commas):            "));
		tagsPanel.add(tagsText);
		
		advTextPanel.add(tagsPanel);
		advTextPanel.add(ingredientsPanel);
		
		//Prepare the second part of this section
		firstRowPanel.setLayout(new BoxLayout(firstRowPanel, BoxLayout.LINE_AXIS));
		secondRowPanel.setLayout(new BoxLayout(secondRowPanel, BoxLayout.LINE_AXIS));
		
		coursesPanel.setMaximumSize(new Dimension(9999, 80));
		coursesPanel.add(breakfastCheckBox);
		coursesPanel.add(lunchCheckBox);
		coursesPanel.add(dinnerCheckBox);
		coursesPanel.add(dessertCheckBox);
		coursesPanel.add(snackCheckBox);
		coursesPanel.add(generalCheckBox);
		
		SpinnerNumberModel timeModel = new SpinnerNumberModel(0, 0, 999, 1);
		totalTime.setModel(timeModel);
		totalTimePanel.setMaximumSize(new Dimension(9999, 80));
		totalTimePanel.add(totalTime);
		
		coursesPanel.setBorder(BorderFactory.createTitledBorder("Course"));
		totalTimePanel.setBorder(BorderFactory.createTitledBorder("Total Time"));
		firstRowPanel.add(coursesPanel);
		firstRowPanel.add(totalTimePanel);
		
		SpinnerNumberModel servingsModel = new SpinnerNumberModel(0, 0, 16, 1);
		servings.setModel(servingsModel);
		servingsPanel.setMaximumSize(new Dimension(9999, 80));
		servingsPanel.add(servings);
		
		ratingsPanel.setMaximumSize(new Dimension(9999, 80));
		ratingsPanel.add(oneCheckBox);
		ratingsPanel.add(twoCheckBox);
		ratingsPanel.add(threeCheckBox);
		ratingsPanel.add(fourCheckBox);
		ratingsPanel.add(fiveCheckBox);
		
		servingsPanel.setBorder(BorderFactory.createTitledBorder("Servings"));
		ratingsPanel.setBorder(BorderFactory.createTitledBorder("Rating"));
		secondRowPanel.add(servingsPanel);
		secondRowPanel.add(ratingsPanel);
		
		advancedPanel.add(advTextPanel);
		advancedPanel.add(firstRowPanel);
		advancedPanel.add(secondRowPanel);
	}
	
	public void isAdvanced(boolean adv){
		if (adv)
				advancedPanel.setVisible(true);
		else
				advancedPanel.setVisible(false);
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
