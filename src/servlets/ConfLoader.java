package servlets;
import configs.GenericConfig;
import configs.Graph;
import java.io.*;
import java.util.*;
import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;

/**
 * This class implements the ConfLoader which handles requests for loading configurations and generates 
 * the HTML representation of the graph based on the loaded configurations.
 */
public class ConfLoader implements Servlet{
    private GenericConfig config;

    /**
     * Handles the incoming request by processing the HTTP parameters, creating a file with the uploaded content,
     * loading the configuration and creating a graph, generating HTML for the computational graph, and sending
     * the HTML response back to the client.
     *
     * @param  requestInfo  the request information containing the HTTP parameters and content
     * @param  toClient     the output stream to send the response back to the client
     * @throws IOException  if there is an error reading or writing to the file or the output stream
     */
    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        if(config != null){
            config.close();
            config = null;
        }
        Map<String, String> httpParameters = requestInfo.getParameters();
        String fileName = httpParameters.get("filename");
        
        String fileContent = new String(requestInfo.getContent());
        File file = new File("config_files/" + fileName);
        file.createNewFile();
        BufferedReader reader = new BufferedReader(new StringReader(fileContent));
        FileWriter writer = new FileWriter(file);

        // Write the uploaded file content to the file
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.write(System.lineSeparator());
        }
        writer.close();

        // Load the configuration and create the graph
        config = new GenericConfig();
        config.setConfFile("config_files/" + fileName);
        config.create();
        Graph graph = new Graph();
        graph.createFromTopics();

        // Generate the HTML for the computational graph
        String graphHtml = generateGraphHtml(graph);

        // Convert the response to bytes
        byte[] responseBytes = graphHtml.toString().getBytes("UTF-8");
        
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
        "Content-Type: text/html\r\n" +
        "Content-Length: " + responseBytes.length + "\r\n" +
        "\r\n";

        // Convert the response body to a string (assuming it's a string)
        String responseBody = new String(responseBytes, "UTF-8");
        // Combine the HTTP response and the response body
        String fullResponse = httpResponse + responseBody;
    
        // Combine the HTTP response and the response body
        toClient.write(fullResponse.getBytes("UTF-8"));

        try{// Flush the toClient to ensure all content is sent
            toClient.flush();
        }catch(Exception e){
            e.printStackTrace();
        }       
    }
    

    /**
     * Generates an HTML representation of the given graph and returns it as a string.
     *
     * @param  graph  the graph to be converted to HTML
     * @return        an HTML string representing the graph, or an error message if no graph data is available
     * @throws IOException if there is an error generating the HTML
     */
    private String generateGraphHtml(Graph graph) throws IOException {
        ArrayList<String> htmlGraphStrings= new ArrayList<>();
        htmlGraphStrings = HtmlGraphWriter.getGraphHTML(graph);
        if (htmlGraphStrings.isEmpty()) {
            return "<html><body><h1>No Graph Data Available</h1></body></html>";
        } else {
            return String.join("", htmlGraphStrings);
        }
    }

    @Override
    public void close() {
        config.close();                
    }
}