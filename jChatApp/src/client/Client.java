
package client;

import java.io.*;
import java.net.*;
import java.util.Observable;

import server.*;

public class Client extends Observable {
	private ObjectOutputStream output = null;
	ClientReceiver receiver = null;
	static server.User user = null;
	
	public Client() {
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			System.out.println("addr = " + addr);
			Socket socket = new Socket(addr, Server.PORT); // Get the servers port number
			output = new ObjectOutputStream(socket.getOutputStream());
			receiver = new ClientReceiver(socket, this);	
			user = new server.User("new User");
			System.out.println(user.getUsername());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void sendText(String text, int messageTyp) {
		try {
			output.writeObject(new Message(messageTyp, text, "default", user.getUsername()));
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