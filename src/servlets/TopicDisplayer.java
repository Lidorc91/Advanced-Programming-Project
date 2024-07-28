package servlets;
import graph.*;
import graph.TopicManagerSingleton.TopicManager;
import java.io.IOException;
import java.io.OutputStream;
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
                }else if(t._name.equals(topic) && !t.getPublishers().isEmpty()){
                    t.setMessage(new Message("Invalid Entry"));
                }
            });
            generateResponse(toClient);
        }else{
            toClient.write("message/topic missing".getBytes());
        }
    }
    /**
     * Generates an HTML response containing a table of topics and their latest values.
     *
     * @param  toClient  the output stream to write the response to
     * @throws IOException  if an I/O error occurs while writing the response
     */
    private void generateResponse(OutputStream toClient) throws IOException {
        StringBuilder response = new StringBuilder();
        // Start of HTML content
        response.append("<html>");
        response.append("<head><title>Topic Values</title></head>");
        response.append("<body>");
        response.append("<h2>Topic Values</h2>");

        // Start of table
        response.append("<table border='1'>");
        response.append("<tr><th>Topic</th><th>Latest Value</th></tr>");

        // Iterate over each currentTopic and add rows to the table
        for (var topic : TopicManagerSingleton.get().getTopics()) {
            response.append("<tr>");
            response.append("<td>").append(topic._name).append("</td>");
            response.append("<td>").append(topic.getMessage().asText).append("</td>");
            response.append("</tr>");
        }
        
        // End of table and HTML content
        response.append("</table>");
        response.append("</body>");
        response.append("</html>");

        // Convert the response to bytes
        byte[] responseBytes = response.toString().getBytes("UTF-8");
    
        // Send the HTTP response headers
        toClient.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        toClient.write("Content-Type: text/html\r\n".getBytes("UTF-8"));
        toClient.write(("Content-Length: " + responseBytes.length + "\r\n").getBytes("UTF-8"));
        toClient.write("\r\n".getBytes("UTF-8"));

        // Send the response body
        toClient.write(responseBytes);

        // Flush the toClient to ensure all content is sent
        toClient.flush();
    }

    @Override
    public void close() throws IOException {
        // No resources to close
    }

}   