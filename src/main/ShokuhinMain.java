package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

import home.ShokuhinHome;
import mp3Player.MP3Player;
import recipeSearch.RecipeSearch;

/**
 * ShokuhinMain
 * <br>
 * Initialising Class for Shokuhin - Creates Full Interface
 * <br>
 * Welcome to Shokuhin!
 *
 * @author Shaylen Pastakia
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
	
	//Format to store Dates in.
	public static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	
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
		
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
		
		//Create Frame
		frame = new ShokuhinFrame("Shokuhin", this);
		
		//Create Timer Bar
		timer = new TimerBar(this);
		//Create a Tabbed Pane to display tabs for all Modules
		tabPane = new JTabbedPane();

		//Add a listener to set the Dynamic Menu on the Frame to change according to the current tab
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
		openTab(new ShokuhinHome(this));
		openTab(new RecipeSearch(this));
		tabPane.setSelectedIndex(0);
		
		/**
		 * Code based on: http://stackoverflow.com/questions/5344823/
		 * how-can-i-listen-for-key-presses-within-java-swing-across-all-components
		 * <br>
		 * Answer by: Matt Crinklaw-Vogt
		 */
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		  .addKeyEventDispatcher(new KeyEventDispatcher() {
		      @Override
		      public boolean dispatchKeyEvent(KeyEvent e) {
		        ((Module)tabPane.getSelectedComponent()).KeyPressed(e);
		        return false;
		      }
		});
		//End Code Based On
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
	 * Rename the current tab
	 */
	public void renameTab(String newName) {
		tabPane.setTitleAt(tabPane.getSelectedIndex(), newName);
	}
	
	public MP3Player getPlayer(){
		Component[] comps = tabPane.getComponents();
		for (Component c : comps)
			if (c instanceof MP3Player)
				return (MP3Player) c;
		return null;
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
//                	test();
//                	MidiPlayer.play(new Pattern(MidiPlayer.getPattern(new File("INSERT '.mid' FILENAME HERE"))));
                    main = new ShokuhinMain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
	} //closes main method */

	public static void test() throws ClassNotFoundException, SQLException{
		 final String DB_URL = "";
		 final String USER = "";
		 final String PASS = "";
		 Class.forName("com.mysql.jdbc.Driver");
		 
		 Connection conn = null;
		 Statement stmt = null;
		 conn = DriverManager.getConnection(DB_URL,USER,PASS);
		 
		 String sql = "SELECT title FROM Shokuhin";
		 ResultSet rs = stmt.executeQuery(sql);
		 while(rs.next()){
			 System.out.println(rs.getString("title"));
		 }
		 
		 rs.close();
	      stmt.close();
	      conn.close();
	}
} //closes class definition