package server;

import java.util.ArrayList;
import java.util.Iterator;

public class Conversation {

	private String conversationName;
	private ArrayList<User> users;
	
	public Conversation(String s){
		conversationName = s;
		users = new ArrayList<User>();
	}
	
	public void userJoin(User user){
		if (!users.contains(user)){
			Server.LOG.info("<"+ this.hashCode() +"> Updating Clinets to add user '" +user.getUsername()+"' to Conversation"+ this.getConversationName());
			sendMessage(new Message(2,user.getUsername(),this.getConversationName(),user.getUsername()));
			users.add(user);
		}
		else{Server.LOG.info("<"+ this.hashCode() +"> " +user.getUsername()+" is allready in conversation: "+ this.getConversationName());}
	}
	
	public void userLeave(User user){
		Server.LOG.info("<"+ this.hashCode() +"> Updating Clinets to remove user '" +user.getUsername()+"' from Conversation"+ this.getConversationName());
		users.remove(user);
		sendMessage(new Message(3,user.getUsername(),this.getConversationName(),user.getUsername()));
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
					it.next().sendMessage(message);
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("<"+ this.hashCode() +"> Could not send message to " + it.next().getUsername() +" with exeption: "+ ex);
		    }
		}
	}
}
