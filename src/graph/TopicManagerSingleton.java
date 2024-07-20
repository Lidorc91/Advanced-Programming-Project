package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {
	

    public static class TopicManager{
    	ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<String, Topic>();
    	private static final TopicManager instance = new TopicManager();
    	private TopicManager() {
	    	
    	}
    	
		public Topic getTopic(String topicName) { 
			
			if(!topics.containsKey(topicName)) {
				topics.put(topicName, new Topic(topicName));
			}
			return topics.get(topicName);
		}
		public Collection<Topic> getTopics() { 			
			return topics.values();
		}
						
    
		public void clear() {
			topics.clear();
		}

    }
    public static TopicManager get() {
		return TopicManager.instance;	
	}
}
