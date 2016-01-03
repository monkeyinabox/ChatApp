package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//Importing Classes from client packages
import server.Conversation;

/**
 * 
 * @author rifl This Class is starting two thread - ClientHandler is dealing
 *         with client connections and will receive incoming messages and save
 *         them in messageQueue - MessageHandler will process messages in
 *         messageQueue and will parse and forward messages
 */

public class Server {

	// Collection of all connected clientsOutputStreams
	public static ArrayList<ObjectOutputStream> clientOutputStreams = new ArrayList<ObjectOutputStream>();
	ArrayList<Conversation> conversations;
	ArrayList<User> users;
	// MessageQueue that caches incoming messages
	public static Queue<Message> messageQueue = new LinkedList<Message>();

	// Network Port
	public static final int PORT = 1337;
	// Logger initialization
	final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public static void jChatAppServer() throws IOException {

		LOG.setLevel(Level.INFO);
		//ServerSocket serverSender = new ServerSocket(SERVERSENDPORT);
		ServerSocket serverReceiver = new ServerSocket(PORT);
		LOG.info("Info: Server Socket is up an running, start serving clients");
		System.out.println("JChatApp Server v0.1");
		System.out.println("--------------------");
		ipAddress();

		LOG.info("Info: Starting MessageHandler thread");
		try {
			Thread mh = new Thread(new MessageHandler());
			mh.start();
		} catch (Exception ex) {
			LOG.info("Error : starting MessageHandler failed with exeption: " + ex);
		}

		// Statistics

		try {
			while (true) {
				Socket socket = serverReceiver.accept();
				LOG.info("Info: Client connected with Address: " + serverReceiver.getInetAddress()
						+ ", starting new communication thread");
				Thread t = new Thread(new ClientHandler(socket));
				t.start();

			}
		} catch (IOException e) {
			LOG.warning("Error: Got an IO Exception while starting ClientThread.. closing socket..(or not)");
			serverReceiver.close();
		}

		finally {
			serverReceiver.close();
			LOG.info("Server is Stopping...");
		}

	}

	/**
	 * ipAddress is providing console Output on server startup with Address
	 * Informations Note ->> Multiple Interfaces are not handled by this script!
	 */
	public static void ipAddress() {
		InetAddress ip;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("IP Addr.: " + ip);
			System.out.println("Your current Hostname : " + hostname);
			System.out.println("Port Nr.: " + PORT);

		} catch (UnknownHostException e) {
			LOG.severe("Error: Could not determine servers IP address, shuting down... " + e);
		}
	}
}
