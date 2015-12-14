package server;

import java.util.ArrayList;

public class Conversation {

	String conversationID;
	ArrayList<String> users;
	ArrayList<Message> messages;
	
	public Conversation(String cid){
		conversationID = cid;
	}
	
}
