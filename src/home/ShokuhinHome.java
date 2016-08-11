package home;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;
import recipeEditor.RecipeEditor;
import recipeSearch.RecipeSearch;
import recipeViewer.RecipeViewer;

/**
 * Class that represents the home page of Shokuhin
 * 
 * @author Shaylen Pastakia
 *
 */
public class ShokuhinHome extends Module implements ActionListener, WindowStateListener
{
	private static final long serialVersionUID = 3395189302456031507L;

	private JPanel leftPane = new JPanel();
	private JPanel termPane = new JPanel();
	private JScrollPane resultPane;
	private JPanel buttonPane = new JPanel();
	private JPanel rightPane = new JPanel();
	private JPanel topPane = new JPanel();
	private JPanel bottomPane = new JPanel();

	private DefaultListModel<String> listModel = new DefaultListModel<String>();

	private JLabel termLabel = new JLabel("Search Term: ");
	private JTextField termText = new JTextField();
	private JList<String> list = new JList<>(listModel);
	private JButton openButton = new JButton("View");
	private JButton editButton = new JButton("Edit");
	
	JButton create = new JButton("Create");
	JButton search = new JButton("Search");
	JButton sync = new JButton("Sync");
	
	JPanel textPane = new JPanel();
	
	private ArrayList<String> recipes = RecipeMethods.getRecipeFileNames();
	private Card currentCard;
	
	private Image image;
	
	//As per
	//http://stackoverflow.com/questions/2444005/
	//how-do-i-make-my-arraylist-thread-safe-another-approach-to-problem-in-java
	List<Card> cards = Collections.synchronizedList(new ArrayList<Card>());
	
public ShokuhinHome(ShokuhinMain m) {
		super(m, "Home");
		
		//Override the Left Pane's JPanel to produce a Rounded Rectangle background
		leftPane = new JPanel(){
			private static final long serialVersionUID = 6309736538686170226L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(ShokuhinMain.themeColour);
				g.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
			}
		};
		
		//Override the JScrollPane to remove the border around the Scroll Pane
		resultPane = new JScrollPane(){
			private static final long serialVersionUID = -4823426376560198438L;

			@Override
			public void setBorder(Border border) {}
		};
		
		//Override the Vertical Scrollbar, to produce a custom scrollbar
		resultPane.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
			//As per http://stackoverflow.com/questions/23037433/changing-the-thumb-color-and-background-color-of-a-jscrollpane
			protected void configureScrollBarColors(){
				thumbColor = Color.BLACK;
				trackColor = ShokuhinMain.themeColour;
				
			};
			//As per http://stackoverflow.com/questions/7633354/how-to-hide-the-arrow-buttons-in-a-jscrollbar
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
			protected JButton createDecreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
		});
		
		//Override the Horizontal Scrollbar, to produce a custom scrollbar
		resultPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI(){
			//As per http://stackoverflow.com/questions/23037433/changing-the-thumb-color-and-background-color-of-a-jscrollpane
			protected void configureScrollBarColors(){
				thumbColor = Color.BLACK;
				trackColor = ShokuhinMain.themeColour;
				
			};
			//As per http://stackoverflow.com/questions/7633354/how-to-hide-the-arrow-buttons-in-a-jscrollbar
			@Override
			protected JButton createIncreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
			protected JButton createDecreaseButton(int orientation) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(0, 0));
				return b;
			}
		});
		
		termLabel.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		termLabel.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 18));
		list.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		list.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 14));
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		termPane.setLayout(new BoxLayout(termPane, BoxLayout.LINE_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		leftPane.setMinimumSize(new Dimension(300, 0));
		leftPane.setMaximumSize(new Dimension(400, 9999));
		termText.setMaximumSize(new Dimension(9999, 30));
		resultPane.setMinimumSize(new Dimension(300, 0));
		leftPane.setPreferredSize(new Dimension(300, 0));
		leftPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		leftPane.add(termPane);
		leftPane.add(resultPane);
		termPane.add(termLabel);
		termPane.add(termText);
		resultPane.setViewportView(list);
		buttonPane.add(openButton);
		buttonPane.add(editButton);
		leftPane.add(buttonPane);
		
		list.setBackground(ShokuhinMain.themeColour);
		
		//Decorate the buttons on the left pane
		decorButton(openButton, 20, 40);
		decorButton(editButton, 20, 40);
		
		//Try to load in the packaged White Shokuhin Logo
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("src/ShokuhinInvert.png");
			if (input == null)
				input = classLoader.getResourceAsStream("ShokuhinInvert.png");
			image = ImageIO.read(input);
		} catch (Exception e1) {
			e1.printStackTrace();
			image = null;
		}
		
		//Add a listener to the search term text field, to perform immediate searches
		termText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e){

			}

			@Override
			public void keyReleased(KeyEvent e){
				actionPerformed(null);
			}

			@Override
			public void keyPressed(KeyEvent e){}
		});
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		//Open the selected recipe, when View is pressed
		openButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (listModel.isEmpty() || list.getSelectedValue() == null)
				{
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
				getPar().openTab(new RecipeViewer(getPar(),
						RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + list.getSelectedValue() + ".rec"))));
			}
		});
		
		//Edit the selected recipe, when Edit is pressed
		editButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (listModel.isEmpty() || list.getSelectedValue() == null)
				{
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
				getPar().openTab(new RecipeEditor(getPar(),
						RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + list.getSelectedValue() + ".rec"))));
			}
		});

		rightPane.setLayout(new BorderLayout());
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.PAGE_AXIS));
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.LINE_AXIS));

		topPane.add(create);
		topPane.add(search);
		topPane.add(sync);
		rightPane.add(topPane, BorderLayout.NORTH);
		rightPane.add(bottomPane, BorderLayout.PAGE_END);
		
		//Set panels to transparent, to show the background behind
		topPane.setOpaque(false);
		rightPane.setOpaque(false);
		leftPane.setOpaque(false);
		buttonPane.setOpaque(false);
		termPane.setOpaque(false);
		for (Component c : topPane.getComponents()){
			if (c instanceof JButton){
				decorButton((JButton)c, 60, 100);
			}
		}
		
		add(leftPane);
		add(new JPanel()); //Produce white space between the left and right panes
		add(rightPane);
		
		//Open a Search module, when the Search button is pressed
		search.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getPar().openTab(new RecipeSearch(getPar()));
			}
		});
		
		//Open a Recipe Viewer in Creation Mode, when the Create button is pressed
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Please enter a name for the new Recipe.");
				if (name == null || name.trim().equals(""))
					return;
				
				if (name.contains("&")){
					name = name.replaceAll("&", "and");
				}
				
				ArrayList<String> names = RecipeMethods.getRecipeFileNames();
				for (String s : names){
					if (s.toLowerCase().trim().equals(name.toLowerCase().trim())){
						ShokuhinMain.displayMessage("Recipe Exists", "A Recipe with this title already exists.", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				getPar().openTab(new RecipeEditor(getPar(), name));
			}
		});
		
		//Start the synchronisation process, when the Sync button is pressed
		sync.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getPar().authAndSync();
			}
		});
		
		prepareCards();
		
		actionPerformed(null);
	}
	
/**
 * Download recipes, and add them to the Cards ArrayList. Start the Carousel after one recipe.
 */
	private void prepareCards(){
		String url = "http://www.bbcgoodfood.com/recipes/";
		
		//Download newest recipes from BBC Good Food, in a multi-threaded manner
		try {
			new Thread(new Runnable() {
				
				@Override
				public void run(){
					Document doc;
					try {
						doc = RecipeMethods.getDocument(url);
						Element div = doc.getElementById("new-recipes");
						Elements els = div.getElementsByClass("vertical-push-item");
						
						ExecutorService threads = Executors.newCachedThreadPool();
						for (Element e : els){
							Callable<Card> CallCard = new CardRetrieve(e, url, getPar());
							Future<Card> futureCard = threads.submit(CallCard);
							cards.add(futureCard.get());
							if (cards.size() == 1)
								StartCarousel();
						}
						
					} catch (Exception e1) {
						//Nothing important happened
					}
					
				}
			}).start();
		} catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			bottomPane.add(new Card(getPar(), "", image, "Unable to connect to BBC Good Food."));
		}
	}
	
	/**
	 * Start a Carousel of the recipes from BBC Good Food
	 */
	private void StartCarousel(){
		if (cards.isEmpty()){
			bottomPane.add(new Card(getPar(), "", image, "Unable to connect to BBC Good Food."));
			return;
		}
		
		Card card = cards.get(0); 
		bottomPane.add(card);
		currentCard = card;
		
		//Start a scheduled task to rotate the carousel
		Timer timer = new Timer();
		TimerTask t = new TimerTask() {
			
			@Override
			public void run() {
				int lastIndex = cards.indexOf(currentCard);
				int size = cards.size();
				Card c;
				
				if (lastIndex+1 < size){
					c = cards.get(lastIndex+1);
				} else {
					c = cards.get(0);
				}

				bottomPane.remove(currentCard);
				bottomPane.add(c);
				currentCard = c;
				bottomPane.repaint();
			}
		};
		
		timer.scheduleAtFixedRate(t, 0, 10000);
	}
	
	/**
	 * Decorate a JButton to suit the Home theme
	 * @param c The button to decorate
	 * @param fontSize the font size to use for the button's text
	 * @param buttonHeight the height to get the button (for spacing)
	 */
	public void decorButton(JButton c, int fontSize, int buttonHeight){
		((JButton)c).setOpaque(false);
		((JButton)c).setPreferredSize(new Dimension(0, buttonHeight));
		((JButton)c).setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, fontSize));
		((JButton)c).setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		((JButton)c).setContentAreaFilled(false);
		((JButton)c).setBorderPainted(true);
		((JButton)c).addMouseListener(new HoverMouseListener());
	}
	
	/**
	 * Draw a background onto the HomeScreen
	 */
	@Override
	protected void paintComponent(java.awt.Graphics g) {
		
		super.paintComponent(g);
		
		//Resize the buttons, according to the window size
		for (Component c : topPane.getComponents()){
			if (c instanceof JButton){
				((JButton)c).setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, rightPane.getHeight()/10));
			}
		}
		
		//Draw a rounded rectangle as the background
		g.setColor(ShokuhinMain.themeColour);
		g.fillRoundRect(rightPane.getX(), rightPane.getY(), rightPane.getWidth(), rightPane.getHeight()/2, 25, 25);
		
			//Draw the White Shokuhin logo
			if (image != null)
				g.drawImage(image,
						(rightPane.getX()+rightPane.getWidth()/2)-163,
						(rightPane.getY()+rightPane.getHeight()/2)-260
						, 327, 270, null);
	};
	
	@Override
	public boolean close()
	{
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Home");

		// A menu item that can be used when there are no functions to be
		// displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

	/**
	 * Perform immediate searching of recipes.
	 * <br />
	 * Currently inefficient, shouldn't need to load in all recipe names every time.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		listModel.clear();
		recipes = RecipeMethods.getRecipeFileNames();
		String searchTerm = termText.getText();
		try
		{
			recipes.removeIf(new Predicate<String>() {
				@Override
				public boolean test(String t) {
					if (t.toLowerCase().contains(searchTerm))
						return false;
					else
						return true;
				}
			});

			
			for (String title : recipes){
				listModel.addElement(new String(title.getBytes(), "UTF-8").trim());
			}

		}
		catch (Exception ex)
		{
			ShokuhinMain.displayMessage("Error", "Unable to find results\n" + ex.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void KeyPressed(KeyEvent e) {}

	@Override
	public void windowStateChanged(WindowEvent e) {}
}

/**
 * Class to represent a task for the Executor threadpool to retrieve a Card with
 * @author Shaylen Pastakia
 *
 */
class CardRetrieve implements Callable<Card>{
	private Element e;
	private String url;
	private ShokuhinMain main;
	
	public CardRetrieve(Element e, String url, ShokuhinMain main){
		this.e = e;
		this.url = url;
		this.main = main;
	}
	
	@Override
	public Card call() throws Exception {
		String href = url + e.select("a[href]").attr("href").replaceAll("/recipes/", "");
		Document recDoc = RecipeMethods.getDocument(href);
		//Get the image and desc, using the href.
		Element img = recDoc.select("img[itemprop]").first();
		String src = img.absUrl("src");
		URL imgUrl = new URL(src);
		Image bufImg = ImageIO.read(imgUrl.openStream());
		Elements titleEls = recDoc.select("h1[itemprop=name]");
		Elements descEls = recDoc.select("div[itemprop=description]");
		Element descP = descEls.select("p").first();
		return new Card(main, href, bufImg, titleEls.first().ownText() + ":\n" + descP.ownText());
	}
	
}

/**
 * Helper Class to represent a 'card' to display in the Carousel
 * @author Shaylen Pastakia
 *
 */
class Card extends JPanel {
	private static final long serialVersionUID = -651625780618074977L;

	private ShokuhinMain main;
	
	private String url;
	
	private JLabel imageIcon = new JLabel();
	private JTextArea descriptionPane = new JTextArea();
	private JPanel buttonPane = new JPanel();
	private JButton viewButton = new JButton("View");
	private JButton editButton = new JButton("Edit");
	private JButton webButton = new JButton("Web");
	
	/**
	 * 
	 * @param _main The instance of ShokuhinMain, in order to use openTab()
	 * @param _url The URL for the webpage of the Recipe this Card refers to 
	 * @param _img The image from the webpage, to represent the recipe
	 * @param _desc The description from the webpage, to describe the recipe
	 */
	public Card(ShokuhinMain _main, String _url, Image _img, String _desc){
		main = _main;
		url = _url;
		
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS));
		setOpaque(false);
		descriptionPane.setMaximumSize(new Dimension(600, 9999));
		imageIcon.setIcon(new ImageIcon(_img.getScaledInstance(300, 250, Image.SCALE_DEFAULT)));
		descriptionPane.setText(_desc);
		descriptionPane.setEditable(false);
		descriptionPane.setBackground(ShokuhinMain.themeColour);
		descriptionPane.setBorder(null);
		descriptionPane.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 25));
		descriptionPane.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		descriptionPane.setLineWrap(true);
		descriptionPane.setWrapStyleWord(true);
		buttonPane.setOpaque(false);
		add(imageIcon);
		add(descriptionPane);
		
		descriptionPane.setOpaque(false);
		
		buttonPane.add(viewButton);
		buttonPane.add(editButton);
		buttonPane.add(webButton);
		
		viewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Recipe r = RecipeMethods.parseRecipeUrl(url);
				if (r.getTitle().contains("&")){
					String title = r.getTitle().replaceAll("&", "and");
					r.setTitle(title);
				}
				main.openTab(new RecipeViewer(main, r));
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Recipe recipe = RecipeMethods.parseRecipeUrl(url);
				if (recipe.getTitle().contains("&")){
					String title = recipe.getTitle().replaceAll("&", "and");
					recipe.setTitle(title);
				}
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
			}
		});
		
		webButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException | URISyntaxException e1) {
					ShokuhinMain.displayMessage("Unable to open", "Unable to open the Web Browser", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});
		
		add(buttonPane);
		
		for (Component c : buttonPane.getComponents()){
			if (c instanceof JButton){
				((JButton)c).setOpaque(false);
				((JButton)c).setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 50));
				((JButton)c).setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
				((JButton)c).setContentAreaFilled(false);
				((JButton)c).setBorderPainted(true);
				((JButton)c).addMouseListener(new HoverMouseListener());
			}
		}
	}
	
	public String getUrl(){
		return url;
	}
	
	@Override
	protected void paintComponent(Graphics g){
		g.setColor(ShokuhinMain.themeColour);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
	}
}

/**
 * Helper Class to provide a white-black mouse on-off feature to any button with this listener
 * @author Shaylen Pastakia
 *
 */
class HoverMouseListener implements MouseListener {
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {
		((JButton)e.getSource()).setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
	}	
	@Override
	public void mouseEntered(MouseEvent e) {
		((JButton)e.getSource()).setForeground(ShokuhinMain.ANY_COLOUR_SO_LONG_AS_IT_IS_BLACK);
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
}