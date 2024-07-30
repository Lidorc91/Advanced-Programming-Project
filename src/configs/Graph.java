package configs;

import graph.Agent;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the computational graph. 
 * It contains the nodes (agents and topics) and their connections.
 */
public class Graph extends ArrayList<Node>{

	public Graph() {
		super();
		
	}
    
    /**
     * Checks if the computational graph has any cycles.
     *
     * @return  true if the graph has cycles, false otherwise
     */
    public boolean hasCycles() {
    	for(Node node : this) {
	    	if(node.hasCycles()) {
	    		return true;
	    	}
    	}
        return false;
    }

	/**
	 * Creates a graph from the topics in the TopicManagerSingleton.
	 * 
	 * This method iterates over each topic in the TopicManagerSingleton and creates a node for it in the graph.
	 * It then adds edges between the topic node and the nodes representing the subscribers and publishers of the topic.
	 * If a subscriber or publisher agent node already exists in the graph, it adds an edge between the topic node and the existing agent node.
	 * If a subscriber or publisher agent node does not exist in the graph, it creates a new agent node and adds an edge between the topic node and the new agent node.
	 * Finally, it adds the topic node to the graph.
	 * 
	 * @return void
	 */
    public void createFromTopics(){ 
		TopicManager tm = TopicManagerSingleton.get();
		//ArrayList<Agent> visitedAgents = new ArrayList<Agent>();
		tm.getTopics().forEach((topic) -> {
			Node n = new Node("T" + topic._name);
			//Add subscribers
			for (Agent a : topic.getSubscribers()) {
				if(searchGraph("A" + a.getName()) == -1) {
					Node tempAgent = new Node("A" + a.getName());
					this.add(tempAgent);
					//visitedAgents.add(a);
					n.addEdge(tempAgent);
				}else {
					n.addEdge(this.get(searchGraph("A" + a.getName())));
				}				
			}
			//Add publishers
			for (Agent a : topic.getPublishers()) {
				if(searchGraph("A" + a.getName()) == -1) {
					Node tempAgent = new Node("A" + a.getName());
					this.add(tempAgent);
					//visitedAgents.add(a);
					tempAgent.addEdge(n);
				}else {
					this.get(searchGraph("A" + a.getName())).addEdge(n);
				}				
			}
			this.add(n);	
		});	
    }    
	
	/**
	 * An assistant method that searches for a node in the graph with the given _name and returns its index if found,
 or -1 if not found.
	 *
	 * @param  name  the _name of the node to search for
	 * @return       the index of the node if found, or -1 if not found
	 */
    private int searchGraph(String name) {
		HashMap<String, Node> agentMap = new HashMap<>();

		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

    
}
