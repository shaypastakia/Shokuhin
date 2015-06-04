package mp3Player;

import java.util.ArrayList;

import maryb.player.Player;

public class PlayDevice {

	/**
	 * Player to play the songs, as well as an array list of songs for a
	 * playlist
	 */
	private Player p;
	private ArrayList<String> songs;

	/**
	 * Struct used to depict the state of the player
	 *
	 */
	private enum PLAYER_STATE {
		PLAYING, STOPPED, PAUSED;
	};

	/**
	 * Stores the last state of the player before pausing
	 */
	private PlayerStorage ps;

	/**
	 * Stores the current state of the player
	 */
	private PLAYER_STATE state;

	public PlayDevice() {
		p = new Player();
		songs = new ArrayList<String>();
		state = PLAYER_STATE.STOPPED;
		ps = null;
	}

	/**
	 * Plays the current playlist
	 */
	public synchronized void play() {
		// if we're starting a fresh playlist, and have nothing saved from a
		// previous pause
		if (ps == null) {
			for (String song : songs) {
				p.setSourceLocation(song);
				p.play();
				state = PLAYER_STATE.PLAYING;
				while (!p.isEndOfMediaReached())
					;
			}
		}
	}

	/**
	 * Pauses the state of the player and stores it
	 */
	public synchronized void pause() {
		if (state == PLAYER_STATE.PLAYING) {
			p.pause();

			ps = new PlayerStorage(p.getSourceLocation(),
					p.getCurrentPosition());

			state = PLAYER_STATE.PAUSED;
		}
	}

	/**
	 * Resumes the state of the player and calls play once more
	 */
	public synchronized void resume() {
		if (state == PLAYER_STATE.PAUSED) {
			// Find the place where we left off in the array
			int i = 0;
			while (i < songs.size()) {
				if (songs.get(i).compareTo(ps.getCurrentSong()) != 0) {
					i++;
				} else {
					break;
				}

			}
			// Resume playing the song
			p.setSourceLocation(ps.getCurrentSong());
			p.seek(ps.getPosition());
			p.play();
			state = PLAYER_STATE.PLAYING;
			while (!p.isEndOfMediaReached());

			i++;

			// Then loop through the rest of the list
			while (i < songs.size()) {
				p.setSourceLocation(songs.get(i));
				p.play();
				state = PLAYER_STATE.PLAYING;
				while (!p.isEndOfMediaReached());
			}

		}
	}
}
