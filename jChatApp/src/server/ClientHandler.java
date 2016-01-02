package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientHandler implements Runnable {

	private Socket socket;
	private int chID = this.hashCode();
	private User user = new User("tmp");
	
	public ClientHandler(Socket s) throws IOException {
		socket = s;
	}
	
	@Override
	public void run(){
		Server.LOG.info("ClientHandler <"+ chID +">: thread started succsefully: " + socket.toString());
		try{
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Server.LOG.info("ClientHandler <"+ chID  +">: InputStream created: "+ in.toString());
			
			// Save OutputStream to User Object
			user.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
			
			// Add User to default conversation
			Server.conversations.get(0).userJoin(user);
			
			while (true) {
	        	/** Read messages from Socket and save to messageQueue */
				Message message = (Message) in.readObject();
	        	Server.LOG.info("ClientHandler <"+ chID  +">: Action: Reciving Message, MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType()+", recived from: "+ message.getSenderID() + ", Conversation: " +message.getConversationName()+ ", Content: " + message.getContent());
	        	//Server.messageQueue.add(message);
				  
	        	//Server.LOG.info("ClientHandler <"+ chID  +">: Action: Added to Queue, MessageID: "+ ms.getMessageID());
	        
	        	if (user.getUsername() != message.getSenderID()) {
					user.setUsername(message.getSenderID());
				}
	        	
	    		switch (message.getMessageType()){
				// Message Type 1 is broadcasted to all clients
				case 1: sendMessage(message);
						Server.LOG.info("ClinetHandler<"+ chID  +">: Action: Sending, MessageID: "+message.getMessageID()+ ", Content: " + message.getContent());
						break;
				// Message Type 2 User is joining (content is user name)
				case 2: userAdd(message);
						Server.LOG.info("ClientHandler<"+ chID  +">: Action: Adding User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 3 User left (content is user name)
				case 3: userRemove(message);
						Server.LOG.info("ClientHandler<"+ chID  +">: Action: Removing User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 9 Client Disconnect
				case 9: disconnect(message);
						Server.LOG.info("ClientHandler<"+ chID  +">: Action: User Disconnect, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				default: Server.LOG.severe("ClientHandler<"+ chID  +">: Error: Message with unknown Message Type recived");
						break;
				}
			}
		}
		catch(Exception m){	Server.LOG.warning("ClientHandler <"+ chID  +">: Error processing Message from socket: " + this.socket + " with exectipn: " + m + "\n" );
							m.printStackTrace();}
	
	finally {
		
		try {
			Server.LOG.info("ClientHandler:<"+ chID  +">: User removed from default channel");
			Server.conversations.get(0).userLeave(user);
						
			socket.close();
			Server.LOG.info("ClientHandler<"+ chID  +">: Closing client socket, Thank you and please come again..");
		}
		catch (IOException e) {Server.LOG.warning("ClientHandler <"+ chID  +">: Error on closing socket: ");}
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
		// (Unicast) Send all existing users to newly joined user
		Server.userlist.add(message.getContent());
		
		// Update all Client with new User name
		sendMessage(new Message(2,message.getContent(),message.getConversationName(),"server"));
		Server.LOG.info("Updating Userlist: Username:" + message.getContent()+" Conversation: "+message.getConversationName()+ " "+Server.conversations.contains(message.getConversationName()) );
		if (Server.conversations.contains(message.getConversationName())){
			int indexOf = Server.conversations.indexOf(message.getConversationName());
			ArrayList<User> al = Server.conversations.get(indexOf).getUsers();
			
			Iterator<User> it = al.iterator();
		    while (it.hasNext()) {
		    	try {
		    		Server.LOG.info("Sending Udpate: "+it.toString());
					user.getOutputStream().writeObject(new Message(2,it.toString(),message.getConversationName(),"server"));}
		    	catch (IOException e) {
					Server.LOG.warning("ClientHandler: Error: Could not send User " + it.toString() +" with exeption: "+ e);}
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
		Iterator<User> it = Server.users.iterator();
			while (it.hasNext()) {
		        try {
					it.next().getOutputStream().writeObject(message);
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("ClientHandler: Error: Could not send message to " + it.next().getUsername() +" with exeption: "+ ex);
		    }
		}
	}
}