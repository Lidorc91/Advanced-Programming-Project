package configs;

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

public class GenericConfig implements Config {
	private ArrayList<ParallelAgent> activeAgents;
	private String _confFilePath;
	
	public GenericConfig() {
		activeAgents = new ArrayList<ParallelAgent>();				
		_confFilePath = "";
	}
	
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
		PlusAgent agent = new PlusAgent(null, null);
		String fullyQualifiedName = agent.getClass().getName();
		System.out.println(fullyQualifiedName); // Output: configs.PlusAgent
		for(int i = 0; i < data.size(); i+=3) { //read 3 lines per agent
			try {
				String className = data.get(i).replaceAll("project_biu.", "src.");
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
	}

	

	public String getName() {
		return "Generic Config";
	}

	public int getVersion() {
		return 0;
	}

	public void close() {
		for(ParallelAgent a : activeAgents) {
			a.close();
		}		
	}
	
	public void setConfFile(String filePath) {
		// String projectPath = "src/test/";

		// Checks config file in the project folder
		// if(!validPath(projectPath+filePath)) {
		// 	return;
		// }
		_confFilePath = filePath;
	}
	
	
	// Assisting Methods 
	
	private boolean validPath(String filePath) {
		File file = new File(filePath,"");
		 if (file.exists()) 
		 {
			 return true;
		 }
		return false;
	}
	
	public void addAgent(ParallelAgent a) {
		activeAgents.add(a);
	}
	
	public void removeAgent(ParallelAgent a) {
		activeAgents.remove(a);
	}



}
