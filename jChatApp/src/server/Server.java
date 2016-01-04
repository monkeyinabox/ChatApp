package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author rifl
 * This Class is starting threads for each connected client
 * 		- ClientHandler is dealing with client connections and will send, redirect and receive incoming messages 
 */

public class Server{

	public static HashMap<String, Conversation> conversations = new HashMap<String, Conversation>();
	public static ArrayList<User> users = new ArrayList<User>();
	
	// Network Port 	
	public static final int PORT=1337;
	// Logger initialisation
	final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static void jChatAppServer() throws IOException{

		LOG.setLevel(Level.INFO);	
		ServerSocket s = new ServerSocket(PORT);
		
		/** Creating default conversation channel*/
		conversations.put("default", new Conversation("defaut"));
		
		LOG.info("Info: Server Socket is up an running, start serving clients");
		System.out.println("JChatApp Server v0.1");
		System.out.println("--------------------");
		ipAddress();
		
		try{
			while (true){
			Socket socket = s.accept();
			LOG.info("Server: Info:  Client connected with Address: " + s.getInetAddress() + ", starting new communication thread");
			Thread t = new Thread(new ClientHandler(socket));
			t.start();
			}
		}
		catch (IOException e){
			LOG.warning("Server: Error: Got an IO Exception while starting ClientThread.. closing socket..(or not)");
			s.close();
		}
	
		finally {
			s.close();
			LOG.info("Server is Stopping...");
			System.out.println("Server is Shuting down, thanks for the fish and so long suckers");
		}
	
	}
	/**
	 * ipAddress is providing console Output on server startup with Address Informations
	 * Note ->> Multiple Interfaces are not handled by this script!
	 */
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
        	LOG.severe("Error: Could not determine servers IP address, shuting down... "+ e);
        }
    }
}
