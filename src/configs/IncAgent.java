package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * This class represents an agent that performs the increment operation.
 * For methods without a javadoc please see {@link graph.Agent}
 */
public class IncAgent implements Agent{
	private static int agentCounter = 0;
	private String name;
	TopicManager _tm;
	private Topic _inputTopic;
	private Topic _outputTopic;

    public IncAgent(String[] subs, String[] pubs) {
		agentCounter++;
		this.name = "IncAgent"+agentCounter;
        _tm = TopicManagerSingleton.get();
    	_tm.getTopic(subs[0]).subscribe(this);
    	_inputTopic = _tm.getTopic(subs[0]);
    	_tm.getTopic(pubs[0]).addPublisher(this);
		_outputTopic = _tm.getTopic(pubs[0]);
    }

	@Override
	public String getName() {		
    	return this.name;
	}

	@Override
	public void reset() {
		
	}

	/**
	 * Callback method that is called when a message is received on the subscribed topic and return the increment of the message (assuming it is a double)
	 *
	 * @param  topic  the topic on which the message was received
	 * @param  msg    the message received (assuming it is a double)
	 */
	@Override
	public void callback(String topic, Message msg) {
        if(_inputTopic._name.equals(topic)) {
            if(msg.asDouble == Double.NaN) {
                return;
            }
            double result = msg.asDouble + 1;
            _outputTopic.publish(new Message(result));		
		}
	}

	@Override
	public void close() {
		
	}
}
