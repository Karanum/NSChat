package nschat.tcp;

import java.util.Random;

public class SequenceNumber {

	private short SequenceNumber;
	
	public SequenceNumber() {
		SequenceNumber = (short) (new Random()).nextInt(Short.MAX_VALUE - Short.MIN_VALUE + 1);
	}
	
	public short getSeq() {
		return SequenceNumber;
	}
	
	public void increaseSeq() {
		SequenceNumber ++;
	}
}
