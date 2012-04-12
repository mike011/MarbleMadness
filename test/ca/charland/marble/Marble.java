package ca.charland.marble;

import java.util.Set;
import java.util.TreeSet;

/**
 * The Class Marble.
 * 
 * @author mcharland
 */
public class Marble {
	
	private Set<String> colours;

	/**
	 * Instantiates a new marble.
	 * 
	 * @param colour
	 *            the colour
	 */
	public Marble(String colour) {
		colours = new TreeSet<String>();
		colours.add(colour);
	}

	/**
	 * Instantiates a new marble.
	 * 
	 * @param colour
	 *            the colour
	 * @param colour2
	 *            the colour2
	 */
	public Marble(String colour, String colour2) {
		this(colour);
		colours.add(colour2);
	}
}
