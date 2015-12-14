package server;

import java.io.Serializable;
import java.time.Instant;

/**
 * 
 * @author rifl
 * Message Class provide transport message objects for jChatApp 
 *
 */
public class Message implements Serializable {

	/**
	 * Generated by Eclipse form implementing resialazeble
	 */
	private static final long serialVersionUID = 1223417195930483671L;
	
	/**
	 * Messages Types:
	 * 1 - chatMessage
	 * 2 - userAdd
	 * 3 - userRemove
	 * 9 - disconnect
	 */
	private int messageType;
	
	private String conversationID;
	
	private String senderID;
	
	private String content;
	 
	private long timestamp;
	
	// Constructor
	private Message(int i, String c, String cid, String sid){
		messageType = i;
		content = c;
		timestamp = Instant.now().getEpochSecond();
		conversationID = cid;
		senderID = sid;
	}
	
	public int getMessageType() {
		return messageType;
	}
	
	public String getContent() {
		return content;
	}
	public String getConversationID() {
		return conversationID;
	}
}