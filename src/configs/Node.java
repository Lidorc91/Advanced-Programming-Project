package configs;

import graph.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a node in the graph, which could be an agent or a topic.
 */
public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;
    
	public Node(String name) {
		this.name = name;
		edges = new ArrayList<>();
	}
	
    /**
     * Retrieves the name of the node.
     *
     * @return the name of the node
     */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the Node.
	 *
	 * @param  name  the new name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the list of edges of this node.
	 *
	 * @return the list of edges
	 */
	public List<Node> getEdges() {
		return edges;
	}
	
	/**
	 * Sets the edges of this node.
	 *
	 * @param  edges  the list of nodes to set as edges
	 */
	public void setEdges(List<Node> edges) {
		this.edges = edges;
	}
	
    /**
     * Returns the message associated with this node.
     *
     * @return the message associated with this node
     */
	public Message getMsg() {
		return msg;
	}
	
    /**
     * Sets the message of this node.
     *
     * @param  msg  the message to be set
     */
	public void setMsg(Message msg) {
		this.msg = msg;
	}

    /**
     * Adds a new edge to the node, connecting it to the specified node.
     *
     * @param  n  the node to connect to
     */
	public void addEdge(Node n) {
		edges.add(n);
	}
	
	/**
	 * Checks if the current node is part of a cycle in the graph. (uses the checkGraph method mostly)
	 *
	 * @return true if a cycle is found, false otherwise
	 */
	public boolean hasCycles() {
		Node startingNode = this;
		return checkGraph(startingNode);	
	}
	
	/**
	 * Checks if the given startingNode is part of a cycle in the graph.
	 *
	 * @param  startingNode  the Node to check for cycles
	 * @return               true if a cycle is found, false otherwise
	 */
	private boolean checkGraph(Node startingNode) {
		for (Node n : this.edges) {
			if(n.edges.contains(startingNode)) {
				return true;
			}else {
				if (n.checkGraph(startingNode)) {
					return true;
				}
			}			
		}	
		return false;
	}
}