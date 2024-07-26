package servlets;
import views.HtmlGraphWriter;

import java.io.*;
import java.util.*;

import configs.GenericConfig;
import configs.Graph;
import server.RequestParser.RequestInfo;

public class ConfLoader implements Servlet{

    @Override
    public void handle(RequestInfo requestInfo, OutputStream toClient) throws IOException {
        Map<String, String> httpParameters = requestInfo.getParameters();
        String fileName = httpParameters.get("filename");
        String fileContent = new String(requestInfo.getContent());
        File file = new File("config_files/" + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(fileContent);
        }
    
        GenericConfig config = new GenericConfig();
        config.setConfFile("config_files/" + fileName);
        config.create();
        Graph graph = new Graph();
        graph.createFromTopics();
    
        //String graphHtml = generateGraphHtml(graph);
        String graphHtml = "hello world";

        byte[] responseBody = graphHtml.getBytes("UTF-8");
        toClient.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        toClient.write("Content-Type: text/html\r\n".getBytes("UTF-8"));
        toClient.write(("Content-Length: " + responseBody.length + "\r\n").getBytes("UTF-8"));
        toClient.write("\r\n".getBytes("UTF-8"));
        toClient.write(responseBody);
    
        toClient.flush();
    }
    

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
    public void close() throws IOException {
        throw new UnsupportedOperationException("Unimplemented method 'close'");
    }
}