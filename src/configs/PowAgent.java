package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class PowAgent implements Agent{
	TopicManager _tm;
	private Topic _inputTopic;
	private Topic _outputTopic;

    public PowAgent(String[] subs, String[] pubs) {
        _tm = TopicManagerSingleton.get();
    	_tm.getTopic(subs[0]).subscribe(this);
    	_inputTopic = _tm.getTopic(subs[0]);
    	_tm.getTopic(pubs[0]).addPublisher(this);
		_outputTopic = _tm.getTopic(pubs[0]);
    }

	@Override
	public String getName() {		
		return "IncAgent";
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void callback(String topic, Message msg) {
        if(_inputTopic.name.equals(topic)) {
            if(msg.asDouble == Double.NaN) {
                return;
            }
            double result = msg.asDouble * msg.asDouble;
            _outputTopic.publish(new Message(result));		
		}
	}

	@Override
	public void close() {
		
	}
}
