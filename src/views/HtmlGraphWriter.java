package views;

import configs.Graph;
import configs.Node;
import java.io.*;
import java.util.*;

public class HtmlGraphWriter {
    public static ArrayList<String> getGraphHTML(Graph graph) throws IOException {
        ArrayList<String> htmlContent = new ArrayList<>();

		//TODO - Got an Input Stream is Null error here - maybe add a try-catch ?
        // Load the static HTML template
        InputStream inputStream = HtmlGraphWriter.class.getResourceAsStream("/graph.html");
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        String htmlTemplate = new String(buffer);

        // Replace placeholders with graph data
        htmlTemplate = htmlTemplate.replace("${graphNodes}", getGraphNodesHTML(graph));
        htmlTemplate = htmlTemplate.replace("${graphLinks}", getGraphLinksHTML(graph));

        // Split the HTML into a list of strings
        String[] htmlLines = htmlTemplate.split("\n");
        for (String line : htmlLines) {
            htmlContent.add(line);
        }

        return htmlContent;
    }

    private static String getGraphNodesHTML(Graph graph) {
		StringBuilder nodesHTML = new StringBuilder();
		for (Node node : graph) {
			String type = node.getName();
			if(type.charAt(0) == 'A') {
				type = "Agent";
			}else if(type.charAt(0) == 'T') {
				type = "Topic";	
			}
			nodesHTML.append("    { id: \"" + node.getName() + "\", type: \"" + type + "\" }");
			nodesHTML.append(",\n");
		}
		nodesHTML.delete(nodesHTML.length() - 2, nodesHTML.length()); // remove trailing comma and newline
		return "[" + nodesHTML.toString() + "]";
	}
	
	private static String getGraphLinksHTML(Graph graph) {
		StringBuilder linksHTML = new StringBuilder();
		for (Node node : graph) {
			for (Node neighbor : node.getEdges()) {
				linksHTML.append("    { source: \"" + node.getName() + "\", target: \"" + neighbor.getName() + "\" }");
				linksHTML.append(",\n");
			}
		}
		linksHTML.delete(linksHTML.length() - 2, linksHTML.length()); // remove trailing comma and newline
		return "[" + linksHTML.toString() + "]";
	}
}