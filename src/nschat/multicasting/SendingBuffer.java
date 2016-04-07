package nschat.multicasting;

import java.util.HashMap;
import java.util.Map;
import nschat.tcp.SequenceNumberSet;

/**
 * Buffer for sending packets that is thread safe.
 * @author Bart Meyers
 *
 */
public class SendingBuffer {
	
	private Map<SequenceNumberSet, Map<Short, byte[]>> buffer;
	
	public SendingBuffer() {
		buffer = new HashMap<SequenceNumberSet, Map<Short, byte[]>>();
	}
	
	/**
	 * Adds a packet to the buffer belonging to the specified SEQ set.
	 * @param set The SequenceNumberSet that the packet belongs to
	 * @param seq The SEQ number of the packet
	 * @param packet The packet that needs to be added
	 */
	public void add(SequenceNumberSet set, short seq, byte[] packet) {
		synchronized(this) {
			if (!buffer.containsKey(set)) {
				buffer.put(set, new HashMap<Short, byte[]>());
			}
			buffer.get(set).put(seq, packet);
		}
		System.out.println("Added into the buffer: " + new String(packet));
	}

	/**
	 * Removes a packet from the buffer belonging to the specified SEQ set.
	 * @param set The SequenceNumberSet that the packet belongs to
	 * @param seq The SEQ number of the packet
	 */
	public void remove(SequenceNumberSet set, short seq) {
		synchronized(this) {
			if (buffer.containsKey(set)) {
				buffer.get(set).remove(seq);
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
			if (!buffer.containsKey(set)) {
				return null;
			}
			return buffer.get(set).get(seq);
		}
	}
}
