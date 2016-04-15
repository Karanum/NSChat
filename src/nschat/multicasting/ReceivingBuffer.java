package nschat.multicasting;

import java.util.ArrayList;
import java.util.List;

/**
 * Buffer for receiving packets that is thread safe.
 * @author Bart Meyers
 *
 */
public class ReceivingBuffer {

	private List<byte[]> buffer;
	
	public ReceivingBuffer() {
		buffer = new ArrayList<byte[]>();
	}

	/**
	 * Gets and removes the next packet from the buffer.
	 * @return a packet
	 */
	public byte[] getNext() {
		synchronized (this) {
			if (buffer.size() == 0) {
				return null;
			}
			return buffer.remove(0);
		}
	}
	
	/**
	 * Adds a packet to the end of the buffer.
	 * @param packet The packet that will be added
	 */
	public void add(byte[] packet) {
		synchronized (this) {
			buffer.add(packet);
		}
	}	
}
