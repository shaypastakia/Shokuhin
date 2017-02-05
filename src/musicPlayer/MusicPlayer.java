package musicPlayer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import generalResources.GuiMethods;
import generalResources.MyMouseAdapter;
import main.Module;
import main.ShokuhinMain;
import maryb.player.Player;
import maryb.player.PlayerEventListener;
import maryb.player.PlayerState;

public class MusicPlayer extends Module implements PlayerEventListener {
	private static final long serialVersionUID = -3726342681975909996L;

	Thread thread;

	Player playerInternal;

	boolean isScrubbing = false;
	File currentSong;

	JPanel leftPanel = GuiMethods.getStyledJPanel();
	JPanel rightPanel = new JPanel();

	JButton playButton = GuiMethods.getStyledJButton("Play", 40);
	JButton pauseButton = GuiMethods.getStyledJButton("Pause", 40);
	JButton stopButton = GuiMethods.getStyledJButton("Stop", 40);
	JButton previousButton = GuiMethods.getStyledJButton("Previous", 40);
	JButton nextButton = GuiMethods.getStyledJButton("Next", 40);

	JButton timerPlayButton = GuiMethods.getStyledJButton("Play/Pause", 20);
	JButton timerPrevButton = GuiMethods.getStyledJButton("Previous", 20);
	JButton timerNextButton = GuiMethods.getStyledJButton("Next", 20);

	JButton addButton = GuiMethods.getStyledJButton("Add", 20);
	JButton removeButton = GuiMethods.getStyledJButton("Remove", 20);

	JPanel timePanel = GuiMethods.getStyledJPanel();
	JSlider timeSlider = new JSlider();
	JLabel timeLabel = GuiMethods.getStyledJLabel("", 20);

	DefaultListModel<File> listModel = new DefaultListModel<File>();
	JList<File> list = new JList<File>(listModel);

	JScrollPane scrollPane = GuiMethods.getStyledJScrollPane();

	JPanel listButtonPanel = GuiMethods.getStyledJPanel();
	JPanel controlButtonPanel = GuiMethods.getStyledJPanel();

	PlayerState state;

	MyMouseAdapter<File> adaptor = new MyMouseAdapter<File>(list, listModel);

	JFileChooser chooser = new JFileChooser();

	public MusicPlayer(ShokuhinMain main) {
		super(main, "Music Player");

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

		playerInternal = new Player();
		playerInternal.setListener(this);

		scrollPane.setViewportView(list);
		list.setBackground(ShokuhinMain.themeColour);
		list.addMouseListener(adaptor);
		list.addMouseMotionListener(adaptor);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftPanel.setPreferredSize(new Dimension(400, getHeight()));
		leftPanel.setMaximumSize(new Dimension(800, 9999));
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		list.setForeground(ShokuhinMain.FIFTY_SHADES_OF_WHITE);
		list.setFont(new Font(ShokuhinMain.themeFontName, Font.BOLD, 16));

		listButtonPanel.setLayout(new BoxLayout(listButtonPanel, BoxLayout.LINE_AXIS));
		listButtonPanel.add(addButton);
		listButtonPanel.add(removeButton);

		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		timeLabel.setText(String.format("   %02d:%02d/%02d:%02d", 0, 0, 0, 0));

		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File file = chooser.getSelectedFile();
				if (file != null)
					chooser.setCurrentDirectory(file);
				chooser.showOpenDialog(getParent());

				if (chooser.getSelectedFile() == null)
					return;

				listModel.addElement(new File(chooser.getSelectedFile().getAbsolutePath()) {
					private static final long serialVersionUID = 8528766500894517904L;

					@Override
					public String toString() {
						String name = this.getName();
						int i = name.lastIndexOf(".");
						name = name.substring(0, i);
						return name;
					}
				});
			}
		});

		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (playerInternal.getState() == PlayerState.PAUSED) {
					playerInternal.play();
					return;
				}

				if (playerInternal.getState() == PlayerState.PLAYING)
					try {
						playerInternal.stopSync();
					} catch (InterruptedException e1) {
						playerInternal.stop();
						e1.printStackTrace();
					}

				int selection = list.getSelectedIndex();
				if (selection == -1)
					return;

				System.out.println(listModel.get(selection));
				currentSong = listModel.get(selection);
				playerInternal.setSourceLocation(listModel.get(selection).getAbsolutePath());
				timeSlider.setMaximum(Math.toIntExact(playerInternal.getTotalPlayTimeMcsec()));
				try {
					playerInternal.playSync();
				} catch (InterruptedException e1) {
					playerInternal.play();
					e1.printStackTrace();
				}
			}
		});

		timeSlider.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				playerInternal.seek(timeSlider.getValue());
				isScrubbing = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				isScrubbing = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		pauseButton.addActionListener(e -> {
			try {
				playerInternal.pauseSync();
			} catch (InterruptedException e1) {
				playerInternal.pause();
				e1.printStackTrace();
			}
		});

		stopButton.addActionListener(e -> {
			try {
				playerInternal.stopSync();
			} catch (InterruptedException e1) {
				playerInternal.stop();
				e1.printStackTrace();
			}
		});

		removeButton.addActionListener(e -> {
			if (list.getSelectedIndex() != -1)
				listModel.remove(list.getSelectedIndex());
			else
				return;
		});

		previousButton.addActionListener(e -> previous());

		nextButton.addActionListener(e -> {
			endOfMedia();
		});

		listButtonPanel.setOpaque(false);
		controlButtonPanel.setOpaque(false);

		leftPanel.add(scrollPane);
		leftPanel.add(listButtonPanel);

		controlButtonPanel.add(playButton);
		controlButtonPanel.add(pauseButton);
		controlButtonPanel.add(stopButton);
		controlButtonPanel.add(previousButton);
		controlButtonPanel.add(nextButton);

		controlButtonPanel.setLayout(new BoxLayout(controlButtonPanel, BoxLayout.LINE_AXIS));
		timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.LINE_AXIS));
		JLabel label = GuiMethods.getStyledJLabel("", 20);
		label.setText("Time:  ");
		timePanel.add(label);
		timePanel.add(timeSlider);
		timePanel.add(timeLabel);

		JSeparator septimus = new JSeparator(SwingConstants.HORIZONTAL);
		septimus.setPreferredSize(new Dimension(rightPanel.getWidth(), 30));
		septimus.setMaximumSize(new Dimension(rightPanel.getWidth(), 30));

		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		controlButtonPanel.setMaximumSize(new Dimension(9999, 200));
		timePanel.setMaximumSize(new Dimension(9999, 200));
		timeSlider.setMaximumSize(new Dimension(600, 200));
		rightPanel.add(controlButtonPanel);
		rightPanel.add(septimus);
		rightPanel.add(timePanel);
		add(leftPanel);

		JSeparator septimusPrime = new JSeparator(SwingConstants.VERTICAL);
		septimusPrime.setPreferredSize(new Dimension(25, getHeight()));
		septimusPrime.setMaximumSize(new Dimension(30, getHeight()));
		add(septimusPrime);
		add(rightPanel);

		timerPlayButton.setBackground(ShokuhinMain.themeColour);
		timerPlayButton.setOpaque(true);

		timerPrevButton.setBackground(ShokuhinMain.themeColour);
		timerPrevButton.setOpaque(true);

		timerNextButton.setBackground(ShokuhinMain.themeColour);
		timerNextButton.setOpaque(true);

		timerPlayButton.addActionListener(e -> {
			if (playerInternal.getState() == PlayerState.PLAYING)
				pauseButton.doClick();
			else if (playerInternal.getState() == PlayerState.PAUSED)
				playButton.doClick();
		});

		timerPrevButton.addActionListener(e -> previousButton.doClick());
		timerNextButton.addActionListener(e -> nextButton.doClick());

		ShokuhinMain.timer.add(timerPlayButton);
		ShokuhinMain.timer.add(timerPrevButton);
		ShokuhinMain.timer.add(timerNextButton);
	}

	@Override
	public boolean close() {
		try {
			playerInternal.stopSync();
		} catch (InterruptedException e){
			playerInternal.stop();
		}
		ShokuhinMain.timer.remove(timerPlayButton);
		ShokuhinMain.timer.remove(timerPrevButton);
		ShokuhinMain.timer.remove(timerNextButton);
		ShokuhinMain.timer.repaint();
		return true;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("Home");

		// A menu item that can be used when there are no functions to be
		// displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

	@Override
	public void KeyPressed(KeyEvent e) {

	}

	public void stateChanged() {
		this.state = playerInternal.getState();
		switch (playerInternal.getState()) {
		case PAUSED:
			break;
		case PAUSED_BUFFERING:
			break;
		case PLAYING:
			if (!isScrubbing) {
				timeSlider.setMaximum(Math.toIntExact(playerInternal.getCurrentBufferedTimeMcsec()));
				timeSlider.setValue(Math.toIntExact(playerInternal.getCurrentPosition()));

				// Based on
				// http://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java

				long microseconds = playerInternal.getCurrentBufferedTimeMcsec();
				long maxSeconds = (microseconds / 1000000) % 60;
				long maxMinutes = ((microseconds / (1000000 * 60)) % 60);

				microseconds = playerInternal.getCurrentPosition();
				long curSeconds = (microseconds / 1000000) % 60;
				long curMinutes = ((microseconds / (1000000 * 60)) % 60);

				String time = String.format("   %02d:%02d/%02d:%02d", curMinutes, curSeconds, maxMinutes, maxSeconds);

				timeLabel.setText(time);
			}
			break;
		case STOPPED:
			break;
		default:
			break;

		}
	}

	public boolean isPlaying() {
		if (playerInternal.getState() == PlayerState.PLAYING)
			return true;
		else
			return false;
	}

	public void interruptPlay() {
		playButton.doClick();
		timerPlayButton.setEnabled(true);
		timerPrevButton.setEnabled(true);
		timerNextButton.setEnabled(true);
	}

	public void interruptPause() {
		pauseButton.doClick();
		timerPlayButton.setEnabled(false);
		timerPrevButton.setEnabled(false);
		timerNextButton.setEnabled(false);
	}

	@Override
	public void endOfMedia() {
		int i = listModel.indexOf(currentSong) + 1;
		if (i == 0)
			return;

		if (i <= listModel.size() - 1) {
			list.setSelectedIndex(i);
			playButton.doClick();
		}
		scrollPane.repaint();
	}

	public void previous() {
		int i = listModel.indexOf(currentSong) - 1;
		if (i < 0)
			return;

		list.setSelectedIndex(i);
		playButton.doClick();
	}

	@Override
	public void buffer() {
	}

}
