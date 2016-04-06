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
	
	private Map<SequenceNumberSet, Map<Short, byte[]>> buffer = new HashMap<SequenceNumberSet, Map<Short, byte[]>>();
	
	/**
	 * Add a packet to the buffer.
	 * @param seq
	 * @param packet
	 */
	public void add(SequenceNumberSet set, short seq, byte[] packet) {
		synchronized(this) {
			if (!buffer.containsKey(set)) {
				buffer.put(set, new HashMap<Short, byte[]>());
			}
			buffer.get(set).put(seq, packet);
		}
	}

	/**
	 * Remove a packet from the buffer.
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
