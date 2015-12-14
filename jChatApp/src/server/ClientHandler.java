package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;

	public ClientHandler(Socket s) throws IOException {
		
		socket = s;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		Server.LOG.info("in and out initialized");
	}
	
	@Override
	public void run(){		
		try{while (true) {
			// Get messages
			String str = in.readLine();
			if (str.equals("END")) break;

			// modify string
			str = str + str;
			System.out.println("Echoing: " + str); // On the screen
			out.println(str); // To the client
		}
		System.out.println("closing...");
	}
	catch (IOException e) {}
	finally {
		try {socket.close();}
		catch (IOException e) {}
	}
  }	
}
