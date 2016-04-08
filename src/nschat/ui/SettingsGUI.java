package nschat.ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import nschat.Program;

import java.awt.Button;
import java.awt.Component;

import javax.swing.JLabel;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.JTextField;

public class SettingsGUI extends JFrame {

	private JPanel contentPane;
	private JTextField nameField;
	private JTextField portField;
	private Program program;
	private BasicGUI gui;
	//private Choice choice;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsGUI frame = new SettingsGUI(null, null);
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	
	private class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String a = null;
			String b = null;
			if (e.getActionCommand().equals("save")) {
				getProgram().setName(nameField.getText());
				//getProgram().getConnection().getMulticast().setPort(portField.get); //TODO change
				/*try {
					getProgram().getConnection().getMulticast().setInterface(NetworkInterface.getByName(choice.getSelectedItem()));
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				a = nameField.getText();
				b = portField.getText(); //TODO give it to the correct method
			//	c = choice.getSelectedItem(); //TODO give to correct method
				System.out.println("saved settings:\nname: " + a + ", port: " + b);
			}
			gui.setEnabled(true);
			getGUI().dispose();
		}
	}
	
	/*private class ChoiceListener implements ItemListener{

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			System.out.println("choice action");
		}
		
	}*/

	/**
	 * Create the frame.
	 */
	public SettingsGUI(Program program, BasicGUI gui) {
		setAlwaysOnTop(true);
		setResizable(false);
		this.program = program;
		this.gui = gui;
		
		setTitle("Settings");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][][][grow][][][][][][][]", "[][][][][][][]"));
		
		JLabel userNameLabel = new JLabel("UserName:");
		contentPane.add(userNameLabel, "cell 1 0");
		
		nameField = new JTextField();
		if (getProgram().getName() == null) {
			nameField.setText("UserName");
		} else {
			nameField.setText(getProgram().getName());
		}
		contentPane.add(nameField, "cell 2 0 9 1,growx");
		nameField.setColumns(10);
		
		JLabel portLabel = new JLabel("PortNumber:");
		contentPane.add(portLabel, "cell 1 1");
		
		portField = new JTextField();
		portField.setToolTipText("Enter the portnumber that you want to use");
		if (program.getConnection().getMulticast().getPort() == 0) {
			portField.setText("PortNumber");
		} else {
			portField.setText((new Integer(program.getConnection().getMulticast().getPort()).toString()));
		}
		contentPane.add(portField, "cell 2 1 2 1,growx");
		portField.setColumns(10);
		/*
		choice = new Choice();
		contentPane.add(choice, "cell 2 2 2 1,growx");
		*/
		
		Button resetButton = new Button("Cancel");
		contentPane.add(resetButton, "cell 1 3");
		
		Button saveButton = new Button("Save");
		contentPane.add(saveButton, "cell 10 3");
		
		resetButton.setActionCommand("reset");
		saveButton.setActionCommand("save");
		
		resetButton.addActionListener(new ButtonListener());
		saveButton.addActionListener(new ButtonListener());
	}
	
	public SettingsGUI getGUI() {
		return this;
	}
	
	public Program getProgram() {
		return program;
	}
}
