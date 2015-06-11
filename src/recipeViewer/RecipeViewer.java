package recipeViewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import javax.sound.sampled.AudioInputStream;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import main.Module;
import main.ShokuhinMain;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.util.data.audio.AudioPlayer;
import recipe.Recipe;
import recipeEditor.RecipeEditor;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class RecipeViewer extends Module {
	private static final long serialVersionUID = 2180887752409681176L;
	private Recipe recipe;
	private ShokuhinMain main;
	
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		private JScrollPane ingredientsPane = new JScrollPane();
		private JPanel methodPane = new JPanel();
			private JScrollPane firstStepPane = new JScrollPane();
			private JScrollPane secondStepPane = new JScrollPane();
			private JPanel controlPane = new JPanel();
			private JPanel infoPane = new JPanel();
			
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> ingredientsList = new JList<String>(listModel);
	private JTextArea firstStepText = new JTextArea();
	private JTextArea secondStepText = new JTextArea();
	private JTextArea infoText = new JTextArea();
	private JButton previousButton = new JButton("<- Previous Step");
	private JButton readButton = new JButton("Read out Current Step");
	private JButton nextButton = new JButton("Next Step ->");
	
	private LinkedList<String> steps = new LinkedList<String>();
	private Stack<String> previousSteps = new Stack<String>();
	
	private Thread thread;
	
	public RecipeViewer(ShokuhinMain m, Recipe recipe) {
		super(m, recipe.getTitle());
		this.recipe = recipe;
		this.main = m;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//Configure the Panel structures and layouts
		splitPane.setLeftComponent(ingredientsPane);
		splitPane.setRightComponent(methodPane);
		ingredientsPane.setMinimumSize(new Dimension(200, 0));
		ingredientsPane.setViewportView(ingredientsList);
		
		methodPane.setLayout(new BoxLayout(methodPane, BoxLayout.PAGE_AXIS));
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.LINE_AXIS));
		infoPane.setLayout(new BoxLayout(infoPane, BoxLayout.PAGE_AXIS));
		
		methodPane.add(firstStepPane);
		methodPane.add(secondStepPane);
		methodPane.add(controlPane);
		methodPane.add(infoPane);
		
		firstStepPane.setBorder(BorderFactory.createTitledBorder("Current Step"));
		secondStepPane.setBorder(BorderFactory.createTitledBorder("Next Step"));
		infoPane.setBorder(BorderFactory.createTitledBorder("Information"));
		
		firstStepPane.setViewportView(firstStepText);
		secondStepPane.setViewportView(secondStepText);
		infoPane.add(infoText);
		controlPane.add(previousButton);
		controlPane.add(readButton);
		controlPane.add(nextButton);
		
		previousButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
		readButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
		nextButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
		
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previousStep();
			}
		});
		readButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				read(firstStepText.getText());
			}
		});
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextStep();
			}
		});
		
		
		firstStepText.setFont(new Font("SansSerif", Font.BOLD, 18));
		secondStepText.setFont(new Font("SansSerif", Font.PLAIN, 12));
		firstStepText.setEditable(false);
		secondStepText.setEditable(false);
		infoText.setEditable(false);
		infoPane.setMaximumSize(new Dimension(9999, 500));
		
		//Append Tags to info
		String tags = "Tags: ";
		for (String s : recipe.getTags()){
			tags = tags.concat(s);
			if (recipe.getTags().indexOf(s) < recipe.getTags().size()-1){
				tags = tags.concat(", ");
			} else {
				tags = tags.concat(".").trim();
			}
		}
		infoText.append(tags + "\n");
		
		//Append Rating to info
		char[] stars = new char[] {'\u2606', '\u2606', '\u2606', '\u2606', '\u2606'};
		for (int i = 0; i < recipe.getRating(); i++)
			stars[i] = '\u2605';
		infoText.append("Rating: " + String.valueOf(stars) + "\n");
		
		//Append Course to info
		String[] courses = new String[]{"Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "General"};
		infoText.append("Course: " + courses[recipe.getCourse()] + "\n");
		add(splitPane);
		
		//Append Time to info
		infoText.append("Preparation time: " + recipe.getPrepTime() + " minutes, Cooking Time: " + recipe.getCookTime() + " minutes\n");
		
		//Append Servings to info
		infoText.append("Serves: " + recipe.getServings());
		
		displayRecipe();
	}
	
	private void displayRecipe(){
		//Add all Ingredients to the JList
		for (String s : recipe.getIngredients())
			listModel.addElement(s);
		
		//Load the first two steps
		steps.addAll(recipe.getMethodSteps());
		nextStep();
	}
	
	private void previousStep(){
		try {
			String s = previousSteps.pop();
			steps.addFirst(s);
			secondStepText.setText(steps.peek());
			
			if (previousSteps.isEmpty())
				firstStepText.setText("");
			else
				firstStepText.setText(previousSteps.peek());
			
		} catch (Exception e){
			
		}
	}
	
	private void nextStep(){
		try {
			String s = steps.pop();
			previousSteps.push(s);
			firstStepText.setText(s);
			s = steps.peek();
			secondStepText.setText(s);
		} catch (Exception e){
			
		}
	}

	@Override
	public boolean close() {
		return true;
	}
	
	private void read(String text){
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					MaryInterface marytts = new LocalMaryInterface();
					Set<String> voices = marytts.getAvailableVoices();
					marytts.setVoice(voices.iterator().next());
					AudioInputStream audio = marytts.generateAudio(text);
					AudioPlayer player = new AudioPlayer(audio);
					player.start();
					player.join();
				} catch (Exception e) {
					Voice voice;
					VoiceManager voiceManager = VoiceManager.getInstance();
					voice = voiceManager.getVoice("kevin16");
					voice.allocate();
					voice.speak(text);
				}
			}
		});
		thread.start();
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Recipe Viewer");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem editRecipe = new JMenuItem("Edit current Recipe");
		editRecipe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main.openTab(new RecipeEditor(main, recipe));
			}
		});
		menu.add(editRecipe);
		return menu;
	}

}
