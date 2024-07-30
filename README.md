# Computational Graph Implemantion with a Subscriber/Publisher model

This project showcases the implementation of an HTTP server which hosts a computational graph using Java. 

## Background

In this project, we've implemented a multithreaded HTTP Server imitation. We've implemented design patterns and architecture, particulary the MVC architecture, while allowing for different client sides.

We designed a system using the subscriber/publisher model. 
This model allows us to create a computational graph for complex calculations, with nodes working together. 

#### Computational Graph Components:

- **Message**: The data or information that is transferred through the computational graph.
- **Topic**: A subject or channel through which messages are published and subscribed to. It acts as a conduit for nodes to receive and send messages.
- **Agent**: An entity that subscribes to Topics to receive messages, processes the received data, and then publishes the results. Each Agent can handle multiple Topics, both subscribing to and publishing messages across different subjects.

The subscriber/publisher model uses different nodes, identified as subscribers and publishers, to enable data transfer between them. In our case, we used "Topics" to send data and "Agents" to process it and possibly move it to other nodes. Each node can be a subscriber, a publisher, or both. A subscribing node receives data from the nodes it subscribes to whenever those nodes publish a message.

### Project Code Architecture

This project utilized the MVC architecture: 

1. **Model Layer**: This layer handles the creation and execution of the computational graph. It manages the data and the logic for computations, ensuring that the nodes and connections within the graph function correctly.
2. **Controller Layer**: This layer contains the server code and provides a RESTful API. It acts as an intermediary, processing client requests, interacting with the Model layer, and returning appropriate responses. It enables external clients to interact with the computational graph.
3. **View Layer**: Web application that provides the user interface. It allows users to interact with the computational graphs remotely via a browser, enabling them to load, visualize, and operate the graphs in an intuitive and user-friendly manner.

## Project Directory Structure

The repository is organized into several directories:

- **configs**: Contains configurations for initializing different agents based on external data. These configurations define how agents are set up and operated within the system. also, contains the compoenents to create the full graph.
- **graph**: Contains the main components of the graph itself and enables the communication between them.
- **server**: Contains the HTTP server implementation with request handling logic.
- **servlets**: Contains different servlet implementations to address HTTP requests.
- **views**: Contains an "adapter" library which takes the graph's data from the model and converts it into HTML content that can be shown on the browser.

## Program Flow

**Server Operation:**

1. Starting the program by running `Main.java` which initialized the HTTP server (`MyHTTPServer.java`) by opening a server socket ,a thread pool to handle new requests and creating a different running thread for the server itself.
2. In the `Main.java`, different servelets are added to address specific URIs and provide functionality. these servelets are added to `MyHTTPServer.java` based on the HTTP request type.
3. Continuing in `Main.java` and starting the server will start the server running thread.
4. Opening a browser and using the port for the server and addresses spcified in the servelets, will accept a client with the threadpool will take.
5. For Each client accepted, a different thread is created and a new `MyHTTPRequest.java` instance is generated with a run function the threadpool can execute.
6. The thread parses the request with `RequestParser.java` and finds the relevant servelet to handle it, and afterwards closes the connection to the client.

**Browser Operation:**

1. Entering the `localhost:<port_number>/app` page will call the `HtmlLoader.java` servelet which will load the html files in `html_files` directory.
2. When uploading a config file and deploying it, the `ConfLoader.java` is called and it creates the graph based on the elements in the config file that are matched with the elements in the `config` directory.
3. `ConfLoader.java` takes the graph it created with and converts it to HTML using `HtmlGraphWriter.java`, and then finally sends it back to the client with graph in a compatible HTML format.   
4. When a topic and message are entered and "Send Message" is clicked, `TopicDisplayer.java` is called, which takes the current message data for each topic creates an HTTP packet with with HTML that displays a table with the topics and messages and sends it to the client.

## Getting Started

To get started with the Computational Graph project:

1. **Clone the Repository**: Clone this repository to your local machine using:
   ```sh
   git clone https://github.com/Lidorc91/Advanced-Programming-Project.git
   cd src/project
   ```

2. **Build and Run**: Ensure you have Java Development Kit (JDK) installed. Compile and run the project using your IDE of choice or using command line tools.

3. **Explore**: Review the code in each directory to understand how it all works. (refer to program flow above for general understanding)

4. **Test**: Use an HTTP client such as Postmanfor testing different servlet endpoints using different HTTP requests to see intended functionality

## Usage Example : Running the HTTP Server
In the `Main.java` file, you can add different servelets to handle different requests for different URIs. you could also change the port number and the number of thread in the thread pool. code example: (fill in data inside <>)
```java
MyHTTPServer server = new MyHTTPServer(<Port number>>, < Number of Threads>); // Port 8080, 10 threads
server.addServlet("<HTTP Request Type>", "/<new URI> ", new <Servlet Name>());
server.start();
```

## Dependencies

**Java Development Kit (JDK)**: Ensure JDK 8 or higher is installed to compile and run the project.
