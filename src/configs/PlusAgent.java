package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * This class represents an agent that performs the addition operation.
 * For methods without a javadoc please see {@link graph.Agent}
 */
public class PlusAgent implements Agent {
	TopicManager _tm;
	private Topic _inputTopic1;
	private Topic _inputTopic2;
	private Topic _outputTopic;
	private double _x;
	private double _y;

    public PlusAgent(String[] subs, String[] pubs) {
        _tm = TopicManagerSingleton.get();
    	_tm.getTopic(subs[0]).subscribe(this);
    	_tm.getTopic(subs[1]).subscribe(this);
    	_inputTopic1 = _tm.getTopic(subs[0]);
		_inputTopic2 = _tm.getTopic(subs[1]);
		_outputTopic = _tm.getTopic(pubs[0]);
    	
		_tm.getTopic(pubs[0]).addPublisher(this);
		
		_x = 0;
		_y = 0;
    }

    @Override
    public String getName() {
    	return "PlusAgent";
    }

    /**
     * Resets the state of the PlusAgent by resetting its initial values. (sets the values of _x and _y to 0)
     *
     * @return         	None
     */
    @Override
    public void reset() {
        _x=0;
		_y=0;
    }

	/**
	 * This method is called when a message is received by the PlusAgent. It checks if the message is valid
	 * (not NaN), and if so, it updates the internal state of the PlusAgent based on the topic of the message.
	 * If both inputs have been received, it calculates the sum of the inputs and publishes the result to the
	 * output topic. Finally, it resets the internal state of the PlusAgent.
	 *
	 * @param  topic  the topic of the received message
	 * @param  msg    the received message (number)
	 */
    @Override
    public void callback(String topic, Message msg) {
    	if(msg.asDouble == Double.NaN) {
			return;
		}
		if(_inputTopic1.name.equals(topic)) {
			_x = msg.asDouble;
		}
		else if(_inputTopic2.name.equals(topic)) {
			_y = msg.asDouble;
		}
		if((_x != Double.NaN && _y != Double.NaN) && (_x != 0 && _y != 0)) {
			double result = _x + _y;
			_outputTopic.publish(new Message(result));
			reset();
		}
    }

    @Override
    public void close() {        
    }

    
}
