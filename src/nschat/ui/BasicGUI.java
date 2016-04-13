package nschat.ui;

import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import nschat.Program;
import nschat.multicasting.SendingBuffer;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.DropMode;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;

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
	private JEditorPane editorPane;
	private HTMLDocument doc;			//TODO might change
	private JMenuItem menuSendFile;
	private StringBuffer text = new StringBuffer("<html><body>\n");
	
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
				
//				if (text.equalsIgnoreCase("Filefilefile")) {
//					SendingBuffer buffer = program.getConnection().getSendingBuffer();
//					program.getConnection().getFileHandler().sendFile(buffer, "D:\\reuniclus.png");
//					System.out.println("SENDING A FILE WHOOAAAAA");
//				}
				
				short seq = SequenceNumbers.get(PacketType.TEXT);
				Packet p = new Packet(PacketType.TEXT, (byte) 0, seq, (short) 0, null);
				p.setData(text, getProgram().getSecurity());
				//program.getConnection().getSendingBuffer().add(seqSet, seq, p.pack());
				
				System.out.println("Sending text, SEQ: " + p.getSeqNumber() + ", Data: " + p.getDataAsString());

				program.getConnection().getSendingBuffer().add(PacketType.TEXT, seq, p.pack());
			}
		}
	}
	
	private class SendFileListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
		    
		    int returnVal = chooser.showOpenDialog(getParent());
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       System.out.println("You chose to send this file: " +
		            chooser.getSelectedFile().getName());
		       getProgram().getConnection().getFileHandler().sendFile(chooser.getSelectedFile().getPath());
		       //TODO send the file to where it is send!
		    }
		}
		
	}
	
	private class LinkListener implements HyperlinkListener {
		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if(Desktop.isDesktopSupported()) {
				    try {
						Desktop.getDesktop().open(new File(e.getURL().getPath()));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
				}
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
		
		menuSendFile = new JMenuItem("Send File");
		mnFile.add(menuSendFile);
		
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
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		
		doc = (HTMLDocument) editorPane.getDocument();
		
		scrollPane = new JScrollPane(editorPane);
		getContentPane().add(scrollPane, "cell 0 0 15 9,grow");
		scrollPane.setPreferredSize(new Dimension(500,300));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
		scrollPane.setWheelScrollingEnabled(true);
		
		DefaultCaret paneCaret = (DefaultCaret)editorPane.getCaret();
		paneCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
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
		
		menuSendFile.addActionListener(new SendFileListener());
		
		setVisible(true);
		
		textField.requestFocusInWindow();
		
		editorPane.addHyperlinkListener(new LinkListener());
		
	}
	
	//TODO change such that messages are ordered by sending time.
	/**
	 * Print line of text on the textArea.
	 * @param text
	 */
	public void printText(String text) {
		if (text.indexOf(":(") != -1) {
			parseEmote(text);
		} else {
			textArea.append(text + "\n");
			appendString("<font>" + text + "</font><br>");
		}
//		try {
//			doc.insertString(doc.getLength() , text + "\n", new SimpleAttributeSet());
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//printFile("a");
	}
	
	/**
	 * Print a line of text with the name of the sender.
	 * @param text
	 * @param name
	 */
	public void printText(String text, String name) {
		textArea.append("<" + name + "> " + text + "\n");
		appendString("<font>" +  "<" + name + "> " + text + "</font><br>");

//		try {
//			doc.insertString(doc.getLength() , "<" + name + "> " + text + "\n", new SimpleAttributeSet());
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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
	
	/**
	 * Prints a link to the file.
	 * @param filePath
	 */
	public void printFile(Path filePath, String fileName) {
		appendString("<a href=\"file:///" + filePath  + "\"> " + fileName + "</a><br>");
	}
	
	private void appendString(String string) {
		text.append(string);
		editorPane.setText(text.toString());
	}
	
	public void parseEmote(String message) {
		String folder = ((new File("")).getAbsolutePath()).replace("\\", "/").replace(" ", "%20");
		String filePath = "file:///" + folder + "/images/dislike.png";
//		String filePath = "file:///C:/$SSHOME/ss%20eclipse/workspace/NSChat/images/dislike.png";
		System.out.println(filePath);
		printEmote(message, filePath);
	}
	
	public void printEmote(String message, String filePath) {
		String imgTag = "<img src=\"" + filePath + "\"/>";
//		appendString("<img src=\"" + filePath + "\"/><br>");
		String result = message.replace(":(", imgTag);
		System.out.println("result: " + result);
		appendString(result + "<br>");
		
	}
}