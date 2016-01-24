package sqlEngine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import main.ShokuhinMain;
import marytts.util.Pair;
import recipe.Recipe;

/**
 * Class used to contain methods for interfacing with SQL Server
 * @author Shaylen Pastakia
 *
 */
public class SQLEngine {
	
	private String db_url; //jdbc:mysql://www.db4free.net:3306/shokuhin
	private String user; //shokuhin
	private String pass;
	
	private Connection conn = null;
	private PreparedStatement stmt = null;

	private String sql;
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
	
	public boolean addRecipe(Recipe r){
		if (!connect())
			return false;
		
		try {
			//title, course, prepTime, cookTime, rating, servings, lastModifiedDate
			sql = "INSERT INTO recipe VALUES (?,?,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, r.getTitle());
			stmt.setInt(2, r.getCourse());
			stmt.setInt(3, r.getPrepTime());
			stmt.setInt(4, r.getCookTime());
			stmt.setInt(5, r.getRating());
			stmt.setInt(6, r.getServings());
			stmt.setTimestamp(7, r.getLastModificationDate());
			boolean result = stmt.execute();

//			TODO Add Ingredients, Tags, MethodSteps to their respective tables.
		    stmt.close();
		    conn.close();
			
			return result;
		} catch (SQLException e){
			e.printStackTrace();
			ShokuhinMain.displayMessage("Failed to add Recipe", "Failed to add " + r.getTitle() + "\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);;
			return false;
		}
	}
	
	public boolean updateRecipe(Recipe r){
		if (!connect())
			return false;
		
		return true;
	}
	
	public Recipe getRecipe(){
		if (!connect())
			return null;
		
		return null;
	}
	
	public boolean deleteRecipe(){
		if (!connect())
			return false;
		
		return true;
	}
	
	public ArrayList<Pair<String, Timestamp>> getLastModificationDates(){
		
		return null;
	}
	
	/**
	 * Connect to the SQL Server
	 */
	private boolean connect(){
		if (db_url == null || db_url.equals("") || user == null || pass == null){
			ShokuhinMain.displayMessage("Uninitialised", "The SQLEngine has not been initialised with server details.", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		try {
			if (conn != null && conn.isValid(5))
				return true;
			
			conn = DriverManager.getConnection(db_url, user, pass);
			return true;
			
		} catch (SQLException e){
			return false;
		}
	}
}
