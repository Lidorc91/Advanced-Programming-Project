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
    @Override
    public void run(){
    	 while (running) {
         	try {
					Socket client = _serverSocket.accept();
					System.out.println("New request received from " + client.getInetAddress().getHostAddress());
					System.out.println("_servletsGet " + _servletsGet);
					System.out.println("_servletsPost " + _servletsPost);
					System.out.println("_servletsDelete " + _servletsDelete);
					_executorService.execute(new MyHTTPRequest(client, _servletsGet,_servletsPost,_servletsDelete)); 
				} catch (IOException e) {
					
				}
         }
    }
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
		
		// Close the ServerSocket
		try {
			_serverSocket.close();
		} catch (IOException e) {
			
		}
    }
    
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
		@Override
		public void run() {
			RequestInfo parser;
			try {
				BufferedReader _clientReader = new BufferedReader(new InputStreamReader(_client.getInputStream()));
				OutputStream clientOutput = _client.getOutputStream();
				parser = RequestParser.parseRequest(_clientReader);
				Servlet servlet;

				switch (parser.getHttpCommand()) {
				case ("GET"): 
					System.err.println("in GET");
					servlet = findLongestMatch(parser.getUriSegments(), _servletsGet);
                    if(servlet != null){
                        servlet.handle(parser, clientOutput);
                    }
					break;
				case ("POST"): 
					System.err.println("in POST");
					servlet = findLongestMatch(parser.getUriSegments(), _servletsPost);
	                if(servlet != null){
	                    servlet.handle(parser,clientOutput);
	                }
					break;				
				case ("DELETE"): 
					System.err.println("in DELETE");
					servlet = findLongestMatch(parser.getUriSegments(), _servletsDelete);
	                if(servlet != null){
	                    servlet.handle(parser,clientOutput);
	                }
					break;    
				default: 
					break;
			}
				_clientReader.close();
				clientOutput.close();
				_client.close();	
			} catch (IOException e) {
				
			}
			
		}
		
		private Servlet findLongestMatch(String[] uri, ConcurrentHashMap<String, Servlet> servlets) {
			for(int i = 0; i < uri.length; i++){
				//Build the concatenated URI
				StringBuilder concatenatedUri = new StringBuilder();
				System.out.println("Checking URI: " + concatenatedUri.toString());
				if(uri[0].split("/").length == 0){ //Root URI
					concatenatedUri.append("/");
				}else{
					for(int j = 0; j < uri.length-i; j++){
						concatenatedUri.append("/").append(uri[j]);
					}
				}
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
