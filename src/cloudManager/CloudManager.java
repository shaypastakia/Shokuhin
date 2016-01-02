package cloudManager;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import main.Module;
import main.ShokuhinMain;
import recipe.Recipe;
import recipe.RecipeMethods;

public class CloudManager extends Module implements KeyListener{
	private static final long serialVersionUID = -3655761769105844772L;
	
	//integer to store Console's requested Action
	private static final String USE_DEFAULT = "set default";
	private static final String SET_SERVER = "set server";
	private static final String SET_DIRECTORY = "set dir";
	private static final String SET_USERNAME = "set user";
	private static final String SET_PASSWORD = "set password";
	private static final String CONNECT = "connect";
	private static final String DISCONNECT = "disconnect";
	private static final String LIST_NEW_LOCAL = "list new local";
	private static final String LIST_NEW_REMOTE = "list new remote";
	private static final String UPLOAD_NEW_LOCAL = "upload new local";
	private static final String DOWNLOAD_NEW_REMOTE = "download new remote";
	private static final String LIST_UPDATED_LOCAL = "list updated local";
	private static final String LIST_UPDATED_REMOTE = "list updated remote";
	private static final String UPLOAD_UPDATED_LOCAL = "upload updated local";
	private static final String DOWNLOAD_UPDATED_REMOTE = "download updated remote";
	private static final String CLEAR = "clear";
	private static final String SYNC = "sync";
	private static final String HELP = "help";
	
	private static final int DELETE = 0;
	private static final int DONT_DELETE = 1;
	private static final int DONT_DELETE_OR_OUTPUT = 2;
	
	public boolean visible = true;
	
	//Default URL
	String def = "http://194.83.236.93/~spastakia/Shokuhin/";
	
	//Recipe Lists
	ArrayList<String> local = RecipeMethods.getRecipeFileNames();
	ArrayList<String> remote = RecipeMethods.getRemoteRecipeFileNames(def);
	ArrayList<Recipe> localRec = new ArrayList<Recipe>();
	ArrayList<Recipe> remoteRec = new ArrayList<Recipe>();

	//Server to interact with.
	FTPClient client = new FTPClient();
	private String server = "";
	private String user = "";
	private String pass = "";
	private String directory = "";
	
	//GUI Components
	JPanel consolePane = new JPanel();
		JScrollPane outputScroll = new JScrollPane();
			JTextArea outputText = new JTextArea("Welcome to Shokuhin Cloud Manager\nPress 'Enter' to submit input.\nType 'help' for a list of commands");
		JPasswordField inputText = new JPasswordField();
		
	JPanel listPane = new JPanel();
		JScrollPane localScrollPanel = new JScrollPane();
			JList<String> localList = new JList<String>();
			DefaultListModel<String> localModel = new DefaultListModel<String>();
		JScrollPane remoteScrollPanel = new JScrollPane();
			JList<String> remoteList = new JList<String>();
			DefaultListModel<String> remoteModel = new DefaultListModel<String>();

		

	public CloudManager(ShokuhinMain m) {
		super(m, "Cloud Manager");
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		consolePane.setBorder(BorderFactory.createTitledBorder("Console"));
		consolePane.setLayout(new BoxLayout(consolePane, BoxLayout.PAGE_AXIS));
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.LINE_AXIS));
		
		consolePane.add(outputScroll);
		outputScroll.setViewportView(outputText);
		outputScroll.setAutoscrolls(true);
		consolePane.add(inputText);
		inputText.setMaximumSize(new Dimension(9999, 30));
		inputText.addKeyListener(this);
		inputText.setEchoChar(((char)0));
		
		listPane.add(localScrollPanel);
		localScrollPanel.setBorder(BorderFactory.createTitledBorder("Local Recipes"));
		localScrollPanel.setViewportView(localList);
		localList.setModel(localModel);
		listPane.add(remoteScrollPanel);
		remoteScrollPanel.setBorder(BorderFactory.createTitledBorder("Remote Recipes"));
		remoteScrollPanel.setViewportView(remoteList);
		remoteList.setModel(remoteModel);
		
		
		add(consolePane);
		add(listPane);
		
		outputText.setBackground(ShokuhinMain.ANY_COLOUR_SO_LONG_AS_IT_IS_BLACK);
		outputText.setForeground(ShokuhinMain.MANABU_BLUE);
		inputText.setBackground(ShokuhinMain.ANY_COLOUR_SO_LONG_AS_IT_IS_BLACK);
		inputText.setForeground(ShokuhinMain.MANABU_BLUE);
		outputText.setEditable(false);
		
		clearTemp();
	}
	
	private boolean process(String cmd){
		inputText.setText("");
		
		if (cmd.trim().equals("")){
			postOutput("Please enter a command.");
		} else if (cmd.equals(USE_DEFAULT)){
			user = "spastakia";
			server = "194.83.236.93";
			directory = "public_html/Shokuhin";
			postOutput("Set details to default.\nYou will still need to set the password with 'set password'.");
		} else if (cmd.contains(SET_SERVER)){
			server = getLastWord(cmd);
			if (server != null && !server.equals(""))
				postOutput("Server set to: " + server);
			else 
				postOutput("No server provided.\nUsage: " + SET_SERVER + " <address>");
		} else if (cmd.contains(SET_DIRECTORY)){
			directory = getLastWord(cmd);
			if (directory != null && !directory.equals(""))
				postOutput("Directory set to: " + directory);
			else 
				postOutput("No directory provided.\nUsage: " + SET_DIRECTORY + " <directory>");
		} else if (cmd.contains(SET_USERNAME)){
			user = getLastWord(cmd);
			if (user != null && !user.equals(""))
				postOutput("Username set to: " + user);
			else 
				postOutput("No username provided.\nUsage: " + SET_USERNAME + " <username>");
		} else if (cmd.contains(SET_PASSWORD)){
			pass = getLastWord(cmd);
			if (pass != null && !pass.equals(""))
				postOutput("Password has been set.");
			else 
				postOutput("No password provided.\nUsage: " + SET_PASSWORD +  " <password>");
		} else if (cmd.equals(CONNECT) || cmd.equals("c")){
			String[] response = firstConnect();
			if (response != null){
				String temp = "";
				for (String s : response){
					temp += s + "\n";
				}
				postOutput("Connection succeeded with response:\n" + temp);
			} else {
				return false;
			}
		} else if (cmd.equals(DISCONNECT) || cmd.equals("dc")){
			close();
			postOutput("Disconnected from Server");
		} else if (cmd.equals(LIST_NEW_LOCAL)){
			if (!reconnect())
				return false;
			
			local = RecipeMethods.getRecipeFileNames();
			remote = RecipeMethods.getRemoteRecipeFileNames(def);
			local.removeAll(remote);
			
			localModel.clear();
			remoteModel.clear();
			
			if (local.size() > 0){
				for (String s : local)
					localModel.addElement(s);
				
				postOutput("There are " + local.size() + " new recipes.\n"
				+ "These are listed on the 'Local Recipes' List.\n"
				+ "Select the recipes you want to upload to the Remote Server.\n"
				+ "Use the 'upload new local' command to upload selected Recipes.\n"
				+ "Leave the selection blank to upload all new Recipes");
			} else {
				postOutput("There are no new Recipes on the Local Machine.");
			}
		} else if (cmd.equals(LIST_NEW_REMOTE)){
			if (!reconnect())
				return false;
			
			local = RecipeMethods.getRecipeFileNames();
			remote = RecipeMethods.getRemoteRecipeFileNames(def);
			remote.removeAll(local);
			
			localModel.clear();
			remoteModel.clear();

			if (remote.size() > 0){
				for (String s : remote)
					remoteModel.addElement(s);
				
				postOutput("There are " + remote.size() + " new recipes.\n"
				+ "These are listed on the 'Remote Recipes' List.\n"
				+ "Select the recipes you want to download to the Local Machine.\n"
				+ "Use the 'download new remote' command to download selected Recipes.\n"
				+ "Leave the selection blank to download all new Recipes");
			} else {
				postOutput("There are no new Recipes on the Remote Server.");
			}
		} else if (cmd.equals(DOWNLOAD_NEW_REMOTE)){
			if (!reconnect())
				return false;
			
			if (remoteModel.size() == 0){
				postOutput("The Remote List is empty.\nCannot download Recipes.\n"
						+ "Try using the 'list new remote' command.");
				return true;
			}
			
			if (remoteList.getSelectedValuesList().size() == 0){
				for (int i = 0; i < remoteModel.size(); i++){
					String s = remoteModel.getElementAt(i);
					getRemoteFile(new File("./Shokuhin/Recipes/" + s + ".rec"), DONT_DELETE);
				}
			} else {
				for (String s : remoteList.getSelectedValuesList()){
					getRemoteFile(new File("./Shokuhin/Recipes/" + s + ".rec"), DONT_DELETE);
				}
			}
			process(LIST_NEW_REMOTE);
		} else if (cmd.equals(UPLOAD_NEW_LOCAL)){
			if (!reconnect())
				return false;
			
			if (localModel.size() == 0){
				postOutput("The Local List is empty.\nCannot upload Recipes.\n"
						+ "Try using the 'list new local' command.");
			}
			
			if (localList.getSelectedValuesList().size() == 0){
				for (int i = 0; i < localModel.size(); i++){
					String s = localModel.getElementAt(i);
					putLocalFile(s, false);
				}
			} else {
				for (String s : localList.getSelectedValuesList()){
					putLocalFile(s, false);
				}
			}
			process(LIST_NEW_LOCAL);
		} else if (cmd.equals(CLEAR)){
			outputText.setText("");
		} else if (cmd.equals(LIST_UPDATED_LOCAL)){
			return listUpdated(LIST_UPDATED_LOCAL);
		} else if (cmd.equals(LIST_UPDATED_REMOTE)){
			return listUpdated(LIST_UPDATED_REMOTE);
		} else if (cmd.equals(UPLOAD_UPDATED_LOCAL)){
			return true;
		} else if (cmd.equals(DOWNLOAD_UPDATED_REMOTE)){
			if (remoteModel.size() == 0){
				postOutput("The Remote List is empty.\nCannot download Recipes.\n"
						+ "Try using the 'list updated remote' command.");
				return true;
			}
			
			if (remoteList.getSelectedValuesList().size() == 0){
				for (int i = 0; i < remoteModel.size(); i++){
					String s = remoteModel.getElementAt(i);
					getRemoteFile(new File("./Shokuhin/Recipes/" + s + ".rec"), DELETE);
				}
			} else {
				for (String s : remoteList.getSelectedValuesList()){
					getRemoteFile(new File("./Shokuhin/Recipes/" + s + ".rec"), DELETE);
				}
			}
			process(LIST_UPDATED_REMOTE);
			return true;
		} else if (cmd.contains(SYNC)){
			user = "spastakia";
			pass = "99161277";
			server = "194.83.236.93";
			firstConnect();
			process(LIST_UPDATED_LOCAL);
			return true;
		} else if (cmd.equals(HELP) || cmd.equals("?")){
			return true;
		} else if (cmd.equals("exit")){
			getPar().closeTab();
		} else {
			postOutput("Invalid command.\nPlease use 'help' for a list of commands.");
			return true;
		}
		//TODO 'upload updated local'
		//TODO 'download updated remote' *COMPLETED*
		//TODO 'sync <password>'
		//TODO 'help'
		//TODO Invalid command
		return true;
	}
	
	private boolean listUpdated(String mode){
		if (!reconnect())
			return false;
		
		local = RecipeMethods.getRecipeFileNames();
		remote = RecipeMethods.getRemoteRecipeFileNames(def);
		
		if (mode.equals(LIST_UPDATED_LOCAL))
			local.retainAll(remote);
		else if (mode.equals(LIST_UPDATED_REMOTE))
			remote.retainAll(local);
		else {
			postOutput("Invalid List Update mode");
			return false;
		}
		
		localRec.clear();
		for (String s : local){
			localRec.add(RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec")));
		}
		
		remoteRec.clear();
		for (String s : remote){
			File f = new File(System.getProperty("user.dir") + "/Shokuhin/temp/" + s + ".rec");
			getRemoteFile(f, DONT_DELETE_OR_OUTPUT);
			remoteRec.add(RecipeMethods.readRecipe(f));
		}
		
//		if (1==1)
//		return true;
		
		localModel.clear();
		remoteModel.clear();
		
		if (mode.equals(LIST_UPDATED_LOCAL)){
			for (String s : local){
				Recipe loc = RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec"));
				Recipe rem = RecipeMethods.readRecipe(new File("./Shokuhin/temp/" + s + ".rec"));
				if (rem == null)
					break;
				System.out.println(loc.getTitle());
				if (loc.getLastModifiedDate().after(rem.getLastModifiedDate()))
					localModel.addElement(loc.getTitle());
			}
				
			
			if (localModel.size() > 0){
				postOutput("There are " + localModel.size() + " updated recipes.\n"
				+ "These are listed on the 'Local Recipes' List.\n"
				+ "Select the recipes you want to upload to the Remote Server.\n"
				+ "Use the 'upload updated local' command to upload selected Recipes.\n"
				+ "Leave the selection blank to upload all updated Recipes");
			} else {
				postOutput("There are no updated Recipes on the Local Machine.");
			}
			return true;
		} else if (mode.equals(LIST_UPDATED_REMOTE)){
			for (String s : remote){
				Recipe loc = RecipeMethods.readRecipe(new File("./Shokuhin/Recipes/" + s + ".rec"));
				File temp = new File("./Shokuhin/temp/" + s + ".rec");
				Recipe rem = RecipeMethods.readRecipe(temp);
				
				if (rem.getLastModifiedDate().after(loc.getLastModifiedDate()))
					remoteModel.addElement(rem.getTitle());
				
				if(!temp.delete())
					temp.deleteOnExit();
			}
				
			
			if (remoteModel.size() > 0){
				postOutput("There are " + remoteModel.size() + " updated recipes.\n"
				+ "These are listed on the 'Remote Recipes' List.\n"
				+ "Select the recipes you want to download from the Remote Server.\n"
				+ "Use the 'download updated remote' command to download selected Recipes.\n"
				+ "Leave the selection blank to download all updated Recipes");
			} else {
				postOutput("There are no updated Recipes on the Server.");
			}
			return true;
		} else {
			postOutput("Invalid List Update mode");
			return false;
		}
	}
	
	private boolean getRemoteFile(File f, int delete){
		try {
			if (delete == DELETE){
				System.out.println("Deleted" + f.getName());
				Files.deleteIfExists(f.toPath());
			}
			
			new File(f.getParentFile().getAbsolutePath()).mkdirs();
			Files.createFile(f.toPath());
			if (!reconnect()){
				f.delete();
				System.out.println("Deleted on getRemoteFile 1");
				return false;
			}
			
			client.setControlEncoding("UTF-8");
			client.enterLocalPassiveMode();
			client.setFileType(FTP.BINARY_FILE_TYPE);
			
			
			
			//Code based on: http://stackoverflow.com/questions/16439054/download-file-from-remote-ftp-server-file-downloaded-empty
			//Question by: EL Kamel Malek
			OutputStream outputStream = Files.newOutputStream(f.toPath());
			client.retrieveFile(f.getName(), outputStream);
//            InputStream inputStream = client.retrieveFileStream(f.getName());
//            byte[] bytesArray = new byte[4096];
//            int length;
//            while ((length = inputStream.read(bytesArray)) > 0){
//                outputStream.write(bytesArray, 0, length);
//            }

			outputStream.flush();
			outputStream.close();
//			inputStream.close();
			//End code based on
			if (delete != DONT_DELETE_OR_OUTPUT)
				postOutput("Successfully downloaded " + f.getName());
			return true;
		} catch (Exception e){
			e.printStackTrace();
			f.delete();
			String temp = "";
			for (String s1 : client.getReplyStrings())
				temp += s1 + "\n";
			System.out.println("Deleted on getRemoteFile 2");
			postOutput("Failed to download " + name + ", with error:\n" + temp);
			return false;
		}
	}
	
	private boolean putLocalFile(String name, boolean delete){
		File f = new File("./Shokuhin/Recipes/" + name.concat(".rec"));
		if (!reconnect())
			return false;
		
		try {		
			client.setControlEncoding("UTF-8");
			client.enterLocalPassiveMode();
			client.setFileType(FTP.BINARY_FILE_TYPE);
			
			FileInputStream in = new FileInputStream(f);
			client.storeFile(f.getName(), in);
			in.close();
			postOutput("Successfully uploaded " + f.getName());
			return true;
		} catch (Exception e){
			e.printStackTrace();
			String temp = "";
			for (String s1 : client.getReplyStrings())
				temp += s1 + "\n";
			postOutput("Failed to upload " + name + ", with error:\n" + temp + ":: " + e.getMessage());
			return false;
		}
	}
	
	private void postOutput(String s){
		outputText.setText(outputText.getText().trim() + "\n\n" + s);
	}
	
	private String getInput(){
		return String.valueOf(inputText.getPassword());
	}
	
	private String getLastWord(String cmd){
			int x = cmd.split(" ").length;
			
			if (x != 3)
				return null;
			
			return cmd.split(" ")[x-1];
	}

	@Override
	public boolean close() {
		try {
			clearTemp();
			client.logout();
			client.disconnect();
		} catch (IOException e) {
//			postOutput("Unable to disconnect from Server");
//			e.printStackTrace();
//			return false;
			return true;
		}
		return true;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Cloud Manager");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}
	
	private boolean reconnect(){
		if (client.isAvailable())
			return true;
		try {
//			close();
			client.connect(server);
			client.login(user, pass);
			client.changeWorkingDirectory(directory);	
			return true;
		} catch (Exception e){
			postOutput("Unable to connect to Server");
			return false;
		}
	}

	private String[] firstConnect(){
		try {
			if (server == null || server.equals(""))
				throw new Exception();
		} catch (Exception e1){
			postOutput("Server address cannot be empty\nUse 'set server <address>' to set the address.");
			return null;
		}
		
		try {
        	client.connect(server);
        	if (user == null)
        		user = "";
        	if (pass == null)
        		pass = "";
        	if (directory == null)
        		directory = "";
        	
			client.login(user, pass);
			if (!directory.equals(""))
				client.changeWorkingDirectory(directory);
			
			if (!new String("" + client.getReplyCode()).startsWith("2"))
				throw new Exception();

			System.out.println(client.getReplyCode());
			String[] response = client.getReplyStrings();
			for (String s : response)
				System.out.println(s);
			return response;
		} catch (Exception e2){
			String temp = "";
			String[] response = client.getReplyStrings();
			for (String s : response)
				temp += s + ", ";
			postOutput("Failed to connect, with error:\n" + temp);
		}
		return null;
	}
	
	private void clearTemp(){
		File tempDir = new File("./Shokuhin/temp/");
		if (tempDir.exists() && tempDir.isDirectory()){
			for (File f : tempDir.listFiles())
				if (!f.delete())
					f.deleteOnExit();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (getInput().contains("password ") || getInput().contains("sync ")){
			if (inputText.getEchoChar() != '*')
				inputText.setEchoChar('*');
		} else {
			if (inputText.getEchoChar() != (char)0)
				inputText.setEchoChar((char)0);
		}
			
	}

	@Override
	public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == KeyEvent.VK_ENTER){
				process(getInput());
			}
	}

	/**
	 * Don't need to do anything
	 */
	@Override
	public void KeyPressed(KeyEvent e) {
	}
}
