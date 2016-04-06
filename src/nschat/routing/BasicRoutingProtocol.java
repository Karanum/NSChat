package nschat.routing;

import java.util.ArrayList;
import java.util.List;

/**
 * Distance vector routing protocol
 * @author Pieter Jan
 *
 */
public class BasicRoutingProtocol {

	private List<byte[]> sendBuffer;
	private double RTT;
	
	
	
	public BasicRoutingProtocol() {
		packetbuffer = new ArrayList<byte[]>();
	}
	
	
	public void receivePacket(byte[] packet) {
		byte[] old = sendBuffer.get(TCP.getAck(packet));
		double sendTime = TCP.getTimestamp(old);
		RTT = System.currentTimeMillis() - sendTime;
	}
	
	
	
	
	
}
