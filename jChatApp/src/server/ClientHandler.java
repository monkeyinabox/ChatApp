package server;

import java.io.*;
import java.net.*;
import java.util.Iterator;

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
				Message message = (Message) in.readObject();
	        	Server.LOG.info("ClientHandler <"+ chID  +">: Action: Reciving Message, MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType()+", recived from: "+ message.getSenderID() + ", Conversation: " +message.getConversationID()+ ", Content: " + message.getContent());
	        	//Server.messageQueue.add(message);
	        	//Server.LOG.info("ClientHandler <"+ chID  +">: Action: Added to Queue, MessageID: "+ ms.getMessageID());
	        	
	    		switch (message.getMessageType()){
				// Message Type 1 is broadcasted to all clients
				case 1: sendMessage(message);
						Server.LOG.info("MessageHandler: Action: Sending, MessageID: "+message.getMessageID()+ ", Content: " + message.getContent());
						break;
				// Message Type 2 User is joining (content is user name)
				case 2: userAdd(message);
						Server.LOG.info("MessageHandler: Action: Adding User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 3 User left (content is user name)
				case 3: userRemove(message);
						Server.LOG.info("MessageHandler: Action: Removing User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 9 Client Disconnect
				case 9: disconnect(message);
						Server.LOG.info("MessageHandler: Action: User Disconnect, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				default: Server.LOG.severe("MessageHandler: Error: Message with unknown Message Type recived");
						break;
				}
			
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
	
	/**
	 * 
	 * @param message
	 * Client lost connection remove and destroy
	 * 
	 */
	private void disconnect(Message message) {
		Server.clientOutputStreams.remove(message.getContent());
		// If the client did not send a remove message do it for him
		if (Server.userlist.contains(message.getContent())) {
			userRemove(message);	
		}
	}

	/** 
	 * 
	 *@param message 
	 * If user leaves the chat notify all others 
	 * 
	*/
	private void userRemove(Message message) {
		
		//should I check here if message is valid?
		
		Server.userlist.remove(message.getContent());
		sendMessage(new Message(3,message.getContent(),"system","server"));
	}

	
	/**
	 *  
	 * @param message
	 * If users Joins, update him with all Usernames currently connected and send his username to all client
	 * 
	 */
	private void userAdd(Message message) {
		// Update all Client with new Username
		sendMessage(new Message(2,message.getContent(),"system","server"));
		
		// (Unicast) Send all existing users to newly joined user
		Server.userlist.add(message.getContent());
		Iterator<String> it = Server.userlist.iterator();
	    while (it.hasNext()) {
	    	try {
				out.writeObject(new Message(2,it.toString(),"system","server"));
			} catch (IOException e) {
				Server.LOG.warning("MessageHandler: Error: Could not send message to " + it.toString() +" with exeption: "+ e);
			}
		}
	}
	
	/**
	 * 
	 * @param message
	 * Broadcasting messages to all connected client
	 * 
	 */
	public void sendMessage(Message message){
		Iterator<ObjectOutputStream> it = Server.clientOutputStreams.iterator();
			while (it.hasNext()) {
		        try {
					ObjectOutputStream outputStream = (ObjectOutputStream) it.next();
		        	outputStream.writeObject(message);
		        	outputStream.flush();
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("MessageHandler: Error: Could not send message to " + it.toString() +" with exeption: "+ ex);
		        }
			}
	}
}