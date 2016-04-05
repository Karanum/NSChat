package nschat.tcp;

import java.util.Random;

public class SequenceNumber {

	private short SequenceNumber;
	
	public void SequenceNumber() {
		SequenceNumber = (short) (new Random()).nextInt(Short.MAX_VALUE + 1);
	}
	
	public short getSeq() {
		return SequenceNumber;
	}
	
	public void increaseSeq() {
		SequenceNumber += 1;
	}
	
	
	
}
