package mp3Player;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import main.Module;
import main.ShokuhinMain;

public class MP3Player extends Module {
	private static final long serialVersionUID = -6219613302072108822L;

	public MP3Player(ShokuhinMain m) {
		super(m, "MP3 Player");
		// TODO Auto-generated constructor stub
		add(new JLabel("Tom Hiddleston"));
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JMenu getFunctionMenu() {
		JMenu menu = new JMenu("MP3 Player");
		
		//A menu item that can be used when there are no functions to be displayed
		JMenuItem noFuncs = new JMenuItem("(No functions)");
		noFuncs.setEnabled(false);
		menu.add(noFuncs);
		return menu;
	}

}