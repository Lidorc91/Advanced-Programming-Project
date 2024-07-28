package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import static servlets.TopicDisplayer._topicsTable;
/**
 * This class represents an agent that subscribes to all the topics and sends them to out to a table.
 * This allows the user to see the latest values of the topics when a message is published thorugh a topic.
 */
public class NotifierAgent implements Agent{
	TopicManager _tm;

    public NotifierAgent() {
        _tm = TopicManagerSingleton.get();
		for (Topic t : _tm.getTopics()) {
			t.subscribe(this);
		}
    }

	@Override
	public String getName() {		
		return "NotifierAgent";
	}

	@Override
	public void reset() {
		
	}

	/**
	 * Callback method that is called when a message is received on the subscribed topic.
	 * the topic and message will be forwarded to a table that keeps track of them in order to display them.
	 *
	 * @param  topic  the topic on which the message was received
	 * @param  msg    the message received (assuming it is a double)
	 */
	@Override
	public void callback(String topic, Message msg) {      
		_topicsTable.put(topic, msg.asText);
	}

	@Override
	public void close() {
		
	}
}
