package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import server.*;

public class ClientReceiver extends Thread {
	private ServerSocket serverSocket = null;
	Client client;

	public ClientReceiver(Client client) {
		start();
	}

	@Override
	public void run() {
		try {
			this.serverSocket = new ServerSocket(Client.PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				Socket clientSo = serverSocket.accept();
				InputStream in = clientSo.getInputStream();
				ObjectInputStream oIn = new ObjectInputStream(in);
				Message message = (Message) oIn.readObject();
				client.addMessage(message);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println(e);
			}
		}
	}

}
