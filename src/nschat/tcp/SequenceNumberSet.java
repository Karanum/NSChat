package nschat.tcp;

import java.util.Random;

/**
 * Keeps track of the sequence numbers.
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
	 * @return The sequence number
	 */
	public short getSeq() {
		return seq;
	}
	
	/**
	 * increases the sequence number by 1.
	 */
	public void increaseSeq() {
		seq++;
	}
}
