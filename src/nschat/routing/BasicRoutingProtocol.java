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
		int rtt = (int) (System.currentTimeMillis() - sendTime);
	}
	
	public void makeRoute(int dest, int rtt, int nextHop) {
		BasicRoute route = new BasicRoute(dest, rtt, nextHop);
	}
	
	public int getRTT(byte[] packet) {
		byte[] old = sendingBuffer.get(set, TCP.getAckNumber(packet));
		long sendTime = TCP.getTimestamp(old);
		int rtt = (int) (System.currentTimeMillis() - sendTime);
		return rtt;
	}
	
	public void sendPacket() {
		
	}

}
