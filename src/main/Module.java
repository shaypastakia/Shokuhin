package main;

import javax.swing.JMenu;
import javax.swing.JPanel;

/**
 * Module
 * <br>
 * Creates a high level Object definition for all Modules.
 * @author Shaylen Pastakia
 */
public abstract class Module extends JPanel{
	private static final long serialVersionUID = -1628773001939210459L;
	
	protected String name;
	protected String desc;
	protected ShokuhinMain parent;
	
	public Module(ShokuhinMain m, String t){
//		this.setPreferredSize(new Dimension(900, 600));
		name = t;
		parent = m;
	}
	
	/**
	 * Used by the Tab Pane to display a name the Tab
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return The ShokuhinMain associated with the implementing Module
	 */
	protected ShokuhinMain getPar(){
		return parent;
	}
	
	/**
	 * Implemented by all Modules to clean up before closing. Return True to allow the Tab Pane to close the module.
	 * <br />
	 * Return False to refuse permission and prevent the tab from closing if necessary.
	 * <br />
	 * Automatically called when the Window is closed.
	 * 
	 * @return True or False
	 */
	public abstract boolean close();
	
	/**
	 * Implemented by all Modules to provide a Menu that is displayed when the Module is active in the Tab Pane.
	 * @return A Menu to display on the Dynamic Menu
	 */
	public abstract JMenu getFunctionMenu();
	
}
