package nschat.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nschat.exception.PacketFormatException;
import nschat.multicasting.SendingBuffer;
import nschat.tcp.Packet;
import nschat.tcp.SequenceNumbers;
import nschat.tcp.Packet.PacketType;

/**
 * Distance-vector routing protocol.
 * @author Pieter Jan
 *
 */
public class BasicRoutingProtocol {

	private SendingBuffer sendingBuffer;
	private ForwardingTable forwardingTable;
	
	// MAP SENDER -> RTT
	private Map<Integer, Integer> senderRTT;
	
	
	public BasicRoutingProtocol() {
		sendingBuffer = new SendingBuffer();
		forwardingTable = new ForwardingTable(this, (new Packet()).getSender());
		senderRTT = new HashMap<Integer, Integer>();
	}

	/**
	 * Allows to receive a packet, ensued by getting the RTT of the sender, and updating its
	 * own forwardingTable.
	 * @param packet The packet with the routing data.
	 */
	public void receivePacket(Packet packet) {
		int rtt = getRTT(packet);
		setSenderRTT(rtt, packet.getSender());
		
		byte[] bytes = packet.getData();
		List<BasicRoute> routes = getForwardingTable(bytes);
		forwardingTable.updateTable(routes, packet.getSender());
	}
	
	/**
	 * Calculates and returns the RTT value of the sender from the given packet.
	 * @param packet The sender's packet
	 * @return The RTT value of the sender
	 */
	public int getRTT(Packet packet) {
		byte[] old = sendingBuffer.get(PacketType.ROUTING, packet.getAckNumber());
		Packet oldPacket;
		try {
			oldPacket = new Packet(old);
		} catch (PacketFormatException e) { 
			return -1;
		}
		long sendTime = oldPacket.getTimestamp();
		int rtt = (int) (System.currentTimeMillis() - sendTime);
		return rtt;
	}
	
	/**
	 * Converts this forwardingTable into a Routing packet and add it to the sendingBuffer.
	 */
	public void sendPacket() {
		short seq = SequenceNumbers.get(PacketType.ROUTING);
		
		Packet packet = new Packet(PacketType.ROUTING, (byte) 0, seq, (short) 0, null);
		Collection<BasicRoute> routes = forwardingTable.getRoutes();
		byte[] data = new byte[routes.size() * 12];
		
		int i = 0;
		for (BasicRoute route : routes) {
			System.arraycopy(route.getBytes(), 0, data, i * 12, 12);
			++i;
		}
		packet.setData(data);
		
		sendingBuffer.add(PacketType.ROUTING, seq, packet.pack());
	}
	
	/*
	 * Returns a list of the received forwarding tables.
	 */
	private List<BasicRoute> getForwardingTable(byte[] bytes) {
		List<BasicRoute> basicRoutes = new ArrayList<BasicRoute>();
		
		for (byte[] chunk : getBytesChunk(bytes, 12)) {
			int dest = (chunk[0] << 24) + (chunk[1] << 16) + (chunk[2] << 8) + chunk[3];
			int cost = (chunk[4] << 24) + (chunk[5] << 16) + (chunk[6] << 8) + chunk[7];
			int hop = (chunk[8] << 24) + (chunk[9] << 16) + (chunk[10] << 8) + chunk[11];
			basicRoutes.add(new BasicRoute(dest, cost, hop));
		}
		return basicRoutes;
	}
	
	/*
	 * Divide the given byte array into chunks of the given chunk-size.
	 */
	private List<byte[]> getBytesChunk(byte[] bytes, int chunksize) {
		List<byte[]> bytesList = new ArrayList<byte[]>();
		
		int begin = 0;
		while (begin < bytes.length) {
			int end = Math.min(chunksize, begin + chunksize);
			bytesList.add(Arrays.copyOfRange(bytes, begin, end));
			begin += chunksize;
		}
		return bytesList;
	}
	
	/*
	 * Sets the given RTT value for the given sender in the senderRTT map.
	 */
	private void setSenderRTT(int rtt, int sender) {
		if (senderRTT.containsKey(sender)) {
			int currentRTT = senderRTT.get(sender);
			senderRTT.put(sender, (rtt + currentRTT) / 2);
		} else {
			senderRTT.put(sender, rtt);
		}
	}
	
	/**
	 * Returns a map containing all senders with their corresponding RTT cost.
	 */
	public Map<Integer, Integer> getSenderRTT() {
		return senderRTT;
	}
	
}
