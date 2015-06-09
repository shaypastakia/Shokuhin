package recipeSearch;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;
import recipeEditor.RecipeEditor;
import recipeViewer.RecipeViewer;

public class RecipeSearch extends Module implements ActionListener{
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
	JPanel secondPanel = new JPanel();
		JScrollPane searchResultPanel = new JScrollPane();
		JPanel searchControlPanel = new JPanel();
			JPanel searchButtonPanel = new JPanel();
//			JPanel searchImagePanel = new JPanel();
				
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
	
	//Second Panel fields
	DefaultListModel<String> listModel = new DefaultListModel<String>();
	JList<String> searchResultList = new JList<String>(listModel);
	JButton searchBackButton = new JButton("Return to Search");
	JButton searchOpenButton = new JButton("Open Selected Recipes in Recipe Viewer");
	JButton searchEditButton = new JButton("Edit Selected Recipes in Recipe Editor");
	
	
	public RecipeSearch(ShokuhinMain m) {
		super(m, "Recipe Search");
		
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		createTopGui();
		createBottomGui();
		
		firstPanel.add(titlePanel);
		firstPanel.add(advancedPanel);
		firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.PAGE_AXIS));
		add(firstPanel);
		showAdvanced(false);
	}
	
	public void createTopGui(){
		
		//Format JPanels
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		
		titlePanel.setBorder(BorderFactory.createTitledBorder("Recipe Search"));
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
		titlePanel.setMaximumSize(new Dimension(9999, 200));
		
		textPanel.setBorder(BorderFactory.createTitledBorder("Recipe Title"));
		
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		
		searchSelector.setBorder(BorderFactory.createTitledBorder("Search Type"));
		searchSelector.setLayout(new BoxLayout(searchSelector, BoxLayout.PAGE_AXIS));
		
		//Add listeners to Controls
		simpleRadio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAdvanced(false);
			}
		});
		
		advancedRadio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showAdvanced(true);
			}
		});
		
		titleText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
					actionPerformed(null);
			}
		});
		
		//Group Radio Controls together
		ButtonGroup group = new ButtonGroup();
		group.add(simpleRadio);
		group.add(advancedRadio);
		simpleRadio.setSelected(true);
		
		//Format the Search Button
		searchButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
		searchButton.addActionListener(this);
		
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
		coursesPanel.add(new JLabel("Select allowed Courses. Leave blank for any course: "));
		coursesPanel.add(breakfastCheckBox);
		coursesPanel.add(lunchCheckBox);
		coursesPanel.add(dinnerCheckBox);
		coursesPanel.add(dessertCheckBox);
		coursesPanel.add(snackCheckBox);
		coursesPanel.add(generalCheckBox);
		
		SpinnerNumberModel timeModel = new SpinnerNumberModel(0, 0, 999, 1);
		totalTime.setModel(timeModel);
		totalTimePanel.setMaximumSize(new Dimension(9999, 80));
		totalTimePanel.add(new JLabel("Enter a maximum cooking Time. Select 0 for any time: "));
		totalTimePanel.add(totalTime);
		
		coursesPanel.setBorder(BorderFactory.createTitledBorder("Course"));
		totalTimePanel.setBorder(BorderFactory.createTitledBorder("Total Time"));
		firstRowPanel.add(coursesPanel);
		firstRowPanel.add(totalTimePanel);
		
		SpinnerNumberModel servingsModel = new SpinnerNumberModel(0, 0, 16, 1);
		servings.setModel(servingsModel);
		servingsPanel.setMaximumSize(new Dimension(9999, 80));
		servingsPanel.add(new JLabel("Select a minimum number of Servings. Select 0 for any serving: "));
		servingsPanel.add(servings);
		
		ratingsPanel.setMaximumSize(new Dimension(9999, 80));
		ratingsPanel.add(new JLabel("Select allowed Ratings. Leave blank for any rating: "));
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
	
	/**
	 * Generate the Interface for the Search Results
	 */
	private void createSecondGui(){
		searchResultPanel.setViewportView(searchResultList);
		
		searchControlPanel.setLayout(new BoxLayout(searchControlPanel, BoxLayout.PAGE_AXIS));
		searchControlPanel.add(searchBackButton);
		searchControlPanel.add(searchOpenButton);
		searchControlPanel.add(searchEditButton);
		
		secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.LINE_AXIS));
		
		searchResultPanel.setBorder(BorderFactory.createTitledBorder("Results"));
		searchControlPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		
		searchBackButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		searchEditButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		searchOpenButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		
		//Add Action Listeners to buttons on second GUI
		searchBackButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listModel.clear();
				remove(secondPanel);
				add(firstPanel);
				repaint();
			}
		});
		
		searchOpenButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (searchResultList.isSelectionEmpty()){
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				} else {
					List<String> selection = searchResultList.getSelectedValuesList();
					for (String s : selection){
						getPar().openTab(new RecipeViewer(getPar(), RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec"))));
					}
				}
			}
		});
		
		searchEditButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (searchResultList.isSelectionEmpty()){
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				} else {
					List<String> selection = searchResultList.getSelectedValuesList();
					for (String s : selection){
						getPar().openTab(new RecipeEditor(getPar(), RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec"))));
					}
				}
			}
		});
		
		secondPanel.add(searchResultPanel);
		secondPanel.add(searchControlPanel);
	}
	
	/**
	 * Sets the visibility of the Advanced Criteria
	 * @param adv True to show Advanced criteria, False to hide
	 */
	public void showAdvanced(boolean adv){
		if (adv)
				advancedPanel.setVisible(true);
		else
				advancedPanel.setVisible(false);
	}

	/**
	 * No cleanup to perform. Always permit closing
	 */
	@Override
	public boolean close() {
		return true;
	}

	/**
	 * No functions. Return empty Menu
	 */
	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Search");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

	/**
	 * Action Listener for Search Button
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		remove(firstPanel);
		createSecondGui();
		add(secondPanel);
		
		if (simpleRadio.isSelected())
			simpleSearch();
		else
			advancedSearch();
		
		paintAll(getGraphics());
	}

	/**
	 * Search by Title only
	 */
	public void simpleSearch(){
		String title = titleText.getText().toLowerCase();
		boolean succeeded = false;
		
		for (String s : RecipeMethods.getRecipeFileNames()){
			if (s.toLowerCase().contains(title)){
				listModel.addElement(s);
				if (!succeeded)
					succeeded = true;
			}
		}
		
		if (!succeeded){
			failed("Simple Search");
		}
		
	}
	
	/**
	 * Search using any combination of Advanced Criteria
	 * <br />
	 * Calls a series of methods to traverse through search criteria
	 */
	private void advancedSearch(){
		//Perform a simple title search
		ArrayList<Recipe> resultSet = advancedTitleSearch();
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Title Search");
			return;
		}
		
		//If the user specified tags, perform a tag search, otherwise skip it
		if (!tagsText.getText().equals("")){
			resultSet = advancedTagSearch(resultSet);
		}
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Tag Search");
			return;
		}
		
		//If the user specified ingredients, perform an ingredient search, otherwise skip it
		if (!ingredientsText.getText().equals("")){
			resultSet = advancedIngredientSearch(resultSet);
		}
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Ingredient Search");
			return;
		}
		
		//Filter down results based on Courses
		resultSet = advancedCourseSearch(resultSet);
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Course Search");
			return;
		}
		
		//Filter down results based on Total Time
		resultSet = advancedTotalTimeSearch(resultSet);
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Total Time Search");
			return;
		}
		
		//Filter down results based on Servings
		resultSet = advancedServingsSearch(resultSet);
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Servings Search");
			return;
		}
		
		//Filter down results based on Ratings
		resultSet = advancedRatingsSearch(resultSet);
		//If there are no results, perform fail procedure and do not continue
		if (resultSet.size() == 0){
			failed("Ratings Search");
			return;
		}
		
		for (Recipe r : resultSet){
			listModel.addElement(r.getTitle());
		}
	}
	
	/**
	 * Initial step for the Advanced Search. Search filenames, then produce Recipes from the results
	 * @return An ArrayList of Recipes that pass a simple title search
	 */
	private ArrayList<Recipe> advancedTitleSearch(){
		String title = titleText.getText().toLowerCase();
		ArrayList<String> matches = new ArrayList<String>();
		ArrayList<Recipe> recipeMatches = new ArrayList<Recipe>();
		
		//Get the name of all Recipes matching the Title Search
		for (String s : RecipeMethods.getRecipeFileNames()){
			if (s.toLowerCase().contains(title)){
				matches.add(s);
			}
		}
		
		//Load in all matched Recipes as Objects
		for (String s : matches){
			recipeMatches.add(RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec")));
		}
		return recipeMatches;
	}
	
	/**
	 * Second step for Advanced Search. Check user-specified tags to find matches.
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by tags
	 */
	private ArrayList<Recipe> advancedTagSearch(ArrayList<Recipe> recipes){
		ArrayList<Recipe> filtered = new ArrayList<Recipe>();
		
		//Filter out any Recipes that don't match the tags
		String[] userTags = tagsText.getText().split(",");
		for (int i = 0; i < userTags.length; i++){
			userTags[i] = userTags[i].toLowerCase();
		} //Have the user's search tags in lower case
		
		//Format all Recipe's tags as above with user search tags
		for (Recipe r : recipes){
			String[] recipeTags = (String[]) r.getTags().toArray(new String[r.getTags().size()]);
			for (int i = 0; i < recipeTags.length; i++){
				recipeTags[i] = recipeTags[i].toLowerCase();
			}
			r.setTags(new ArrayList<String>(Arrays.asList(recipeTags)));
		} //Have the User Tags and Recipe tags formatted
		
		//Add a recipe if it contains any of the tags
		for (Recipe r : recipes){
			breakloop:
			for (String s : userTags){
				for (String s2 : r.getTags()){
					if (s2.contains(s)){
						filtered.add(r);
						break breakloop;
					}
				}
			}
		} //Have the Recipes that pass the given Tags
		
		return filtered;
	}
	
	/**
	 * Third step for Advanced Search. Check user-specified ingredients to find matches.
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by ingredients
	 */
	private ArrayList<Recipe> advancedIngredientSearch(ArrayList<Recipe> recipes){
		ArrayList<Recipe> filtered = new ArrayList<Recipe>();
		
		String[] userIngredients = ingredientsText.getText().split(",");
		for (int i = 0; i < userIngredients.length; i++){
			userIngredients[i] = userIngredients[i].toLowerCase();
		} //Have the user's search Ingredients in lower case
		
		//Format all Recipe's Ingredients as above with user search Ingredients
		for (Recipe r : recipes){
			String[] recipeIngredients= (String[]) r.getIngredients().toArray(new String[r.getIngredients().size()]);
			for (int i = 0; i < recipeIngredients.length; i++){
				recipeIngredients[i] = recipeIngredients[i].toLowerCase();
			}
			r.setIngredients(new ArrayList<String>(Arrays.asList(recipeIngredients)));
		} //Have the User Ingredients and Recipe Ingredients formatted
		
		//Add a recipe if it contains any of the Ingredients
		for (Recipe r : recipes){
			breakloop:
			for (String s : userIngredients){
				for (String s2 : r.getIngredients()){
					if (s2.contains(s)){
						filtered.add(r);
						break breakloop;
					}
				}
			}
		} //Have the Recipes that pass the given Ingredients
		return filtered;
	}
	
	/**
	 * Fourth step for Advanced Search. Filter down Recipes to selected Courses
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by course
	 */
	private ArrayList<Recipe> advancedCourseSearch(ArrayList<Recipe> recipes){
		ArrayList<Integer> courses = new ArrayList<Integer>();
		if (breakfastCheckBox.isSelected())
			courses.add(Recipe.BREAKFAST);
		if (lunchCheckBox.isSelected())
			courses.add(Recipe.LUNCH);
		if (dinnerCheckBox.isSelected())
			courses.add(Recipe.DINNER);
		if (dessertCheckBox.isSelected())
			courses.add(Recipe.DESSERT);
		if (snackCheckBox.isSelected())
			courses.add(Recipe.SNACK);
		if (generalCheckBox.isSelected())
			courses.add(Recipe.GENERAL);
		
		if (courses.isEmpty())
			return recipes;
		
		recipes.removeIf(new Predicate<Recipe>() {

			@Override
			public boolean test(Recipe t) {
				if (courses.contains(t.getCourse()))
					return false;
				
				return true;
			}
			
		});
		
		return recipes;
	}
	
	/**
	 * Fifth step for Advanced Search. Filter down Recipes based on Total Time
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by total time
	 */
	private ArrayList<Recipe> advancedTotalTimeSearch(ArrayList<Recipe> recipes){
		if ((Integer) totalTime.getValue() == 0)
			return recipes;
		
		recipes.removeIf(new Predicate<Recipe>() {

			@Override
			public boolean test(Recipe t) {
				if ((t.getPrepTime() + t.getCookTime()) <= (Integer) totalTime.getValue())
					return false;
				
				return true;
			}
			
		});
		
		return recipes;
	}
	
	/**
	 * Sixth step for Advanced Search. Filter down Recipes based on Servings
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by servings
	 */
	private ArrayList<Recipe> advancedServingsSearch(ArrayList<Recipe> recipes){
		if ((Integer) servings.getValue() == 0)
			return recipes;
		
		recipes.removeIf(new Predicate<Recipe>() {

			@Override
			public boolean test(Recipe t) {
				if ((t.getServings() < (Integer) servings.getValue()))
					return true;
				
				return false;
			}
			
		});
		
		return recipes;
	}
	
	/**
	 * Seventh step for Advanced Search. Filter down Recipes based on selected Ratings
	 * @param recipes A list of Recipes to filter.
	 * @return A list of Recipes filtered by ratings
	 */
	private ArrayList<Recipe> advancedRatingsSearch(ArrayList<Recipe> recipes){
		ArrayList<Integer> ratings = new ArrayList<Integer>();
		if (oneCheckBox.isSelected())
			ratings.add(1);
		if (twoCheckBox.isSelected())
			ratings.add(2);
		if (threeCheckBox.isSelected())
			ratings.add(3);
		if (fourCheckBox.isSelected())
			ratings.add(4);
		if (fiveCheckBox.isSelected())
			ratings.add(5);
		
		if (ratings.isEmpty())
			return recipes;
		
		recipes.removeIf(new Predicate<Recipe>() {

			@Override
			public boolean test(Recipe t) {
				if (ratings.contains(t.getRating()))
					return false;
				
				return true;
			}
			
		});
		
		return recipes;
	}
	
	/**
	 * The action for when there are no Search Results
	 */
	private void failed(String s){
		listModel.addElement("(No Results Found)");
		searchEditButton.setEnabled(false);
		searchOpenButton.setEnabled(false);
		System.out.println("Failed on: " + s);
	}
}
