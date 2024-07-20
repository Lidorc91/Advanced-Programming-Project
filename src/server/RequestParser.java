package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {        
		// implement
    	//Set up and read request
        ArrayList<String> requestLine = new ArrayList<>();        
        while ((reader.ready())) {
            requestLine.add(reader.readLine());
        }

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
        
        /* if(uriSegmentsParsed[uriSegmentsParsed.length - 1].contains(".")) {
            uriSegmentsParsed[uriSegmentsParsed.length - 1] = uriSegmentsParsed[uriSegmentsParsed.length - 1].split("\\.")[0];
        } */
		
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
    
		//Find Content Length to find the content starting index
        int contentIndex = 0;
        for (int i = 1; i < requestLine.size(); i++) {
			if (requestLine.get(i).startsWith("Content-Length")) {
				contentIndex = i;
				break;
			}
            contentIndex++;            
        }
        
        //No content case
        if(contentIndex == requestLine.size()-1){
            return new RequestInfo(httpCommand, uri, uriSegmentsParsed, null, null);
        }

        contentIndex = contentIndex + 2; //Move index to content
        //Get the file name if there is one
        if(requestLine.get(contentIndex).contains("filename=")) {
        	String[] fileParamater = requestLine.get(contentIndex).split("=");
    		parameters.put(fileParamater[0], fileParamater[1]);
    		contentIndex = contentIndex + 2;
		}

		//Parse Content
		byte[] content;
		StringBuilder contentBuilder = new StringBuilder();
        while (!requestLine.get(contentIndex).equals("")) {
        	contentBuilder.append(requestLine.get(contentIndex)).append("\n");
            contentIndex++;
        }
		content = contentBuilder.toString().getBytes();
		
		//Close the reader
        reader.close();

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

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
