package servlets;
import graph.*;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser.RequestInfo;

public class TopicDisplayer implements Servlet{
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        // Generate the HTML response        
        String topic = requestInfo.getParameters().get("topic");
        String message = requestInfo.getParameters().get("message");
        if (topic != null && message != null) {
            Message msg = new Message(message);
            TopicManagerSingleton.get().getTopic(topic).publish(msg);
            generateResponse(toClient);
        }else{
            toClient.write("message/topic missing".getBytes());
        }
    }
    private void generateResponse(OutputStream toClient) throws IOException {
        StringBuilder response = new StringBuilder();
        // Get all topics from the TopicManager
        toClient.write("OK".getBytes());
        
        // Start of HTML content
        response.append("<html>");
        response.append("<head><title>Topic Values</title></head>");
        response.append("<body>");
        response.append("<h2>Topic Values</h2>");

        // Start of table
        response.append("<table border='1'>");
        response.append("<tr><th>Topic</th><th>Latest Value</th></tr>");

        // Iterate over each topic and add rows to the table
        for (var topic : TopicManagerSingleton.get().getTopics()) {
            String topicName = topic.name;
            //String latestValue = topic.
            response.append("<tr>");
            response.append("<td>").append(topicName).append("</td>");
            //response.append("<td>").append(latestValue != null ? latestValue : "No messages yet").append("</td>");
            response.append("</tr>");
        }

        // End of table and HTML content
        response.append("</table>");
        response.append("</body>");
        response.append("</html>");


        // Convert the response to bytes
        byte[] responseBytes = response.toString().getBytes();
    
        // Send the HTTP response headers
        toClient.write(("HTTP/1.1 200 OK\r\n").getBytes());
        toClient.write(("Content-Type: text/html\r\n").getBytes());
        toClient.write(("Content-Length: " + responseBytes.length + "\r\n").getBytes());
        toClient.write(("\r\n").getBytes());
    
        // Send the response body
        toClient.write(responseBytes);
    
        // Flush the toClient to ensure all content is sent
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
 
}
