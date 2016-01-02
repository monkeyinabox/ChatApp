package server;

public class User {

	
	private String username;
	private int userID;
	private boolean userIsAdmin;
	private boolean userIsMuted;		
			
	public User (String n){
		username = n;
		userID = this.hashCode();
		
	}

	public String getUsername() {
		return username;
	}

	public int getUserID() {
		return userID;
	}

	public boolean isUserIsAdmin() {
		return userIsAdmin;
	}

	public boolean isUserIsMuted() {
		return userIsMuted;
	}
	
}
