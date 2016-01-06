package server;

import java.util.ArrayList;
import java.util.Iterator;

public class Conversation {

	private String conversationName;
	private ArrayList<User> users;
	private ArrayList<Message> messages;
	
	public Conversation(String s){
		conversationName = s;
		users = new ArrayList<User>();
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
	
	public String getConversationName(){
		return conversationName;
	}
	
	public int getConversationID(){
		return this.hashCode();
	}
	
	public void sendMessage(Message message){
		Iterator<User> it = users.iterator();
			while (it.hasNext()) {
		        try {
					it.next().getOutputStream().writeObject(message);
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("Conversation: Warning: Could not send message to " + it.next().getUsername() +" with exeption: "+ ex);
		    }
		}
	}
}
