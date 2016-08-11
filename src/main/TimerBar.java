package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import midiPlayer.MidiPlayer;
import midiPlayer.MidiSongs;
import mp3Player.MP3Player;

/**
 * TimerBar
 * <br>
 * Produces the Timer Bar for the Main Interface
 * 
 * @author Shaylen Pastakia
 */
public class TimerBar extends JPanel {
	private static final long serialVersionUID = 7320494451364057870L;
	private JSpinner hour;
	private JLabel septimus = new JLabel(":");
	private JSpinner mins;
	private JLabel septimusPrime = new JLabel(":");
	private JSpinner secs;
	private JButton start = new JButton("Start");
	private JButton stop = new JButton ("Stop");
	private Timer timer = new Timer();
	private ShokuhinMain main;
	private boolean interrupted = false;
	
	public TimerBar(ShokuhinMain m) {
		this.main = m;
		setMaximumSize(new Dimension(9999, 500));
		createGui();
	}
	
	/**
	 * Produce the controls to add to this JPanel
	 */
	private void createGui(){
		//Create Number Models (99 hours, 60 mins, 60 secs) for Spinners
		SpinnerNumberModel modelH = new SpinnerNumberModel(0, 0, 99, 1);
		SpinnerNumberModel modelM = new SpinnerNumberModel(0, 0, 60, 1);
		SpinnerNumberModel modelS = new SpinnerNumberModel(0, 0, 60, 1);
		
		//Create the Spinners with their Number models
		hour = new JSpinner(modelH);
		mins = new JSpinner(modelM);
		secs = new JSpinner(modelS);
		
		//Set the size and font of Spinners, Colon labels and Start/Stop Buttons
		hour.setMaximumSize(new Dimension(100, 500));
		hour.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		septimus.setFont(new Font("Times New Roman", Font.BOLD, 30));
		mins.setMaximumSize(new Dimension(100, 500));
		mins.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		septimusPrime.setFont(new Font("Times New Roman", Font.BOLD, 30));
		secs.setMaximumSize(new Dimension(100, 500));
		secs.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		start.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		stop.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		
		//Center the text of all Spinners
		JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)hour.getEditor();
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		spinnerEditor = (JSpinner.DefaultEditor)mins.getEditor();
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		spinnerEditor = (JSpinner.DefaultEditor)secs.getEditor();
		spinnerEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
		
		//Add all elements to the TimerBar
		
		try {
			//Adding Image code based on http://stackoverflow.com/questions/3775373/java-how-to-add-image-to-jlabel, Answer by Tomas Narros
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream input = classLoader.getResourceAsStream("src/ShokuhinLogo.png");
			if (input == null)
				input = classLoader.getResourceAsStream("ShokuhinLogo.png");
			Image image = ImageIO.read(input);
			
			image = image.getScaledInstance(65, 55, Image.SCALE_SMOOTH);
			ImageIcon icon = new ImageIcon(image); 
			JLabel thumb = new JLabel();
			thumb.setIcon(icon);
			add(thumb);
		} catch (Exception e){
			System.out.println(e.getMessage());
			//Don't bother doing anything
		}
		
		add(hour);
		add(septimus);
		add(mins);
		add(septimusPrime);
		add(secs);
		add(new JLabel("       "));
		add(start);
		add(stop);
		stop.setEnabled(false);
		
		//Set the layout to draw controls in a horizontal line, and make the bar visible
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setVisible(true);
		
		//Add the function to the Start Button
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Create the task to trigger the decrementSecs() method
				TimerTask task = new TimerTask(){
					@Override
					public void run() {
						decrementSecs();
					}
					
				};
				
				//Check the values of all Spinners, to make sure there is a non-zero value for the Timer
				int valH = (int) hour.getValue();
				int valM = (int) mins.getValue();
				int valS = (int) secs.getValue();
				if (valH == 0 && valM == 0 && valS == 0){
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}
				//If there is a value on at least one Spinner, start the timer. Disable the Start button, and enable the Stop button
				start.setEnabled(false);
				stop.setEnabled(true);
				timer = new Timer();
				timer.scheduleAtFixedRate(task, 1000, 1000);
			}
		});
		
		//Add the function to the Stop button
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Cancel the timer, stop the melody, disable the Stop button, enable the Start button, and restore the background colour
				timer.cancel();
				MidiPlayer.stop();
				MP3Player player = main.getPlayer();
				if (player != null && interrupted)
					player.resume();
				stop.setEnabled(false);
				start.setEnabled(true);
				setBackground(ShokuhinMain.DEFAULT_CAMPUS_FOG);
			}
		});
	}
	
	/**
	 * Decrement the secs Spinner. If it has reached Zero, call decrementMins()
	 * <br />
	 * If all fields have reached Zero, call alert()
	 */
	private void decrementSecs(){
		int val = (int) secs.getValue();
		if (val > 0) {
			secs.setValue(val-1);
			if ((int) secs.getValue() == 0 && (int) mins.getValue() == 0 && (int) hour.getValue() == 0)
				alert();	
		} else {
			decrementMins();
		}
	}
	
	/**
	 * Decrement the mins Spinner. If it has reached Zero, call decrementHour()
	 */
	private void decrementMins(){
		int val = (int) mins.getValue();
		if (val > 0){
			secs.setValue(59);
			mins.setValue(val-1);
		} else
			decrementHour();
	}
	
	/**
	 * Decrement the hour Spinner. If is has reached Zero, call alert()
	 */
	private void decrementHour(){
		int val = (int) hour.getValue();
		if (val > 0){
			mins.setValue(59);
			secs.setValue(59);
			hour.setValue(val-1);
		} else {
			alert();
		}
	}
	
	/**
	 *  Produce an audiovisual alarm and stop the Timer
	 */
	private void alert(){
		MP3Player player = main.getPlayer();
		if (player != null){
			if (player.getPlayerState() == 1){
				player.interrupt();
				interrupted = true;
			} else {
				interrupted = false;
			}
		}
		MidiPlayer.play(MidiSongs.FF7_PRELUDE); //audio
		setBackground(ShokuhinMain.REDRUM_RED); //visual
		timer.cancel();
	}

}
