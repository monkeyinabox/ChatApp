package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * @author rifl
 * This Class is starting threads for each connected client and hold all global accessable variables 
 * 		- Starts ClientHandler thread dealing with client connections
 */

public class Server{

	public static HashMap<String, Conversation> conversations = new HashMap<String, Conversation>();
	// public static ArrayList<User> users = new ArrayList<User>();
	// Network Port 	
	public static final int PORT=1337;
	// Logger initialisation
	final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void jChatAppServer() throws IOException{

		/**
		 * Here we are defining a file where the Logs are written and setting a proper format
		 * 
		 * -->TBD: Maybe we should setup a record method as it is called twice
		 */
	    try {  
	    	// This block configure the logger with handler and formatter 
	    	// Log Format -> Date - Loglevel: [ClassName.MethodName]: Message
	    	FileHandler fh = new FileHandler("jChatAppServer.log");
	    	LOG.setUseParentHandlers(false);
	    	Handler conHdlr = new ConsoleHandler();
	    	// File HanderFormat
	        fh.setFormatter(new SimpleFormatter() {
	            public String format(LogRecord record) {
	                return  new Date(record.getMillis()) + (" - ")
		                	+ record.getLevel() + ": ["
		                    + record.getSourceClassName() + "."
		                    + record.getSourceMethodName() + "]: "
		                    + record.getMessage() + "\n";
	              }
	            }); 
	        // ConsoleHandler Format
	        conHdlr.setFormatter(new SimpleFormatter() {
	            public String format(LogRecord record) {
	                return  new Date(record.getMillis()) + (" - ")
		                	+ record.getLevel() + ": ["
		                    + record.getSourceClassName() + "."
		                    + record.getSourceMethodName() + "]: "
		                    + record.getMessage() + "\n";
	            	}
	            });
	        
	        LOG.addHandler(fh);
	        LOG.addHandler(conHdlr);
	        LOG.setLevel(Level.INFO);
	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
			
		ServerSocket s = new ServerSocket(PORT);
		/** Creating default conversation channel*/
		conversations.put("default", new Conversation("defaut"));
		
		LOG.info("Info: Server Socket is up an running, start serving clients");
		System.out.println("JChatApp Server v0.1");
		System.out.println("--------------------");
		ipAddress();
		
		
		/** 
		 * I am the main loop of this class!
		 */
		try{
			while (true){
			Socket socket = s.accept();
			LOG.info("Client connected with Address: " + s.getInetAddress() + ", starting new communication thread");
			Thread t = new Thread(new ClientHandler(socket));
			t.start();
			}
		}
		catch (IOException e){
			LOG.warning("Got an IOException while starting ClientThread.. closing socket..(or not)");
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
        	LOG.severe("Could not determine servers IP address, shuting down... "+ e);
        	return;
        }
    }
}
