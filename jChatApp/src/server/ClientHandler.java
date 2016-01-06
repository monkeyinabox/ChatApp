package server;

import java.io.*;
import java.net.*;
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
			Server.conversations.get("default").userJoin(user);
			
			while (true) {
	        	/** Read messages from Socket and save to messageQueue */
				Message message = (Message) in.readObject();
	        	Server.LOG.info("ClientHandler <"+ chID  +">: Action: Reciving Message, MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType()+", recived from: "+ message.getSenderName() + ", Conversation: " +message.getConversationName()+ ", Content: " + message.getContent());

				  
	        	//Updating User name
	           	if (user.getUsername() != message.getSenderName()) {
					user.setUsername(message.getSenderName());
					Server.LOG.info("ClientHandler <"+ chID  +">: Action: Username Updated: " +message.getSenderName());
				}
	        	
	    		switch (message.getMessageType()){
				// Message Type 1 is broadcasted to all clients in a conversation
				case 1: Server.conversations.get(message.getConversationName()).sendMessage(message);
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
				//Message Type 4 User changed username
				case 4: changeUsername(message);
						Server.LOG.info("ClientHandler<"+ chID  +">: Action: Changing Username, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
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
			Server.conversations.get("default").userLeave(user);
						
			socket.close();
			Server.LOG.info("ClientHandler<"+ chID  +">: Closing client socket, Thank you and please come again..");
		}
		catch (IOException e) {Server.LOG.warning("ClientHandler <"+ chID  +">: Error on closing socket: ");}
		}
	}
	/**
	 * 
	 * @param message
	 * Sending an update to all users where content is new User name and senderName is old User name
	 * 
	 */
	private void changeUsername(Message message) {
		
		String oldName = user.getUsername();
		
		user.setUsername(message.getSenderName());
		Server.LOG.info("ClientHandler <"+ chID  +">: Action: Username Updated: " +message.getSenderName());
		
		//I don't know which conversations the user has joined, will send to all known clients on default channel
		Server.conversations.get("default").sendMessage(new Message(4,message.getSenderName(),"default",oldName));
		
	}

	/**
	 * 
	 * @param message
	 * Client lost connection remove and destroy
	 * 
	 */
	private void disconnect(Message message) {
		
		// If the client did not send a remove message do it for him
		if (message.getSenderName() == user.getUsername()) {
			Server.users.remove(user);
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
		
		Server.conversations.get(message.getConversationName()).sendMessage(new Message(3,message.getContent(),message.getConversationName(),"server"));
		
		// only users Client Handler can remove from conversation
		if (message.getSenderName() == user.getUsername()) {
			Server.conversations.get(message.getConversationName()).userLeave(user);
		}
	}

	
	/**
	 *  
	 * @param message
	 * If users Joins, update him with all user names currently connected and send his user name to all client
	 * 
	 */
	private void userAdd(Message message) {
	
		// Update all Client with new User name
		if (Server.users.contains(message.getContent())){
			
			Server.conversations.get(message.getConversationName()).sendMessage(new Message(2,message.getContent(),message.getConversationName(),"server"));
			
			Server.LOG.info("Sending Broadcast Update:" + message.getContent()+" Conversation: "+message.getConversationName());
		
		}
			
		if (Server.conversations.containsKey(message.getConversationName())){
			
			Server.LOG.info("Updating Userlist: Username:" + message.getContent()+" Conversation: "+message.getConversationName());
			
			Iterator<User> it = Server.conversations.get(message.getConversationName()).getUsers().iterator();
			
		    while (it.hasNext()) {
		       	user.sendMessage(new Message(2,it.next().getUsername(),message.getConversationName(),"server"));
			}
		    	
		}
	}
	
	/**
	 * 
	 * @param message
	 * Deprecated: Broadcasting messages to all connected client
	 * 
	 
	public void sendMessage(Message message){
		Iterator<User> it = Server.users.iterator();
			while (it.hasNext()) {
		        try {
					it.next().getOutputStream().writeObject(message);
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("ClientHandler: Warning: Could not send message to " + it.next().getUsername() +" with exeption: "+ ex);
		    }
		}
	}
	*/
}