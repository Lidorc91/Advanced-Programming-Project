package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class manages the topics in the graph. 
 * It provides methods to retrieve a topic by its name, get all the topics currently being managed, and clear the topics.
 */
public class TopicManagerSingleton {
	

    public static class TopicManager{
    	ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<String, Topic>();
    	private static final TopicManager instance = new TopicManager();
    	private TopicManager() {
	    	
    	}
    	
		/**
		 * Retrieves a topic by its name. If the topic does not exist, it is created and added to the topics map.
		 *
		 * @param  topicName  the name of the topic to retrieve
		 * @return            the topic with the given name, or a newly created topic if it did not exist
		 */
		public Topic getTopic(String topicName) { 
			
			if(!topics.containsKey(topicName)) {
				topics.put(topicName, new Topic(topicName));
			}
			return topics.get(topicName);
		}
		/**
		 * Returns a collection of all the topics currently being managed.
		 *
		 * @return a collection of Topic objects representing all the topics being managed
		 */
		public Collection<Topic> getTopics() { 			
			return topics.values();
		}
						
    
		/**
		 * Clears all the topics from the topics map.
		 *
		 * @return void
		 */
		public void clear() {
			topics.clear();
		}

    }
	/**
	 * Returns the singleton instance of the TopicManager class.
	 *
	 * @return the singleton instance of the TopicManager class
	 */
    public static TopicManager get() {
		return TopicManager.instance;	
	}
}
