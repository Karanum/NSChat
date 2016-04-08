package nschat.multicasting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nschat.tcp.SequenceNumberSet;

/**
 * Buffer for sending packets that is thread safe.
 * @author Bart Meyers
 *
 */
public class SendingBuffer {
	
	private List<byte[]> buffer;
	private Map<SequenceNumberSet, Map<Short, byte[]>> archive;
	
	public SendingBuffer() {
		buffer = new ArrayList<byte[]>();
		archive = new HashMap<SequenceNumberSet, Map<Short, byte[]>>();
	}
	
	/**
	 * Adds a packet to the buffer belonging to the specified SEQ set.
	 * @param set The SequenceNumberSet that the packet belongs to
	 * @param seq The SEQ number of the packet
	 * @param packet The packet that needs to be added
	 */
	public void add(SequenceNumberSet set, short seq, byte[] packet) {
		synchronized(this) {
			buffer.add(packet);
			if (!archive.containsKey(set)) {
				archive.put(set, new HashMap<Short, byte[]>());
			}
			archive.get(set).put(seq, packet);
		}
		System.out.println("Added into the buffer: " + new String(packet));
	}
	
	public void forward(byte[] packet) {
		synchronized(this) {
			buffer.add(packet);
		}
	}

	/**
	 * Removes a packet from the buffer belonging to the specified SEQ set.
	 * @param set The SequenceNumberSet that the packet belongs to
	 * @param seq The SEQ number of the packet
	 */
	public void remove(SequenceNumberSet set, short seq) {
		synchronized(this) {
			if (archive.containsKey(set)) {
				archive.get(set).remove(seq);
			}
		}
	}
	
	/**
	 * Returns the value that belongs to the key.
	 * @param seq
	 * @return
	 */
	public byte[] get(SequenceNumberSet set, short seq) {
		synchronized(this) {
			if (!archive.containsKey(set)) {
				return null;
			}
			return archive.get(set).get(seq);
		}
	}
	
	public List<byte[]> getAllFromBuffer() {
		List<byte[]> packets = new ArrayList<byte[]>();
		synchronized(this) {
			for (byte[] packet : buffer) {
				packets.add(packet);
			}
			buffer.clear();
		}
		return packets;
	}
}
