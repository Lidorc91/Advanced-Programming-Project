package graph;

import java.util.Date;

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

    
    private double getDouble(String str) {
    	try {
			return Double.parseDouble(str);
		}
		catch (Exception e) {
			return Double.NaN;
    	}		
    }
}
