package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import recipe.RecipeMethods;
import recipeEditor.RecipeEditor;
import recipeViewer.RecipeViewer;

public class ShokuhinHome extends Module implements ActionListener {
	private static final long serialVersionUID = 3395189302456031507L;

	private JPanel leftPane = new JPanel();
		private JPanel termPane = new JPanel();
		private JScrollPane resultPane = new JScrollPane();
		private JPanel buttonPane = new JPanel();
	private JPanel rightPane = new JPanel();

	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private ArrayList<String> listUrls = new ArrayList<String>();
	
	private JLabel parentLabel = new JLabel("BBC Food & BBC Good Food Recipe Search");
	private JLabel termLabel = new JLabel("Search Term: ");
	private JTextField termText = new JTextField();
	private JButton searchButton = new JButton("Search");
	private JList<String> list = new JList<String>(listModel);
	private JButton openButton = new JButton("View Selected Recipe");
	private JButton editButton = new JButton("Edit Selected Recipe");
	
	public ShokuhinHome(ShokuhinMain m) {
		super(m, "Home");
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		termPane.setLayout(new BoxLayout(termPane, BoxLayout.LINE_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		
		termText.setMaximumSize(new Dimension(9999, 30));
		leftPane.add(parentLabel);
		leftPane.add(termPane);
		leftPane.add(resultPane);
		termPane.add(termLabel);
		termPane.add(termText);
		termPane.add(searchButton);
		resultPane.setViewportView(list);
		buttonPane.add(openButton);
		buttonPane.add(editButton);
		leftPane.add(buttonPane);
		
		termText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					actionPerformed(null);
			}
		});
		searchButton.addActionListener(this);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listModel.isEmpty()){
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
				getPar().openTab(new RecipeViewer(getPar(), RecipeMethods.parseRecipeUrl(listUrls.get(list.getSelectedIndex()))));
			}
		});
		editButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (listModel.isEmpty()){
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
				getPar().openTab(new RecipeEditor(getPar(), RecipeMethods.parseRecipeUrl(listUrls.get(list.getSelectedIndex()))));
			}
		});
		
		add(leftPane);
		add(rightPane);
	}

	@Override
	public boolean close() {
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Home");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}
	
	public void concurrentSearch(){
		ExecutorService executor = Executors.newFixedThreadPool(5);
		//Store the results in a Set
		Set<Future<String>> results = new HashSet<Future<String>>();
		
		String searchTerm;
		
		Callable<String> bbcCall = bbcSearch(searchTerm); 
		Future<String> bbcFut = executor.submit(bbcCall);
		Callable<String> wikiCall = wikipediaSearch(text.getText()); 
		Future<String> wikiFut = executor.submit(wikiCall);
	}
	
	public Callable<String> bbcSearch(final String term){
		return new Callable<String>() {

			@Override
			public String call () {
				
				return term;
			}
		};
		
	}
	
	public Callable<String> bbcGoodFoodSearch(final String term){
		return new Callable<String>() {

			@Override
			public String call () {
				
				return term;
			}
		};
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		listModel.clear();
		listUrls.clear();
		String searchTerm = termText.getText().replaceAll(" ", "\\+");
		String url = "http://www.bbcgoodfood.com/search/recipes?query=" + searchTerm;
		String url2 = "http://www.bbc.co.uk/food/recipes/search?keywords=" + searchTerm;
		
		if (searchTerm == null || searchTerm.equals(""))
			return;
		
		try {
		//Connect to the Recipe URL
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Chrome");
		
		HttpURLConnection connection2 = (HttpURLConnection) new URL(url2).openConnection();
		connection2.setRequestProperty("User-Agent", "Chrome");

		//Get the resulting page 
		InputStreamReader in = new InputStreamReader(connection.getInputStream());
		BufferedReader bufIn = new BufferedReader(in);
		String temp;
		String response = "";
		
		InputStreamReader in2 = new InputStreamReader(connection2.getInputStream());
		BufferedReader bufIn2 = new BufferedReader(in2);
		String temp2;
		String response2 = "";
		
		//Write the page into a String
		while ((temp = bufIn.readLine()) != null){
			response = response.concat(temp);
		}
		bufIn.close();
		
		while ((temp2 = bufIn2.readLine()) != null){
			response2 = response2.concat(temp2);
		}
		bufIn2.close();
		
		//Produce a HTML Document from the response
		Document doc = Jsoup.parse(response);
		Document doc2 = Jsoup.parse(response2);
		
		Elements links2 = doc2.select("div.left");
		System.out.println(links2.size());
		for (Element el : links2){
			listModel.addElement(new String(el.child(0).text().getBytes(), "UTF-8").trim());
			listUrls.add("http://www.bbc.co.uk" + el.child(0).child(0).attr("href"));
		}
		
		Elements links = doc.getElementsByClass("node-title");
		for (Element el : links){
			listModel.addElement(new String(el.text().getBytes(), "UTF-8").trim());
			listUrls.add(el.select("a").get(0).attr("href"));
		}
		
		leftPane.paintAll(leftPane.getGraphics());
		if (listUrls.isEmpty())
			ShokuhinMain.displayMessage("No Results", "Unable to find any results", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex){
			ShokuhinMain.displayMessage("Error", "Unable to find results\n" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
}