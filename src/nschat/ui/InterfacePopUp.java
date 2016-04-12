package nschat.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Choice;
import java.awt.Button;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import nschat.Program;

public class InterfacePopUp extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Program program;
	private Choice choice;
	private boolean finished = false;
	private Button button;
	private JButton enterPress;
	private KeyboardFocusManager manager;
	private KeyEventDispatcher dispatcher;
	
	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("button action: " + e.getActionCommand());
			try {
				getProgram().getConnection().getMulticast().setInterface(NetworkInterface.getByName(choice.getSelectedItem()));
				Thread.sleep(500);
				finished = true;
				dispose();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	
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
				choice.add(ni.nextElement().getName());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		button = new Button("Select");
		contentPane.add(button, BorderLayout.SOUTH);
		
		button.addActionListener(new Listener());
		
		enterPress = new JButton();
		enterPress.setVisible(false);
		//getRootPane().setDefaultButton(enterPress);
		
		enterPress.addActionListener(new Listener());
		
		manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		dispatcher =  (new KeyEventDispatcher()  {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("enter pressed");
					//button.setActionCommand("enter pressed");
					enterPress.doClick();
					manager.removeKeyEventDispatcher(dispatcher);
				}
				return false;
			}
        	
        });	
        manager.addKeyEventDispatcher(dispatcher);
	}

	public Program getProgram() {
		return program;
	}
	
	public boolean finished() {
		return finished;
	}
}
