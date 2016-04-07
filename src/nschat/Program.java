package nschat;

import java.awt.EventQueue;
import java.io.IOException;

import nschat.multicasting.Connection;
import nschat.ui.BasicGUI;

/**
 * Main class for the application.
 * @author Karanum
 */
public class Program {

	public static void main(String[] args) {
		new Program();
	}
	
	private Connection conn;
	
	public Program() {
		System.out.println("Setting up connection");
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
		
		/*
		 * Creating the GUI window
		 */
		BasicGUI frame = new BasicGUI(this);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
					System.out.println("Window created!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
		 * Thread cleanup
		 */
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return conn;
	}

}
