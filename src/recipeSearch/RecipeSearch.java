package recipeSearch;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import main.Module;
import main.ShokuhinMain;

public class RecipeSearch extends Module {
	private static final long serialVersionUID = -8627032843621798303L;

	public RecipeSearch(ShokuhinMain m) {
		super(m, "Recipe Search");
		// TODO Auto-generated constructor stub
		add(new JLabel("Mia Wasikowska"));
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
