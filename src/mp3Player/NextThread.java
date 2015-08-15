package mp3Player;

/**
 * Represents the thread that deals with moving to the next song in a playlist
 *
 * @author Samuel Barker
 */
public class NextThread extends Thread
{

	/**
	 * Determines whether the run method is executed or not
	 */
	private boolean RUNNABLE = true;

	/**
	 * Creates a NextThread
	 */
	public NextThread(Runnable r)
	{
		super(r);
	}

	/**
	 * Returns whether the thread is 'runnable' or not
	 * 
	 */
	public boolean isRunnable()
	{
		return RUNNABLE;
	}

	/**
	 * Sets whether the thread is 'runnable' or not
	 */
	public void setRunnable(boolean x)
	{
		this.RUNNABLE = x;
	}
}
