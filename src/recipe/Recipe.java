package recipe;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * @author Shaylen Pastakia
 *
 */
public class Recipe implements Serializable{
	private static final long serialVersionUID = -800402407141475554L;
	
	public static final int BREAKFAST = 0;
	public static final int LUNCH = 1;
	public static final int DINNER = 2;
	public static final int DESSERT = 3;
	public static final int SNACK = 4;
	public static final int GENERAL = 5;
	
	private String title; //Primary Key - will be used as the file name
	private ArrayList<String> ingredients = new ArrayList<String>(); //List of Ingredients
	private ArrayList<String> methodSteps = new ArrayList<String>(); //List of steps in the method
	private int course = 5; //Course that this Recipe suits (use defined constants)
	private ArrayList<String> tags = new ArrayList<String>(); //List of tags/keywords that apply to this Recipe
	private int prepTime; //The amount of preparation time for the dish in mins
	private int cookTime; //The amount of cooking time for the dish in mins (add to prepTime for Total Time)
	private int rating = 0; //The user's rating for the dish. Assert value between 0-5, where 0 is no rating applied
	private int servings; //The number of servings this Recipe caters for
	
	private Timestamp lastModificationDate; //The SQL compatible date, for synchronisation.
	
	/**
	 * Create a full recipe
	 * <br>
	 * Populate every field when creating a new recipe
	 */
	public Recipe (String title, ArrayList<String> ingredients, ArrayList<String> methodSteps,
			   int course, ArrayList<String> tags, int prepTime, int cookTime, int rating, int servings){
		this.title = title;
		this.ingredients = ingredients;
		this.methodSteps = methodSteps;
		this.course = course;
		this.tags = tags;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.rating = rating;
		this.servings = servings;
		this.lastModificationDate = new Timestamp(new Date().getTime());
	}
	
	public Timestamp getLastModificationDate() {
		return lastModificationDate;
	}

	/**
	 * Sets the date of a Recipe
	 * @param lastModification The date the recipe was last modified.
	 * <br>
	 * Use the ridiculous value of: Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
	 */
	public void setLastModificationDate(Timestamp lastModification) {
		this.lastModificationDate = lastModification;
	}

	/**
	 * Constructor
	 * @param title The title and filename to give this Recipe
	 */
	public Recipe(String title) {
		this.title = title;
		this.lastModificationDate = new Timestamp(new Date().getTime());
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<String> getIngredients() {
		return ingredients;
	}

	public void setIngredients(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}

	public ArrayList<String> getMethodSteps() {
		return methodSteps;
	}

	public void setMethodSteps(ArrayList<String> methodSteps) {
		this.methodSteps = methodSteps;
	}

	public int getCourse() {
		return course;
	}

	public void setCourse(int course) {
		this.course = course;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}

	public int getCookTime() {
		return cookTime;
	}

	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getServings() {
		return servings;
	}

	public void setServings(int servings) {
		this.servings = servings;
	}

}
