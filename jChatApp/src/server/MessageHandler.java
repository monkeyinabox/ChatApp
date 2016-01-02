package server;

import java.io.*;
import java.util.*;

public class MessageHandler implements Runnable {

	public MessageHandler(){}
	
	@Override
	public void run() {
	Server.LOG.info("MessagaeHandler thread started succsefully");
	while (true){
	Iterator it = Server.messageQueue.iterator();
	
	while (it.hasNext()){
		//Get next Message from MessageQueue
		Message message = Server.messageQueue.remove();
		Server.LOG.info("Action: Processing Message, MessageID: "+message.getMessageID()+", MessageType: "+message.getMessageType());
		
		switch (message.getMessageType()){
			// Message Type 1 is broadcasted to all clients
			case 1: sendMessage(message);
					Server.LOG.info("Action: Sending, MessageID: "+message.getMessageID()+ ", Content: " + message.getContent());
					break;
			// Message Type 2 User is joining (content is user name)
			case 2: userAdd(message);
					Server.LOG.info("Action: Adding User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
					break;
			// Message Type 3 User left (content is user name)
			case 3: userRemove(message);
					Server.LOG.info("Action: Removing User, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
					break;
			// Message Type 9 Client Disconnect
			case 9: disconnect(message);
					Server.LOG.info("Action: User Disconnect, MessageID: "+message.getMessageID()+ ", Username: " + message.getContent());
					break;
			default: Server.LOG.info("Message with unknown Message Type recived");
					break;
			}
	   	}
	  }
	}	
	
	private void disconnect(Message message) {
			// TODO Auto-generated method stub
			
		}
	
	private void userRemove(Message message) {
			// TODO Auto-generated method stub
			
		}
	
	private void userAdd(Message message) {
			// TODO Auto-generated method stub
			
		}

/**
 * 
 * @param message
 * Broadcasting all messages from Message Queue to all connected client
 */
	public void sendMessage(Message message){
	
	Iterator it = Server.clientOutputStreams.iterator();
	
	    while (it.hasNext()) {
	        try {
				ObjectOutputStream outputStream = (ObjectOutputStream) it.next();
	        	outputStream.writeObject(message);
	        	outputStream.flush();
	        } 
	        catch (Exception ex) {
	        	Server.LOG.warning("Send Error: " + ex);
	        }
	    }
	}
}