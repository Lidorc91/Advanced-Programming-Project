package configs;

import graph.Agent;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.ArrayList;


public class Graph extends ArrayList<Node>{
	
	public Graph() {
		super();
		
	}
    
    public boolean hasCycles() {
    	for(Node node : this) {
	    	if(node.hasCycles()) {
	    		return true;
	    	}
    	}
        return false;
    }
    public void createFromTopics(){ 
		TopicManager tm = TopicManagerSingleton.get();
		//ArrayList<Agent> visitedAgents = new ArrayList<Agent>();
		tm.getTopics().forEach((topic) -> {
			Node n = new Node("T" + topic.name);
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
    private int searchGraph(String name) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

    
}
