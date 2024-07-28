package configs;

import static servlets.TopicDisplayer._topicsTable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import configs.*;
import graph.Agent;
import graph.ParallelAgent;


/**
 * This class represents a configuration of the computational graph. 
 * it allows to create an instance of the computational graph from a configuration file.
 */
public class GenericConfig implements Config {
	private ArrayList<ParallelAgent> activeAgents;
	private String _confFilePath;
	
	public GenericConfig() {
		activeAgents = new ArrayList<ParallelAgent>();
		_confFilePath = "";
	}
	
	/**
	 * Creates a computational graph based on the configuration file.
	 * Reads each line of the file and creates agents based on the data.
	 * Each agent is created using the constructor of its corresponding class. (using wildcards)
	 * The agent's arguments are decoded from data.
	 * The agents are added to the activeAgents ArrayList.
	 * 
	 * @return  void
	 * @throws ClassNotFoundException  if the class of the agent is not found
	 * @throws NoSuchMethodException  if the constructor of the agent class is not found
	 * @throws InstantiationException  if an instance of the agent class cannot be created
	 * @throws IllegalAccessException  if access to the agent class is illegal
	 * @throws InvocationTargetException  if the agent constructor cannot be invoked
	 * @throws ClassCastException  if the agent cannot be cast to the expected type
	 * @throws IOException  if an error occurs while reading the file
	 */
	public void create() {
		ArrayList<String> data = new ArrayList<>();	
		Path filePath = Paths.get(_confFilePath);
		try {
            Files.lines(filePath).forEach(data::add); // Read each line and add it to the ArrayList

        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
		
		if(data.size() %3 != 0) {
			return;
		}

		for(int i = 0; i < data.size(); i+=3) { //read 3 lines per agent
			try {
				String className = data.get(i).replaceAll("project_biu.", "");
				className = className.trim(); // Remove whitespace characters
				Class<?> agentClass = Class.forName(className);
				Class<?>[] parameterTypes = new Class<?>[2]; 
				parameterTypes[0] = String[].class;
				parameterTypes[1] = String[].class;
				Constructor<?> constructor = agentClass.getConstructor(parameterTypes);
				//Decode Data
				String[] args1 = data.get(i+1).split(",");
				String[] args2 = data.get(i+2).split(",");
				//Create Agents
				Agent a = (Agent) constructor.newInstance(new Object[] { args1, args2 });
				ParallelAgent pa = new ParallelAgent(a, 10);
				activeAgents.add(pa);

			} catch (ClassNotFoundException e) {
					System.err.println("Class not found: " + e.getMessage()); // Detailed error message
			} catch (NoSuchMethodException e) {
				System.err.println("No such method: " + e.getMessage()); // Detailed error message
			} catch (InstantiationException e) {
				System.err.println("Instantiation exception: " + e.getMessage()); // Detailed error message
			} catch (IllegalAccessException e) {
				System.err.println("Illegal access: " + e.getMessage()); // Detailed error message
			} catch (InvocationTargetException e) {
				System.err.println("Invocation target exception: " + e.getMessage()); // Detailed error message
			} catch (ClassCastException e) {
				System.err.println("Class cast exception: " + e.getMessage()); // Detailed error message
			} catch (Exception e) {
				e.printStackTrace(); // Generic exception handler
			}							
		}

		//Reset Data for new Config

		//Clear Topics Table to remove old topics and values
		_topicsTable.clear();

		// Create NotifierAgent for notification
		NotifierAgent _na = new NotifierAgent();
		ParallelAgent _parallelnotifierAgent = new ParallelAgent(_na, 10);
		activeAgents.add(_parallelnotifierAgent);

		// Reset all active agents (on new graph creation)
		for (ParallelAgent a : activeAgents) {
			a.reset();			
		}		
	}

    /**
     * Returns the name of the current configuration.
     *
     * @return the name of the configuration
     */
	public String getName() {
		return "Generic Config";
	}

	/**
	 * Returns the version of the current configuration.
	 *
	 * @return an integer representing the version of the configuration
	 */
	public int getVersion() {
		return 0;
	}

	/**
	 * Closes all the active agents.
	 *
	 * This method iterates over each agent in the activeAgents list and calls the close() method on each parallel agent.
	 * This ensures that all the resources used by the agents are properly released.
	 */
	public void close() {
		for(ParallelAgent a : activeAgents) {
			a.close();
		}		
	}
	
	/**
	 * Sets the file path for the configuration file.
	 *
	 * @param  filePath  the path to the configuration file
	 */
	public void setConfFile(String filePath) {
		_confFilePath = filePath;
	}
	
	// Assisting Methods 
		
	/**
	 * Adds a ParallelAgent to the list of active agents.
	 *
	 * @param  a  the ParallelAgent to be added
	 */
	public void addAgent(ParallelAgent a) {
		activeAgents.add(a);
	}
	
	/**
	 * Removes a ParallelAgent from the list of active agents.
	 *
	 * @param  a  the ParallelAgent to be removed
	 */
	public void removeAgent(ParallelAgent a) {
		activeAgents.remove(a);
	}



}
