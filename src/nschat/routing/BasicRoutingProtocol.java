package nschat.routing;

import java.util.ArrayList;
import java.util.List;

import nschat.multicasting.SendingBuffer;
import nschat.tcp.SequenceNumberSet;
import nschat.tcp.TCP;

/**
 * Distance vector routing protocol
 * @author Pieter Jan
 *
 */
public class BasicRoutingProtocol {

	private SequenceNumberSet set = new SequenceNumberSet();
	private SendingBuffer sendingBuffer = new SendingBuffer();
	
	public BasicRoutingProtocol() {
		
	}
	
	public void receivePacket(byte[] packet) {
		byte[] old = sendingBuffer.get(set, TCP.getAckNumber(packet));
		long sendTime = TCP.getTimestamp(old);
		long RTT = System.currentTimeMillis() - sendTime;
		
	}
	
}
