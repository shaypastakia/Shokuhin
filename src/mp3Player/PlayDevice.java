package mp3Player;

import java.util.ArrayList;

import maryb.player.Player;

public class PlayDevice {

	/**
	 * Player to play the songs, as well as an array list of songs for a
	 * playlist.
	 */
	private Player p;
	private ArrayList<String> songs;

	/**
	 * Store the current song number and the position when paused
	 */
	private int songNumber;
	private long pos;

	/**
	 * Signifies if the player has been paused or stopped
	 */
	private boolean PAUSED = false;
	private boolean STOPPED = false;

	public PlayDevice() {
		p = new Player();
		songs = new ArrayList<String>();
		songNumber = 0;
		pos = 0;
	}

	/**
	 * Functions which cause the pausing, stopping and playing, executed in
	 * separate threads when buttons are pressed
	 */
	public synchronized void pause() {
		PAUSED = true;
	}

	public synchronized void stop() {
		STOPPED = true;
	}

	public synchronized void play() {
		PAUSED = false;
		STOPPED = false;
	}

	/**
	 * Stops the player
	 */
	private void stopPlayer() {
		p.pause();
		songNumber = 0;
		pos = 0;
	}

	/**
	 * Pauses the player
	 */
	private void pausePlayer() {
		p.pause();
		pos = p.getCurrentPosition();
	}

	/**
	 * Plays a single song
	 * 
	 * @return returns true if the player has been interrupted
	 */
	public synchronized boolean playSong() {
		p.setSourceLocation(songs.get(songNumber));
		p.seek(pos);
		p.play();
		while (!p.isEndOfMediaReached()) {
			if (PAUSED) {
				pausePlayer();
				return true;
			}

			if (STOPPED) {
				stopPlayer();
				return true;
			}
		}
		songNumber++;
		return false;
	}

	/**
	 * Plays a playlist, run this in a thread
	 */
	public synchronized void playPlaylist() {
		while (songNumber < songs.size()) {
			if (playSong())
				return;
		}
	}

}
