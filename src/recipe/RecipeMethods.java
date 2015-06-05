package recipe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import main.ShokuhinMain;

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

}
