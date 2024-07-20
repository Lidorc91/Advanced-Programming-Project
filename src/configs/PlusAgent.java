package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

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

    @Override
    public void reset() {
        _x=0;
		_y=0;
    }

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
