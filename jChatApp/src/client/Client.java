package client;


import java.io.*;
import java.net.*;

import server.Server;

public final class Client {
	static final int PORT=1337;
	
	private Client() {}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// Get the servers IP Address
		InetAddress addr = InetAddress.getByName("localhost");
		System.out.println("addr = " + addr);

		// Get the servers port number
		Socket socket = new Socket(addr, PORT);

		// Keep everything in a try-finally to make sure that the socket is closed
		try {
			System.out.println("socket = " + socket);
			System.out.println("READY to work ");

			// Prepare the in stream
			// BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Output is automatically flushed by PrintWriter
			// PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// Keyboard Input
			BufferedReader KeyboardIn = new BufferedReader(new InputStreamReader(System.in));
			
			OutputStream out = socket.getOutputStream();
			ObjectOutputStream oOut = new ObjectOutputStream(out);
			
			while (true) {
				// What to say to server?
				System.out.print("What to say to server: ");
				String what = KeyboardIn.readLine();
				oOut.writeObject(new server.Message(1, what, "default", "hans"));
				oOut.flush();

            	ObjectInputStream oIn = new ObjectInputStream(socket.getInputStream());
            	server.Message ms = (server.Message) oIn.readObject();
            	System.out.println("Message <"+ms.getMessageType()+"> recived from: "+ ms.getSenderID() + ", Conversation: " +ms.getConversationID()+ ", Content: " + ms.getContent());
			}

		}
		finally{
			System.out.println("closing...");
			socket.close();
		}
	}
}