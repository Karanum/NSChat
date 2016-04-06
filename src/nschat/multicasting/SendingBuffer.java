package nschat.multicasting;

import java.util.HashMap;
import java.util.Map;

/**
 * Buffer for sending packets that is thread safe.
 * @author Bart Meyers
 *
 */
public class SendingBuffer {
	
	private Map<Short, byte[]> buffer = new HashMap<Short, byte[]>();
	
	/**
	 * Add a packet to the buffer.
	 * @param seq
	 * @param packet
	 */
	public void add(short seq, byte[] packet) {
		synchronized(this) {
		buffer.put(seq, packet);
		}
	}

	/**
	 * Remove a packet from the buffer.
	 */
	public void remove(short seq) {
		synchronized(this) {
			buffer.remove(seq);
		}
	}
	
	public byte[] get(short seq) {
		synchronized(this) {
			return buffer.get(seq);
		}
	}
}
