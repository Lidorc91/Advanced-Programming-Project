package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
	private final BlockingQueue<MessageforQueue> _queue;
    private final Agent _agent;
    private final Thread _runningThread;
    
	public ParallelAgent(Agent agent, int maxQueueSize) {
		this._agent = agent;
		this._queue = new ArrayBlockingQueue<>(maxQueueSize);
		this._runningThread = new Thread(() -> {			
				try {
					while (true) {
						MessageforQueue msg = _queue.take();
						_agent.callback(msg.getTopic(), msg.getMessage());
					}
				} catch (InterruptedException e) {
				}			
		});
		_runningThread.start();
	}

	public String getName() {
		return _agent.getName();
	}

	public void reset() {
		_agent.reset();
	}

	public void callback(String topic, Message msg) {
		try {	
			_queue.put(new MessageforQueue(topic, msg));
		} catch (InterruptedException e) {
		}		
	}

	public void close() {
		_runningThread.interrupt();
		_agent.close();
	}

	private static class MessageforQueue { // Message for the queue. Made it static so it can be used in the runnable
		private String _topic;
		private Message _msg;
		public MessageforQueue(String topic, Message msg) {
			this._topic = topic;
			this._msg = msg;
		}
		private String getTopic() {
			return _topic;
		}
		private Message getMessage() {
			return _msg;
		}
	}
}
