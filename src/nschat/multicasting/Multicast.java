package nschat.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;


public class Multicast {

	private final int BUFFER_LENGTH = 1028;
	private final String GROUP_ADDRESS = "227.21.137.0";
	private int groupPort = 8637;
	private ReceivingBuffer receivingBuffer; 
	private NetworkInterface nInterface;

	MulticastSocket mcsocket;
	InetAddress group = null;
	
	public Multicast(ReceivingBuffer receivingBuffer) throws IOException {
		//System.out.println(NetworkInterface.getNetworkInterfaces().nextElement());
		mcsocket = new MulticastSocket(groupPort);
		this.receivingBuffer = receivingBuffer; 
	}
	
	public void joinGroup() {
		try {
			/*if (System.getProperty("os.name").contains("Linux")) {
				mcsocket.setNetworkInterface(NetworkInterface.getNetworkInterfaces().nextElement()); //TODO change such that it can be chosen in GUI
			}*/
			if (nInterface == null) {
				System.out.println("Interface not declared!");
			} else {
				group = InetAddress.getByName(GROUP_ADDRESS);
				System.out.println("Connected with Interface: " + mcsocket.getNetworkInterface().getDisplayName());
				mcsocket.joinGroup(group);
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public void leaveGroup() {
		
		try {
			mcsocket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public DatagramPacket makeDgramPacket(byte[] bytes) {
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
				group, groupPort);	
		return packet;
	}
	
	public void sendDatagram(DatagramPacket dgram) {
		try {
			mcsocket.send(dgram);
			Thread.sleep(1000);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public byte[] receiveDatagram() {
		while (true) {
			byte[] bytes = new byte[BUFFER_LENGTH];
			DatagramPacket received = new DatagramPacket(bytes, bytes.length);
			try {
				mcsocket.receive(received);
				byte[] data = received.getData();
				byte[] actualData = Arrays.copyOfRange(data, 0, received.getLength());
				receivingBuffer.add(actualData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getPort() {
		return groupPort;
	}
	
	public void setPort(int port) {
		groupPort = port;
	}
	
	public MulticastSocket getSocket() {
		return mcsocket;
	}
	
	/*public void setInterface(NetworkInterface ni) {
		if (group == null) {
			leaveGroup();
		}
		nInterface = ni;
		System.out.println("Succesfullty changed interface to: " + nInterface.getDisplayName());
		joinGroup();
	}*/
}
