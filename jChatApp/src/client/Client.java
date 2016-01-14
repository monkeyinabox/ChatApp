
package client;

import java.io.*;
import java.net.*;
import java.util.Observable;

import server.*;

public class Client extends Observable {
	public static ObjectOutputStream output = null;
	ClientReceiver receiver = null;
	static server.User user = null;
	//static InetAddress addr;
	
	public Client(InetAddress addr,String username) {
		try {
	        String hostname;
	        hostname = addr.getHostName();       
	    	System.out.println("IP Addr.: "+ addr);
	    	System.out.println("Your current Hostname : " + hostname);
	    	System.out.println("Port Nr.: "+ Server.PORT);

			Socket socket = new Socket(addr, Server.PORT); // Get the servers port number
			output = new ObjectOutputStream(socket.getOutputStream());
			receiver = new ClientReceiver(socket, this);	
			user = new server.User();
			user.setUsername(username);
			output.writeObject(new Message(MessageType.ADDUSER, "connected..", "default", user.getUsername()));
			System.out.println(user.getUsername());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void sendText(String text, MessageType messageType) {
		try {
			output.writeObject(new Message(messageType, text, "default", user.getUsername()));
			output.flush();
		} catch (Exception ex) {
			System.out.println(ex);
		 }
	}

	public void addMessage(Message message) {
		// TODO implement Conversation which handles all Messages and update the GUI.
		this.setChanged();
		notifyObservers(message.getChatMessage());
		
	}
}