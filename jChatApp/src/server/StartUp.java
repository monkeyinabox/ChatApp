package server;

import java.io.IOException;

public class StartUp {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException {
		
		Server newServer = new Server();
		newServer.jChatAppServer();
		

	}

}
