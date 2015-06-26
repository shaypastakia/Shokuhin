package mp3Player;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javazoom.jl.decoder.JavaLayerException;
import main.Module;
import main.ShokuhinMain;
import main.TimerBar;

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
	 * Handles the continuous play of the playlist
	 */
	private NextThread nextThread;

	/**
	 * Controls for TimerBar
	 */
	private TimerBar bar = ShokuhinMain.timer;
	private JSeparator septimus = new JSeparator();
	private JButton playButton = new JButton("Play");
	private JButton pauseButton = new JButton("Pause");
	private JButton prevButton = new JButton("Previous");
	private JButton nextButton = new JButton("Next");

	/**
	 * Creates an MP3Player module
	 * 
	 * @param m
	 *            The ShokuhinMain used
	 */
	public MP3Player(ShokuhinMain m) {
		super(m, "MP3 Player");

		createGui();
		songs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setUpListeners();
		setUpControls();
		add(splitPane);

		nextThread = new NextThread(new Runnable() {
			@Override
			public void run() {
				while (nextThread.isRunnable()) {
					if (player == null) {

					} else if (player.state() == 3) {
						if (next.isEnabled())
							next.doClick();
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		nextThread.start();
	}

	private void createGui() {
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
	}

	/**
	 * Closes the module
	 */
	@Override
	public boolean close() {
		if (player != null) {
			player.close();
		}

		nextThread.setRunnable(false);

		while (nextThread.isAlive())
			;
		nextThread = null;

		bar.remove(septimus);
		bar.remove(playButton);
		bar.remove(pauseButton);
		bar.remove(prevButton);
		bar.remove(nextButton);

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

					File[] files = fc.getSelectedFiles();
					for (File f : files) {
						String filename = f.getName();
						if (filename.endsWith(".mp3")) {
							listModel.addElement(filename.replace(".mp3", ""));
							songPaths.add(f);
						}
					}

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
					if (songPaths.size() > 0 && currentSong == -1) {
						currentSong = 0;
					}

					update();

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
					pauseButton.setEnabled(true);
				} else if (player.state() == 2) {
					play.setEnabled(false);
					playButton.setEnabled(false);
					pause.setEnabled(true);
					pauseButton.setEnabled(true);
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
				playButton.setEnabled(true);
				stop.setEnabled(true);
				pause.setEnabled(false);
				pauseButton.setEnabled(false);
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
				if (player == null || player.state() == 2) {
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

		songs.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// Double clicked
					int index = songs.locationToIndex(e.getPoint());
					currentSong = index;
					update();

					if (player.state() == 1) {
						player.pause();
					}

					playSong();
				}
			}
		});
	}

	/**
	 * Produces the controls for the Timer Bar
	 */
	private void setUpControls() {
		bar.add(septimus);
		bar.add(playButton);
		bar.add(pauseButton);
		bar.add(prevButton);
		bar.add(nextButton);
		playButton.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		pauseButton.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		prevButton.setFont(new Font("Times New Roman", Font.PLAIN, 26));
		nextButton.setFont(new Font("Times New Roman", Font.PLAIN, 26));

		playButton.setEnabled(false);
		pauseButton.setEnabled(false);

		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.setEnabled(false);
				pauseButton.setEnabled(true);
				resume();
			}
		});

		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				playButton.setEnabled(true);
				pauseButton.setEnabled(false);
				interrupt();
			}
		});

		nextButton.addActionListener(new ActionListener() {
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
		prevButton.addActionListener(new ActionListener() {
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
		JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
		JMenuItem savePlaylist = new JMenuItem("Save Playlist");

		FileFilter loadFilter = new FileNameExtensionFilter(
				"Shokuhin Playlist '.spl'", "spl");
		loadPlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(loadFilter);
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				System.out.println("5");
				if (file == null)
					return;
				System.out.println("4");
				try {
					System.out.println("3");
					FileInputStream in = new FileInputStream(file);
					ObjectInputStream inStream = new ObjectInputStream(in);
					@SuppressWarnings("unchecked")
					ArrayList<File> files = (ArrayList<File>) inStream
							.readObject();
					inStream.close();
					System.out.println("2");
					System.out.println("1");
					player = null;
					INTERRUPTED = false;
					currentSong = -1;
					songPaths.clear();
					listModel.clear();
					for (File f : files) {
						System.out.println(f.getAbsolutePath());
						songPaths.add(f);
						listModel
								.addElement(f.getName().replaceAll(".mp3", ""));
					}

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

					/*
					 * createGui();
					 * songs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION
					 * ); setUpListeners(); setUpControls();
					 */
				} catch (Exception e1) {
					ShokuhinMain.displayMessage("Error",
							"Failed to load Playlist.\n" + e1.getMessage(),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		savePlaylist.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (songPaths.isEmpty()) {
						ShokuhinMain
								.displayMessage(
										"Error",
										"Can't save an empty Playlist.\nTry adding some songs to the Playlist first.",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
					JFileChooser chooser = new JFileChooser();
					chooser.showSaveDialog(null);
					File file = chooser.getSelectedFile();
					if (file == null)
						return;
					if (!file.getPath().endsWith(".spl"))
						file = new File(file.getAbsoluteFile() + ".spl");
					FileOutputStream out = new FileOutputStream(file);
					ObjectOutputStream outStream = new ObjectOutputStream(out);
					outStream.writeObject(songPaths);
					out.close();
					ShokuhinMain.displayMessage("Success",
							"Successfully saved Playlist to " + file.getPath(),
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					ShokuhinMain.displayMessage("Error",
							"Failed to save Playlist.\n" + ex.getMessage(),
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		menu.add(loadPlaylist);
		menu.add(savePlaylist);
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
			nextButton.setEnabled(true);
		} catch (Exception e1) {
			// There is no next song
			next.setEnabled(false);
			nextButton.setEnabled(false);
		}

		try {
			songPaths.get(currentSong - 1);
			// There is a previous song
			previous.setEnabled(true);
			prevButton.setEnabled(true);
		} catch (Exception e1) {
			// There is no previous song
			previous.setEnabled(false);
			prevButton.setEnabled(false);
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

	/**
	 * Return the state of the internal Player
	 * 
	 * @return player state
	 */
	public int getPlayerState() {
		return player.state();
	}

}
