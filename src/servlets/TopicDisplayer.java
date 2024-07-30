package servlets;
import graph.*;
import graph.TopicManagerSingleton.TopicManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import server.MyHTTPServer;
import server.RequestParser.RequestInfo;

/**
 * This class handles the incoming requests about messages sent through a topic and displays their latest values in an HTML table.
 */
public class TopicDisplayer implements Servlet{    
        /**
         * Handles the incoming HTTP request by parsing the parameters, storing the topic and message in a map,
         * creating a new message object, publishing the message to the specified topic, and generating an HTML response.
         * If the topic or message is missing, an error message is written to the client.
         *
         * @param  requestInfo  the request information containing the parameters of message and topic
         * @param  toClient     the output stream to write the response to
         * @throws IOException  if an I/O error occurs while writing the response
         */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        // Generate the HTML response        
        String topic = requestInfo.getParameters().get("topic");
        String message = requestInfo.getParameters().get("message");
        TopicManager tm = TopicManagerSingleton.get();      
        if (topic != null && message != null) {
            Message msg = new Message(message);
            tm.getTopics().forEach((t) -> { //Check if topic exists
                if(t._name.equals(topic) && t.getPublishers().isEmpty()){
                    tm.getTopic(topic).publish(msg);
                    tm.getTopic(topic).setMessage(msg);
                }else if(t._name.equals(topic) && !t.getPublishers().isEmpty()){
                    t.setMessage(new Message("Invalid Entry"));
                }
            });
            generateResponse(toClient);
  
        }else{
            String httpResponse = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 13\r\n" +
                "\r\n" +
                "Message missing";
            toClient.write(httpResponse.getBytes());
        }
    }
    /**
     * Generates an HTML response containing a table of topics and their latest values.
     *
     * @param  toClient  the output stream to write the response to
     * @throws IOException  if an I/O error occurs while writing the response
     */
    private void generateResponse(OutputStream toClient) throws IOException {
        File file = new File("html_files/topicTable.html");
        StringBuilder htmlTemplateBuilder = new StringBuilder();
    
        try (BufferedReader buffer = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = buffer.readLine()) != null) {
                htmlTemplateBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            // Handle the exception, e.g., log the error or throw a custom exception
            System.err.println("Error reading file: " + e.getMessage());
        }
    
        String table = createTable();
        String values = createValues();
    
        // Replace placeholders with graph data
        htmlTemplateBuilder.replace(0, htmlTemplateBuilder.length(), htmlTemplateBuilder.toString().replace("<!--TABLE-->", table));
        htmlTemplateBuilder.replace(0, htmlTemplateBuilder.length(), htmlTemplateBuilder.toString().replace("topics_values", values));
    
        byte[] responseBytes = htmlTemplateBuilder.toString().getBytes("UTF-8");
    
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + responseBytes.length + "\r\n" +
                "\r\n";
        String responseBody = new String(responseBytes, "UTF-8");
        String fullResponse = httpResponse + responseBody;
    
        System.out.println("full response from server: " + fullResponse);
        // Combine the HTTP response and the response body
        toClient.write(fullResponse.getBytes("UTF-8"));
    }
    public String createTable(){
        StringBuilder table = new StringBuilder();
        for (var topic : TopicManagerSingleton.get().getTopics()) {
            table.append("<tr>");
            table.append("<td>").append(topic._name).append("</td>");
            table.append("<td>").append(topic.getMessage().asText).append("</td>");
            table.append("</tr>");
        }
     return table.toString(); 
    }
    public String createValues() {
        StringBuilder values = new StringBuilder();
        boolean first = true;  // To manage the leading comma issue
    
        for (var topic : TopicManagerSingleton.get().getTopics()) {
            if (topic.getMessage() == null) {
                continue;
            }
            
            // Handle the message based on its type
            String valueStr;
            if (Double.isNaN(topic.getMessage().asDouble)) {
                valueStr = String.format("\"%s\": \"%s\"", topic._name, topic.getMessage().asText);
            } else {
                valueStr = String.format("\"%s\": %.02f", topic._name, topic.getMessage().asDouble);
            }
    
            // Append the value to the StringBuilder
            if (!first) {
                values.append("\n"); // Append comma and newline for subsequent values
            }
            values.append(valueStr);
            first = false;  // Subsequent entries should be preceded by a comma
        }
        
        // Wrap the whole string in curly braces
        return values.toString();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }

}   