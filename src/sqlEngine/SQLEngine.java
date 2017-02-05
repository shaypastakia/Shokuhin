package sqlEngine;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import generalResources.Pair;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;

/**
 * Class used to contain methods for interfacing with SQL Server
 * @author Shaylen Pastakia
 *
 */
public class SQLEngine {
	
	private String db_url; //jdbc:mysql://www.db4free.net:3306/shokuhin
	private String user; //shokuhin
	private String pass;
	
	public Connection conn = null;
	private PreparedStatement stmt = null;

	private String sql;
	
	private enum exists{
		FAILED, NO, YES;
	}
	
	/**
	 * 
	 * @param url The SQL Database URL to connect to.
	 * @param user The username to connect with.
	 * @param pass The password to connect with.
	 */
	public SQLEngine(String url, String user, String pass){
		this.db_url = url;
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * Synchronise Recipes with an SQL Server
	 * @param silent True, if the user should be prompted with a confirmation dialog. False, if the process should perform without dialogs.<br>Any errors will still result in dialogs.
	 */
	public boolean synchronise(boolean silent){
		
		if (!connect())
			return false;
		
		final ArrayList<String> originalLocal = RecipeMethods.getRecipeFileNames();
		final ArrayList<String> originalRemote = getAllRecipeTitles();
		
		final ArrayList<Pair> originalLocalUpdate = RecipeMethods.getLastModificationDates();
		final ArrayList<Pair> originalRemoteUpdate = getLastModificationDates();
		
		ArrayList<String> localUpdates = new ArrayList<String>();
		ArrayList<String> remoteUpdates = new ArrayList<String>();
		
//		List the updated recipes on the Server as well as on the Local Machine
		for (Pair loc : originalLocalUpdate){
			for (Pair rem : originalRemoteUpdate){
				if (loc.getFirst().equals(rem.getFirst())){
					if (loc.getSecond().before(rem.getSecond()))
						remoteUpdates.add(rem.getFirst());
					else if (loc.getSecond().after(rem.getSecond()))
						localUpdates.add(loc.getFirst());
//					else if (loc.getSecond().equals(rem.getSecond()))
//						System.out.println(loc.getFirst() + " has not been updated.");
				}
			}
		}
		System.out.println("loc " + localUpdates.size());
		System.out.println("rem " + remoteUpdates.size());
		
//		List the new recipes on the Server.
		ArrayList<String> newRemote = new ArrayList<String>(originalRemote);
		newRemote.removeAll(originalLocal);
		
//		List the new recipes on the Local Machine.
		ArrayList<String> newLocal = new ArrayList<String>(originalLocal);
		newLocal.removeAll(originalRemote);
		
//		If not silent, then inform the user and gain permission to proceed with Synchronisation.
		String msg = "";
		int option;
		if (remoteUpdates.size() > 0)
			msg += "There are " + remoteUpdates.size() + " updated recipe(s) on the Server to download.\n\n";
		
		if (localUpdates.size() > 0)
			msg += "There are " + localUpdates.size() + " updated recipe(s) on the Local Machine to upload.\n\n";
		
		if (newRemote.size() > 0)
			msg += "There are " + newRemote.size() + " new recipe(s) on the Server to download.\n\n";
		
		if (newLocal.size() > 0)
			msg += "There are " + newLocal.size() + " new recipe(s) on the Local Machine to upload.\n\n";
		
		if (!silent){
			if (newRemote.size() > 0 || newLocal.size() > 0 || localUpdates.size() > 0 || remoteUpdates.size() > 0){
				msg += "Press 'OK' to perform the above action(s), or 'Cancel' to cancel Synchronisation.";
				option = JOptionPane.OK_CANCEL_OPTION;
			} else {
				msg += "There are no actions to be performed.";
				ShokuhinMain.displayMessage("Synchronisation", msg, JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			int result = JOptionPane.showConfirmDialog(null, msg, "Files to Synchronise", option);
			
			if (result == JOptionPane.CANCEL_OPTION)
				return false;
		}
		
		System.out.println("Beginning Synchronisation");
		int total = remoteUpdates.size() + localUpdates.size() + newRemote.size() + newLocal.size();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int current = 1;
				ShokuhinMain.setSync(true);
				JLabel label = new JLabel("Preparing to sync...");
				label.setFont(new Font("Times New Roman", Font.BOLD, 18));
				JSeparator septimus = new JSeparator();
				ShokuhinMain.timer.setBackground(ShokuhinMain.SATSUMA_MANDARIN_CLEMENTINE_ORANGE);
				ShokuhinMain.timer.add(septimus);
				ShokuhinMain.timer.add(label);
				ShokuhinMain.timer.validate();
				
				//Timing code based on http://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java, answer by Sufiyan Ghori
				Instant start = Instant.now();
				
//				Download the updated recipes from the Server.
				if (remoteUpdates.size() > 0){
					
					for (String s : remoteUpdates){
						label.setText("Synchronising file " + current + " of " + total);
						RecipeMethods.deleteRecipe(new Recipe(s));
						RecipeMethods.writeRecipe(getRecipe(s));
						current++;
						System.out.println("Downloaded " + s);
					}
				}
//				Upload the updated recipes from the Local Machine.
				if (localUpdates.size() > 0){
					for (String s : localUpdates){
						label.setText("Synchronising file " + current + " of " + total);
						updateRecipe(RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec")));
						current++;
						System.out.println("Uploaded " + s);
					}
				}
				
//				Download the updated recipes from the Server.
				if (newRemote.size() > 0){
					for (String s : newRemote){
						label.setText("Synchronising file " + current + " of " + total);
						RecipeMethods.writeRecipe(getRecipe(s));
						current++;
						System.out.println("Downloaded " + s);
					}
				}
				
//				Upload the new recipes from the Local Machine.
				if (newLocal.size() > 0){
					for (String s : newLocal){
						label.setText("Synchronising file " + current + " of " + total);
						addRecipe(RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec")));
						current++;
						System.out.println("Uploaded " + s);
					}
				}
				
				Instant end = Instant.now();
				label.setText("Sync Completed (" + Duration.between(start, end).getSeconds() + " second(s))");
				ShokuhinMain.timer.setBackground(ShokuhinMain.QUESTIONABLE_SALAD_LEAF_GREEN);
				ShokuhinMain.setSync(false);
				JButton dismiss = new JButton("Dismiss");
				ShokuhinMain.timer.add(dismiss);
				ShokuhinMain.timer.validate();
				dismiss.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ShokuhinMain.timer.remove(label);
						ShokuhinMain.timer.remove(dismiss);
						ShokuhinMain.timer.setBackground(ShokuhinMain.DEFAULT_CAMPUS_FOG);
						ShokuhinMain.timer.validate();
					}
				});
				
				if (silent)
					dismiss.doClick();
				
				System.out.println("Completed Synchronisation in " + Duration.between(start, end).getSeconds() + " seconds.");
			}
		}).start();

		return true;
	}
	
	/**
	 * Checks whether or not a Recipe exists on the SQL Server
	 * @param r The Recipe to check
	 * @return -1 if there was an error when checking.<br>0 if the Recipe does not exist.<br>1 if the Recipe does exist
	 */
	public exists recipeExists(Recipe r){
		if (!connect())
			return exists.FAILED;
		
		try {
			sql = "SELECT COUNT(*) AS total FROM recipes WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, r.getTitle());
			ResultSet rs = stmt.executeQuery();
			
			rs.next();
			System.out.println(rs.getInt("total"));
			if (rs.getInt("total") > 0){
				return exists.YES;
			}
			else 
				return exists.NO;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to check recipe existence", "Failed to check existence of " + r.getTitle() + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return exists.FAILED;
		}
	}
	
	/**
	 * Add a brand new Recipe to the SQL Server
	 * @param r The Recipe to add
	 * @return True, if the method succeeds without throwing an Exception. False otherwise.
	 */
	public boolean addRecipe(Recipe r){
		if (!connect())
			return false;
		
		try {
			exists check = recipeExists(r);
			if (check == exists.YES){
				ShokuhinMain.displayMessage("Failed to add Recipe", "The Recipe " + r.getTitle() + " already exists on the SQL Server", JOptionPane.ERROR_MESSAGE);
				return false;
			} else if (check == exists.FAILED){
				return false;
			}
			insertRecipes(r);
			insertIngredients(r);
			insertTags(r);
			insertMethodSteps(r);
			if (!insertImage(r))
				System.out.println(r.getTitle() + " has no image.");
			
		    stmt.close();
			
			return true;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to add Recipe", "Failed to add " + r.getTitle() + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	
	public boolean updateRecipe(Recipe newRec){
		
		if (!connect())
			return false;
		
		Instant start = Instant.now();
		exists check = recipeExists(newRec);
		if (check == exists.NO){
			ShokuhinMain.displayMessage("Failed to update Recipe", "The Recipe " + newRec.getTitle() + " does not exist on the SQL Server", JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (check == exists.FAILED){
			return false;
		}
		
		Recipe oldRec = RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + newRec.getTitle() + ".rec"));
		if (oldRec == null){
			ShokuhinMain.displayMessage("Error Updating", "Cannot read or find the existing " + newRec.getTitle() + " recipe.", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Instant end = Instant.now();
		System.out.println(1 + " " + Duration.between(start, end));
		
		try {
			start = Instant.now();
			//UPDATE the fields stored within the 'recipes' table.
			//UPDATE course, prepTime, cookTime, rating, servings, lastModificationDate WHERE title
			sql = "UPDATE recipes SET course = ?, prepTime = ?, cookTime = ?, rating = ?, servings = ?, lastModificationDate = ? WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, newRec.getCourse());
			stmt.setInt(2, newRec.getPrepTime());
			stmt.setInt(3, newRec.getCookTime());
			stmt.setInt(4, newRec.getRating());
			stmt.setInt(5, newRec.getServings());
			stmt.setTimestamp(6, newRec.getLastModificationDate());
			stmt.setString(7, newRec.getTitle());
			stmt.execute();
			System.out.println("Updated 'recipes'");
			end = Instant.now();
			System.out.println(2 + " " + Duration.between(start, end));
			
			start = Instant.now();
			//DELETE and INSERT ingredients if and only if the ingredients have been changed.
			if (!selectIngredients(oldRec).equals(newRec.getIngredients())){
				sql = "DELETE FROM ingredients WHERE title = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, newRec.getTitle());
				stmt.execute();
				insertIngredients(newRec);
				System.out.println("Updated 'ingredients'");
			}
			
			//DELETE and INSERT method if and only if the method have been changed.
			if (!selectMethodSteps(oldRec).equals(newRec.getMethodSteps())){
				sql = "DELETE FROM methodSteps WHERE title = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, newRec.getTitle());
				stmt.execute();
				insertMethodSteps(newRec);
				System.out.println("Updated 'methodSteps'");
			}
			
			//DELETE and INSERT tags if and only if the tags have been changed.
			if (!selectTags(oldRec).equals(newRec.getTags())){
				sql = "DELETE FROM tags WHERE title = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, newRec.getTitle());
				stmt.execute();
				insertTags(newRec);
				System.out.println("Updated 'tags'");
			}
			
			end = Instant.now();
			
			System.out.println(3 + " " + Duration.between(start, end));
			
			return true;
		} catch (IOException | SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to update Recipe", "Failed to update " + oldRec.getTitle() + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/**
	 * Retrieve a Recipe in its entirety from the SQL Server
	 * @param title The name of the Recipe to retrieve
	 * @return The Recipe in Recipe format
	 */
	public Recipe getRecipe(String title){
		if (!connect())
			return null;
		
		Recipe temp = new Recipe(title);
		exists check = recipeExists(temp);
		if (check == exists.NO){
			ShokuhinMain.displayMessage("Failed to read Recipe", "The Recipe " + title + " does not exist on the SQL Server", JOptionPane.ERROR_MESSAGE);
		} else if (check == exists.FAILED){
			return null;
		}
		
		try {
			ResultSet rs = selectRecipes(temp);
			rs.next();
			//course, prepTime, cookTime, rating, servings, lastModificationDate
			temp.setCourse(rs.getInt(1));
			temp.setPrepTime(rs.getInt(2));
			temp.setCookTime(rs.getInt(3));
			temp.setRating(rs.getInt(4));
			temp.setServings(rs.getInt(5));
			temp.setLastModificationDate(rs.getTimestamp(6));
			
			temp.setIngredients(selectIngredients(temp));
			temp.setTags(selectTags(temp));
			temp.setMethodSteps(selectMethodSteps(temp));
			return temp;
		} catch (IOException | SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to read Recipe", "Failed to read " + title + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	 * Delete a Recipe from the SQL Server.
	 * <br>
	 * Deletes from the 'recipes' table, which cascades to ingredients, methodSteps, tags, and images.
	 * @param r The Recipe to delete from the SQL Server.
	 * @return True, if the code runs without any Exceptions.
	 */
	public boolean deleteRecipe(Recipe r){
		if (!connect())
			return false;
		
		if (recipeExists(r) == exists.NO)
			return false;
		
		if (recipeExists(r) == exists.FAILED)
			return false;

		try {
			sql = "DELETE FROM recipes WHERE title = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, r.getTitle());
			stmt.execute();
			return true;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to delete Recipe", "Failed to remove " + r.getTitle() + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/**
	 * Get all the recipe titles, and their last modification dates.
	 * @return A list of Pairs. Each Pair contains the name of all recipes, and the date they were last modified.
	 */
	public ArrayList<Pair> getLastModificationDates(){
		if (!connect())
			return null;

		try {
		ArrayList<Pair> temp = new ArrayList<Pair>();
		
		sql = "SELECT title, lastModificationDate FROM recipes";
		stmt = conn.prepareStatement(sql);
		ResultSet result = stmt.executeQuery();
		
		while (result.next()){
			temp.add(new Pair(result.getString(1), result.getTimestamp(2)));
		}
		
		return temp;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to retrieve dates", "Failed to read from SQL Server." + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}

	/**
	 * Get all titles of recipes stored on the SQL Server.
	 * @return An ArrayList containing the titles of every Recipe on the SQL Server.
	 */
	public ArrayList<String> getAllRecipeTitles(){
		if (!connect())
			return null;

		try {
			ArrayList<String> temp = new ArrayList<String>();
			
			sql = "SELECT title FROM recipes";
			stmt = conn.prepareStatement(sql);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()){
				temp.add(result.getString(1));
			}
			
			return temp;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to retrieve titles", "Failed to read from SQL Server." + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	/**
	 * Remove this method once complete
	 */
	public boolean execute(String s){
		if (!connect())
			return false;

		try {
			sql = s;
			stmt = conn.prepareStatement(sql);
			return stmt.execute();
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to retrieve titles", "Failed to read from SQL Server." + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/**
	 * Helper method for add and update methods
	 * @param r 
	 * @throws SQLException
	 */
	private void insertRecipes(Recipe r) throws SQLException{
		//title, course, prepTime, cookTime, rating, servings, lastModifiedDate
		sql = "INSERT INTO recipes VALUES (?,?,?,?,?,?,?)";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, r.getTitle());
		stmt.setInt(2, r.getCourse());
		stmt.setInt(3, r.getPrepTime());
		stmt.setInt(4, r.getCookTime());
		stmt.setInt(5, r.getRating());
		stmt.setInt(6, r.getServings());
		stmt.setTimestamp(7, r.getLastModificationDate());
		stmt.execute();
	}
	
	/**
	 * Helper method for add and update methods
	 * @param r 
	 * @throws SQLException
	 */
	private void insertIngredients(Recipe r) throws SQLException{
		if (r.getIngredients().size() == 0)
			return;
		
		//Batch INSERT based on http://viralpatel.net/blogs/batch-insert-in-java-jdbc/
		sql = "INSERT INTO ingredients VALUES (?,?)";
		stmt = conn.prepareStatement(sql);
		for (String s : r.getIngredients()){
			stmt.setString(1, r.getTitle());
			stmt.setString(2, s);
			stmt.addBatch();
		}
		stmt.executeBatch();
	}
	
	/**
	 * Helper method for add and update methods
	 * @param r 
	 * @throws SQLException
	 */
	private void insertTags(Recipe r) throws SQLException{
		if (r.getTags().size() == 0)
			return;

		//Batch INSERT based on http://viralpatel.net/blogs/batch-insert-in-java-jdbc/
		sql = "INSERT INTO tags VALUES (?,?)";
		stmt = conn.prepareStatement(sql);
		for (String s : r.getTags()){
			stmt.setString(1, r.getTitle());
			stmt.setString(2, s);
			stmt.addBatch();
		}
		stmt.executeBatch();
	}
	
	/**
	 * Helper method for add and update methods
	 * @param r 
	 * @throws SQLException
	 */
	private void insertMethodSteps(Recipe r) throws SQLException{
		if (r.getMethodSteps().size() == 0)
			return;

		//Batch INSERT based on http://viralpatel.net/blogs/batch-insert-in-java-jdbc/
		sql = "INSERT INTO methodSteps VALUES (?,?)";
		stmt = conn.prepareStatement(sql);
		for (String s : r.getMethodSteps()){
			stmt.setString(1, r.getTitle());
			stmt.setString(2, s);
			stmt.addBatch();
		}
		stmt.executeBatch();
	}
	
	/**
	 * Helper method for add method
	 * @param r 
	 * @throws SQLException
	 */
	private boolean insertImage(Recipe r){
		String title = r.getTitle();
		File file = new File("./Shokuhin/Images/" + title + ".jpg");
		if (!file.exists())
			return false;
		
		try {
			//As per https://www.postgresql.org/docs/7.4/static/jdbc-binary-data.html
			FileInputStream fis;
			fis = new FileInputStream(file);
			stmt = conn.prepareStatement("INSERT INTO images VALUES (?, ?)");
			stmt.setString(1, title);
			stmt.setBinaryStream(2, fis, file.length());
			stmt.executeUpdate();
			fis.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Read the standard details of an existing Recipe from the SQL Server
	 * @param r
	 * @return The Details as a ResultSet, in order to maintain String and Timestamp format.
	 * @throws SQLException
	 */
	private ResultSet selectRecipes(Recipe r) throws SQLException{
		if (!connect())
			return null;
		
		//SELECT course, prepTime, cookTime, rating, servings, lastModificationDate FROM recipes WHERE title
		sql = "SELECT course, prepTime, cookTime, rating, servings, lastModificationDate FROM recipes WHERE title = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, r.getTitle());
		ResultSet rs = stmt.executeQuery();
		
		return rs;

	}
	
	/**
	 * Select the Ingredients of a current Recipe from the SQL Server
	 * @param r The local recipe to get the Ingredients for
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<String> selectIngredients(Recipe r) throws SQLException{
		if (!connect())
			return null;
		
		sql = "SELECT ingredient FROM ingredients WHERE title = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, r.getTitle());
		
		ResultSet rs = stmt.executeQuery();

		ArrayList<String> temp = new ArrayList<String>();
		while (rs.next())
			temp.add(rs.getString(1));
		
		return temp;
	}
	
	/**
	 * Select the Tags of a current Recipe from the SQL Server
	 * @param r The local recipe to get the Tags for
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<String> selectTags(Recipe r) throws SQLException{
		if (!connect())
			return null;
		
		sql = "SELECT tag FROM tags WHERE title = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, r.getTitle());
		
		ResultSet rs = stmt.executeQuery();

		ArrayList<String> temp = new ArrayList<String>();
		while (rs.next())
			temp.add(rs.getString(1));
		
		return temp;
	}
	
	/**
	 * Select the MethodSteps of a current Recipe from the SQL Server
	 * @param r The local recipe to get the MethodSteps for
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private ArrayList<String> selectMethodSteps(Recipe r) throws SQLException, IOException{
		if (!connect())
			return null;
		
		sql = "SELECT methodStep FROM methodSteps WHERE title = ?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, r.getTitle());
		
		ResultSet rs = stmt.executeQuery();

		ArrayList<String> temp = new ArrayList<String>();
		while (rs.next()){
			Reader reader;
			StringWriter writer = new StringWriter();
			
			reader = rs.getCharacterStream(1);
			int x;
			
			while((x = reader.read()) > -1){
				writer.write(x);
			}
			
			temp.add(writer.toString());
			reader.close();
			writer.close();
		}
		
		return temp;
	}
	
	/**
	 * Connect to the SQL Server
	 */
	public boolean connect(){
		if (db_url == null || db_url.equals("") || user == null){
			ShokuhinMain.displayMessage("Uninitialised", "The SQLEngine has not been initialised with server details.", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		try {
			if (conn != null && conn.isValid(5))
				return true;
			
			System.out.println("Connecting");
			conn = DriverManager.getConnection(db_url, user, pass);
			return true;
			
		} catch (SQLException e){
			ShokuhinMain.displayMessage("Unable to connect", "Unable to connect to the SQL Server. Please check your internet connection.", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}
