package recipeViewer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;

public class RecipeViewer extends Module {
	private static final long serialVersionUID = 2180887752409681176L;
	private JTextArea text = new JTextArea();
	
	public RecipeViewer(ShokuhinMain m, Recipe recipe) {
		super(m, recipe.getTitle());
		// TODO Auto-generated constructor stub
		add(text);
		text.append("Title: " + recipe.getTitle() + "\n");
		text.append("Tags: ");
		for (String s : recipe.getTags())
			text.append(s + ", ");
		text.append("\nIngredients: ");
		for (String s : recipe.getIngredients())
			text.append(s + ", ");
		text.append("\nIngredients: ");
		for (String s : recipe.getMethodSteps())
			text.append(s + ", ");
		text.append("\nCourse: " + recipe.getCourse() + "\nTotal Time: " + recipe.getPrepTime() + " + " + recipe.getCookTime() + "\n");
		text.append("Servings: " + recipe.getServings() + "\nRating: " + recipe.getRating());
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Viewer");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

}
