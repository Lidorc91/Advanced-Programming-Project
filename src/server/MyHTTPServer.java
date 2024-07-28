package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import server.RequestParser.RequestInfo;
import servlets.Servlet;

/**
 * This class implements the HTTPServer using the Command pattern.
 * It is a multi-threaded server that handles HTTP requests in a thread pool which executes servlets.
 */
public class MyHTTPServer extends Thread implements HTTPServer{
    private boolean running = true;
    private final Thread runningThread;
    private final ConcurrentHashMap<String, Servlet> _servletsGet;
	private final ConcurrentHashMap<String, Servlet> _servletsPost;
	private final ConcurrentHashMap<String, Servlet> _servletsDelete;
	private final ExecutorService _executorService;
	private final ServerSocket _serverSocket;
	
    public MyHTTPServer(int port,int nThreads) throws IOException{
    	_serverSocket = new ServerSocket(port);
    	_serverSocket.setSoTimeout(1000);
    
	    _servletsGet = new ConcurrentHashMap<>();
		_servletsPost = new ConcurrentHashMap<>();
		_servletsDelete = new ConcurrentHashMap<>();
		_executorService = Executors.newFixedThreadPool(nThreads);
		
		runningThread = new Thread(() -> {
			this.run();
		});               
    }

	/**
	 * Adds a servlet to the appropriate map based on the HTTP command and URI.
	 *
	 * @param  httpCommanmd   the HTTP command (GET, POST, or DELETE)
	 * @param  uri            the URI of the servlet
	 * @param  s              the servlet to be added
	 */
    @Override
    public void addServlet(String httpCommanmd, String uri, Servlet s){
		if(httpCommanmd.equals("GET")){
			_servletsGet.put(uri, s);
			return;
		}
		if(httpCommanmd.equals("POST")){
			_servletsPost.put(uri, s);
			return;
		}
		if(httpCommanmd.equals("DELETE")){
			_servletsDelete.put(uri, s);
		}
    }
	/**
	 * Removes a servlet from the corresponding map based on the HTTP command and URI.
	 *
	 * @param  httpCommanmd  the HTTP command (GET, POST, or DELETE)
	 * @param  uri           the URI of the servlet
	 */
    @Override
    public void removeServlet(String httpCommanmd, String uri){
		if(httpCommanmd.equals("GET")){
			_servletsGet.remove(uri);
		}
		if(httpCommanmd.equals("POST")){
			_servletsPost.remove(uri);
		}
		if(httpCommanmd.equals("DELETE")){
			_servletsDelete.remove(uri);
		}
    }
    /**
     * The method runs the HTTP server in its separate thread. Continuously accepts incoming client connections
     * and send each new MyHTTPRequest instance for each connection to the threadpool for execution. Prints the IP address of each
     * new client and the contents of the servlet maps for debugging purposes.
     *
     * @throws IOException if there is an error accepting a new client connection
     */
    @Override
    public void run(){
    	 while (running) {
         	try {
					Socket client = _serverSocket.accept();
					System.out.println("New request received from " + client.getInetAddress().getHostAddress());
					_executorService.execute(new MyHTTPRequest(client, _servletsGet,_servletsPost,_servletsDelete)); 
				} catch (IOException e) {
					
				}
         }
    }
		/**
		 * Closes the running thread and shuts down all servlets. 
		 * Waits for the executor service (Thread Pool which handles new requests) to terminate for up to 800 milliseconds.
		 * If the executor service does not terminate, it shuts down immediately.
		 * Closes the ServerSocket.
		 *
		 * @throws InterruptedException if the current thread is interrupted while waiting for the executor service to terminate
		 */
    @Override
    public void close(){
		//close running thread
        running = false;

		//close all servlets
        _executorService.shutdown();
        try {
            if (!_executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                _executorService.shutdownNow();
            } 
        } catch (InterruptedException e) {
            _executorService.shutdownNow();
        }  

		//close agents
		for(Servlet s : _servletsGet.values()){
            try {
                s.close();
            } catch (IOException ex) {
            }
		}
		for(Servlet s : _servletsPost.values()){
            try {
                s.close();
            } catch (IOException ex) {
            }
		}
		for(Servlet s : _servletsDelete.values()){
            try {
                s.close();
            } catch (IOException ex) {
            }
		}
		
		// Close the ServerSocket
		try {
			_serverSocket.close();
		} catch (IOException e) {
			
		}
    }
    
	/**
	 * Starts the execution of the HTTP server in a separate thread.
	 *
	 * @return 	void
	 */
	@Override
	public void start() {
		runningThread.start();
	}
	
	//Used Command pattern to encapsulate Requests and Create a separate Instance for each one
	private static class MyHTTPRequest implements Runnable {
		private final Socket _client;
		private final ConcurrentHashMap<String, Servlet> _servletsGet;
		private final ConcurrentHashMap<String, Servlet> _servletsPost;
		private final ConcurrentHashMap<String, Servlet> _servletsDelete;
		public MyHTTPRequest(Socket client, ConcurrentHashMap<String, Servlet> servletsGet, ConcurrentHashMap<String, Servlet> servletsPost, ConcurrentHashMap<String, Servlet> servletsDelete) {
			this._client = client;
			this._servletsGet = servletsGet;
			this._servletsPost = servletsPost;
			this._servletsDelete = servletsDelete;
		}
    /**
     * Runs the HTTP server thread to handle an incoming client request in the ThreadPool.
     * Parses the request using the RequestParser class and determines the appropriate servlet to handle the request.
     * Prints debug messages for each HTTP command type.
     * Closes the client reader, output stream, and socket after handling the request.
     *
     * @throws IOException if an I/O error occurs while reading from or writing to the client.
     */
		@Override
		public void run() {
			RequestInfo parser;
			BufferedReader _clientReader = null;
			OutputStream _clientOutput = null;
			try {
				_clientReader = new BufferedReader(new InputStreamReader(_client.getInputStream()));
				_clientOutput = _client.getOutputStream();
				parser = RequestParser.parseRequest(_clientReader);
				Servlet servlet;

				switch (parser.getHttpCommand()) {
				case ("GET"): 
					servlet = findLongestMatch(parser.getUriSegments(), _servletsGet);
                    if(servlet != null){
                        servlet.handle(parser, _clientOutput);
                    }
					break;
				case ("POST"): 
					servlet = findLongestMatch(parser.getUriSegments(), _servletsPost);
	                if(servlet != null){
	                    servlet.handle(parser,_clientOutput);
	                }
					break;				
				case ("DELETE"): 
					servlet = findLongestMatch(parser.getUriSegments(), _servletsDelete);
	                if(servlet != null){
	                    servlet.handle(parser,_clientOutput);
	                }
					break;    
				default: 
					break;
			}	
			} catch (IOException e) {
				
			} finally{
				try {
					if (_clientReader != null) {
						_clientReader.close();
					}
					if (_clientOutput != null) {
						_clientOutput.close();
					}
					if (_client != null) {
						_client.close();
					}
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			
		}
		
	/**
	 * An assistant method that finds the longest match of a given URI in a map of servlets so it will know which servlet to call.
	 *
	 * @param  uri        the URI to search for a match
	 * @param  servlets  the map of servlets to search in
	 * @return            the servlet that matches the longest part of the URI,
	 *                    or null if no match is found
	 */
		private Servlet findLongestMatch(String[] uri, ConcurrentHashMap<String, Servlet> servlets) {
			for(int i = 0; i < uri.length; i++){
				//Build the concatenated URI
				StringBuilder concatenatedUri = new StringBuilder();
				//concatenatedUri.append("/");
				if(uri[uri.length-1].contains(".") && i ==0){//Check if it is a file
					continue;					
				}
				for(int j = 0; j < uri.length-i; j++){
					concatenatedUri.append("/").append(uri[j]);
				}
				if(i>0 || (i==0 && uri.length == 1 && uri[0].equals("app"))){
					concatenatedUri.append("/");
				}
				/* if(uri[0].split("/").length == 0){ //Root URI
					concatenatedUri.append("/");
				}else{
					for(int j = 0; j < uri.length-i; j++){
						concatenatedUri.append("/").append(uri[j]);
					}
				} */


				/* if(i != 0 && !uri[0].equals("/") ){ 
					concatenatedUri.append("/");
				} */
				System.out.println("Concatenated URI: " + concatenatedUri.toString());
				//Check if it matches any servlet
				
				if(servlets.containsKey(concatenatedUri.toString())){
					System.out.println("Match found! Returning servlet: " + servlets.get(concatenatedUri.toString()).getClass().getSimpleName());
					return servlets.get(concatenatedUri.toString());
				}
			}	
			System.out.println("No match found. Returning null.");		
			return null;
		}
	}

}
