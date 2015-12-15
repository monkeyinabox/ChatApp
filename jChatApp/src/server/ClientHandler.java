package server;

import java.io.*;
import java.net.*;


public class ClientHandler implements Runnable {

	private Socket socket;
	private ObjectOutputStream out;
	
	public ClientHandler(Socket s) throws IOException {
		socket = s;
	}
	
	@Override
	public void run(){
		Server.LOG.info("ClientHandler thread started succsefully");
		try{
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Server.LOG.info("InputStream created");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			Server.LOG.info("OutputStream created");
			Server.clientOutputStreams.add(out);
			Server.LOG.info("New Outputstream added to collection of Outputstreams");
			
			while (true) {
	        	Message ms = (Message) in.readObject();
	        	Server.LOG.info("Message <"+ ms.getMessageType()+"> recived from: "+ ms.getSenderID() + ", Conversation: " +ms.getConversationID()+ ", Content: " + ms.getContent());
	        	Server.messageQueue.add(ms);
	        	Server.LOG.info("Message Added to Queue");
			}
		}
		catch(Exception m){	Server.LOG.warning("Error on reading input stream at " + this.socket + " with exectipn: " + m);}
	
	finally {
		
		try {
			socket.close();
			Server.LOG.info("Closing client socket");
			Server.clientOutputStreams.remove(out);
			Server.LOG.info("Outputstream removed");
		}
		catch (IOException e) {Server.LOG.warning("Error on closing socket: " + this.socket);}
		}
	}
}


