package nschat.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Choice;
import net.miginfocom.swing.MigLayout;
import nschat.Program;

import javax.swing.JLabel;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.SwingConstants;

public class InterfaceChoser extends JDialog {

	private Program program;
	private Choice choice;
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		try {
			InterfaceChoser dialog = new InterfaceChoser();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public InterfaceChoser(Program program) {
		this.program = program;
		
		setTitle("Network Interface");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		{
			choice = new Choice();
			getContentPane().add(choice, BorderLayout.CENTER);
			
			try {
				Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
				while (ni.hasMoreElements()) {
					choice.add(ni.nextElement().getDisplayName());
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		{
			JLabel lblSelectNetworkInterface = new JLabel("Select Network Interface from the list");
			lblSelectNetworkInterface.setHorizontalAlignment(SwingConstants.CENTER);
			getContentPane().add(lblSelectNetworkInterface, BorderLayout.NORTH);
		}
		{
			Button button = new Button("Select");
			
			getContentPane().add(button, BorderLayout.SOUTH);
			
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						getProgram().getConnection().getMulticast().setInterface(NetworkInterface.getByName(choice.getSelectedItem()));
						getProgram().notifyAll();
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			});
		}
	}
	
	public Program getProgram() {
		return program;
	}

}
