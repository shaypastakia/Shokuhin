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
		String header = "<!DOCTYPE html>\n<html>\n<head>\n<title>" + title + "</title>\n</head>\n<body>\n";

		String titleHeader = "<p title = \"" + title + "\">\n";

		String titleBody = "<body>\n<h1>" + title + "</h1>\n<br />\n";
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

		html = html.concat(header).concat(titleHeader).concat(titleBody).concat(ingredientsHeader)
				.concat(ingredientsList).concat(methodHeader).concat(methodList).concat(footer);
	}

	public String getHTML()
	{
		return html;
	}
}
