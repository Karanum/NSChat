package nschat.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nschat.exception.PacketFormatException;
import nschat.multicasting.SendingBuffer;
import nschat.tcp.Packet;
import nschat.tcp.SequenceNumberSet;
import nschat.tcp.Packet.PacketType;

/**
 * Distance vector routing protocol
 * @author Pieter Jan
 *
 */
public class BasicRoutingProtocol {

	private SequenceNumberSet set;
	private SendingBuffer sendingBuffer;
	private ForwardingTable forwardingTable;
	
	
	public BasicRoutingProtocol() {
		set = new SequenceNumberSet();
		sendingBuffer = new SendingBuffer();
		forwardingTable = new ForwardingTable();
	}
	
	//TODO Finish function
	public void receivePacket(Packet packet) {
		int rtt = getRTT(packet);
		byte[] bytes = packet.getData();
		List<BasicRoute> routes = getForwardingTable(bytes);
		
	}
	//TODO Finish function
	public void makeRoute(int dest, int rtt, int nextHop) {
		BasicRoute route = new BasicRoute(dest, rtt, nextHop);
	}
	
	public int getRTT(Packet packet) {
		byte[] old = sendingBuffer.get(set, packet.getAckNumber());
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
	
	public void sendPacket() {
		short seq = set.get();
		
		Packet packet = new Packet(PacketType.ROUTING, (byte) 0, seq, (short) 0, null);
		Collection<BasicRoute> routes = forwardingTable.getRoutes();
		byte[] data = new byte[routes.size() * 12];
		
		int i = 0;
		for (BasicRoute route : routes) {
			System.arraycopy(route.getBytes(), 0, data, i * 12, 12);
			++i;
		}
		packet.setData(data);
		
		sendingBuffer.add(set, seq, packet.pack());
	}
	//TODO Finish function
	public List<BasicRoute> getForwardingTable(byte[] bytes) {
		
		return null;
	}

}
