package nschat.tcp;

import java.util.Random;

/**
 * Keeps track of and dispenses sequence numbers.
 * Use one SequenceNumberSet object per packet type for correct numbering.
 * @author Bart Meyers
 *
 */
public class SequenceNumberSet {

	private short seq;
	
	public SequenceNumberSet() {
		seq = (short) (new Random()).nextInt(Short.MAX_VALUE - Short.MIN_VALUE + 1);
	}

	/**
	 * Get the next sequence number.
	 * @return The sequence number
	 */
	public short get() {
		return seq++;
	}
}
