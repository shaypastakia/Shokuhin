package recipeViewer;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import main.Module;
import main.ShokuhinMain;

public class RecipeViewer extends Module {
	private static final long serialVersionUID = 2180887752409681176L;

	public RecipeViewer(ShokuhinMain m) {
		super(m, "Recipe Viewer");
		// TODO Auto-generated constructor stub
		add(new JLabel("Charlie Hunnam"));
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
