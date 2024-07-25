package graph;
//package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a topic in the graph, and holds its subscribers and publishers.
 */
public class Topic {
    public final String name;
    List<Agent> subscribers = new ArrayList<>();
    List<Agent> publishers = new ArrayList<>();
    
    Topic(String name){
        this.name=name;
    }

    /**
     * Subscribes an Agent to this Topic if it is not already subscribed.
     *
     * @param  a  the Agent to subscribe
     */
    public void subscribe(Agent a){
    	//look for a in subscribers and add if not present
    	if(subscribers.contains(a))
    		return;
		subscribers.add(a);
    }
    /**
     * Removes the specified Agent from the list of subscribers.
     *
     * @param  a  the Agent to unsubscribe
     */
    public void unsubscribe(Agent a){
    	if(subscribers.contains(a)) {    		
    		subscribers.remove(a);
    	}
    }

		/**
		 * Publishes a message to all subscribed agents.
		 *
		 * @param  m  the message to publish
		 */
    public void publish(Message m){
		for(Agent a:subscribers) {
			a.callback(name, m);
		}
    }

    /**
     * Adds an Agent as a publisher to this Topic if it is not already a publisher.
     *
     * @param  a  the Agent to add as a publisher
     */
    public void addPublisher(Agent a){
    	if(publishers.contains(a))
    		return;
		publishers.add(a);
    }

    /**
     * Removes the specified Agent from the list of publishers for this Topic.
     *
     * @param  a  the Agent to remove as a publisher
     */
    public void removePublisher(Agent a){
    	if(publishers.contains(a)) {    		
    		publishers.remove(a);
    	}
    }

		/**
		 * Returns a list of all the agents subscribed to this topic.
		 *
		 * @return a list of Agent objects representing the subscribers
		 */
    public List<Agent> getSubscribers() {
    	return subscribers;
    }

    /**
     * Returns a list of all the agents that are publishers for this topic.
     *
     * @return a list of Agent objects representing the publishers
     */
    public List<Agent> getPublishers() {
    	return publishers;
    }
}
