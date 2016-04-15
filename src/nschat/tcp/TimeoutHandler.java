package nschat.tcp;

import java.util.HashMap;
import java.util.Map;

import nschat.multicasting.Connection;
import nschat.multicasting.SendingBuffer;
import nschat.tcp.Packet.PacketType;

/**
 * Helper class for packet acknowledgement timeouts. Accessed fully by the AckList class.
 * @author Karanum
 */
public class TimeoutHandler {

	private static final byte MAX_RETRANSMISSIONS = 3;
	private static final long TIMEOUT_INTERVAL = 1000L;
	
	private long lastTime;
	private Connection conn;
	
	private Map<PacketType, Map<Short, Long>> lastSent;
	private Map<PacketType, Map<Short, Byte>> numRetransmissions;
	
	public TimeoutHandler(Connection conn) {
		lastTime = System.currentTimeMillis();
		this.conn = conn;
		
		lastSent = new HashMap<PacketType, Map<Short, Long>>();
		numRetransmissions = new HashMap<PacketType, Map<Short, Byte>>();
	}
	
	public void update() {
		long deltaTime = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		for (PacketType type : lastSent.keySet()) {
			for (Short packet : lastSent.get(type).keySet()) {
				synchronized (this) {
					if (!lastSent.containsKey(packet)) {
						continue;
					}
					
					long lastSentTime = lastSent.get(type).get(packet);
					long totalTime = lastSentTime + deltaTime;
					while (totalTime >= TIMEOUT_INTERVAL && numRetransmissions.get(type).get(packet) < MAX_RETRANSMISSIONS) {
						SendingBuffer buffer = conn.getSendingBuffer();
						byte[] bytes = buffer.get(type, packet);
						if (bytes != null) {
							buffer.add(bytes);
						}
	
						byte num = numRetransmissions.get(type).get(packet);
						numRetransmissions.get(type).put(packet, (byte) (num + 1));
						
						lastSent.get(type).put(packet, System.currentTimeMillis() - totalTime);
						totalTime -= TIMEOUT_INTERVAL;
					}
				}
				
				if (numRetransmissions.get(type).get(packet) >= MAX_RETRANSMISSIONS) {
					removePacket(type, packet);
				}
			}
		}
	}
	
	public void addPacket(PacketType type, short seq) {
		if (lastSent.containsKey(seq)) {
			return;
		}
		
		synchronized (this) {
			if (!lastSent.containsKey(type)) {
				lastSent.put(type, new HashMap<Short, Long>());
				numRetransmissions.put(type, new HashMap<Short, Byte>());
			}
			
			lastSent.get(type).put(seq, System.currentTimeMillis());
			numRetransmissions.get(type).put(seq, (byte) 0);
		}
	}
	
	public void removePacket(PacketType type, short seq) {
		synchronized (this) {
			lastSent.get(type).remove(seq);
			numRetransmissions.get(type).remove(seq);
		}
	}
	
}
