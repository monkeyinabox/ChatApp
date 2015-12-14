package client;


import java.io.*;
import java.net.*;

public final class Client {

	static final int PORT=1337;
	
	private Client() {}

	public static void main(String[] args) throws IOException {

		// Get the servers IP Address
		InetAddress addr = InetAddress.getByName("localhost");
		System.out.println("addr = " + addr);

		// Get the servers port number
		Socket socket = new Socket(addr, PORT);

		// Keep everything in a try-finally to make sure that the socket is closed
		try {
			System.out.println("socket = " + socket);
			System.out.println("READY to work ");

			// Prepare the in stream
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Output is automatically flushed by PrintWriter
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

			// Keyboard Input
			BufferedReader KeyboardIn = new BufferedReader(new InputStreamReader(System.in));

			while (true) {
				// What to say to server?
				System.out.print("What to say to server: ");
				String what = KeyboardIn.readLine();

				// Say it
				out.println(what);

				// Get the echo
				String str = in.readLine();
				System.out.println("From server: " + str);
				System.out.println("Enter END to stop");

				if (what.equals("END")) break; // The server is waiting for "END"
			}

		}
		finally{
			System.out.println("closing...");
			socket.close();
		}
	}
}