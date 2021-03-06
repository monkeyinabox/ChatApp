package client;

import java.io.*;
import java.net.*;
import java.util.Observable;

import server.*;

public class Client extends Observable {
	static final int PORT = 1337;
	private ObjectOutputStream output = null;
	
	public Client() {
		try {
			InetAddress addr = InetAddress.getByName("localhost");
			System.out.println("addr = " + addr);

			// Get the servers port number
			Socket socket = new Socket(addr, PORT);

			// chatArea.appendText("Connected to: "+":1337\n");
			output = new ObjectOutputStream(socket.getOutputStream());
			ClientReceiver receiver = new ClientReceiver(this);
			
		} catch (Exception ex) {
			System.out.println(ex); //test
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// Get the servers IP Address
		InetAddress addr = InetAddress.getByName("localhost");
		System.out.println("addr = " + addr);

		// Get the servers port number
		Socket socket = new Socket(addr, PORT);

		// Keep everything in a try-finally to make sure that the socket is
		// closed
		try {
			System.out.println("socket = " + socket);

			BufferedReader KeyboardIn = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Got the keyboard..");

			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Got the OutputStream..");

			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			System.out.println("Got the InputStream..");

			while (true) {
				// What to say to server?

				output.writeObject(new Message(2, "Hans", "system", "hans"));
				output.writeObject(new Message(3, "Hans", "system", "hans"));

				System.out.print("What to say to server: ");
				String what = KeyboardIn.readLine();
				output.writeObject(new Message(1, what, "default", "hans"));
				output.flush();

				Message ms = (Message) input.readObject();
				System.out.println("Message <" + ms.getMessageType() + "> recived from: " + ms.getSenderID()
						+ ", Conversation: " + ms.getConversationID() + ", Content: " + ms.getContent());
			}

		} finally {
			System.out.println("closing...");
			socket.close();
		}
	}

	public void sendText(String text) {
		try {
			// Get the servers IP Address
			output.writeObject(new Message(2, text, "system", "hans"));
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