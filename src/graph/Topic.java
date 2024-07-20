package graph;
//package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    List<Agent> subscribers = new ArrayList<>();
    List<Agent> publishers = new ArrayList<>();
    
    Topic(String name){
        this.name=name;
    }

    public void subscribe(Agent a){
    	//look for a in subscribers and add if not present
    	if(subscribers.contains(a))
    		return;
		subscribers.add(a);
    }
    public void unsubscribe(Agent a){
    	if(subscribers.contains(a)) {    		
    		subscribers.remove(a);
    	}
    }

    public void publish(Message m){
		for(Agent a:subscribers) {
			a.callback(name, m);
		}
    }

    public void addPublisher(Agent a){
    	if(publishers.contains(a))
    		return;
		publishers.add(a);
    }

    public void removePublisher(Agent a){
    	if(publishers.contains(a)) {    		
    		publishers.remove(a);
    	}
    }

    public List<Agent> getSubscribers() {
    	return subscribers;
    }

    public List<Agent> getPublishers() {
    	return publishers;
    }


}
