package main;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

import recipe.Recipe;
import recipe.RecipeMethods;

/**
 * ShokuhinMain
 * <br>
 * Initialising Class for Shokuhin - Creates Full Interface
 * <br>
 * Welcome to Shokuhin!
 *
 */
public class ShokuhinMain {
	//Static fields for the (text-safe) Colours used in Shokuhin
	public static final Color MANABU_BLUE = new Color(46, 220, 255);
	public static final Color REDRUM_RED = new Color(220,25,25);
	public static final Color SATSUMA_MANDARIN_CLEMENTINE_ORANGE = new Color(255,155,25);
	public static final Color EASY_PEASY_LEMON_SQUEEZY_YELLOW = new Color(238,240,65);
	public static final Color QUESTIONABLE_SALAD_LEAF_GREEN = new Color(92, 200, 87);
	public static final Color FIFTY_SHADES_OF_WHITE = new Color(240, 240, 240);
	public static final Color ANY_COLOUR_SO_LONG_AS_IT_IS_BLACK = new Color(0, 0, 0);
	public static final Color DEFAULT_CAMPUS_FOG = new Color(214, 217, 223);
	
	//Create fields to store the Main class, as well as the Voice Command class.
	public static ShokuhinMain main;
	
	//Store the main components of the Window
	JTabbedPane tabPane;
	static ShokuhinFrame frame;
	public static TimerBar timer;
	
	/**
	 * Constructor
	 * <br>
	 * Welcome to Shokuhin!
	 * <br>
	 * Performs the Setup necessary for Shokuhin to run. 
	 */
	public ShokuhinMain() {
		/*Try to set the theme the Nimbus (Cross Platform - Win/Lin/MacOS)
		  Failing that, set the theme to the System Theme
		  If that fails, assume there's some deep, underlying issue,
		  which Shokuhin would only exacerbate, and therefore conclude that exiting is
		  the best option.
		*/
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.put("nimbusBase", new ColorUIResource(46, 220, 255));
		     UIManager.put("textForeground", new ColorUIResource(0, 0, 0));
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			try {
				System.out.println("Reverting to Default Look and Feel");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e1) {
				JOptionPane.showMessageDialog(null, "Failed to set both Nimbus theme and System theme. \n Returned the error: " + e.getMessage());
				System.exit(0);
			}
		}
		
		//Create Frame
		frame = new ShokuhinFrame("Shokuhin", this);
		
		//Create Timer Bar
		timer = new TimerBar();
		//Create a Tabbed Pane to display tabs for all Modules
		tabPane = new JTabbedPane();

		//Add a listener to set the Module Menu on the Frame to change according to the current tab
		tabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Module m = (Module) tabPane.getSelectedComponent();
					frame.setFunctionMenu(m.getFunctionMenu());
			}
		});
		
		//Add the Tabbed Pane to the Window
		frame.add(tabPane);
		frame.add(timer);
		
	} //closes constructor
	
	/**
	 * Can be called by any Class to display a Message Window
	 * @param title
	 * @param message
	 * @param type
	 */
	public static void displayMessage(String title, String message, int type){
		JOptionPane.showMessageDialog(null, message, title, type);
	}
	
	/**
	 * Take a new ModulePanel, and add it to Shokuhin
	 * <br>
	 * Certain modules cannot have multiple instances
	 * @param m
	 */
	public void openTab(Module m){
		tabPane.add((Component) m);
		tabPane.setSelectedComponent((Component) m);
	}
	
	/**
	 * Close the current tab
	 * <br />
	 * The tab must provide permission to do so (see Module close() methods)
	 */
	public void closeTab(){
		//Get the currently active tab
		Object current = tabPane.getSelectedComponent();
		
		//Request permission to close a Module, then close it upon receiving it.
		if (((Module) current).close()){
			tabPane.remove((Component) current);
		}
	}

	/**
	 * 
	 * @param m (A class to check if its Module Type already exists)
	 * @return true, if the Module is already open. Additionally gives that tab focus.
	 */
	private boolean tabExists(Class<?> m) {
		for (Component c : tabPane.getComponents()){
			if (c.getClass() == m){
				tabPane.setSelectedComponent(c);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a graphical component to the Timer Bar
	 * @param c The Component to add
	 */
	public static void addToTimerBar(Component c){
		timer.add(c);
	}
	
	/**
	 * Remove a graphical component from the Timer Bar
	 * @param c The Component to remove
	 */
	public static void removeFromTimerBar(Component c){
		timer.remove(c);
	}
	
	/**
	 * Main
	 * Initialises a running instance of Shokuhin
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
//                	MidiPlayer.play(new Pattern(MidiPlayer.getPattern(new File("INSERT '.mid' FILENAME HERE"))));
                    main = new ShokuhinMain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	} //closes main method */

} //closes class definition
