package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cloudManager.CloudManager;
import mp3Player.MP3Player;
import recipe.Recipe;
import recipe.RecipeMethods;
import recipeEditor.RecipeEditor;
import recipeSearch.RecipeSearch;
import sqlEngine.AuthenticationPanel;

/**
 * ShokuhinFrame
 * <br>
 * Creates and prepares a Frame for ShokuhinMain to use
 * @author Shaylen Pastakia
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
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		//Set the window size to the minimally standard resolution of a Laptop
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(1366,768));
		this.setJMenuBar(createMenu());
//		this.setVisible(true);
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
		
		//Add new modules to the 'Open New Tab' Menu
		JMenuItem recipeEditor = new JMenuItem("Create a Recipe");
		JMenuItem recipeSearch = new JMenuItem("Search for Recipes");
		JMenuItem mp3Player = new JMenuItem("MP3 Player");
		JMenuItem cloudManager = new JMenuItem("Cloud Manager");
		JMenuItem sqlSync = new JMenuItem("Synchronise with Server");
		
		fileMenu.add(recipeEditor);
		fileMenu.add(recipeSearch);
		fileMenu.add(mp3Player);
		fileMenu.add(cloudManager);
		fileMenu.add(sqlSync);
		recipeEditor.addActionListener(this);
		recipeSearch.addActionListener(this);
		mp3Player.addActionListener(this);
		cloudManager.addActionListener(this);
		sqlSync.addActionListener(this);
		
		//Add parsing menu item
		JMenuItem parse = new JMenuItem("Parse Recipe");
		parse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String copy = "";
				try {
					String temp = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					if (temp.contains("bbcgoodfood.com") || temp.contains("allrecipes.co.uk") || temp.contains("bbc.co.uk/food"))
						copy = temp;
				} catch (Exception e1) {}
				String url = JOptionPane.showInputDialog(null,"Enter a URL for a recipe on:\nBBC Good Food (bbcgoodfood.com)\nAll Recipes (allrecipes.co.uk)\nBBC (bbc.co.uk/food)\nto parse:", copy);
				Recipe recipe = RecipeMethods.parseRecipeUrl(url);

				if (recipe != null){
					String title = JOptionPane.showInputDialog(null, "Enter a title for the recipe:", recipe.getTitle());
					if (title == null || title.equals(""))
						return;
					ArrayList<String> titles = RecipeMethods.getRecipeFileNames();
					for (String s : titles){
						if (s.toLowerCase().equals(title.toLowerCase())){
							ShokuhinMain.displayMessage("Error", "A Recipe with this Title already exists", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}
					
					recipe.setTitle(title);
					main.openTab(new RecipeEditor(main, recipe));
				}
					
				else {
					if (url == null || url.equals(""))
						return;
					ShokuhinMain.displayMessage("Error", "Unable to parse Recipe from " + url, JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		fileMenu.addSeparator();
		fileMenu.add(closeTab);
		fileMenu.addSeparator();
		fileMenu.add(parse);
		
		return fileMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//All File->Open New Tab buttons are set to trigger this method.
		//The switch statement determines which Menu Item was pressed, then requests for ShokuhinMain to open a new tab
			switch(e.getActionCommand()) {
			case "Create a Recipe": 
				String name = JOptionPane.showInputDialog("Please enter a name for the new Recipe.");
				if (name == null || name.trim().equals(""))
					return;
				
				if (name.contains("&")){
					ShokuhinMain.displayMessage("Invalid Character", "Recipe names cannot contain '&'.", JOptionPane.ERROR_MESSAGE);
					return;
				}
				ArrayList<String> names = RecipeMethods.getRecipeFileNames();
				for (String s : names){
					if (s.toLowerCase().trim().equals(name.toLowerCase().trim())){
						ShokuhinMain.displayMessage("Recipe Exists", "A Recipe with this title already exists.", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				main.openTab(new RecipeEditor(main, name));
				break;
			case "Search for Recipes": main.openTab(new RecipeSearch(main));
				break;
			case "MP3 Player": main.openTab(new MP3Player(main));
				break;
			case "Cloud Manager": main.openTab(new CloudManager(main));
				break;
			case "Synchronise with Server": authAndSync();
            	break;
			}
	}
	
	private void authAndSync(){
		if (main.getSQLEngine() != null){
			main.getSQLEngine().synchronise(false);
			return;
		}
		
		AuthenticationPanel panel = new AuthenticationPanel();
    	if (panel.succeeded()){
    		try {
    			List<String> details = panel.getDetails();
    			if (details != null){
	    			main.initialiseSQLEngine(details);
	    			main.getSQLEngine().synchronise(false);
    			}
    		} catch (IOException ex){
    			ShokuhinMain.displayMessage("Token Error", "Unable to read details from 'token.mkey'.\n" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
    		}
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
		if (ShokuhinMain.getSync()){
			ShokuhinMain.displayMessage("Please Wait", "Please allow the SQL Synchronisation to complete before exiting.", JOptionPane.WARNING_MESSAGE);
			return;
		}
		//Call the close() method in all Modules before Closing
		int count = -1;
		for (Component m: main.tabPane.getComponents()){
			if (!((Module) m).close())
				count++;
		}

		try {
			Files.deleteIfExists(new File("./log/server.log").toPath());
		} catch (Exception e1) {

		}
		if (count == 0)
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
