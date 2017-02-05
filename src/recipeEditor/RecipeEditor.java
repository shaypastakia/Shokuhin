package recipeEditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import generalResources.GuiMethods;
import generalResources.MyMouseAdapter;
import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;

/**
 * Represents the Recipe Editor
 * 
 * @author Shaylen Pastakia
 *
 */
public class RecipeEditor extends Module{
	private static final long serialVersionUID = 2180887752409681176L;
	private Recipe recipe;
	
	// Panel structure for Recipe Editor
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JPanel ingredientsPane = GuiMethods.getStyledJPanel();//new JPanel();
	private JScrollPane ingredientsScroll = GuiMethods.getStyledJScrollPane();//new JScrollPane();
	private JPanel ingredientsButtonPane = new JPanel();
	private JPanel methodContainer = GuiMethods.getStyledJPanel();
	private JScrollPane methodStepPane = GuiMethods.getStyledJScrollPane();//new JScrollPane();
	private JPanel controlPane = new JPanel();
	private JPanel buttonPane = GuiMethods.getStyledJPanel();//new JPanel();
	private JPanel infoPane = GuiMethods.getStyledJPanel();//new JPanel();
	private JPanel generalPane = new JPanel();
	private JPanel tagsPane = new JPanel();

	// Data structures used in Recipe Editor
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private ArrayList<String> steps = new ArrayList<String>();

	// Swing objects used in Recipe Editor
	private JList<String> ingredientsList = new JList<String>(listModel);
	private JTextArea methodText = new JTextArea();
	private JButton previousButton = GuiMethods.getStyledJButton("<- Previous Step", 16);
	private JLabel stepCurrent = new JLabel("0", SwingConstants.RIGHT);
	JLabel sepLabel = GuiMethods.getStyledJLabel("/", 16);
	private JLabel stepMax = new JLabel("0", SwingConstants.LEFT);
	private JButton nextButton = GuiMethods.getStyledJButton("Next Step/Add Step To End->", 16);
	private JButton insButton = GuiMethods.getStyledJButton("Insert Step", 16);
	private JButton delButton = GuiMethods.getStyledJButton("Delete Step", 16);
	private JButton addButton = GuiMethods.getStyledJButton("Add", 16);//new JButton("Add");
	private JButton editButton = GuiMethods.getStyledJButton("Edit", 16);
	private JButton remButton = GuiMethods.getStyledJButton("Remove", 16);
	private JButton saveButton = GuiMethods.getStyledJButton("Save Recipe", 16);
	
	private String[] courseValues = new String[]{"Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "General"};
	private JComboBox<String> courseCombo = new JComboBox<String>(courseValues);
	
	private Integer[] ratingValues = new Integer[]{0,1,2,3,4,5};
	private JComboBox<Integer> ratingCombo = new JComboBox<Integer>(ratingValues);
	private JSpinner servesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
	private JSpinner prepSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
	private JSpinner cookSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
	private JTextField tagsText = new JTextField(50);
	
	private int index = 0;
	
	/**
	 * Constructor
	 * 
	 * @param m The ShokuhinMain parent
	 * @param recipe The Recipe to edit
	 */
	public RecipeEditor(ShokuhinMain m, Recipe recipe){
		super(m, recipe.getTitle());
		this.recipe = recipe;
		
		createGui();
		populateFields();
		firstStep();
	}
	
	/**
	 * Constructor
	 * 
	 * @param m The ShokuhinMain parent
	 * @param name The Title of a new Recipe
	 */
	public RecipeEditor(ShokuhinMain m, String name){
		super(m, name);
		recipe = new Recipe(name);
		steps.add("");
		stepCurrent.setText("" + (index+1));
		stepMax.setText("" + steps.size());
		createGui();
	}
	
	/**
	 * Produce the Graphical Interface of the Module
	 */
	private void createGui(){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		splitPane.setLeftComponent(ingredientsPane);
		ingredientsPane.setLayout(new BoxLayout(ingredientsPane, BoxLayout.PAGE_AXIS));
		ingredientsScroll.setViewportView(ingredientsList);
		ingredientsPane.add(ingredientsScroll);
		ingredientsPane.add(ingredientsButtonPane);
		ingredientsPane.setMinimumSize(new Dimension(300, 0));
//		ingredientsPane.setBorder(BorderFactory.createTitledBorder("Ingredients (Drag to reorder)"));
		ingredientsPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				
		//Code from http://stackoverflow.com/questions/3804361/how-to-enable-drag-and-drop-inside-jlist, by Grains
		MyMouseAdapter<String> adaptor = new MyMouseAdapter<String>(ingredientsList, listModel);
		ingredientsList.addMouseListener(adaptor);
		ingredientsList.addMouseMotionListener(adaptor);
		//End code from
		
		ingredientsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ingredientsList.setBackground(ShokuhinMain.themeColour);
		ingredientsList.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));
		ingredientsList.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		ingredientsButtonPane.setLayout(new BoxLayout(ingredientsButtonPane, BoxLayout.LINE_AXIS));
		ingredientsButtonPane.setOpaque(false);
		ingredientsButtonPane.add(addButton);
		ingredientsButtonPane.add(editButton);
		ingredientsButtonPane.add(remButton);
		
		splitPane.setRightComponent(controlPane);
		controlPane.setLayout(new FlowLayout());
		methodContainer.add(methodStepPane);
		controlPane.add(methodContainer);
		methodStepPane.setViewportView(methodText);
//		firstStepPane.setBorder(BorderFactory.createTitledBorder("Method Step (Press CTRL + S to Save)"));
		buttonPane.add(previousButton);
		stepCurrent.setPreferredSize(new Dimension(50, 30));
		stepMax.setPreferredSize(new Dimension(50, 30));
		methodStepPane.setPreferredSize(new Dimension(1000, 300));
		methodText.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));
		methodText.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		methodText.setBackground(ShokuhinMain.themeColour);

		buttonPane.add(stepCurrent);
		buttonPane.add(sepLabel);
		buttonPane.add(stepMax);
		buttonPane.add(nextButton);
		buttonPane.add(Box.createHorizontalStrut(50));
		buttonPane.add(delButton);
		buttonPane.add(insButton);
		buttonPane.add(Box.createHorizontalStrut(50));
		buttonPane.add(saveButton);
		controlPane.add(buttonPane);
		
		stepCurrent.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));
		stepMax.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));
		stepCurrent.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		stepMax.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		
//		infoPane.setBorder(BorderFactory.createTitledBorder("Recipe Information"));
		infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.PAGE_AXIS));
		generalPane.add(GuiMethods.getStyledJLabel("Course: ", 16));
		generalPane.add(courseCombo);
		generalPane.add(Box.createHorizontalStrut(15));
		generalPane.add(GuiMethods.getStyledJLabel("Rating: ", 16));
		generalPane.add(ratingCombo);
		generalPane.add(Box.createHorizontalStrut(15));
		generalPane.add(GuiMethods.getStyledJLabel("Servings: ", 16));
		generalPane.add(servesSpinner);
		generalPane.add(Box.createHorizontalStrut(15));
		generalPane.add(GuiMethods.getStyledJLabel("Preparation Time: ", 16));
		generalPane.add(prepSpinner);
		generalPane.add(Box.createHorizontalStrut(15));
		generalPane.add(GuiMethods.getStyledJLabel("Cooking Time: ", 16));
		generalPane.add(cookSpinner);
		generalPane.setOpaque(false);
		tagsPane.add(GuiMethods.getStyledJLabel("Tags: ", 16));
		tagsPane.setOpaque(false);
		tagsPane.add(tagsText);
		infoPane.add(generalPane);
		infoPane.add(tagsPane);
		
		courseCombo.setSelectedItem("General");
		
		add(splitPane);
		controlPane.add(infoPane);
		
//		changeHeight(infoPane, 50);
		
		//Set up listeners for controls
		nextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				nextStep();
			}
		});
		
		previousButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				prevStep();
			}
		});
		
		delButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				delStep();
			}
		});
		
		insButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				insStep();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String>temp = new ArrayList<String>();
				Enumeration<String> enumer = listModel.elements();
				while (enumer.hasMoreElements())
					temp.add(enumer.nextElement());
				
				recipe.setIngredients(temp);
				recipe.setMethodSteps(steps);
				
				recipe.setCookTime((int)cookSpinner.getValue());
				recipe.setPrepTime((int)prepSpinner.getValue());
				recipe.setServings((int)servesSpinner.getValue());
				
				recipe.setRating(ratingCombo.getSelectedIndex());
				recipe.setCourse(courseCombo.getSelectedIndex());
				
				ArrayList<String> temp2 = new ArrayList<String>();
				if (tagsText.getText() != null && !tagsText.getText().trim().equals("")){
					for (String s : tagsText.getText().split(","))
						temp2.add(s.trim());
					recipe.setTags(temp2);
				}
				recipe.setTags(temp2);
				
				recipe.setLastModificationDate(Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
				
				if (Files.exists(new File("./Shokuhin/Recipes/" + recipe.getTitle() + ".rec").toPath())){
					if (!RecipeMethods.deleteRecipe(recipe))
						ShokuhinMain.displayMessage("Failed", "Unable to delete old Recipe", JOptionPane.WARNING_MESSAGE);
				}
						
				
				if (RecipeMethods.writeRecipe(recipe))
					ShokuhinMain.displayMessage("Success", recipe.getTitle() + " has been saved.", JOptionPane.INFORMATION_MESSAGE);
					
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = new IngredientsDialog().ingredientText;
				if (s != null && !s.equals(""))
					listModel.addElement(s);
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = new IngredientsDialog(ingredientsList.getSelectedValue()).ingredientText;
				if (s != null && !s.equals(""))
					listModel.set(ingredientsList.getSelectedIndex(), s);
			}
		});
		
		remButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.remove(ingredientsList.getSelectedIndex());
			}
		});
		
		methodText.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				steps.set(index, methodText.getText());				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
	}
	
	/**
	 * Populate the Editor fields with the Recipe details.
	 * <br>
	 * Used when Editing, and not Creating a Recipe
	 * @return False, if the Recipe is null, otherwise return True
	 */
	private boolean populateFields(){
		if (recipe == null)
			return false;
		
		for (String s : recipe.getIngredients()){
			if (s.contains("<html>")){
				s = s.replaceAll("<html>", "<html><p style=\"color:black;\">");
				s = s.replaceAll("</html>", "</p></html>");
			}
			listModel.addElement(s);
		}
		
		steps = recipe.getMethodSteps();
		
		courseCombo.setSelectedIndex(recipe.getCourse());
		ratingCombo.setSelectedIndex(recipe.getRating());
		
		prepSpinner.setValue(recipe.getPrepTime());
		cookSpinner.setValue(recipe.getCookTime());
		servesSpinner.setValue(recipe.getServings());
		
		for (String s : recipe.getTags())
			if (recipe.getTags().indexOf(s) < recipe.getTags().size()-1)
				tagsText.setText(tagsText.getText() + s + ", ");
			else
				tagsText.setText(tagsText.getText() + s);
		return true;
	}
	
	/**
	 * Set the first Step in the Text Area, when Editing a Recipe
	 */
	private void firstStep(){
		stepMax.setText("" + steps.size());
		methodText.setText(steps.get(0));
		stepCurrent.setText("" + 1);
	}
	
	/**
	 * Traverse forwards through the array of Method Steps
	 * <br>
	 * If there are no more steps, then add a new one
	 */
	private void nextStep(){
		steps.set(index, methodText.getText());
		String temp;
		try {
			temp = steps.get(index+1);
			methodText.setText(temp);
			index++;
			stepCurrent.setText("" + (index + 1));
		} catch (IndexOutOfBoundsException e){
			steps.add("");
			stepMax.setText("" + steps.size());
			nextStep();
		}
	}
	
	/**
	 * Traverse backwards through the array of Method Steps
	 */
	private void prevStep(){
		steps.set(index, methodText.getText());
		String temp;
		try {
			temp = steps.get(index-1);
			methodText.setText(temp);
			index--;
			stepCurrent.setText("" + (index + 1));
		} catch (IndexOutOfBoundsException e){
			
		}
	}
	
	/**
	 * Delete the current Method Step
	 */
	private void delStep(){
		try {
			String temp = steps.get(index + 1);
			methodText.setText(temp);
			steps.remove(index);
			stepMax.setText("" + steps.size());
		} catch (IndexOutOfBoundsException e){
			try {
				String temp = steps.get(index - 1);
				methodText.setText(temp);
				steps.remove(index);
				index--;
				stepCurrent.setText("" + (index + 1));
				stepMax.setText("" + steps.size());
			} catch (IndexOutOfBoundsException e1){
				
			}
		}
	}
	
	/**
	 * Insert a new Method Step after the current one
	 */
	private void insStep(){
		steps.add(index+1, "");
		stepMax.setText("" + steps.size());
		nextStep();
	}
	
	/**
	 * Method based on http://stackoverflow.com/questions/12730230/set-the-same-font-for-all-component-java
	 * <br>
	 * Answer by: Mikle Garin
	 * @param component The Parent window to apply Max Height to
	 * @param height The height to apply
	 */
	private void changeHeight ( Component component, int height)
	{
	    component.setMaximumSize(new Dimension(9999, height));
	    if ( component instanceof Container )
	    {
	        for ( Component child : ( ( Container ) component ).getComponents () )
	        {
	            changeHeight ( child, height );
	        }
	    }
	}

	
	@Override
	public boolean close(){
		int val = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the Editor?\nEnsure the Recipe is saved.");
		if (val == JOptionPane.YES_OPTION)
			return true;
		return false;
	}

	@Override
	public JMenu getFunctionMenu(){
		JMenu menu = new JMenu("Recipe Editor");
		
		// A menu item that can be used when there are no functions to be
		// displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

	/**
	 * Save when CTRL + S is pressed
	 */
	@Override
	public void KeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() && e.getID() == KeyEvent.KEY_RELEASED)
			saveButton.doClick();
	}

}