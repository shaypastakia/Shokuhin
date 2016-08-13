package generalResources;

import java.sql.Timestamp;

/**
 * Convenience class for RecipeMethods & SQLEngine
 * @author Shaylen Pastakia
 *
 */
public class Pair {

	private String s;
	private Timestamp t;
	
	public Pair(String s, Timestamp t){
		this.s = s;
		this.t = t;
	}
	
	public String getFirst(){
		return s;
	}
	
	public Timestamp getSecond(){
		return t;
	}
}
