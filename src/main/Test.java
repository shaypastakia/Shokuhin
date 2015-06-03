package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import midiPlayer.MidiPlayer;
import midiPlayer.MidiSongs;

import org.jfugue.pattern.Pattern;

/**
 * A Test Module to test functions with
 *
 */
public class Test extends Module {
	private static final long serialVersionUID = 2266982084653971484L;
	
	private JSeparator septimus = new JSeparator(SwingConstants.VERTICAL);
	private JButton play = new JButton("Play");
	private JButton pause = new JButton("Pause");
	
	public Test(ShokuhinMain m) {
		super(m, "Test Module");
		
		add(new JLabel("Flazéda"));
		
		//Add controls to the Timer Bar. The separator ensures it is grouped and well spaced from the Timer controls
		ShokuhinMain.addToTimerBar(septimus);
		ShokuhinMain.addToTimerBar(play);
		ShokuhinMain.addToTimerBar(pause);
	}

	/**
	 * Provide a menu for the Frame's Dynamic Menu
	 * @return a test menu demonstrating how to use the method
	 */
	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Test");
		JMenuItem play = new JMenuItem("Play");
		JMenuItem stop = new JMenuItem("Stop");
		JMenuItem conv = new JMenuItem("Convert");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
	
		
		play.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MidiPlayer.play(MidiSongs.FF7_PRELUDE);
			}
		});
		
		stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MidiPlayer.stop();
			}
		});
		
		//Select a Midi file to convert to a String
		conv.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				MidiPlayer.play(new Pattern(MidiPlayer.getPattern(chooser.getSelectedFile())));
			}
		});
		
		menu.add(play);
		menu.add(stop);
		menu.add(conv);
		menu.add(noFuncs);
		return menu;
	}



	@Override
	public boolean close() {
		//Remove all controls from the Timer Bar before closing
		ShokuhinMain.removeFromTimerBar(pause);
		ShokuhinMain.removeFromTimerBar(play);
		ShokuhinMain.removeFromTimerBar(septimus);
		ShokuhinMain.timer.repaint();
		return true;
	}

}
