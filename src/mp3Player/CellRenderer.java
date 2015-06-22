package mp3Player;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Class to deal with colour changes of playing tracks
 *
 */
public class CellRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 2644181371111623670L;

	/**
	 * The MP3Player used
	 */
	private MP3Player mp3;

	/**
	 * Creates a CellRenderer
	 */
	public CellRenderer(MP3Player mp3) {
		setOpaque(true);
		this.mp3 = mp3;
	}

	/**
	 * obtains the cell renderer component
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// Assumes the stuff in the list has a pretty toString
		setText(value.toString());

		// based on the index you set the color. This produces the every other
		// effect.
		if (isSelected) {
			setBackground(Color.LIGHT_GRAY);
		} else {
			setBackground(Color.WHITE);
		}

		if (index == mp3.getCurrentSong()) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}

		return this;
	}
}