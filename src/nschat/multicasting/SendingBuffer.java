package nschat.multicasting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nschat.tcp.AckList;
import nschat.tcp.Packet.PacketType;

/**
 * Buffer for sending packets that is thread safe.
 * @author Bart Meyers
 *
 */
public class SendingBuffer {
	
	private List<byte[]> buffer;
	private Map<PacketType, Map<Short, byte[]>> archive;
	private Connection conn;
	
	public SendingBuffer(Connection conn) {
		buffer = new ArrayList<byte[]>();
		archive = new HashMap<PacketType, Map<Short, byte[]>>();
		this.conn = conn;
	}
	
	/**
	 * Adds a packet to the buffer belonging to the specified SEQ set.
	 * @param type The PacketType of the packet being sent
	 * @param seq The SEQ number of the packet
	 * @param packet The packet that needs to be added
	 */
	public void add(PacketType type, short seq, byte[] packet) {
		synchronized (this) {
			if (type != PacketType.UNDEFINED && type != PacketType.ROUTING) {
				AckList.createInstance(conn, type, seq);
			}
			
			buffer.add(packet);
			if (seq != 0) {
				if (!archive.containsKey(type)) {
					archive.put(type, new HashMap<Short, byte[]>());
				}
				archive.get(type).put(seq, packet);
			}
		}
	}
	
	/**
	 * Adds a packet to the buffer without storing it for later use.
	 * @param packet The packet that needs to be added
	 */
	public void add(byte[] packet) {
		synchronized (this) {
			buffer.add(packet);
		}
	}

	/**
	 * Removes a packet from the buffer belonging to the specified SEQ set.
	 * @param type The PacketType of the packet
	 * @param seq The SEQ number of the packet
	 */
	public void remove(PacketType type, short seq) {
		synchronized (this) {
			if (archive.containsKey(type)) {
				archive.get(type).remove(seq);
			}
		}
	}
	
	/**
	 * Returns the value that belongs to the key.
	 * @param seq
	 * @return
	 */
	public byte[] get(PacketType type, short seq) {
		synchronized (this) {
			if (!archive.containsKey(type)) {
				return null;
			}
			return archive.get(type).get(seq);
		}
	}
	
	public Map<Short, byte[]> getAll(PacketType type) {
		synchronized (this) {
			if (!archive.containsKey(type)) {
				return null;
			}
			return archive.get(type);
		}
	}
	
	public List<byte[]> getAllFromBuffer() {
		List<byte[]> packets = new ArrayList<byte[]>();
		synchronized (this) {
			for (byte[] packet : buffer) {
				packets.add(packet);
			}
			buffer.clear();
		}
		return packets;
	}
}
