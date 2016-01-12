package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;

public class ClientHandler implements Runnable {

	private Socket socket;
	private int chID = this.hashCode();
	private User user = new User();
	
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
						
			while (true) {
	        	/** Read messages from Socket and save to messageQueue */
				Message message = (Message) in.readObject();
	        	Server.LOG.info("<"+ chID  +">: Reciving Message with MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType()+", recived from: "+ message.getSenderName() + ", Conversation: " +message.getConversationName()+ ", Content: " + message.getContent());
      	
	        	switch (message.getMessageType()){
				// Message Type 1 is broadcasted to all clients in conversation
				case 1: Server.conversations.get(message.getConversationName()).sendMessage(message);
						break;
				// Message Type 2 User is joining (content is user name)
				case 2: userAdd(message);
						break;
				// Message Type 3 User left (content is user name)
				case 3: userRemove(message);
						break;
				//Message Type 4 User changed username
				case 4: changeUsername(message);
						break;
				//Message Type 4 User changed username
				case 5: joinConversation(message);
						break;
				case 6: leaveConversation(message);
						break;
				// Message Type 9 Client Disconnect
				case 9: disconnect();
						break;
				default: Server.LOG.severe("<"+ chID  +">: Error: Message with unknown Message Type recived");
						break;	
				}
			}
		}
		/**
		 * Catch client disconnect
		 */
		catch (SocketException se) {
			//disconnect();
			Server.LOG.info("<"+ chID  +">:User Disconnected: " + user.getUsername());}
		
		// Catch unexpected exception and print Stacktrace
		catch(Exception m){	
			Server.LOG.warning("<"+ chID  +">: Error processing Message from socket: " + this.socket + " with exectipn: " + m + "\n" );
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
	 * Add User to conversation and create new conversation if it does not exist
	 * @param message
	 */
	private void joinConversation(Message message) {
		if (!Server.conversations.containsKey(message.getConversationName())){
			Server.LOG.warning("<"+ chID  +">: Conversation created: " +message.getConversationName());
			Server.conversations.put(message.getConversationName(), new Conversation(message.getConversationName()));
		}
		Server.conversations.get(message.getConversationName()).userJoin(user);
	}
	
	/**
	 * Remove User from conversation and delete it if no users are joined
	 * @param message
	 */
	private void leaveConversation(Message message) {
		Server.LOG.info("<"+ chID  +">: Leaving conversation: " +message.getConversationName() +", Username: " + message.getContent());
		Server.conversations.get(message.getConversationName()).userLeave(user);
	
		if (Server.conversations.get(message.getConversationName()).getUsers().isEmpty()){
			Server.LOG.warning("<"+ chID  +">: Conversation destroyed: " +message.getConversationName());
			Server.conversations.remove(message.getConversationName());	
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
		Server.LOG.info("<"+ chID  +">: Username Updated New: " +message.getSenderName()+" Old: "+ oldName);
		Iterator<HashMap.Entry<String, Conversation>> it = Server.conversations.entrySet().iterator();
		while (it.hasNext()) {
			String cn = it.next().getKey();
	       	if (Server.conversations.get(cn).getUsers().contains(user)){
	       		Server.conversations.get(cn).sendMessage(new Message(4,message.getSenderName(),cn,oldName));
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
		Iterator<HashMap.Entry<String, Conversation>> it = Server.conversations.entrySet().iterator();
		while (it.hasNext()) {
			String cn = it.next().getKey();
			Server.LOG.info("User "+user.getUsername()+" Leaving Conversation: "+cn);
	       	if (Server.conversations.get(cn).getUsers().contains(user)){ 
	       		Server.LOG.info("Send Update to all in: "+cn);
	       		Server.conversations.get(cn).userLeave(user);
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
				// If a new user is joining the server update his username
		if (user.getUsername() != message.getSenderName())
			changeUsername(message);
		
		// Check if it is a valid conversation the user tries to join
		if (Server.conversations.containsKey(message.getConversationName())){
			Server.LOG.info("<"+ chID  +">: Adding User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
			joinConversation(message);
				
			Server.LOG.info("Updating Userlist: Username:" + message.getContent()+" Conversation: "+message.getConversationName());
			// Update User list for client with unicast
			Iterator<User> it = Server.conversations.get(message.getConversationName()).getUsers().iterator();
			while (it.hasNext()) {
		       	user.sendMessage(new Message(2,it.next().getUsername(),message.getConversationName(),"server"));
			}	
		}
	}
}