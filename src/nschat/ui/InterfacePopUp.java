package nschat.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nschat.Program;

import java.awt.Choice;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class InterfacePopUp extends JFrame {

	private JPanel contentPane;
	private Program program;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfacePopUp frame = new InterfacePopUp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public InterfacePopUp(Program program) {
		this.program = program;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Choice choice = new Choice();
		contentPane.add(choice, BorderLayout.CENTER);
		
		try {
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
			while (ni.hasMoreElements()) {
				choice.add(ni.nextElement().getDisplayName());
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println(getProgram().getConnection().getMulticast().getSocket().getNetworkInterface().getDisplayName());
			choice.select(getProgram().getConnection().getMulticast().getSocket().getNetworkInterface().getDisplayName());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Program getProgram() {return program;}
}
