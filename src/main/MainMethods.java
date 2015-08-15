package main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * 
 * @author Shaylen Pastakia
 *
 */
public class MainMethods {
	
	//Format to store Dates in.
	public static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	
	//Server to interact with.
	private static String urlString = new String("<SERVER ADDRESS>");
	private static String user = "";
	private static String pass = "";
	
	/**
	 * 
	 * @return A list of Recipes stored on a Server, and their respective upload dates.
	 */
	public static HashMap<String, Date> getList(){
		try {
			//Create a file to store the server's list to
			File temp = new File("./Shokuhin/list.hmap");
			Files.deleteIfExists(temp.toPath());
			temp.createNewFile();
			
			//Grab the list from the server
			FileOutputStream output = new FileOutputStream(temp);
			FTPClient client = new FTPClient();
				
				//Set up the Connection
			client.connect(urlString);
			client.login(user, pass);
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			client.setAutodetectUTF8(true);
//			client.changeWorkingDirectory("/Shokuhin/");
			
				//Retrieve the File
			InputStream input = client.retrieveFileStream("list.hmap");
			IOUtils.copy(input, output);
			
				//Close Connection
			input.close();
			output.close();
			
			//De-Serialise the List into a HashMap
			InputStream fileIn = Files.newInputStream(temp.toPath());
			BufferedInputStream buff = new BufferedInputStream(fileIn);
			ObjectInputStream obj = new ObjectInputStream(buff);
			@SuppressWarnings("unchecked")
			HashMap<String, Date> list = (HashMap<String, Date>) obj.readObject();
			obj.close();
			
			return list;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Upload a list of Recipes and their dates to a Server
	 * @param list
	 */
	public static void putList(HashMap<String, Date> list){
		try {
			
			FTPClient client = new FTPClient();
			File temp = new File("./Shokuhin/list.hmap");
			Files.deleteIfExists(temp.toPath());
			temp.createNewFile();
			FileInputStream in = new FileInputStream(temp);
			
			//Serialise the List
			OutputStream fileOut = Files.newOutputStream(temp.toPath());
			BufferedOutputStream buff = new BufferedOutputStream(fileOut);
			ObjectOutputStream obj = new ObjectOutputStream(buff);
			obj.writeObject(list);
			obj.close();
			
			//Set up the Connection
			client.connect(urlString);
			client.login(user, pass);
//			client.changeWorkingDirectory("/Shokuhin/");
			
			//Upload the file and disconnect
			client.storeFile("list.hmap", in);
			client.logout();
			client.disconnect();
			in.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
