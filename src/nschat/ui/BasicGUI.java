package nschat.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
import nschat.Program;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.SequenceNumberSet;

import javax.swing.JScrollPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Basic GUI that can print any text and accept printed text from the user.
 * @author Bart Meyers
 *
 */
public class BasicGUI extends JFrame {
	
	private JTextField textField;
	private JTextArea textArea;
	private JMenuItem menuExit;
	private JButton sendButton;
	
	private SequenceNumberSet seqSet;
	private Program program;
	
	/**
	 * Launch the application for testing purposes.
	 */
	public static void main(String[] args) {
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
	}

	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!textField.getText().isEmpty()) {
				String text = textField.getText();
				printText(text);
				textField.setText("");
				
				short seq = seqSet.get();
				Packet p = new Packet(PacketType.TEXT, (byte) 0, seq, (short) 0, null);
				p.setData(text);
				program.getConnection().getSendingBuffer().add(seqSet, seq, p.pack());
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public BasicGUI(/*Program program*/) {
		this.program = program;
		seqSet = new SequenceNumberSet();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat21");
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("Menu");
		menuBar.add(mnFile);
		
		menuExit = new JMenuItem("Exit");
		mnFile.add(menuExit);
		getContentPane().setLayout(new MigLayout("", "[grow][grow][][][][][][][][][][][][][]", "[grow][][][][][][][][][]"));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 0 15 9,grow");
		scrollPane.setPreferredSize(new Dimension(500,300));
		scrollPane.setAutoscrolls(true);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		textField = new JTextField();
		getContentPane().add(textField, "cell 0 9 14 1,grow");
		textField.setColumns(10);
		
		sendButton = new JButton("Send");
		getContentPane().add(sendButton, "cell 14 9,grow");
		
		sendButton.addActionListener(new Listener());
		textField.addActionListener(new Listener());

		menuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);									//TODO change to proper exit mechanism	
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
}