package cloudManager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import main.Module;
import main.ShokuhinMain;
import recipe.RecipeMethods;

public class CloudManager extends Module implements ActionListener{
	private static final long serialVersionUID = 1207689665109547515L;
	
	//Format to store Dates in.
	public static final DateFormat format = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
	
	//Server to interact with.
	FTPClient client = new FTPClient();
	private String server = "";
	private String user = "";
	private String pass = "";
	private String directory = "";
	
	//GUI Components
	JPanel topPanel = new JPanel();
		JLabel addressLabel = new JLabel("Server: ");
		JLabel dirLabel = new JLabel("          Directory: ");
		JLabel userLabel = new JLabel("          Username: ");
		JLabel passLabel = new JLabel("          Password: ");
		
		JTextField addressField = new JTextField(20);
		JTextField dirField = new JTextField(20);
		JTextField userField = new JTextField(20);
		JPasswordField passField = new JPasswordField(20);
		
		JButton okButton = new JButton("Connect");
		
	JPanel bottomPanel = new JPanel();
		JPanel localPanel = new JPanel();
			JPanel localButtonPanel = new JPanel();
				JButton localMoveButton = new JButton("Move Recipe(s) to Server");
			JScrollPane localScrollPanel = new JScrollPane();
				JList<String> localList = new JList<String>();
				DefaultListModel<String> localModel = new DefaultListModel<String>();
		JPanel remotePanel = new JPanel();
			JPanel remoteButtonPanel = new JPanel();
				JButton remoteDeleteButton = new JButton("Delete Remote Recipe(s)");
				JButton remoteMoveButton = new JButton("Move Recipe(s) to Local Machine");
				JButton remoteListButton = new JButton("Generate Server Recipe List");
			JScrollPane remoteScrollPanel = new JScrollPane();
				JList<String> remoteList = new JList<String>();
				DefaultListModel<String> remoteModel = new DefaultListModel<String>();

		

	public CloudManager(ShokuhinMain m) {
		super(m, "Cloud Manager");
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		createTopGui();
		createBottomGui();
		add(topPanel);
		add(new JSeparator());
		add(bottomPanel);
	}
	
	public void createTopGui(){
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.setMaximumSize(new Dimension(9999, 50));
		topPanel.add(addressLabel);
		topPanel.add(addressField);
		topPanel.add(dirLabel);
		topPanel.add(dirField);
		topPanel.add(userLabel);
		topPanel.add(userField);
		topPanel.add(passLabel);
		topPanel.add(passField);
		topPanel.add(okButton);
		okButton.setPreferredSize(new Dimension(150, 50));
		
		addressField.setMinimumSize(addressField.getPreferredSize());
		addressField.setMaximumSize(addressField.getPreferredSize());
		dirField.setMinimumSize(dirField.getPreferredSize());
		dirField.setMaximumSize(dirField.getPreferredSize());
		userField.setMinimumSize(userField.getPreferredSize());
		userField.setMaximumSize(userField.getPreferredSize());
		passField.setMinimumSize(passField.getPreferredSize());
		passField.setMaximumSize(passField.getPreferredSize());
		
		passField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
					actionPerformed(null);
			}
		});
		
		dirField.setText("public_html/Shokuhin");
		okButton.addActionListener(this);
	}
	
	public void createBottomGui(){
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		localPanel.setLayout(new BoxLayout(localPanel, BoxLayout.PAGE_AXIS));
		remotePanel.setLayout(new BoxLayout(remotePanel, BoxLayout.PAGE_AXIS));
		localButtonPanel.setLayout(new BoxLayout(localButtonPanel, BoxLayout.LINE_AXIS));
		remoteButtonPanel.setLayout(new BoxLayout(remoteButtonPanel, BoxLayout.LINE_AXIS));
		
		localButtonPanel.add(localMoveButton);
		
		remoteButtonPanel.add(remoteDeleteButton);
		remoteButtonPanel.add(remoteMoveButton);
		remoteButtonPanel.add(remoteListButton);
		
		localScrollPanel.setViewportView(localList);
		remoteScrollPanel.setViewportView(remoteList);
		
		localPanel.add(localButtonPanel);
		localPanel.add(localScrollPanel);
		remotePanel.add(remoteButtonPanel);
		remotePanel.add(remoteScrollPanel);
		
		bottomPanel.add(localPanel);
		bottomPanel.add(remotePanel);
		
		localList.setModel(localModel);
		remoteList.setModel(remoteModel);
		
		for (String s : RecipeMethods.getRecipeFileNames())
			localModel.addElement(s);
		
		localMoveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> toMove = localList.getSelectedValuesList();
				ArrayList<File> files = new ArrayList<File>();
				for (String s : toMove)
					files.add(new File("./Shokuhin/Recipes/" + s + ".rec"));
				
				for (File f : files){
					reconnect();
					try {
						FileInputStream in = new FileInputStream(f);
						try {
							client.deleteFile(f.getName());
						} catch (Exception e1) {e1.printStackTrace();}
						reconnect();
						client.setFileType(FTP.BINARY_FILE_TYPE);
						client.storeFile(f.getName(), in);
						in.close();
						remoteModel.addElement(f.getName().replace(".rec", ""));
						for (String s1 : client.getReplyStrings())
							System.out.println(s1);
					} catch (Exception e1) {
						ShokuhinMain.displayMessage("Error", "Unable to Upload " + f.getName(), JOptionPane.ERROR_MESSAGE);
						System.out.println(e1.getMessage());
					}
				}
				ShokuhinMain.displayMessage("Complete", "Completed Transfer of Recipes.\n"
						+ "Press 'Generate Server Recipe List' to update the database and refresh the list", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		remoteDeleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> toDelete = remoteList.getSelectedValuesList();
				for (String s : toDelete){
					try {
						client.deleteFile(s + ".rec");
					} catch (Exception e1) {
						ShokuhinMain.displayMessage("Error", "Unable to Delete " + s, JOptionPane.ERROR_MESSAGE);
						System.out.println(e1.getMessage());
					}
				}
				ShokuhinMain.displayMessage("Complete", "Completed Deletion of Recipes.\n"
						+ "Press 'Generate Server Recipe List' to update the database and refresh the list", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		remoteListButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					putNewList();
					ShokuhinMain.displayMessage("Success", "Successfully uploaded the Recipe List.", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1){
					ShokuhinMain.displayMessage("Failed", "Unable to upload Recipe List.\n" + e1.getMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	@Override
	public boolean close() {
		try {
			client.logout();
			client.disconnect();
		} catch (IOException e) {
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
	
	public void putList(HashMap<String, Date> _list){
		try {

			File temp = new File("./Shokuhin/list.hmap");
			temp.delete();
			temp.createNewFile();
			FileInputStream in = new FileInputStream(temp);
			
			//Serialise the List
			OutputStream fileOut = Files.newOutputStream(temp.toPath());
			BufferedOutputStream buff = new BufferedOutputStream(fileOut);
			ObjectOutputStream obj = new ObjectOutputStream(buff);
			obj.writeObject(_list);
			obj.close();

			//Upload the file and disconnect
			
			try {
				reconnect();
				client.deleteFile("list.hmap");
			} catch (Exception e){}
			reconnect();
			client.storeFile("list.hmap", in);
			for (String s : client.getReplyStrings())
				System.out.println(s);
			in.close();
		} catch (Exception e) {
			for (String s : client.getReplyStrings())
				System.out.println(s);
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void putNewList(){
		HashMap<String, Date> list = new HashMap<String, Date>();
		for (String s : RecipeMethods.getRecipeFileNames())
			list.put(s, new Date());
		putList(list);
	}

	/**
	 * 
	 * @return A list of Recipes stored on a Server, and their respective upload dates.
	 */
	public HashMap<String, Date> getList(){
		try {
			//Create a file to store the server's list to
			File temp = new File("./Shokuhin/list.hmap");
			Files.deleteIfExists(temp.toPath());
			temp.createNewFile();
			
			//Grab the list from the server
			FileOutputStream output = new FileOutputStream(temp);

			//reconnect
			reconnect();
			
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			client.setAutodetectUTF8(true);
			
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
			try {
				Files.deleteIfExists(new File("./Shokuhin/list.hmap").toPath());
			} catch (IOException e1) {
				new File("./Shokuhin/list.hmap").deleteOnExit();
			}
			return new HashMap<String, Date>();
		}
	}
	
	public boolean reconnect(){
//		if (client.isAvailable())
//			return true;
		try {
			client.connect(server);
			client.login(user, pass);
			client.changeWorkingDirectory(directory);	
			return true;
		} catch (Exception e){
			return false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			server = addressField.getText();
			user = userField.getText();
			pass = new String(passField.getPassword());
			directory = dirField.getText();
			if (server == null || server.equals(""))
				throw new Exception();
		} catch (Exception e1){
			ShokuhinMain.displayMessage("Missing Details", "Server cannot be empty", JOptionPane.INFORMATION_MESSAGE);
			return;
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
			
			okButton.setText("Connected");
			okButton.setEnabled(false);
			System.out.println(client.getReplyCode());
			for (String s : client.getReplyStrings())
				System.out.println(s);
			for (String s : getList().keySet())
				remoteModel.addElement(s);
		} catch (Exception e2){
			okButton.setText("Connect");
			okButton.setEnabled(true);
			ShokuhinMain.displayMessage("Connection Error", "Incorrect Details\n" + e2.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
}
