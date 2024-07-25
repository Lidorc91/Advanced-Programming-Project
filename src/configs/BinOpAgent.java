package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.function.BinaryOperator;

/**
 * This class represents an agent that performs the binary operation.
 */
public class BinOpAgent implements Agent {
	private String _name;
	private Topic _inputTopic1;
	private Topic _inputTopic2;
	private Topic _outputTopic;
	private double _msg1;
	private double _msg2;
	private BinaryOperator<Double> op;
	
	public BinOpAgent(String name, String arg1, String arg2, String result, BinaryOperator<Double> op) {
		this._name = name;
		//Set topics
		TopicManager tm = TopicManagerSingleton.get();
		_inputTopic1 = tm.getTopic(arg1);
		_inputTopic2 = tm.getTopic(arg2);
		_outputTopic = tm.getTopic(result);
		this.op = op;
		
		//Subscribe Topics
		_inputTopic1.subscribe(this);
		_inputTopic2.subscribe(this);
		_outputTopic.addPublisher(this);
		_msg1 = 0;
		_msg2 = 0;
	}

	@Override
	public void reset() {
		this._inputTopic1 = null;
		this._inputTopic2 = null;		
	}

	
	/**
	 * This method is the callback function for handling incoming messages. It checks if the message is not NaN,
	 * and if the topic matches either of the input topics, it updates the corresponding message value. If both
	 * message values are not 0, it publishes the result of the binary operation to the output topic and resets
	 * the input message values.
	 *
	 * @param  topic  the topic of the incoming message
	 * @param  msg    the incoming message
	 */
	@Override
	public void callback(String topic, Message msg) {
		if(msg.asDouble == Double.NaN) {
			return;
		}
		if(_inputTopic1.name.equals(topic)) {
			_msg1 = msg.asDouble;
		}
		else if(_inputTopic2.name.equals(topic)) {
			_msg2 = msg.asDouble;
		}
		if(_msg1 != 0 && _msg2 != 0) {
			_outputTopic.publish(new Message(op.apply(_msg1, _msg2)));
			reset();
		}
	}

	@Override
	public void close() {
		
	}


	@Override
	public String getName() {
		return _name;
	}
	
}
