package configs;

import graph.Message;
import java.util.ArrayList;
import java.util.List;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;
    
	public Node(String name) {
		this.name = name;
		edges = new ArrayList<>();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Node> getEdges() {
		return edges;
	}
	public void setEdges(List<Node> edges) {
		this.edges = edges;
	}
	public Message getMsg() {
		return msg;
	}
	public void setMsg(Message msg) {
		this.msg = msg;
	}

	public void addEdge(Node n) {
		edges.add(n);
	}
	
	public boolean hasCycles() {
		Node startingNode = this;
		return checkGraph(startingNode);	
	}
	
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