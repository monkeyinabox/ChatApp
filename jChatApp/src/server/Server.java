package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Server{
	
	//Collection of all connected clientsOutputStreams
	public static ArrayList clientOutputStreams = new ArrayList();
	ArrayList conversations;
	ArrayList<String> users;
	//MessageQueue that caches incoming messages
	public static Queue<Message> messageQueue = new LinkedList<Message>();
	
	
	// Network Port 	
	static final int PORT=1337;
	//Logger
	final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static void jChatAppServer() throws IOException{
		// ArrayList cip = new ArrayList();
		LOG.setLevel(Level.INFO);	
		ServerSocket s = new ServerSocket(PORT);
		LOG.info("Server Socket is up an running, start serving clients");
	
		System.out.println("JChatApp Server v0.1");
		System.out.println("--------------------");
		ipAddress();
	
		Thread mt = new Thread(new MessageHandler());
		mt.start();
		
		try{
			while (true){
			Socket socket = s.accept();
			LOG.info("Client connected with Address: " + s.getInetAddress() + ", starting new communication thread");
			Thread t = new Thread(new ClientHandler(socket));
			t.start();
			}
		}
		catch (IOException e){
			LOG.warning("Got an IO Exception while starting ClientThread.. closing client socket..(or not)");
			// socket.close();
		}
	finally {
		s.close();
		LOG.info("Server is Stopping...");
	}
	
	}
	
	public static void ipAddress() {
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();       
    		System.out.println("IP Addr.: "+ ip);
    		System.out.println("Your current Hostname : " + hostname);
    		System.out.println("Port Nr.: "+ PORT);
 
        } catch (UnknownHostException e) { 
            e.printStackTrace();
        }
    }
}
