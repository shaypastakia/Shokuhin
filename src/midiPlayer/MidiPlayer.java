package midiPlayer;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.swing.JOptionPane;

import main.ShokuhinMain;

import org.jfugue.midi.MidiParser;
import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.staccato.StaccatoParserListener;

/**
 * MidiPlayer
 * <br>
 * Provides ability to play and stop MIDI songs
 */
public class MidiPlayer {
	
	private static Player player = new Player();
	private static Thread thread;
	
	/**
	 * Play a provided MIDI Pattern on a new Thread
	 * 
	 * @param p A Pattern to play
	 */
	public static void play(Pattern p){
		player = new Player();
		thread = new Thread() {
			
			@Override
			public void run() {
		        player.play(p);
			}
		};
		
		thread.start();
	}
	
	/**
	 * Stop a currently playing MIDI Pattern
	 */
	public static void stop(){
		player.play("");
	}
	
	/**
	 * Take a Midi File, and parse it into a String, to incorporate into Shokuhin.
	 * <br />
	 * Utilises JFugue library: http://www.jfugue.org/
	 * 
	 * @param file A '.mid' file to parse. Will copy result to Clipboard.
	 * @return The parsed pattern. Can be passed to the MidiPlayer, when wrapped in a new Pattern to play a file directly
	 */
	public static String getPattern(File file){
		//Create the Parser
        MidiParser parser = new MidiParser();
        StaccatoParserListener listener = new StaccatoParserListener();
        parser.addParserListener(listener);
        try {
        	//Parse the '.mid' file into a Pattern
			parser.parse(MidiSystem.getSequence(file));
	        Pattern staccatoPattern = listener.getPattern();
	        
	        //Copy the Pattern to the Clipboard, and return it
	        StringSelection stringSelection = new StringSelection (staccatoPattern.toString());
	        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
	        clpbrd.setContents (stringSelection, null);
	        return staccatoPattern.toString(); 
		} catch (InvalidMidiDataException | IOException e) {
			ShokuhinMain.displayMessage("Unable to Parse Midi File", e.getMessage(), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
        
        return "";

	}

}
