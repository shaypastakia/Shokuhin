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

import javax.swing.JOptionPane;

import main.ShokuhinMain;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	 * Attempt to parse a Recipe from BBC Good Food
	 * @param url The full URL of the Recipe to parse
	 * @return the Recipe parsed from the URl
	 */
	public static Recipe parseBBCGoodFood(String url){
		Recipe parsedRecipe = new Recipe("");
		if (!url.contains("bbcgoodfood") || !url.contains("recipes")){
			System.out.println("Invalid URL. Cannot parse from " + url + "\n" + "Please use a recipe from www.bbcgoodfood.com");
			return null;
		}
		
		try {
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
		
		//Get the title of the Recipe
		Elements titleElement = doc.getElementsByAttributeValue("itemprop", "name");
		String titleElementText = titleElement.get(0).text();
		parsedRecipe.setTitle(titleElementText);
		
		//Get the rating of the Recipe
		String ratingValue = doc.getElementsByAttributeValue("itemprop", "ratingValue").get(0).attr("content");
		parsedRecipe.setRating((int) Math.round(Double.parseDouble(ratingValue)));
		
		//Get the ingredients of the Recipe
		Elements ingredientsElement = doc.getElementsByAttributeValue("itemprop", "ingredients");
		ArrayList<String> ingredients = new ArrayList<String>();
		for (Element e : ingredientsElement){
			ingredients.add(e.text());
		}
		parsedRecipe.setIngredients(ingredients);
		
		//Get the method steps of the Recipe
				Elements methodElement = doc.getElementsByAttributeValue("itemprop", "recipeInstructions");
				ArrayList<String> methodSteps = new ArrayList<String>();
				for (Element e : methodElement){
					methodSteps.add(e.text());
				}
				parsedRecipe.setMethodSteps(methodSteps);
				
		return parsedRecipe;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
