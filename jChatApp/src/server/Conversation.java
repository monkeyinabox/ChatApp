package server;

import java.util.ArrayList;

public class Conversation {

	private String conversationName;
	private int conversationID;
	private ArrayList<User> users;
	private ArrayList<Message> messages;
	
	public Conversation(String s){
		conversationName = s;
		conversationID = this.hashCode();
		users= new ArrayList<User>();
		messages= new ArrayList<Message>();
	}
	
	public void addMessage(Message message){
		messages.add(message);
	}
	
	public void userJoin(User user){
		users.add(user);
	}
	
	public void userLeave(User user){
		users.remove(user);
	}
	
	public ArrayList<User> getUsers(){
		return users;
	}
	
	public ArrayList<Message> getMessages(){
		return messages;
	}
	
	public String getConversationName(){
		return conversationName;
	}
	
	public int getConversationID(){
		return this.hashCode();
	}
}
