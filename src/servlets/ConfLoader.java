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
        String currentWorkingDir = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentWorkingDir);
        String fileContent = new String(requestInfo.getContent());
        File file = new File("config_files/" + fileName);
        file.createNewFile();
        if (file.exists() && file.isFile()){
            System.out.println("File exists: " + file.getAbsolutePath());
        }else{
            System.out.println("File does not exist: " + file.getAbsolutePath());
        }
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
        GenericConfig config = new GenericConfig();
        config.setConfFile("config_files/" + fileName);
        config.create();
        Graph graph = new Graph();
        graph.createFromTopics();

        // Generate the HTML for the computational graph
        String graphHtml = generateGraphHtml(graph);
        toClient.write(graphHtml.getBytes("UTF-8"));
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