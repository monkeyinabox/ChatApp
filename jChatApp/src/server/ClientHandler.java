package server;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

public class ClientHandler implements Runnable {

	private Socket socket;
	private int chID = this.hashCode();
	private User user = new User("tmp");
	
	public ClientHandler(Socket s) throws IOException {
		socket = s;
	}
	
	@Override
	public void run(){
		Server.LOG.info("<"+ chID +">: thread started succsefully: " + socket.toString());
		try{
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Server.LOG.info("<"+ chID  +">: InputStream created: "+ in.toString());
			
			// Save OutputStream in User Object
			user.setOutputStream(new ObjectOutputStream(socket.getOutputStream()));
			
			// Add User to default conversation
			Server.conversations.get("default").userJoin(user);
			
			while (true) {
	        	/** Read messages from Socket and save to messageQueue */
				Message message = (Message) in.readObject();
	        	Server.LOG.info("<"+ chID  +">: Reciving Message with MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType()+", recived from: "+ message.getSenderName() + ", Conversation: " +message.getConversationName()+ ", Content: " + message.getContent());

	        	switch (message.getMessageType()){
				// Message Type 1 is broadcasted to all clients in conversation
				case 1: Server.conversations.get(message.getConversationName()).sendMessage(message);
						Server.LOG.info("<"+ chID  +">: Action: Sending, MessageID: "+message.getMessageID()+ ", Content: " + message.getContent());
						break;
				// Message Type 2 User is joining (content is user name)
				case 2: userAdd(message);
						Server.LOG.info("<"+ chID  +">: Action: Adding User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 3 User left (content is user name)
				case 3: userRemove(message);
						Server.LOG.info("<"+ chID  +">: Action: Removing User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				//Message Type 4 User changed username
				case 4: changeUsername(message);
						Server.LOG.info("<"+ chID  +">: Action: Changing Username, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				// Message Type 9 Client Disconnect
				case 9: disconnect();
						Server.LOG.info("<"+ chID  +">: Action: User Disconnect, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
						break;
				default: Server.LOG.severe("<"+ chID  +">: Error: Message with unknown Message Type recived");
						break;	
				}
			}
		}
		/**
		 * Catch Disconnect
		 */
		catch (SocketException se) {
			disconnect();
			Server.LOG.info("<"+ chID  +">:User Disconnected: " + user.getUsername());}
		
		// Catch unexpected exception and print Stacktrace
		catch(Exception m){	Server.LOG.warning("<"+ chID  +">: Error processing Message from socket: " + this.socket + " with exectipn: " + m + "\n" );
							m.printStackTrace();}
	
	
	finally {
		
		try {
			disconnect();
			socket.close();
			Server.LOG.info("<"+ chID  +">: Closing client socket, Thank you and please come again..");
		}
		catch (IOException e) {Server.LOG.warning("<"+ chID  +">: Error on closing socket: ");}
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
		Server.LOG.info("<"+ chID  +">: Action: Username Updated: " +message.getSenderName());
		
		Iterator<Map.Entry<String, Conversation>> it = Server.conversations.entrySet().iterator();
		while (it.hasNext()) {
			Conversation c = it.next().getValue();
	       	if (c.getUsers().contains(user)){
	       		c.sendMessage(new Message(4,message.getSenderName(),c.getConversationName(),oldName));
	       	}
		}
	}

	/**
	 * 
	 * @param message
	 * Client lost connection remove and destroy
	 * 
	 */
	private void disconnect() {
		// Remove User from every conversation if socket fails
		Iterator<Map.Entry<String, Conversation>> it = Server.conversations.entrySet().iterator();
		while (it.hasNext()) {
			Server.LOG.info("Test: "+it.toString());
			Conversation c = it.next().getValue();
	       	if (c.getUsers().contains(user.getUsername())){
	       		c.userLeave(user);
	       	}
		}
	}

	/** 
	 * 
	 *@param message 
	 * If user leaves the chat notify all others 
	 * 
	*/
	private void userRemove(Message message) {
		// only users Client Handler can remove from conversation
		if (message.getSenderName() == user.getUsername()) {
			Server.LOG.info("<"+ chID  +">: User "+ message.getSenderName() +" Removed from" +message.getConversationName());
			Server.conversations.get(message.getConversationName()).userLeave(user);
		}
	}

	
	/**
	 *  
	 * @param message
	 * If users Joins, update him with all user names currently connected and send his user name to all other client
	 * 
	 */
	private void userAdd(Message message) {
	
		// Check if it is a valid user and conversation
		if (Server.users.contains(message.getContent()) && Server.conversations.containsKey(message.getConversationName())){
			Server.conversations.get(message.getConversationName()).userJoin(user);
	
			Server.LOG.info("Updating Userlist: Username:" + message.getContent()+" Conversation: "+message.getConversationName());
			Iterator<User> it = Server.conversations.get(message.getConversationName()).getUsers().iterator();
			
			// Update Userlist for client with unicast
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