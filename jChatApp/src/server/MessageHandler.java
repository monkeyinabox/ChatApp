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
	switch (message.getMessageType()){
	
		// Message Type 1 is broadcasted to all clients
		case 1: sendMessage(message);
				Server.LOG.info("Message send: " + message.getContent());
				break;
		// Message Type 2 User is joining (content is user name)
		case 2: userAdd(message);
				Server.LOG.info("User Joined: " + message.getContent());
				break;
		// Message Type 3 User left (content is user name)
		case 3: userRemove(message);
				Server.LOG.info("User left: " + message.getContent());
				break;
		// Message Type 9 Client Disconnect
		case 9: disconnect(message);
				Server.LOG.info("Disconnect: " + message.getSenderID());
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
	public void sendMessage(Message msg){
	
	Iterator it = Server.clientOutputStreams.iterator();
	
	    while (it.hasNext()) {
	        try {
				ObjectOutputStream outputStream = (ObjectOutputStream) it.next();
	        	outputStream.writeObject(msg);
	        	outputStream.flush();
	        } 
	        catch (Exception ex) {
	        	Server.LOG.warning("Send Error: " + ex);
	        }
	    }
	}
}