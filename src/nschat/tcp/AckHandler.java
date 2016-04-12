package nschat.tcp;

import nschat.multicasting.Connection;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import java.util.Map;
import java.util.Optional;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for handling acknowledgements, retransmissions and the like.
 * Must be obtained with the static getHandler method.
 * @author Karanum
 */
public abstract class AckHandler {

	private static Map<PacketType, AckHandler> handlers = new HashMap<PacketType, AckHandler>();
	private static final int FAST_RETRANSMIT_COUNT = 3;
	
	private Map<Integer, List<Short>> seenSeqs;
	private Map<Integer, Short> lastAcked;
	private Map<Integer, Short> lastSeenAck;
	private Map<Integer, Integer> retransmitCount;
	
	public AckHandler() {
		seenSeqs = new HashMap<Integer, List<Short>>();
		lastAcked = new HashMap<Integer, Short>();
		lastSeenAck = new HashMap<Integer, Short>();
		retransmitCount = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Generates an acknowledgement packet for a received packet.
	 * @param p The received packet
	 * @return Optional containing the acknowledgement packet, empty if the packet should not be acknowledged
	 */
	public Optional<Packet> createAck(Packet p) {
		InetAddress senderAddr = p.getSenderAddress();
		try {
			if (p.getData().length == 0 || senderAddr.equals(InetAddress.getLocalHost()) || senderAddr.isLoopbackAddress()) {
				return Optional.empty();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		short ack = p.getSeqNumber();
		int sender = p.getSender();
		if (lastAcked.containsKey(sender)) {
			short lastAck = lastAcked.get(sender);
			if (ack != (short) (lastAck + 1)) {
				ack = lastAck;
				if (!seenSeqs.containsKey(sender)) {
					seenSeqs.put(sender, new ArrayList<Short>());
				}
				seenSeqs.get(sender).add(ack);
			}
		}
		
		short seq = SequenceNumbers.get(p.getPacketType());
		return Optional.of(new Packet(p.getPacketType(), Packet.ACK_FLAG, seq, ack, p.getSenderAddress()));
	}
	
	/**
	 * Handles a received acknowledgement packet.
	 * @param conn The connection object
	 * @param p The received packet
	 */
	public void handleAck(Connection conn, Packet p) {		
		if (!p.isAck()) {
			return;
		}

		int sender = p.getSender();
		short ack = p.getAckNumber();
		
		if (!lastSeenAck.containsKey(sender) || ack == (short) (lastSeenAck.get(sender) + 1)) {
			retransmitCount.put(sender, 0);
			if (!seenSeqs.containsKey(sender) || seenSeqs.get(sender).size() == 0) {
				lastSeenAck.put(sender, ack);
			} else {
				setLastSeenAck(sender, ack);
			}
		} else {
			handleMissingPacket(conn, p);
		}
	}
	
	/**
	 * Sets the last seen acknowledgement number.
	 * @param sender The sender of the acknowledgement
	 * @param ack The acknowledgement number
	 */
	private void setLastSeenAck(int sender, short ack) {
		short nextAck = (short) (ack + 1);
		boolean found = true;
		while (found) {
			found = false;
			Iterator<Short> iter = seenSeqs.get(sender).iterator();
			while (iter.hasNext()) {
				short s = iter.next();
				if (s == nextAck) {
					seenSeqs.get(sender).remove(s);
					found = true;
				}
			}
			if (found) {
				nextAck = (short) (nextAck + 1);
			}
		}
		lastSeenAck.put(sender, (short) (nextAck - 1));
	}
	
	/**
	 * Handles what should happen when a sequence number is missing.
	 * @param conn The connection object
	 * @param p The last received packet
	 */
	private void handleMissingPacket(Connection conn, Packet p) {
		int sender = p.getSender();
		if (!retransmitCount.containsKey(sender)) {
			retransmitCount.put(sender, 1);
			return;
		}
		
		PacketType type = p.getPacketType();
		short ack = (short) (p.getAckNumber() + 1);
		
		int rCount = retransmitCount.get(sender) + 1;
		retransmitCount.put(sender, rCount);
		if (rCount % FAST_RETRANSMIT_COUNT == 0) {
			byte[] data = conn.getSendingBuffer().get(type, ack);
			if (data != null) {
				Packet packet = new Packet(data);
				packet.setRecipient(p.getSenderAddress());
				conn.getSendingBuffer().add(packet.pack());
			}
		}
	}
	
	/**
	 * Returns the unique AckHandler for the specified packet type.
	 * @param type The packet type
	 * @return A AckHandler object
	 */
	public static AckHandler getHandler(PacketType type) {
		
		if (!handlers.containsKey(type)) {
			handlers.put(type, new AckHandler() {});
		}
		return handlers.get(type);
	}
	
}
