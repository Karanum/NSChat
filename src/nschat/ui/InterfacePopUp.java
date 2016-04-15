package nschat.ui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
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
	private Map<String, NetworkInterface> interfaces;
	
	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("button action: " + e.getActionCommand());
			try {
				getProgram().getConnection().getMulticast().setInterface(interfaces.get(choice.getSelectedItem()));
				Thread.sleep(500);
				finished = true;
				dispose();
				manager.removeKeyEventDispatcher(dispatcher);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Create a pop-up frame that can not be closed.
	 * The frame asks for a network interface to be selected.
	 * @param program The Program object
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
		interfaces = new HashMap<String, NetworkInterface>();
		try {
			Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
			List<NetworkInterface> netList = new ArrayList<NetworkInterface>();
			while (ni.hasMoreElements()) {
				NetworkInterface ne = ni.nextElement();
				if (ne.isUp()) {
					interfaces.put(ne.getDisplayName(), ne);
					choice.add(ne.getDisplayName());
				}
			}
			for (NetworkInterface network : netList) {
				choice.add(network.getDisplayName());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		button = new Button("Select");
		contentPane.add(button, BorderLayout.SOUTH);
		
		button.addActionListener(new Listener());
//		button.setBackground(Color.LIGHT_GRAY);
//		button.setBorderPainted(false);
		
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
					//manager.removeKeyEventDispatcher(dispatcher);
				}
				return false;
			}
        	
        });	
        manager.addKeyEventDispatcher(dispatcher);
	}

	/**
	 * Returns the Program object.
	 */
	public Program getProgram() {
		return program;
	}
	
	/**
	 * Returns whether the popup window is finished and can be closed.
	 */
	public boolean finished() {
		return finished;
	}
}
