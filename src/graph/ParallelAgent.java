package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This class represents an agent that runs in parallel with other agents using the decorator design pattern.
 */
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

	/**
	 * Returns the name of the agent.
	 *
	 * @return the name of the agent
	 */
	public String getName() {
		return _agent.getName();
	}

	/**
	 * Resets the parallel agent by calling the reset method of the internal agent.
	 */
	public void reset() {
		_agent.reset();
	}

	/**
	 * Adds a new message to the queue for processing by the internal agent. the Message includes the topic and the message.
	 *
	 * @param  topic  the topic of the message
	 * @param  msg    the message to be processed
	 * @throws InterruptedException if the thread is interrupted while waiting to add the message to the queue
	 */
	public void callback(String topic, Message msg) {
		try {	
			_queue.put(new MessageforQueue(topic, msg));
		} catch (InterruptedException e) {
		}		
	}

	/**
	 * Closes the parallel agent by interrupting its running thread and closing the internal agent.
	 */
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
		/**
		 * Returns the topic from the MessageQueue object.
		 *
		 * @return the topic of the message
		 */
		private String getTopic() {
			return _topic;
		}
		
		/**
		 * Returns the message stored in the MessageQueue object.
		 *
		 * @return the message stored in the object
		 */
		private Message getMessage() {
			return _msg;
		}
	}
}
