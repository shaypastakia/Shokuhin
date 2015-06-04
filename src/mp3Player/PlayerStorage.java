package mp3Player;

public class PlayerStorage {

	private String currentSong;
	private long position;
	
	public PlayerStorage(String currentSong, long position)
	{
		this.currentSong = currentSong;
		this.position = position;
	}

	public String getCurrentSong() {
		return currentSong;
	}

	public long getPosition() {
		return position;
	}
}
