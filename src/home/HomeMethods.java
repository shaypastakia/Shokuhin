package home;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * @author Shaylen Paskatkia
 *
 */
public class HomeMethods {

	private static int BBC_GOOD_FOOD = 0;
	private static int BBC_FOOD = 1;

	private static String BBC_GOOD_FOOD_URL = "http://www.bbcgoodfood.com";
	private static String BBC_FOOD_URL;

	private static Document getDocumentForWebsite(int website) {

		String url = "";
		if (website == BBC_GOOD_FOOD) {
			url = BBC_GOOD_FOOD_URL;
		} else if (website == BBC_FOOD) {
			url = BBC_FOOD_URL;
		} else {
			return null;
		}

		// Connect to the Recipe URL
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
		} catch (IOException e) {
			return null;
		}
		connection.setRequestProperty("User-Agent", "Chrome");

		// Get the resulting page
		InputStreamReader in;
		try {
			in = new InputStreamReader(connection.getInputStream());
		} catch (IOException e) {
			return null;
		}
		BufferedReader bufIn = new BufferedReader(in);
		String temp;
		String response = "";

		// Write the page into a String
		try {
			while ((temp = bufIn.readLine()) != null) {
				response = response.concat(temp);
			}
		} catch (IOException e) {
			return null;
		}
		try {
			bufIn.close();
		} catch (IOException e) {
			return null;
		}

		// Produce a HTML Document from the response
		Document doc = Jsoup.parse(response);
		return doc;
	}

	public static HashMap<String, String> getBBCGoodFoodTopRecipes() {
		Document doc = getDocumentForWebsite(BBC_GOOD_FOOD);
		Elements e = doc.getElementsByClass("views-row");

		// Use the name as a key and the url as the value
		HashMap<String, String> map = new HashMap<String, String>();

		for (int i = 0; i < 5; i++) {
			Element temp = e.get(i).getElementsByTag("a").get(0);

			String urlExtension = temp.toString().split("\"")[1];
			String fullURL = BBC_GOOD_FOOD_URL + urlExtension;
			String name = temp.toString().split("\"")[2];
			name = name.replace(">", "");
			name = name.replace("</a", "");

			map.put(name, fullURL);
		}

		return map;
	}
	
	public static ArrayList<BufferedImage> getImages() {
		HashMap<String, String> map = getBBCGoodFoodTopRecipes();
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		urls.addAll(map.values());
		
		for (String url :urls){
			HttpURLConnection connection;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
			} catch (IOException e) {
				return null;
			}
			connection.setRequestProperty("User-Agent", "Chrome");
			InputStreamReader in;
			try {
				in = new InputStreamReader(connection.getInputStream());
			} catch (IOException e) {
				return null;
			}
			BufferedReader bufIn = new BufferedReader(in);
			String temp;
			String response = "";
			try {
				while ((temp = bufIn.readLine()) != null) {
					response = response.concat(temp);
				}
			} catch (IOException e) {
				return null;
			}
			try {
				bufIn.close();
			} catch (IOException e) {
				return null;
			}
			Document doc = Jsoup.parse(response);
			Element e = doc.getElementsByAttributeValue("itemprop", "image").get(0);
			try {
				images.add(ImageIO.read(new URL(e.absUrl("src"))));
			} catch (Exception e1) {
				return null;
			}
			
		}
		return images;
	}

	private static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
