package nschat.ui;


import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import nschat.Program;
import javax.swing.JCheckBox;

public class SettingsGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField nameField;
	private JTextField portField;
	private Program program;
	private BasicGUI gui;
	private JCheckBox toolTipCheckbox;
	private JButton resetButton;
	private JButton saveButton;
	private JButton encButton;

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
				//getProgram().getConnection().getMulticast().setPort(parseInt(portField.getText())); //TODO change
				/*try {
					getProgram().getConnection().getMulticast().setInterface(NetworkInterface.getByName(choice.getSelectedItem()));
				} catch (SocketException e1) {
					e1.printStackTrace();
				}*/
				
				if (toolTipCheckbox.isSelected()) {
					gui.setTooltips(true);
				} else {
					gui.setTooltips(false);
				}
				
				a = nameField.getText();
				b = portField.getText(); //TODO give it to the correct method
			//	c = choice.getSelectedItem(); //TODO give to correct method
				System.out.println("saved settings:\nname: " + a /*+ ", port: " + b*/); //TODO if port change works change!
			}
			if (e.getActionCommand().equals("enc")) {
				getProgram().getSecurity().setup(true);
			}
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException ex) {
				ex.printStackTrace();
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
		
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
       
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
		portField.setEditable(false);
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
		
		toolTipCheckbox = new JCheckBox("SeeTooltips", gui.getTooltips());
		contentPane.add(toolTipCheckbox, "cell 1 4");
		
		resetButton = new JButton("Cancel");
		contentPane.add(resetButton, "cell 1 5");
		
		saveButton = new JButton("Save");
		contentPane.add(saveButton, "cell 10 5");
		
		encButton = new JButton("Reset encryption");
		contentPane.add(encButton, "cell 1 3");
		
		resetButton.setActionCommand("reset");
		saveButton.setActionCommand("save");
		encButton.setActionCommand("enc");
		
		resetButton.addActionListener(new ButtonListener());
		saveButton.addActionListener(new ButtonListener());
		encButton.addActionListener(new ButtonListener());
	
		if (toolTipCheckbox.isSelected()) {
			portField.setToolTipText("Not supported in this version");
			nameField.setToolTipText("Enter your username here,\n this version does not use this information yet.");
			resetButton.setToolTipText("Reset your recent changes");
			saveButton.setToolTipText("Save the changes you made");
			encButton.setToolTipText("Reset the encryption");
		} else {
			portField.setToolTipText(null);
			nameField.setToolTipText(null);
			resetButton.setToolTipText(null);
			saveButton.setToolTipText(null);
			encButton.setToolTipText(null);
		}
	}
	
	public SettingsGUI getGUI() {
		return this;
	}
	
	public Program getProgram() {
		return program;
	}
}
