package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the RequestParser which parses an HTTP request and returns a RequestInfo object containing the parsed information.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from a BufferedReader and returns a RequestInfo object containing the parsed information.
     *
     * @param  reader  the BufferedReader from which to read the request
     * @return         a RequestInfo object containing the parsed information which includes the HTTP command, URI, and parameters
     * @throws IOException if an I/O error occurs while reading the request
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {        
		// implement
    	//Set up and read request
        ArrayList<String> requestLine = new ArrayList<>();        
        while ((reader.ready())) {
            requestLine.add(reader.readLine());
        }

        //System.out.println("requestLine: " + requestLine);

        //Split the Command and URI Line
        String httpCommand = requestLine.get(0).split(" ")[0];
        String uri = requestLine.get(0).split(" ")[1];

        //Parse URI
        String[] uriSegments = uri.split("/");
        String uriSegmentsParsed[];
        if(uriSegments.length == 0) { //Root URI
            uriSegmentsParsed = new String[]{"/"};
        }else{
            uriSegmentsParsed = new String[uriSegments.length - 1];
            for(int i = 0; i < uriSegments.length-1; i++) {
                uriSegmentsParsed[i] = uriSegments[i+1];
            }
        }

        System.out.print("uriSegments requested: ");
        
        for (String s : uriSegmentsParsed) {
            System.out.print("/"+s);
        }
        System.out.print('\n');
        
		//Parse the Parameters 
  		Map<String, String> parameters = new HashMap<>();
		if(uriSegmentsParsed[uriSegmentsParsed.length - 1].contains("?")) {
            uriSegmentsParsed[uriSegmentsParsed.length - 1] = uriSegmentsParsed[uriSegmentsParsed.length - 1].split("\\?")[0];
			String allParameters = uri.split("\\?")[1];
	  		String[] parameterPairs = allParameters.split("&");
	  		for(String pair : parameterPairs) {
	  			String[] keyValue = pair.split("=");
	  			parameters.put(keyValue[0], keyValue[1]);
	  		}			
		}
        int lastLineIndex = 0;
        int contentLength = 0;
        for (int i = 0; i < requestLine.size() - 1; i++ ) {
            String line = requestLine.get(i);
            if (line.startsWith("Content-Length:")) {
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    try {
                        contentLength = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {}
                }
            }
            //catching content-length
            if (line.isEmpty()) 
                lastLineIndex = i;
        }
        
        //file name exraction
        Pattern filenamePattern = Pattern.compile("filename=\"([^\"]+)\"");
        for (int i = 0; i < lastLineIndex; i++) {
            Matcher matcher = filenamePattern.matcher(requestLine.get(i));
            if (matcher.find()) {
                String filename = matcher.group(1); // extracts the filename
                parameters.put("filename", filename);
                System.out.println(filename); // prints "testing.json"
                break;
            }
        }

        //content extraction
        byte[] content;
		StringBuilder contentBuilder = new StringBuilder();
        int contentIndex = lastLineIndex+1;
        int remainingBytes = contentLength;
        while (contentIndex < requestLine.size()-1) {
            String contentLine = requestLine.get(contentIndex);
            int bytesRead = Math.min(remainingBytes, contentLine.length());
            contentBuilder.append(contentLine.substring(0, bytesRead));
            contentBuilder.append("\n");
            contentIndex++;
            remainingBytes -= bytesRead;
        }
        content = contentBuilder.toString().getBytes();

        //Create the RequestInfo
        return new RequestInfo(httpCommand, uri, uriSegmentsParsed, parameters, content);
    }
        

	
	// RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

    /**
     * Returns the HTTP command associated with the request (e.g., GET, POST, etc.).
     *
     * @return the HTTP command as a string
     */
        public String getHttpCommand() {
            return httpCommand;
        }

    /**
     * Returns the full URI of the request.
     *
     * @return the URI of the request as a String
     */
        public String getUri() {
            return uri;
        }

    /**
     * Returns an array of all URI segments in the request.
     *
     * @return an String array of URI segments
     */
        public String[] getUriSegments() {
            return uriSegments;
        }

    /**
     * Returns a map of the parameters in the request.
     *
     * @return a map of parameters
     */
        public Map<String, String> getParameters() {
            return parameters;
        }

    /**
     * Returns the content of the request as a byte array.
     *
     * @return the content as a byte array
     */
        public byte[] getContent() {
            return content;
        }
    }
}
