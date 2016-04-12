package nschat.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import nschat.Program;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.SequenceNumbers;

import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.text.DefaultCaret;

/**
 * Basic GUI that can print any text and accept printed text from the user.
 * @author Bart Meyers
 *
 */
public class BasicGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextArea textArea;
	private JMenuItem menuExit;
	private JButton sendButton;
	private JMenuItem menuSettings;
	private SettingsGUI frame;
	private JScrollPane scrollPane;
	
	private Program program;
	
	/**
	 * Launch the application for testing purposes.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BasicGUI frame = new BasicGUI();
					frame.pack();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!textField.getText().isEmpty()) {
				String text = textField.getText();
				textField.setText("");
				
				//printText(text);
				
				short seq = SequenceNumbers.get(PacketType.TEXT);
				Packet p = new Packet(PacketType.TEXT, (byte) 0, seq, (short) 0, null);
				p.setData(text);
				//program.getConnection().getSendingBuffer().add(seqSet, seq, p.pack());
				
				System.out.println("Sending text, SEQ: " + p.getSeqNumber() + ", Data: " + p.getDataAsString());

				program.getConnection().getSendingBuffer().add(PacketType.TEXT, seq, p.pack());
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public BasicGUI(Program program) {
		this.program = program;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat21");
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("Menu");
		menuBar.add(mnFile);
		
		menuSettings = new JMenuItem("Settings");
		mnFile.add(menuSettings);
		
		menuExit = new JMenuItem("Exit");
		mnFile.add(menuExit);
		
		getContentPane().setLayout(new MigLayout("", "[grow][grow][][][][][][][][][][][][][]", "[grow][][][][][][][][][]"));
		
		textArea = new JTextArea();
		//textArea.setDropMode(DropMode.INSERT);
		textArea.setEditable(false);
		//textArea.setLineWrap(true);
		//textArea.setWrapStyleWord(true);
		//scrollPane.setViewportView(textArea);
		
		scrollPane = new JScrollPane(textArea);
		getContentPane().add(scrollPane, "cell 0 0 15 9,grow");
		scrollPane.setPreferredSize(new Dimension(500,300));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
		scrollPane.setWheelScrollingEnabled(true);
		
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		textField = new JTextField();
		getContentPane().add(textField, "cell 0 9 14 1,grow");
		textField.setColumns(10);
		
		sendButton = new JButton("Send");
		getContentPane().add(sendButton, "cell 14 9 1 1,grow");
		
		sendButton.addActionListener(new Listener());
		textField.addActionListener(new Listener());

		menuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getProgram().stop();
			}
		});
		
		menuSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				runSettings();
			}
		});
		
	}
	
	//TODO change such that messages are ordered by sending time.
	/**
	 * Print line of text on the textArea.
	 * @param text
	 */
	public void printText(String text) {
		textArea.append(text + "\n");
	}
	
	/**
	 * Print a line of text with the name of the sender.
	 * @param text
	 * @param name
	 */
	public void printText(String text, String name) {
		textArea.append("<" + name + "> " + text);
	}
	
	public Program getProgram() {
		return program;
	}
	
	public BasicGUI getGUI() {
		return this;
	}
	
	public void runSettings() {
		try {
			setEnabled(false);
			frame = new SettingsGUI(getProgram(), getGUI());
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e)
			    {
			        setEnabled(true);
			    }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if (frame != null) {
			frame.dispose();
			dispose();
		}
	}
}