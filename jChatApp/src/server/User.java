package server;

import java.io.ObjectOutputStream;

public class User {

	
	private String username;
	private int userID;
	private ObjectOutputStream outputStream; 
	private boolean userIsAdmin;
	private boolean userIsMuted;		
			
	public User (String n){
		username = n;
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

	public boolean isUserAdmin() {
		return userIsAdmin;
	}

	public boolean isUserMuted() {
		return userIsMuted;
	}

	public void sendMessage(Message message){
	        try {
					outputStream.writeObject(message);
		        	outputStream.flush();
		        	Server.LOG.warning("User: Send message to " + username +"<"+userID+">" +" succssesfully");
		        } 
		        catch (Exception ex) {
		        	Server.LOG.warning("MessageHandler: Error: Could not send message to " + username +"<"+userID+">" +" with exeption: "+ ex);
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