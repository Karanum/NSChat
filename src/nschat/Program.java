package nschat;

import java.io.IOException;

import nschat.multicasting.Connection;

/**
 * Main class for the application.
 * @author Karanum
 */
public abstract class Program {

	public static void main(String[] args) {
		System.out.println("Setting up connection");
		Connection conn;
		try {
			conn = new Connection();
		} catch (IOException e) {
			System.err.println("Could not set up connection: The port number might be in use");
			//UI.error("Port already in use, please restart your system!");
			return;
		}
		
		Thread t = new Thread(conn);
		t.start();
		System.out.println("Connection established!");
		
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
