package nschat.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Choice;
import java.awt.Button;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.SwingConstants;

import nschat.Program;

public class InterfacePopUp extends JFrame {

	private JPanel contentPane;
	private Program program;
	private Choice choice;
	
	/**
	 * Create a pop-up frame that can not be closed.
	 * The frame asks for a network interface to be selected.
	 */
	public InterfacePopUp(Program program) {
		this.program = program;
		
		setTitle("Network Interface selector");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JLabel lblSelectTheNetwork = new JLabel("Select the network interface that you want to use for the connection");
		lblSelectTheNetwork.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblSelectTheNetwork, BorderLayout.NORTH);
		
		choice = new Choice();
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
		
		Button button = new Button("Select");
		contentPane.add(button, BorderLayout.SOUTH);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					getProgram().getConnection().getMulticast().setInterface(NetworkInterface.getByName(choice.getSelectedItem()));
					Thread.sleep(500);
					dispose();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public Program getProgram() {
		return program;
	}
}
