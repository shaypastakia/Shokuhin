package sqlEngine;

import javax.swing.JOptionPane;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import main.ShokuhinMain;

/**
 * This class has been imported from Manabu (final year project).
 * The code is mine, except where otherwise stated.
 * <br>
 * CryptEngine
 * <br>
 * Encrypts and Decrypts a String, using the Password provided in the Constructor.
 * <br>
 * Uses the jasypt library
 * @author Shaylen Pastakia
 *
 */
public class CryptEngine {
	//This class utilises the jasypt library, found at http://www.jasypt.org/
	StandardPBEStringEncryptor crypt = new StandardPBEStringEncryptor();

	/**
	 * Constructor
	 * <br>
	 * Initiates the Crypt Library with a provided password
	 * @param password
	 */
	public CryptEngine(String password) {
		crypt.setPassword(password);
	}
	
	/**
	 * Encrypts a provided String
	 * @param text
	 * @return the encrypted String
	 */
	public String encrypt(String text){
		return crypt.encrypt(text);
	}
	
	/**
	 * Decrypts a provided String
	 * @param text
	 * @return the decrypted String
	 */
	public String decrypt(String text){
		try {
			return crypt.decrypt(text);
		} catch (Exception e){
			ShokuhinMain.displayMessage("Authentication Error", "Unable to decrypt token.key\nYou may have entered the wrong Password", JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}
}
