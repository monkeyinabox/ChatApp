package server;

import java.io.*;
import java.util.*;

public class MessageHandler implements Runnable {
	
	public MessageHandler(){}
	
	@Override
	public void run() {
		
	Server.LOG.info("Info: MessagaeHandler thread started succsefully");
	while (true){
		
	Iterator<Message> it = Server.messageQueue.iterator();
	while (it.hasNext()){
		
		//Get next Message from MessageQueue
		Message message = Server.messageQueue.remove();
		Server.LOG.info("MessageHandler: Action: Processing Message, MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType());
		
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
			// Message Type 4 Create new Conversation
			case 4: createConversation(message);
					Server.LOG.info("MessageHandler: Action: Removing User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
					break;
			// Message Type 5 remove Conversation
			case 5: removeConversation(message);
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
	}	
	
	private void removeConversation(Message message) {
		// TODO Auto-generated method stub
		
	}

	private void createConversation(Message message) {
		// TODO Auto-generated method stub
		
	}

	private void disconnect(Message message) {
			// TODO Auto-generated method stub
			
		}
	
	private void userRemove(Message message) {
			// TODO Auto-generated method stub
		Server.userlist.remove(message.getContent());
		}
	
	private void userAdd(Message message) {
			// TODO Auto-generated method stub
			Server.userlist.add(message.getContent());
			
			Iterator<String> it = Server.userlist.iterator();
				
		    while (it.hasNext()) {
		    	sendMessage(new Message(2,(String)it.next(),"system","server"));
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