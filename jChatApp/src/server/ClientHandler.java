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
		// in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		Server.clientOutputStreams.add(out);
		Server.LOG.info("New Outputstream added to collection");
	}
	
	@Override
	public void run(){		
		try{while (true) {
			// Read socket and push to messageQueue
			try{InputStream in = socket.getInputStream();
            	ObjectInputStream oIn = new ObjectInputStream(in);
            	server.Message ms = (server.Message) oIn.readObject();
            
            	Server.LOG.info("Message <"+ms.getMessageType()+"> recived from: "+ ms.getSenderID() + ", Conversation: " +ms.getConversationID()+ ", Content: " + ms.getContent());
            	Server.messageQueue.add(ms);
            	Server.LOG.info("Message Added to Queue");
            	
			}
			catch(Exception m){	Server.LOG.warning("Error on reading input stream at " + this.socket + " with exectipn: " + m);}
		}
		
	}
	finally {
		try {
		socket.close();
		Server.LOG.info("Closing client socket");
		Server.clientOutputStreams.remove(out);
		Server.LOG.info("Outputstream removed");}
		catch (IOException e) {Server.LOG.warning("Error on closing socket: " + this.socket);}
		}
	}
}


