package mp3Player;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import javazoom.jl.decoder.JavaLayerException;
import main.Module;
import main.ShokuhinMain;

public class MP3Player extends Module {
	private static final long serialVersionUID = -6219613302072108822L;

	/**
	 * The panes used in the GUI
	 */
	private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	private JScrollPane songPane = new JScrollPane();
	private JPanel otherPane = new JPanel();
	private JPanel controlPane = new JPanel();

	/**
	 * Determines whether the player was interrupted or just paused;
	 */
	private boolean INTERRUPTED = false;

	/**
	 * Stores the index of the current song
	 */
	private int currentSong = -1;

	/**
	 * The player used to play the songs
	 */
	private PausablePlayer player;

	/**
	 * The JButtons used in the GUI
	 */
	private JButton play, pause, stop, next, previous, add, remove;

	/**
	 * Lists used to keep track of the songs being played and display them in
	 * the GUI
	 */
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JList<String> songs = new JList<String>(listModel);
	private ArrayList<File> songPaths = new ArrayList<File>();

	/**
	 * Creates an MP3Player module
	 * 
	 * @param m
	 *            The ShokuhinMain used
	 */
	public MP3Player(ShokuhinMain m) {
		super(m, "MP3 Player");
		// Create the MP3 player

		// Create the necessary buttons
		play = new JButton("Play");
		pause = new JButton("Pause");
		stop = new JButton("Stop");
		next = new JButton("Next");
		previous = new JButton("Previous");
		add = new JButton("Add new song");
		remove = new JButton("Remove song");

		// Set up the panes
		splitPane.setLeftComponent(songPane);
		splitPane.setRightComponent(otherPane);
		songPane.setMinimumSize(new Dimension(200, 0));
		songPane.setViewportView(songs);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		otherPane.setLayout(new BoxLayout(otherPane, BoxLayout.PAGE_AXIS));
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.LINE_AXIS));

		otherPane.add(controlPane);

		// Set up Borders for Panels
		songPane.setBorder(BorderFactory.createTitledBorder("Playlist: "));

		controlPane.setBorder(BorderFactory
				.createTitledBorder("Current Song: "));

		controlPane.add(play);
		controlPane.add(pause);
		controlPane.add(stop);
		controlPane.add(next);
		controlPane.add(previous);
		controlPane.add(add);
		controlPane.add(remove);

		play.setEnabled(false);
		pause.setEnabled(false);
		stop.setEnabled(false);
		next.setEnabled(false);
		previous.setEnabled(false);
		remove.setEnabled(false);

		songs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setUpListeners();
		add(splitPane);
	}

	/**
	 * Closes the module
	 */
	@Override
	public boolean close() {
		if (player != null) {
			player.close();
		}
		return true;
	}

	/**
	 * Sets up the listeners for the buttons for this module
	 */
	private void setUpListeners() {
		// Adds the listener that adds a song to the playlist
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setMultiSelectionEnabled(true);
				int returnVal = fc.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// String fileName = fc.getSelectedFile().getName();
					// File file = fc.getSelectedFile();

					File[] files = fc.getSelectedFiles();
					for (File f : files) {
						String filename = f.getName();
						if (filename.endsWith(".mp3")) {
							listModel.addElement(filename.replace(".mp3", ""));
							songPaths.add(f);
						}
					}

					// if (fileName.endsWith(".mp3")) {
					// // We have an mp3 file
					// listModel.addElement(fileName.replace(".mp3", ""));
					// songPaths.add(file);

					if (songPaths.size() > 0
							&& (player == null || player.state() != 1)) {
						play.setEnabled(true);
					} else {
						play.setEnabled(false);
					}

					checkForNextAndPrev();

					remove.setEnabled(true);
					// Don't need to activate pause and stop until the song
					// is
					// actually playing
					if (songPaths.size() > 0) {
						currentSong = 0;
						update();
					}

				} else {
					// We don't have an mp3 file
					JOptionPane.showMessageDialog(null,
							"Only Mp3 files can be selected.");
				}
			}
			// }
		});

		// Adds the listener that removes a song from the playlist
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = songs.getSelectedIndex();
				if (selected < 0) {
					JOptionPane.showMessageDialog(null,
							"Please select a song to be removed.");
				} else {

					if (player == null) {

					} else if (selected == currentSong
							&& (player.state() == 1 || player.state() == 2)) {
						player.close();
					}

					listModel.remove(selected);
					songPaths.remove(selected);
					currentSong--;
					update();

					if (songPaths.size() == 0) {
						play.setEnabled(false);
						previous.setEnabled(false);
						next.setEnabled(false);
						remove.setEnabled(false);
						pause.setEnabled(false);
						stop.setEnabled(false);
					}

					if (songPaths.size() == 1) {
						currentSong = 0;
						update();
					}

					if (songPaths.size() == 0) {
						currentSong = -1;
						update();
					}
				}
			}
		});

		// Adds the listener that plays the playlist
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (player == null) {
					playSong();
				} else if (player.state() == 2) {
					play.setEnabled(false);
					pause.setEnabled(true);
					stop.setEnabled(true);
					player.resume();
				} else if (player.state() == 3) {
					currentSong = 0;
					update();
					playSong();
				}
			}
		});

		// Adds the listener that pauses the playlist
		pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.pause();
				play.setEnabled(true);
				stop.setEnabled(true);
				pause.setEnabled(false);
			}
		});

		// Adds the listener that stops the playlist
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				currentSong = 0;
				update();
				play.setEnabled(true);
				pause.setEnabled(false);
				stop.setEnabled(false);
				previous.setEnabled(false);
				add.setEnabled(true);
				remove.setEnabled(true);

				try {
					songPaths.get(currentSong + 1);
					// There is a next song
					next.setEnabled(true);
				} catch (Exception e1) {
					// There is no next song
					next.setEnabled(false);
				}
			}
		});

		// Add the listener that moves to the next song
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (player == null || player.state() == 2
						|| player.state() == 3) {
					currentSong++;
					update();
				} else {
					player.close();
					currentSong++;
					update();
					playSong();
				}

				checkForNextAndPrev();
			}
		});

		// Add the listener that moves to the previous song
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (player == null || player.state() == 2
						|| player.state() == 3) {
					currentSong--;
					update();
				} else {
					player.close();
					currentSong--;
					update();
					playSong();
				}

				checkForNextAndPrev();
			}
		});
	}

	/**
	 * obtains the function menu
	 */
	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("MP3 Player");

		// A menu item that can be used when there are no functions to be
		// displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

	/**
	 * Plays the current song
	 */
	private void playSong() {
		if (currentSong >= 0) {
			try {
				// if there is a next song or if there is a
				// previous
				// song
				checkForNextAndPrev();

				play.setEnabled(false);
				pause.setEnabled(true);
				stop.setEnabled(true);

				player = new PausablePlayer(new BufferedInputStream(
						new FileInputStream(songPaths.get(currentSong))));
				player.play();
			} catch (FileNotFoundException | JavaLayerException e2) {
				JOptionPane.showMessageDialog(null,
						"Error playing the current song.");
			}

		} else {
			JOptionPane.showMessageDialog(null, "There are no songs to play!");
		}
	}

	/**
	 * Gets the index of the current song being playing
	 * 
	 * @return currentSong
	 */
	public int getCurrentSong() {
		return currentSong;
	}

	/**
	 * Updates the JList handling the playlist, as well as labels
	 */
	private void update() {
		songs.setCellRenderer(new CellRenderer(this));

		controlPane.setBorder(BorderFactory.createTitledBorder("Current Song: "
				+ songs.getModel().getElementAt(currentSong)));
	}

	/**
	 * Checks whether there is a next or previous song and disables and enables
	 * the buttons accordingly
	 */
	private void checkForNextAndPrev() {
		try {
			songPaths.get(currentSong + 1);
			// There is a next song
			next.setEnabled(true);
		} catch (Exception e1) {
			// There is no next song
			next.setEnabled(false);
		}

		try {
			songPaths.get(currentSong - 1);
			// There is a previous song
			previous.setEnabled(true);
		} catch (Exception e1) {
			// There is no previous song
			previous.setEnabled(false);
		}
	}

	/**
	 * Interrupts the player and pauses the current music, if music is playing
	 */
	public void interrupt() {
		if (player != null && player.state() == 1) {
			player.pause();
			play.setEnabled(true);
			stop.setEnabled(true);
			pause.setEnabled(false);
			INTERRUPTED = true;
		}
	}

	/**
	 * Resumes the player after an interrupt, but if the player was paused
	 * before the interrupt was called, then the player remains paused
	 */
	public void resume() {
		if (INTERRUPTED) {
			play.doClick();
			INTERRUPTED = false;
		}
	}

}
