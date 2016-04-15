package nschat.tcp;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import nschat.tcp.Packet.PacketType;

/**
 * Keeps track of and dispenses sequence numbers.
 * Uses a different set of sequence numbers depending on the package type.
 * @author Bart Meyers
 */
public abstract class SequenceNumbers {

	private static Map<PacketType, Short> seq = new HashMap<PacketType, Short>();

	/**
	 * Get the next sequence number.
	 * @param type The PacketType of the packet
	 * @return The sequence number
	 */
	public static short get(PacketType type) {
		if (!seq.containsKey(type)) {
			short random = (short) (new Random()).nextInt(Short.MAX_VALUE - Short.MIN_VALUE + 1);
			seq.put(type, random);
		}
		
		short result = seq.get(type);
		if (result == Short.MAX_VALUE) {
			seq.put(type, Short.MIN_VALUE);
		} else {
			seq.put(type, (short) (seq.get(type) + 1));
		}
		
		return result;
	}
}
