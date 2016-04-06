package nschat.tcp;

import java.util.Random;

/**
 * Keeps track of the sequence numbers.
 * @author Bart Meyers
 *
 */
public class SequenceNumber {

	private short SequenceNumber;
	
	public SequenceNumber() {
		SequenceNumber = (short) (new Random()).nextInt(Short.MAX_VALUE - Short.MIN_VALUE + 1);
	}

	/**
	 * @return The sequence number
	 */
	public short getSeq() {
		return SequenceNumber;
	}
	
	/**
	 * increases the sequence number by 1.
	 */
	public void increaseSeq() {
		SequenceNumber ++;
	}
}
