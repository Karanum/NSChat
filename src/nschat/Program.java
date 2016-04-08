package nschat;

import java.awt.EventQueue;
import java.io.IOException;

import nschat.multicasting.Connection;
import nschat.tcp.Packet;
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
	private BasicGUI ui;
	private static boolean running = false;
	
	/**
	 * Starts a new instance of the program, can only be called once.
	 */
	public Program() {
		if (running) { return; }	//Guard in case someone accidentally makes a new Program
		
		System.out.println("Setting up connection");
		try {
			conn = new Connection(this);
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
		ui = new BasicGUI(this);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ui.pack();
					ui.setLocationRelativeTo(null);
					ui.setVisible(true);
					System.out.println("Window created!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
		 * Main loop
		 */
		running = true;
		while (running) {
			long time = System.currentTimeMillis();
			conn.receive();		//Check for incoming messages
			conn.send();		//Check for outgoing messages
			long diff = System.currentTimeMillis() - time;
			if (diff < 100) {
				try {
					Thread.sleep(100 - diff);
				} catch (InterruptedException e) { }
			}
		}
		
		/*
		 * Thread cleanup
		 */
		ui.setVisible(false);
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Shutdown");
	}
	
	/**
	 * Returns the connection of the program.
	 * @return A Connection object
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Returns the user interface of the program.
	 * @return A BasicGUI object
	 */
	public BasicGUI getUI() {
		return ui;
	}
	
	/**
	 * Tells the program to stop running.
	 */
	public void stop() {
		running = false;
	}

}
