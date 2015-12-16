package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

	private Socket socket;
	private ObjectOutputStream out;
	
	// Get userID from Client to identify clientHandler <-> user relation
	private int chID = this.hashCode();
	private User user;
	
	
	public ClientHandler(Socket s) throws IOException {
		socket = s;
	}
	
	@Override
	public void run(){
		Server.LOG.info("ClientHandler <"+ chID +">: thread started succsefully: " + socket.toString());
		try{
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Server.LOG.info("ClientHandler <"+ chID  +">: InputStream created: "+ in.toString());
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			Server.LOG.info("ClientHandler <"+ chID  +">: OutputStream created: "+ out.toString());
			
			Server.clientOutputStreams.add(out);
			
			while (true) {
	        	/** Read messages from Socket and save to messageQueue */
				Message ms = (Message) in.readObject();
	        	Server.LOG.info("ClientHandler <"+ chID  +">: Action: Reciving Message, MessageID: "+ms.getMessageID()+", MessageType: "+ms.getMessageType()+", recived from: "+ ms.getSenderID() + ", Conversation: " +ms.getConversationID()+ ", Content: " + ms.getContent());
	        	Server.messageQueue.add(ms);
	        	Server.LOG.info("ClientHandler <"+ chID  +">: Action: Added to Queue, MessageID: "+ ms.getMessageID());
			}
		}
		catch(Exception m){	Server.LOG.warning("ClientHandler <"+ chID  +">: Error on reading input stream at " + this.socket + " with exectipn: " + m);}
	
	finally {
		
		try {
			socket.close();
			Server.LOG.info("Closing client socket");
			Server.clientOutputStreams.remove(out);
			Server.LOG.info("Outputstream removed");
		}
		catch (IOException e) {Server.LOG.warning("ClientHandler <"+ chID  +">: Error on closing socket: " + this.socket);}
		}
	}
}


