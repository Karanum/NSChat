package nschat.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Arrays;


public class Multicast {

	private static final int BUFFER_LENGTH = 4096;
	private static final String GROUP_ADDRESS = "227.21.137.0";
	private int groupPort = 8637;
	private ReceivingBuffer receivingBuffer; 
	private NetworkInterface nInterface;

	MulticastSocket mcsocket;
	InetAddress group = null;
	
	public Multicast(ReceivingBuffer receivingBuffer) throws IOException {
		mcsocket = new MulticastSocket(groupPort);
		this.receivingBuffer = receivingBuffer; 
	}
	
	/**
	 * Joins the MultiSocket network using the group_address.
	 */
	public void joinGroup() {
		try {
			if (nInterface == null) {
				System.out.println("Interface not declared!");
			} else {
				mcsocket.setNetworkInterface(nInterface);
				group = InetAddress.getByName(GROUP_ADDRESS);
				System.out.println("Connected with Interface: " + 
				    mcsocket.getNetworkInterface().getDisplayName());
				mcsocket.joinGroup(group);
			}
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * Leaves the MultiCastSocket group.
	 */
	public void leaveGroup() {
		try {
			mcsocket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts the the given byte array into a DatagramPacket and returns it.
	 * @param bytes The given byte array
	 * @return The corresponding DatagramPacket of the byte array
	 */
	public DatagramPacket makeDgramPacket(byte[] bytes) {
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, 
				  group, groupPort);	
		return packet;
	}
	
	/**
	 * Sends the given DatagramPacket to the MultiSocket group.
	 * @param dgram
	 */
	public void sendDatagram(DatagramPacket dgram) {
		try {
			mcsocket.send(dgram);
			Thread.sleep(1000);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loop for receiving packets, calling other functions to process the received packet.
	 */
	public void receiveDatagram() {
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
	
	
	/**
	 * Returns the port number of the MultiSocket group.
	 */
	public int getPort() {
		return groupPort;
	}
	
	/**
	 * Sets the port of the MultiSocket group with the given port number.
	 * @param port The port number the MultiSocket group will have
	 */
	public void setPort(int port) {
		groupPort = port;
	}
	
	/**
	 * Returns the MultiCast Socket.
	 */
	public MulticastSocket getSocket() {
		return mcsocket;
	}
	
	/**
	 * Sets the network interface with the given network interface.
	 * @param ni The network interface that will be used
	 */
	public void setInterface(NetworkInterface ni) {
		nInterface = ni;
		joinGroup();
	}
}
