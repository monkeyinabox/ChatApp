package server;

import java.io.ObjectOutputStream;

public class User {

	
	private String username;
	private int userID;
	private ObjectOutputStream outputStream; 	
			
	public User (){
		username = "player"; // default name
		userID = this.hashCode();
		
	}

	public void setOutputStream( ObjectOutputStream o) {
		outputStream = o;
	}

	public void setUsername(String s) {
		username = s;
	}
	
	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public String getUsername() {
		return username;
	}

	public int getUserID() {
		return userID;
	}

	public void sendMessage(Message message){
	        try {
					outputStream.writeObject(message);
		        	outputStream.flush();
		        	Server.LOG.info("<"+userID+">Sending... MessageType: ["+message.getMessageType()+"] Content: ("+message.getContent()+") to " + username);
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("<"+userID+"> Could not send message to " + username +" with exeption: "+ ex);
		        }	
	}
	
	public boolean equals(String str) {
		if (username == str) return true;
		return false;
	}

	@Override
	public String toString() {
		return (username);
	}

}