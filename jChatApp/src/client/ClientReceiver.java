package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import server.*;

public class ClientReceiver extends Thread {
	private Socket socket = null;
	Client client;

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
			while (true) {
				Message message = (Message) oIn.readObject();
				client.addMessage(message);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e);
		}
	}

}
