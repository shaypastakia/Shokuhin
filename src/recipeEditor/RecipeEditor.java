package recipeEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;

public class RecipeEditor extends Module {
	private static final long serialVersionUID = 306718353578602520L;

	private Recipe recipe;
	private ShokuhinMain main;

	// Panel structure for Recipe Viewer, indicated by indentation
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JScrollPane ingredientsPane = new JScrollPane();
	private JPanel methodPane = new JPanel();
	private JScrollPane firstStepPane = new JScrollPane();
	private JScrollPane secondStepPane = new JScrollPane();
	private JPanel controlPane = new JPanel();
	private JPanel infoPane = new JPanel();

	// Data structures used in Recipe Viewer
	private DefaultListModel<String> listModel = new DefaultListModel<String>();

	private ArrayList<String> instructions = new ArrayList<String>();
	private int currentInstruction = 0;

	// Swing objects used in Recipe Viewer
	private JList<String> ingredientsList = new JList<String>(listModel);
	private JTextArea firstStepText = new JTextArea();
	private JTextArea secondStepText = new JTextArea();

	private JButton startButton = new JButton("Start Recipe");
	private JButton previousButton = new JButton("<- Previous Step");
	private JButton saveButton = new JButton("Save Recipe");
	private JButton nextButton = new JButton("Next Step ->");
	private JButton ingredientButton = new JButton("Add Ingredient");

	// Radio buttons for the meal
	private JRadioButton breakfast = new JRadioButton("Breakfast");
	private JRadioButton lunch = new JRadioButton("Lunch");
	private JRadioButton dinner = new JRadioButton("Dinner");
	private JRadioButton dessert = new JRadioButton("Dessert");
	private JRadioButton snack = new JRadioButton("Snack");
	private JRadioButton general = new JRadioButton("General");

	private ButtonGroup group, group2;
	private JRadioButton rating1 = new JRadioButton("1");
	private JRadioButton rating2 = new JRadioButton("2");
	private JRadioButton rating3 = new JRadioButton("3");
	private JRadioButton rating4 = new JRadioButton("4");
	private JRadioButton rating5 = new JRadioButton("5");
	private JRadioButton rating0 = new JRadioButton("0");

	private Component[] editableComponents = { previousButton,
			saveButton, nextButton, ingredientButton };

	private JTextField prepTime, cookTime, serveText, tagField;

	/**
	 * Constructor
	 * 
	 * @param m
	 *            The ShokuhinMain parent
	 * @param recipe
	 *            The Recipe to view
	 */
	public RecipeEditor(ShokuhinMain m, Recipe recipe) {
		super(m, recipe.getTitle());
		this.recipe = recipe;
		this.main = m;

		// If there is no recipe being loaded
		if (recipe.getMethodSteps().isEmpty()) {
			setEnableDisable(false);
		} else {
			// if there is a recipe that this has been opened with
			setEnableDisable(true);
		}

		// Configure the Panel structures and layouts
		splitPane.setLeftComponent(ingredientsPane);
		splitPane.setRightComponent(methodPane);
		ingredientsPane.setMinimumSize(new Dimension(200, 0));
		ingredientsPane.setViewportView(ingredientsList);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		methodPane.setLayout(new BoxLayout(methodPane, BoxLayout.PAGE_AXIS));
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.LINE_AXIS));
		infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.PAGE_AXIS));

		// Set up Panel structure
		methodPane.add(firstStepPane);
		methodPane.add(secondStepPane);
		methodPane.add(controlPane);
		methodPane.add(infoPane);

		// Set up Borders for Panels
		firstStepPane.setBorder(BorderFactory
				.createTitledBorder("Current Step"));
		secondStepPane.setBorder(BorderFactory.createTitledBorder("Next Step"));
		infoPane.setBorder(BorderFactory.createTitledBorder("Information"));

		// Set up Swing items
		firstStepPane.setViewportView(firstStepText);
		secondStepPane.setViewportView(secondStepText);

		// Adding to the infoPane

		controlPane.add(startButton);
		controlPane.add(saveButton);

		controlPane.add(ingredientButton);
		controlPane.add(previousButton);
		controlPane.add(nextButton);

		// Set fonts for JButtons
		startButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
		previousButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
		saveButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
		nextButton.setFont(new Font("SansSerif", Font.PLAIN, 15));
		ingredientButton.setFont(new Font("SansSerif", Font.PLAIN, 15));

		// Set up Listeners for JButtons
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane
						.showInputDialog("Enter a name for the recipe");
				if (!name.equals(null)) {
					recipe.setTitle(name);
					m.renameTab(name);
				}

				int steps;
				while (true) {
					String number = JOptionPane
							.showInputDialog("How many steps does your recipe involve?");
					steps = Integer.parseInt(number);
					if (steps > 0) {
						break;
					}
				}

				for (int i = 0; i < steps; i++) {
					instructions.add("Instruction " + (i + 1));
				}

				setEnableDisable(true);
				startButton.setText("Restart Recipe");

				firstStepText.setText(instructions.get(0));
				if (currentInstruction == instructions.size()) {
					secondStepText.setText("");
				} else {
					secondStepText.setText(instructions.get(1));
				}
			}
		});

		ingredientButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ingred = JOptionPane
						.showInputDialog("Which ingredient would you like to add? And how much?");
				if (!ingred.equals(null)) {
					listModel.addElement(ingred);
				}
			}

		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int mealType;

				if (breakfast.isSelected()) {
					mealType = Recipe.BREAKFAST;
				} else if (lunch.isSelected()) {
					mealType = Recipe.LUNCH;
				} else if (dinner.isSelected()) {
					mealType = Recipe.DINNER;
				} else if (dessert.isSelected()) {
					mealType = Recipe.DESSERT;
				} else if (snack.isSelected()) {
					mealType = Recipe.SNACK;
				} else {
					mealType = Recipe.GENERAL;
				}

				int rating;

				if (rating0.isSelected()) {
					rating = 0;
				} else if (rating1.isSelected()) {
					rating = 1;
				} else if (rating2.isSelected()) {
					rating = 2;
				} else if (rating3.isSelected()) {
					rating = 3;
				} else if (rating4.isSelected()) {
					rating = 4;
				} else {
					rating = 5;
				}

				int prep = Integer.parseInt(prepTime.getText());
				int cook = Integer.parseInt(cookTime.getText());
				int serves = Integer.parseInt(serveText.getText());

				recipe.setCourse(mealType);
				recipe.setRating(rating);
				recipe.setCookTime(cook);
				recipe.setPrepTime(prep);
				recipe.setServings(serves);
				recipe.setMethodSteps(instructions);

				Object[] tagArray = listModel.toArray();

				ArrayList<Object> tagArrList = new ArrayList<Object>(Arrays
						.asList(tagArray));

				ArrayList<String> finalList = new ArrayList<String>();

				for (Object obj : tagArrList) {
					finalList.add(obj.toString());
				}

				recipe.setIngredients(finalList);

				// Now we must parse the tags
				ArrayList<String> tagList = new ArrayList<String>();

				for (String word : tagField.getText().split(" ")) {
					tagList.add(word);
				}

				recipe.setTags(tagList);
				
				System.out.println("Tag List:");
				for (String string : tagList) {
					System.out.println(string);
				}
				
				System.out.println("Ingredients");
				for (String string : finalList) {
					System.out.println(string);
				}
				
				System.out.println("Steps:");
				for (String string : instructions) {
					System.out.println(string);
				}

				RecipeMethods.writeRecipe(recipe, new File(
						"./Shokuhin/Recipes/" + recipe.getTitle() + ".rec"));
			}
		});

		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previousStep();
			}
		});

		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextStep();
			}
		});

		// Set up Text Areas
		firstStepText.setFont(new Font("SansSerif", Font.BOLD, 18));
		secondStepText.setFont(new Font("SansSerif", Font.PLAIN, 12));

		firstStepText.setEditable(true);
		secondStepText.setEditable(false);
		infoPane.setMaximumSize(new Dimension(9999, 500));

		String tags = "";
		// Append Tags to info String tags = "Tags: ";
		for (String s : recipe.getTags()) {
			tags = tags.concat(s);
			if (recipe.getTags().indexOf(s) < recipe.getTags().size() - 1) {
				tags = tags.concat(", ");
			} else {
				tags = tags.concat(".").trim();
			}
		}

		group = new ButtonGroup();
		group.add(breakfast);
		group.add(lunch);
		group.add(dinner);
		group.add(dessert);
		group.add(snack);
		group.add(general);

		JPanel buttonPanel = new JPanel();
		JLabel buttonLabel = new JLabel("Meal type:     ");
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(buttonLabel);
		buttonPanel.add(breakfast);
		buttonPanel.add(lunch);
		buttonPanel.add(dinner);
		buttonPanel.add(dessert);
		buttonPanel.add(snack);
		buttonPanel.add(general);

		infoPane.add(buttonPanel);

		// Append Course to info
		switch (recipe.getCourse()) {
		case Recipe.BREAKFAST:
			breakfast.setSelected(true);
		case Recipe.LUNCH:
			lunch.setSelected(true);
		case Recipe.DINNER:
			dinner.setSelected(true);
		case Recipe.DESSERT:
			dessert.setSelected(true);
		case Recipe.SNACK:
			snack.setSelected(true);
		case Recipe.GENERAL:
			general.setSelected(true);
		}
		add(splitPane);

		JLabel ratingLabel = new JLabel("Rating:     ");
		int rating = recipe.getRating();

		group2 = new ButtonGroup();
		group2.add(rating0);
		group2.add(rating1);
		group2.add(rating2);
		group2.add(rating3);
		group2.add(rating4);
		group2.add(rating5);

		buttonPanel.add(new JLabel("              "));
		buttonPanel.add(ratingLabel);
		buttonPanel.add(rating0);
		buttonPanel.add(rating1);
		buttonPanel.add(rating2);
		buttonPanel.add(rating3);
		buttonPanel.add(rating4);
		buttonPanel.add(rating5);

		// Append Course to info
		switch (rating) {
		case 0:
			rating0.setSelected(true);
		case 1:
			rating1.setSelected(true);
		case 2:
			rating2.setSelected(true);
		case 3:
			rating3.setSelected(true);
		case 4:
			rating4.setSelected(true);
		case 5:
			rating5.setSelected(true);
		}

		JPanel timePanel = new JPanel();
		JLabel prepTimeLabel = new JLabel("Preparation time (mins): ");
		prepTime = new JTextField(10);
		JLabel cookTimeLabel = new JLabel("Cooking time (mins): ");
		cookTime = new JTextField(10);

		prepTime.setText("" + recipe.getPrepTime());
		cookTime.setText("" + recipe.getCookTime());

		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.LINE_AXIS));
		timePanel.add(prepTimeLabel);
		timePanel.add(prepTime);
		timePanel.add(cookTimeLabel);
		timePanel.add(cookTime);

		infoPane.add(timePanel);

		JLabel serveLabel = new JLabel("Serves: ");
		serveText = new JTextField(10);

		serveText.setText("" + recipe.getServings());

		buttonPanel.add(new JLabel("              "));
		buttonPanel.add(serveLabel);
		buttonPanel.add(serveText);

		JPanel tagPanel = new JPanel();
		tagField = new JTextField(10);
		tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.LINE_AXIS));
		tagPanel.add(new JLabel("Tags:     "));
		tagField.setText(tags);
		tagPanel.add(tagField);
		infoPane.add(tagPanel);

		displayRecipe();
	}

	/**
	 * Populate fields with details of the Recipe
	 */
	private void displayRecipe() {
		// Add all Ingredients to the JList
		for (String s : recipe.getIngredients())
			listModel.addElement(s);

		// Load the first two steps
		instructions.addAll(recipe.getMethodSteps());
		nextStep();
	}

	/**
	 * Move back to the previous step in the Method
	 */
	private void previousStep() {
		if (currentInstruction > 0) {
			instructions.set(currentInstruction, firstStepText.getText());
			currentInstruction--;
			firstStepText.setText(instructions.get(currentInstruction));
			secondStepText.setText(instructions.get(currentInstruction + 1));
		}
	}

	/**
	 * Move forward to the next step in the Method
	 */
	private void nextStep() {
		int max_instruction = instructions.size() - 1;

		if (currentInstruction < max_instruction) {
			instructions.set(currentInstruction, firstStepText.getText());
			currentInstruction++;
			firstStepText.setText(instructions.get(currentInstruction));
			if (currentInstruction == max_instruction) {
				secondStepText.setText("End of recipe.");
			} else {
				secondStepText
						.setText(instructions.get(currentInstruction + 1));
			}
		}
	}

	@Override
	public boolean close() {
		return true;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Editor");

		// A menu item that can be used when there are no functions to be
		// displayed
		JMenuItem new_tab = new JMenuItem("New tab");
		new_tab.setEnabled(true);
		menu.add(new_tab);
		new_tab.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.openTab(new RecipeEditor(main, recipe));
			}

		});
		return menu;
	}

	private void setEnableDisable(boolean b) {

		for (Component component : editableComponents) {
			component.setEnabled(b);
		}
	}

}
