package sqlEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardOpenOption;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sqlEngine.CryptEngine;
import main.ShokuhinMain;

/**
 * This class has been imported from Manabu (final year project).
 * The code is mine, except where otherwise stated.
 * It has been modified to suit the needs of Shokuhin.
 * <br><br>
 * AuthenticationPanel
 * <br>
 * Authenticates a User to their SQL Database
 * <br>
 * Sets up a new Encrypted Token if one doesn't exist
 * @author Shaylen Pastakia
 *
 */
public class AuthenticationPanel extends JDialog {
	private static final long serialVersionUID = 1318488529883258965L;
	private final static String root = "./Shokuhin/";
	private String password;
	private boolean succeed = false;
	private JPanel panel = new JPanel();

	//The token file containing the Encrypted Token
	private File token = new File(root.concat("token.mkey"));

	/**
	 * Constructor
	 * <br>
	 * Choose an appropriate Authentication process based on whether or not the token file exists
	 */
	public AuthenticationPanel() {
		setLocationRelativeTo(null);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		setModalityType(ModalityType.APPLICATION_MODAL);
		new File(root).mkdirs();
		
		//Either log the user in, or create a new Token file
		if (Files.exists(token.toPath(), LinkOption.NOFOLLOW_LINKS)){
			succeed = authoriseGui();
			dispose();
			return;
		} else {
			newTokenGui();
		}
		add(panel);
		pack();
		setVisible(true);
	}
	
	/**
	 * Use a Confirm Dialog with a Password Field to gain a Password from the user
	 * @return whether or not the User entered a password
	 */
	private boolean authoriseGui(){
		//Based on code from http://www.coderanch.com/t/341832/GUI/java/Password-Input-Box
		JPasswordField field = new JPasswordField(15);
		int action = JOptionPane.showConfirmDialog(null, field, "Please enter your chosen password", JOptionPane.OK_CANCEL_OPTION);
		if (action > 0){
		//End Based on Code
			System.out.println("Password Entry cancelled. Unable to authenticate User.");
			return false;
		} else {
			System.out.println("Tried to auth user :: " + action);
			password = String.valueOf(field.getPassword());
			return true;
		}
	}
	
	/**
	 * Used if no details exists, and the Authentication needs to be set up
	 */
	private void newTokenGui(){
		//Display instructions on setting up Password
		panel.add(new JLabel("To use SQL Synchronisation, you must first set up access."));
		panel.add(new JLabel("Enter the connection details of your SQL server below."));
		
		final JTextField dbPassField = new JTextField(0);
		final JTextField user = new JTextField();
		final JTextField dbURL = new JTextField();
		final JButton okButton = new JButton("OK");
		
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String dbString = dbURL.getText();
					String userString = user.getText();
					String passString = dbPassField.getText();
					
					//Validate the user details by connecting to the SQL Database
					if (DriverManager.getConnection(dbString, userString, passString).isValid(5)){
						setDetails(dbString, userString, passString);
					}
				} catch (SQLException e1) {
					ShokuhinMain.displayMessage("Error in Authentication", "Unable to connect\nPlease ensure you "
							+ "have correctly entered your token, and you are connected to the Internet", JOptionPane.ERROR_MESSAGE);
				}
								
			}
		});
		panel.add(new JLabel("Database URL: "));
		panel.add(dbURL);
		panel.add(new JLabel("Database Username: "));
		panel.add(user);
		panel.add(new JLabel("Database Password: "));
		panel.add(dbPassField);
		panel.add(okButton);
	}
	
	/**
	 * Allow the User to set a Password
	 * @param dbPass
	 */
	private void setDetails(String db, String user, final String dbPass){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		//Create the new GUI
		panel.add (new JLabel("Success! Please choose a memorable password, for simpler access next time you Synchronise"));
		final JPasswordField passField1 = new JPasswordField(20);
		final JPasswordField passField2 = new JPasswordField(20);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			//Ensure the Passwords match, and are nor empty nor short
			@Override
			public void actionPerformed(ActionEvent e) {
				if (String.valueOf(passField1.getPassword()).equals(String.valueOf(passField2.getPassword()))){
					password = String.valueOf(passField1.getPassword());
					if (password.equals("")){
						ShokuhinMain.displayMessage("Warning", "Password is empty", JOptionPane.WARNING_MESSAGE);
						return;
					} else if (password.length() < 6){
						ShokuhinMain.displayMessage("Warning", "Please choose a longer Password", JOptionPane.WARNING_MESSAGE);
						return;
					}
					//Upon success, proceed to creating the token.key file
					createKeyFile(db, user, dbPass, password);
				} else {
					ShokuhinMain.displayMessage("Warning", "Passwords do not match\nPlease try again", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		setContentPane(panel);
		panel.add(passField1);
		panel.add(passField2);
		panel.add(ok);
		paintAll(getGraphics());
		pack();
	}
	

	/**
	 * Having elicited Database Details and a Password, create the token.key file
	 * @param db The Database URL
	 * @param user The Database Username
	 * @param dbPassword The Database Password
	 * @param userPassword The Password set up by the User
	 */
	private void createKeyFile(String db, String user, String dbPassword, String userPassword){
		try {
			this.token.createNewFile();
			//Call upon the CryptEngine to encrypt the Token
			CryptEngine crypt = new CryptEngine(userPassword);
			String preText = db + "\n" + user + "\n";
			String cipherText = crypt.encrypt(dbPassword);
			Files.write(this.token.toPath(), preText.concat(cipherText).getBytes(), StandardOpenOption.CREATE);
			succeed = true;
			dispose();
		} catch (IOException e) {
			ShokuhinMain.displayMessage("File Writing Error", "Unable to create Key file\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	/**
	 * 
	 * @return the Database URL, Username and Password as a List
	 * @throws IOException When "token.mkey" cannot be read
	 */
	public List<String> getDetails() throws IOException{
		List<String> details = Files.readAllLines(new File(root.concat("token.mkey")).toPath());
		details.set(2, new CryptEngine(password).decrypt(details.get(2)));
		return details;
	}
	
	public boolean succeeded(){
		return succeed;
	}
}
