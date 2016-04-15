package nschat.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;
import nschat.Program;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.SequenceNumbers;

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
	private JMenuItem menuSendFile;
	private boolean tooltips = true;
	private StringBuffer stringBuffer = new StringBuffer("<html><body>\n");
	
	private Program program;
	
	private class Listener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if (!textField.getText().isEmpty()) {
				String text = textField.getText();
				textField.setText("");
				
				short seq = SequenceNumbers.get(PacketType.TEXT);
				Packet p = new Packet(PacketType.TEXT, (byte) 0, seq, (short) 0, null);

				String data = program.getName() + ";" + text;

//				p.setData(data);
				p.setData(data, getProgram().getSecurity());
				

				System.out.println("Sending text, SEQ: " + p.getSeqNumber() +
						  ", Data: " + p.getDataAsString());

				program.getConnection().getSendingBuffer().add(PacketType.TEXT, seq, p.pack());
			}
		}
	}
	
	private class SendFileListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser chooser = new JFileChooser();
		    
		    int returnVal = chooser.showOpenDialog(getParent());
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
		        System.out.println("You chose to send this file: " +
		            chooser.getSelectedFile().getName());
		        getProgram().getConnection().getFileHandler()
		        	.sendFile(chooser.getSelectedFile().getPath());
		    }
		}
		
	}
	
	private class LinkListener implements HyperlinkListener {
		@Override
		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if (Desktop.isDesktopSupported()) {
				    try {
						Desktop.getDesktop().open(new File(e.getURL().getPath()));
					} catch (IOException e1) {
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
		
		getContentPane().setLayout(new MigLayout("", "[grow][grow][][][][][][][][][][][][][]",
				  "[grow][][][][][][][][][]"));
		
		textArea = new JTextArea();
		//textArea.setDropMode(DropMode.INSERT);
		textArea.setEditable(false);
		//textArea.setLineWrap(true);
		//textArea.setWrapStyleWord(true);
		//scrollPane.setViewportView(textArea);
		
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		
//		doc = (HTMLDocument) editorPane.getDocument();
		
		scrollPane = new JScrollPane(editorPane);
		getContentPane().add(scrollPane, "cell 0 0 15 9,grow");
		scrollPane.setPreferredSize(new Dimension(500, 300));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
		scrollPane.setWheelScrollingEnabled(true);
		
		DefaultCaret paneCaret = (DefaultCaret) editorPane.getCaret();
		paneCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
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
	 * Print line of text on the JEditorPane. 
	 * @param text
	 */
	public void printText(String message) {		
		appendString("<font face=\"verdana\">" + parseEmote(message) + "</font><br>");
	}
	
	/**
	 * Print a line of text with the name of the sender.
	 * @param text
	 * @param name
	 */
	public void printText(String message, String name) {
		appendString("<font face=\"verdana\">" +  "[" + name + "] " 
	        + parseEmote(message) + "</font><br>");
	}
	
	public Program getProgram() {
		return program;
	}
	
	public BasicGUI getGUI() {
		return this;
	}
	
	public void runSettings() {
		setEnabled(false);
		frame = new SettingsGUI(getProgram(), getGUI());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
		        setEnabled(true);
		    }
		});
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
		stringBuffer.append(string);
		editorPane.setText(stringBuffer.toString());
	}
	
	/**
	 * Checks the entire string and if it contains any emoji symbols,
	 * and converts them to the corresponding emoji IMG-tag if so.
	 * @param message The received text message 
	 * @return A text message with every emoji-symbol replaced with its IMG-tag
	 */
	public String parseEmote(String message) {
		String folder = ((new File("")).getAbsolutePath()).replace("\\", "/").replace(" ", "%20");
		
		// GET EMOJI FILE PATH
		String filePathSmile = "file:///" + folder + "/images/smile.png";
		String filePathSad = "file:///" + folder + "/images/sad.png";
		String filePathBigsmile = "file:///" + folder + "/images/bigsmile.png";
		String filePathWink = "file:///" + folder + "/images/wink.png";
		String filePathCry = "file:///" + folder + "/images/cry.png";
		String filePathToung = "file:///" + folder + "/images/toung.png";
		String filePathEksdi = "file:///" + folder + "/images/eksdi.png";
		String filePathBadboy = "file:///" + folder + "/images/badboy.png";
		String filePathKappa = "file:///" + folder + "/images/kappa.png";
		String filePathHeart = "file:///" + folder + "/images/heart.png";
		String filePathPoop = "file:///" + folder + "/images/poop.png";
		String filePathLike = "file:///" + folder + "/images/like.png";
		String filePathDislike = "file:///" + folder + "/images/dislike.png";
		String filePathFire = "file:///" + folder + "/images/fire.png";
		
		// REPLACE THE EMOJI SYMBOL WITH THE IMG-TAG OF THE CORRESPONDING EMOJI
		String result = message;
		result = result.replace(" ", "&nbsp;");
		result = result.replace(":)", "<img src=\"" + filePathSmile + "\"/>");
		result = result.replace(":(", "<img src=\"" + filePathSad + "\"/>");
		result = result.replace(":D", "<img src=\"" + filePathBigsmile + "\"/>");
		result = result.replace(";)", "<img src=\"" + filePathWink + "\"/>");
		result = result.replace(":'(", "<img src=\"" + filePathCry + "\"/>");
		result = result.replace(";(", "<img src=\"" + filePathCry + "\"/>");
		result = result.replace(":P", "<img src=\"" + filePathToung + "\"/>");
		result = result.replace(":p", "<img src=\"" + filePathToung + "\"/>");
		result = result.replace("xd", "<img src=\"" + filePathEksdi + "\"/>");
		result = result.replace("xD", "<img src=\"" + filePathEksdi + "\"/>");
		result = result.replace("XD", "<img src=\"" + filePathEksdi + "\"/>");
		result = result.replace("/badboy", "<img src=\"" + filePathBadboy + "\"/>");
		result = result.replace("/kappa", "<img src=\"" + filePathKappa + "\"/>");
		result = result.replace("<3", "<img src=\"" + filePathHeart + "\"/>");
		result = result.replace("/poop", "<img src=\"" + filePathPoop + "\"/>");
		result = result.replace("/like", "<img src=\"" + filePathLike + "\"/>");
		result = result.replace("/dislike", "<img src=\"" + filePathDislike + "\"/>");
		result = result.replace("/burn", "<img src=\"" + filePathFire + "\"/>");

		return result;
	}
	
	public void setTooltips(boolean tips) {
		this.tooltips = tips;
	}
	
	public boolean getTooltips() {
		return tooltips;
	}
}