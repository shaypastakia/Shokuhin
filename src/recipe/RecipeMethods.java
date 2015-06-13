package recipe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.swing.JOptionPane;

import main.ShokuhinMain;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.util.data.audio.AudioPlayer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class RecipeMethods {
	
	/**
	 * Write a Recipe to the hard drive, allowing it to be persistently stored.
	 * @param rec The Recipe to write to disk
	 * @param file The file representing the file path to be written to
	 * @return True if the file write succeeded
	 */
	public static boolean writeRecipe(Recipe rec, File file){
		String filePath = file.getAbsolutePath();
		String[] split = filePath.split("\\\\");
		int end = split.length-1;
		String fileDir = "";
		for (int i = 0; i < end; i++)
			fileDir += split[i] + "\\";
		
		Path fullFile = new File(filePath).toPath();
		Path dirFile = new File(fileDir).toPath();
		
		try {
			Files.createDirectories(dirFile);
			Files.createFile(fullFile);
			OutputStream fileOut = Files.newOutputStream(fullFile);
			BufferedOutputStream buff = new BufferedOutputStream(fileOut);
			ObjectOutputStream obj = new ObjectOutputStream(buff);
			obj.writeObject(rec);
			obj.close();
			
			return true;
		} catch (Exception e){
			ShokuhinMain.displayMessage("Failed to write Recipe", "Returned the error: " + e.getMessage(), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Read a Recipe from the hard drive.
	 * @param file The file representing the file path to be read as a Recipe object
	 * @return The Recipe read in from the specified file
	 */
	public static Recipe readRecipe(File file){
		try {
			String filePath = file.getAbsolutePath();
			InputStream fileIn = Files.newInputStream(new File(filePath).toPath());
			BufferedInputStream buff = new BufferedInputStream(fileIn);
			ObjectInputStream obj = new ObjectInputStream(buff);
			Recipe rec = (Recipe) obj.readObject();
			obj.close();
			return rec;
		} catch (Exception e){
			ShokuhinMain.displayMessage("Failed to write Recipe", "Returned the error: " + e.getMessage(), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean deleteRecipe(Recipe recipe){
		File file = new File("./Shokuhin/Recipes/" + recipe.getTitle() + ".rec");
		try {
			return Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			ShokuhinMain.displayMessage("Failed to delete Recipe", "Returned the error: " + e.getMessage(), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Get all Filenames from the Recipes folder
	 * @return An ArrayList of Strings, containing the Recipe names
	 */
	public static ArrayList<String> getRecipeFileNames(){
		File parent = new File("./Shokuhin/Recipes/");
		ArrayList<String> files = new ArrayList<String>();
		for (File file: parent.listFiles()){
			if (file.isFile() && file.getAbsolutePath().endsWith(".rec"))
				files.add(file.getName().replaceAll(".rec", ""));
		}
		
		return files;
	}
	
	/**
	 * Attempt to parse a Recipe from BBC Good Food or All Recipes
	 * @param url The full URL of the Recipe to parse
	 * @return the Recipe parsed from the URL
	 */
	public static Recipe parseRecipeUrl(String url){
		if (url == null){
			return null;
		}
		//Don't proceed if the link isn't a valid Recipe URL
		if (!url.contains("recipe")){
			System.out.println("Invalid URL. Cannot parse from " + url + "\n" + "Please use a recipe from http://www.bbcgoodfood.com or http://allrecipes.co.uk");
			return null;
		}
		
		//Add protocol if it isn't already there
		if(!url.startsWith("http://"))
			url = "http://www.bbcgoodfood.com".concat(url);
		
		try {
			System.out.println(url);
		//Connect to the Recipe URL
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestProperty("User-Agent", "Chrome");
		
		//Get the resulting page 
		InputStreamReader in = new InputStreamReader(connection.getInputStream());
		BufferedReader bufIn = new BufferedReader(in);
		String temp;
		String response = "";
		
		//Write the page into a String
		while ((temp = bufIn.readLine()) != null){
			response = response.concat(temp);
		}
		bufIn.close();
		//Produce a HTML Document from the response
		Document doc = Jsoup.parse(response);
		if(url.contains("bbcgoodfood"))
			return parseBBCGoodFood(doc);
		else if(url.contains("bbc"))
			return parseBBC(doc);
		else if(url.contains("allrecipes"))
			return parseAllRecipes(doc);
		else
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static Recipe parseBBCGoodFood(Document doc){
		Recipe parsedRecipe = new Recipe("");
		
		try {
			//Get the title of the Recipe
			Elements titleElement = doc.getElementsByAttributeValue("itemprop", "name");
			String titleElementText = new String(titleElement.get(0).text().getBytes(), "UTF-8");
			parsedRecipe.setTitle(titleElementText);
			
			//Get the rating of the Recipe
			String ratingValue = doc.getElementsByAttributeValue("itemprop", "ratingValue").get(0).attr("content");
			parsedRecipe.setRating((int) Math.round(Double.parseDouble(ratingValue)));
			
			//Get the ingredients of the Recipe
			Elements ingredientsElement = doc.getElementsByAttributeValue("itemprop", "ingredients");
			ArrayList<String> ingredients = new ArrayList<String>();
			for (Element e : ingredientsElement){
				ingredients.add(new String(e.text().replaceAll("\\. ", "\\.\n").getBytes(), "UTF-8"));
			}
			parsedRecipe.setIngredients(ingredients);
			
			//Get the method steps of the Recipe
			Elements methodElement = doc.getElementsByAttributeValue("itemprop", "recipeInstructions");
			ArrayList<String> methodSteps = new ArrayList<String>();
			for (Element e : methodElement){
				methodSteps.add(new String(e.text().replaceAll("\\. ", "\\.\n").getBytes(), "UTF-8"));
			}
			parsedRecipe.setMethodSteps(methodSteps);
					
			return parsedRecipe;
		} catch (Exception e){
			return null;
		}
	}
	
	private static Recipe parseAllRecipes(Document doc){
		Recipe parsedRecipe = new Recipe("");
		
		try {
			//Get the title of the Recipe
			Elements titleElement = doc.getElementsByAttributeValue("itemprop", "name");
			String titleElementText = titleElement.get(0).text();
			parsedRecipe.setTitle(new String(titleElementText.getBytes(), "UTF-8"));
			
			//Get the rating of the Recipe
			Element ratingElement = doc.getElementById("starRating");
			String[] ratingHTML = ratingElement.getAllElements().get(0).html().split("rating");
			int ratingRaw = Integer.parseInt(ratingHTML[1].split("\"")[0]);
			if (ratingRaw <= 5){
				parsedRecipe.setRating(ratingRaw);
			} else {
				parsedRecipe.setRating((ratingRaw+5)/10);
			}
			
			//Get the ingredients of the Recipe
			Elements ingredientsElement = doc.getElementsByAttributeValue("itemprop", "ingredients");
			ArrayList<String> ingredients = new ArrayList<String>();
			for (Element e : ingredientsElement){
				ingredients.add(new String(e.text().getBytes(), "UTF-8"));
			}
			parsedRecipe.setIngredients(ingredients);

			//Get the method steps of the Recipe
			Elements methodElement = doc.getElementsByAttributeValue("itemprop", "recipeInstructions");
			doc = Jsoup.parse(methodElement.html());
			methodElement = doc.select("span");
			ArrayList<String> methodSteps = new ArrayList<String>();
			for (Element e : methodElement){
				methodSteps.add(new String(e.text().replaceAll("\\. ", "\\.\n").getBytes(), "UTF-8"));
			}
			parsedRecipe.setMethodSteps(methodSteps);

			return parsedRecipe;
		} catch (Exception e){
			return null;
		}
	}
	
	private static Recipe parseBBC(Document doc){
		Recipe parsedRecipe = new Recipe("");
		
		try {
			//Get the title of the Recipe
			Elements titleElement = doc.getElementsByClass("article-title");
			String titleElementText = new String(titleElement.get(0).text().getBytes(), "UTF-8");
			parsedRecipe.setTitle(titleElementText);
			
			//Get the ingredients of the Recipe
			Elements ingredientsElement = doc.getElementsByAttributeValue("id", "ingredients");
			ingredientsElement = Jsoup.parse(ingredientsElement.html()).select("li");
			ArrayList<String> ingredients = new ArrayList<String>();
			for (Element e : ingredientsElement){
				ingredients.add(new String(e.text().replaceAll("\\. ", "\\.\n").getBytes(), "UTF-8"));
			}
			parsedRecipe.setIngredients(ingredients);
			
			//Get the method steps of the Recipe
			Elements methodElement = doc.getElementsByClass("instructions");
			methodElement = Jsoup.parse(methodElement.html()).select("li");
			ArrayList<String> methodSteps = new ArrayList<String>();
			for (Element e : methodElement){
					Elements el = e.select("p");
					methodSteps.add(new String(el.get(0).ownText().replaceAll("\\. ", "\\.\n").getBytes(), "UTF-8"));
				}
			parsedRecipe.setMethodSteps(methodSteps);

			return parsedRecipe;
		} catch (Exception e){
			return null;
		}
	}
	
	/**
	 * Speak out the Current Step with Mary, or fall back to Kevin if need be
	 * @param text The text to be read out
	 */
	public static void read(String text){
		Thread thread = new Thread(new Runnable() {
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

}
