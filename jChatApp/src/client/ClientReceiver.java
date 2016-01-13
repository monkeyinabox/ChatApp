package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.*;

public class ClientReceiver extends Thread {
	private Socket socket = null;
	Client client;
	public static ObservableList<String> items =FXCollections.observableArrayList ();
	
	public ClientReceiver(Socket socket, Client client) {
		this.socket = socket;
		this.client = client;
		start();
	}

	@Override
	public void run() {
		try {
			InputStream in = socket.getInputStream();
			ObjectInputStream oIn = new ObjectInputStream(in);
			//Client.output.writeObject(new Message(2, "connected..", "default", client.user.getUsername()));
			while (true) {
				Message message = (Message) oIn.readObject();
				
				switch (message.getMessageType()) {
				case 1:
					client.addMessage(message);
					break;
				case 2:
					items.add(message.getContent());
					break;
				case 3:
					items.remove(message.getContent());
					break;

				default:
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e);
		}
	}
	

}
