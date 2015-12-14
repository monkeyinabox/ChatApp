package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server{
	
	//Collection of connected clients and Usernames
	public static ArrayList clientOutputStreams = new ArrayList();
	ArrayList conversations;
	ArrayList<String> users;
	
	// Network Port 	
	static final int PORT=1337;
	//Logger
	final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static void jChatAppServer() throws IOException{
		ArrayList cip = new ArrayList();
		
		LOG.setLevel(Level.INFO);	
		ServerSocket s = new ServerSocket(PORT);
		LOG.info("Server Socket is up an running");
	
			
		try{
			while (true){
			Socket socket = s.accept();
			Thread t = new Thread(new ClientHandler(socket));
			InetAddress ipAddress = s.getInetAddress();
			
			cip.add(ipAddress);
			
			t.start();
			LOG.info("Client connected with Address: " + ipAddress + ", starting new communication thread");
			}
		}
		catch (IOException e){
			LOG.warning("Got an IO Exception while starting ClientThread.. closing client socket..");
			//socket.close();
		}
	finally {
		s.close();
		LOG.info("Server is Stopping...");
	}
	
	}
}
