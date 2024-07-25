package graph;

/**
 * This interface defines the methods that an agent must implement.
 */
public interface Agent {

    /**
     * Returns the name of the agent.
     *
     * @return  the name of the agent as a string
     */
    String getName();

    /**
	 * Resets the state of the Agent to its initial values.
	 *
	 * This method is called to reset the agent to its initial state. since it is a stateless agent, it does not need to be reset,
	 * but could be augemented to do so if needed. (for other implementations)
	 */
    void reset();

    /**
	 * Callback function that is called when a message is received on a specific topic.
	 * this callback is will be run in a separate thread, using a parallel agent.
	 * 
	 * @param  topic   the topic on which the message was received
	 * @param  msg     the received message
	 */
    void callback(String topic, Message msg);

    /**
     * Closes the agent, releasing any resources it holds.
     *
     * This method is called when the agent is no longer needed and should clean up any resources it has acquired.
     * Since this agent does not hold any resources, this method does nothing. (but could be augmented to do so if needed)
     */
    void close();
}
