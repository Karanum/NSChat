package nschat.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class Multicast {

	private final int BUFFER_LENGTH = 1028;
	private final String GROUP_ADDRESS = "227.21.137.0";
	private final int GROUP_PORT = 8637;

	MulticastSocket mcsocket;
	InetAddress group;
	
	public Multicast() throws IOException {
		mcsocket = new MulticastSocket(GROUP_PORT);
	}
	
	public void joinGroup() {
		try {
			group = InetAddress.getByName(GROUP_ADDRESS);
			mcsocket.joinGroup(group);
		} catch (UnknownHostException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void leaveGroup() {
		
		try {
			mcsocket.leaveGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public DatagramPacket makeDgramPacket(byte[] bytes) {
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
				group, GROUP_PORT);
				
		return packet;
		
	}
	
	public void sendDatagram(DatagramPacket dgram) {
		System.err.println("Sending " + dgram.getData().length + " bytes to " + 
			dgram.getAddress() + ":" + dgram.getPort());
		
		while (true) {
			System.out.print(".");
			try {
				mcsocket.send(dgram);
				Thread.sleep(1000);
			} catch (InterruptedException | IOException e) {
				// TODO Which exception first?
				e.printStackTrace();
			}
		}
	}
	
	public void receiveDatagram() {
		byte[] bytes = new byte[BUFFER_LENGTH];
		DatagramPacket received = new DatagramPacket(bytes, bytes.length);
		try {
			mcsocket.receive(received);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
