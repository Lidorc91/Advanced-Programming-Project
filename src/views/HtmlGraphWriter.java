package views;

import configs.Graph;
import configs.Node;
import java.io.*;
import java.util.*;
/**
 * This class generates an HTML representation of the given graph and returns it as an HTML string.
 */
public class HtmlGraphWriter {
    /**
     * Generates an HTML representation of the given graph and returns it as an HTML string.
     *
     * @param  graph  the graph to be converted to HTML
     * @return        an ArrayList of strings representing the HTML representation of the graph
     * @throws IOException if there is an error reading the HTML template file
     */
    public static ArrayList<String> getGraphHTML(Graph graph) throws IOException {
        ArrayList<String> htmlContent = new ArrayList<>();

		File file = new File("html_files/graph.html");
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

		String htmlTemplate = htmlTemplateBuilder.toString();

		// Replace placeholders with graph data
		htmlTemplate = htmlTemplate.replace("graphNodes", getGraphNodesHTML(graph));
		htmlTemplate = htmlTemplate.replace("graphLinks", getGraphLinksHTML(graph));
		
		// Split the HTML into a list of strings
		String[] htmlLines = htmlTemplate.split("\n");
		
		for (String line : htmlLines) {
			htmlContent.add(line);
		}

	return htmlContent;
    }

	/**
	 * Assiting method for creation of an HTML representation of the given graph nodes and returns it as a string.
	 *
	 * @param  graph   the graph containing the nodes to be converted to HTML
	 * @return         a string representing the HTML representation of the graph nodes
	 */
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
	
	/**
	 * Generates an HTML representation of the graph edges.
	 *
	 * @param  graph   the graph containing the edges to be converted to HTML
	 * @return         a string representing the HTML representation of the graph edges
	 */
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