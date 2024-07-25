package graph;

import java.util.Date;

/**
 * This class represents a message in the graph. It can be a string, a double, a date, or a byte array.
 */
public class Message {
    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;
    
    public Message(String str) {
    	date = new Date();
    	asText = str;    	
    	data = str.getBytes();
    	asDouble = getDouble(str);  		
	}	
    
    public Message(byte[] data) {		
		this(new String(data));
	}
    public Message(double num) {
    	this(Double.toString(num));    	
	}
    public Message(int num) {
    	this(Integer.toString(num).toString());    	
	}

    
	/**
	 * Parses the message string to a double value. If the string cannot be parsed to a double,
	 * it returns NaN (Not a Number).
	 *
	 * @param  str  the message string to be parsed to a double
	 * @return      the double value parsed from the string, or NaN if the string cannot be parsed
	 */
    private double getDouble(String str) {
    	try {
			return Double.parseDouble(str);
		}
		catch (Exception e) {
			return Double.NaN;
    	}		
    }
}
