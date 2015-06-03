package recipeEditor;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import main.Module;
import main.ShokuhinMain;

public class RecipeEditor extends Module {
	private static final long serialVersionUID = 306718353578602520L;

	public RecipeEditor(ShokuhinMain m) {
		super(m, "Recipe Editor");
		// TODO Auto-generated constructor stub
		add(new JLabel("Jessica Chastain"));
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Editor");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

}
