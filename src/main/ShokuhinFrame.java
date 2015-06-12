package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import recipe.Recipe;
import recipe.RecipeMethods;
import recipeEditor.RecipeEditor;
import recipeSearch.RecipeSearch;
import recipeViewer.RecipeViewer;
import mp3Player.MP3Player;

/**
 * ShokuhinFrame
 * <br>
 * Creates and prepares a Frame for ShokuhinMain to use
 * 
 */
public class ShokuhinFrame extends JFrame implements ActionListener, WindowListener{

	private ShokuhinMain main;
	JMenuBar bar;
	private JMenu moduleMenu = new JMenu("ModuleMenu");
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * <br>
	 * @param title The title to give the Window
	 * @param main The Main class that initiated this Frame
	 */
	public ShokuhinFrame(String title, ShokuhinMain main){
		
		//Perform standard setting up for a Frame
		this.main = main;
		this.setTitle(title);
		
		this.addWindowListener(this);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		//Set the window size to the minimally standard resolution of a Laptop
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(1366,768));
		this.setJMenuBar(createMenu());
		this.setVisible(true);
	}
	
	/**
	 * Create the main Menu Bar for the Window
	 * @return the JMenuBar for creation
	 */
	private JMenuBar createMenu(){
		//Create the Menu Bar for the main Window
		bar = new JMenuBar();
		JMenu file = createFileMenu();
		bar.add(file);
		bar.add(moduleMenu);
		return bar;
	}
	
	/**
	 * Allows all Modules to be able to set the Function Menu of the Window
	 * @param menu The JMenu to switch in
	 */
	public void setFunctionMenu(JMenu menu){
		bar.remove(moduleMenu);
		moduleMenu = menu;
		bar.add(moduleMenu, 1);
		bar.repaint();
	}
	
	/**
	 * Creates the File Menu for the Window
	 * @return
	 */
	private JMenu createFileMenu(){
		//Create the File Menu for the Menu Bar
		JMenu fileMenu = new JMenu(" File     ");
		
		JMenuItem closeTab = new JMenuItem("Close Current Tab");
		closeTab.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				main.closeTab();
			}
		});
		
		JMenuItem closeShokuhin = new JMenuItem("Exit Shokuhin");
		closeShokuhin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int close = JOptionPane.showConfirmDialog(new JFrame(), "Are you sure you want to exit Shokuhin?" +
						"\nPlease make sure all tabs have been saved.", "Exit Shokuhin", JOptionPane.YES_NO_OPTION);
				if (close == JOptionPane.YES_OPTION){
					//Call the close() method of all Modules before closing
					for (Component m: main.tabPane.getComponents())
						((Module) m).close();

					System.exit(0);
				}
					
			}
		});
		
		//Add new modules to the 'Open New Tab' Menu
		JMenu openTab = new JMenu("Open New Tab");
		JMenuItem openTest = new JMenuItem("Test");
		JMenuItem recipeEditor = new JMenuItem("Recipe Editor");
		JMenuItem recipeSearch = new JMenuItem("Recipe Search");
		JMenuItem recipeViewer = new JMenuItem("Recipe Viewer");
		JMenuItem mp3Player = new JMenuItem("MP3 Player");
		fileMenu.add(openTab);
		openTab.add(openTest);
		openTab.add(recipeEditor);
		openTab.add(recipeSearch);
		openTab.add(recipeViewer);
		openTab.add(mp3Player);
		
		for (Component m : openTab.getMenuComponents())
		((JMenuItem)m).addActionListener(this);
		
		//Add parsing menu item
		JMenuItem parse = new JMenuItem("Parse Recipe");
		parse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String url = JOptionPane.showInputDialog(null,"Enter a URL for a recipe on BBC Good Food (bbcgoodfood.com) or All Recipes (allrecipes.co.uk) to parse:");
				Recipe recipe = RecipeMethods.parseRecipeUrl(url);
				//TODO Change to open in Recipe Editor
				if (recipe != null)
					main.openTab(new RecipeViewer(main, recipe));
				else
					ShokuhinMain.displayMessage("Error", "Unable to parse Recipe from " + url, JOptionPane.ERROR_MESSAGE);
			}
		});
		
		fileMenu.add(closeTab);
		fileMenu.addSeparator();
		fileMenu.add(parse);
		fileMenu.addSeparator();
		fileMenu.add(closeShokuhin);
		
		return fileMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//All File->Open New Tab buttons are set to trigger this method.
		//The switch statement determines which Menu Item was pressed, then requests for ShokuhinMain to open a new tab
			switch(e.getActionCommand()) {
			case "Test": main.openTab(new Test(main));
				break;
			case "Recipe Editor": main.openTab(new RecipeEditor(main, new Recipe("test")));
				break;
			case "Recipe Search": main.openTab(new RecipeSearch(main));
				break;
			case "Recipe Viewer": main.openTab(new RecipeViewer(main, new Recipe("test")));
				break;
			case "MP3 Player": main.openTab(new MP3Player(main));
			}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		//Call the close() method in all Modules before Closing
		for (Component m: main.tabPane.getComponents())
			((Module) m).close();

		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
