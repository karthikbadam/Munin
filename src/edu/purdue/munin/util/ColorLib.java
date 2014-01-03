package edu.purdue.munin.util;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorLib {
	
	/**
	 * Parse a color input string and create corresponding Java Color.
	 * @param input color input of the form "rgb(r, g, b)", for example "rgb(0, 0, 0)" for black
	 * @return a valid Java Color class, or null if malformed input
	 * @author http://stackoverflow.com/questions/7613996/parsing-a-rgb-x-x-x-string-into-a-color-object
	 */
	public static Color parse(String input) {
		Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
	    Matcher m = c.matcher(input);

	    if (m.matches()) {
	        return new Color(Integer.valueOf(m.group(1)),  // r
	                         Integer.valueOf(m.group(2)),  // g
	                         Integer.valueOf(m.group(3))); // b 
	    }

	    return null;  
	}
}
