package nschat.tcp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nschat.multicasting.Connection;
import nschat.tcp.Packet.PacketType;

/**
 * List of expected ACKs for each sent packet.
 * @author Karanum
 */
public class AckList {

	private static Map<PacketType, Map<Short, AckList>> instances
							= new HashMap<PacketType, Map<Short, AckList>>();
	
	private short seq;
	private PacketType type;
	private List<Integer> remaining;
	private Connection conn;
	
	private AckList(Connection conn, PacketType type, short seq, Collection<Integer> knownClients) {
		this.conn = conn;
		seq = this.seq;
		type = this.type;
		
		remaining = new ArrayList<Integer>();
		remaining.addAll(knownClients);
		
		if (!instances.containsKey(type)) {
			instances.put(type, new HashMap<Short, AckList>());
		}
		instances.get(type).put(seq, this);
		
		conn.getTimeout().addPacket(type, seq);
	}
	
	/**
	 * Marks the packet as having been acknowledged by the specified client.
	 * @param client The client that acknowledged the packet
	 */
	public void setAcknowledged(int client) {
		remaining.remove(client);
		if (remaining.isEmpty()) {
			conn.getTimeout().removePacket(type, seq);
		}
	}
	
	public static void createInstance(Connection conn, PacketType type, short seq) {
		//TODO Make this get the list of all clients from the forwarding table
		//Collection<Integer> knownClients = conn.getRouting().getForwardingTable().getDestinations();
	}
	
	public static AckList getInstance(PacketType type, short seq) {
		if (!instances.containsKey(type)) {
			return null;
		}
		return instances.get(type).get(seq);
	}
	
}
