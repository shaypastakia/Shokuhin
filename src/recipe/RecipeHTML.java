package recipe;

/**
 * 
 * @author Shaylen Pastakia
 *
 */
public class RecipeHTML
{
	private String html = "";
	private String title = "";

	public RecipeHTML(Recipe recipe)
	{
		title = recipe.getTitle();
		//As per http://stackoverflow.com/questions/11679567/using-css-for-fade-in-effect-on-page-load [A.M.K]
		
		String header = "<!DOCTYPE html>\n<html>\n<head><style>" + "</style>\n<title>" + title + "</title>\n</head>\n<body>\n";

		String titleHeader = "<p title = \"" + title + "\">\n";

		String titleBody = "<body>\n<h1>" + title + "</h1>\n";
		
		String infoHeader = "<h2>Information</h2>";
		String info = "<ul>";
		info = info.concat("<li>Rating: " + recipe.getRating() + "</li>");

		// Append Course to info
		String[] courses = new String[] { "Breakfast", "Lunch", "Dinner", "Dessert", "Snack", "General" };
		info = info.concat("<li>Course: " + courses[recipe.getCourse()] + "</li>");

		// Append Time to info
		info = info.concat("<li>Preparation time: " + recipe.getPrepTime() + " minutes</li>"
				+ "<li>Cooking Time: " + recipe.getCookTime() + " minutes </li>");
		
		// Append Servings to info
		info = info.concat("<li>Serves: " + recipe.getServings() + "</li>");
		
		String tags = "<li>Tags: ";
		for (String s : recipe.getTags())
		{
			tags = tags.concat(s);
			if (recipe.getTags().indexOf(s) < recipe.getTags().size() - 1)
			{
				tags = tags.concat(", ");
			}
			else
			{
				tags = tags.concat(".").trim();
			}
		}
		info = info.concat(tags + "</li><br />");
		
		info = info.concat("</ul>\n");
		
		String ingredientsHeader = "<h2>Ingredients</h2>";
		String ingredientsList = "<ul>\n";
		for (String s : recipe.getIngredients())
		{
			ingredientsList = ingredientsList.concat("<li>" + s + "</li>\n");
		}
		ingredientsList = ingredientsList.concat("</ul>\n<br />");
		String methodHeader = "<h2>Method</h2>\n";
		String methodList = "<ol>\n";
		for (String s : recipe.getMethodSteps())
		{
			methodList = methodList.concat("<li>" + s.replaceAll("\n", "<br />") + "</li>\n<br />");
		}

		String footer = "\n</body>\n</html>";

		html = html.concat(header).concat(titleHeader).concat(titleBody).concat(infoHeader).concat(info).concat(ingredientsHeader)
				.concat(ingredientsList).concat(methodHeader).concat(methodList).concat(footer);
	}

	public String getHTML()
	{
		return html;
	}
}
