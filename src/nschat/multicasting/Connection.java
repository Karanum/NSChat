package nschat.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nschat.Program;
import nschat.exception.PacketFormatException;
import nschat.routing.BasicRoutingProtocol;
import nschat.tcp.Packet;
import nschat.tcp.SequenceNumbers;
import nschat.tcp.Packet.PacketType;

public class Connection implements Runnable {
	
	private ReceivingBuffer receivingBuffer; 
	private SendingBuffer sendingBuffer;
	private BasicRoutingProtocol routing;
	private Multicast cast;
	private Program program;
	
	private short lastAckReceived;
	private short lastSeqReceived;
	private boolean seqReceived;
	
	private Map<PacketType, List<Integer>> seenPackets;
	
	public Connection(Program program) throws IOException {
		receivingBuffer = new ReceivingBuffer();
		sendingBuffer = new SendingBuffer();
		try {
			cast = new Multicast(receivingBuffer);
			cast.joinGroup();
		} catch (IOException e) {
			throw e;
		}
		
		this.program = program;
		routing = new BasicRoutingProtocol();
		seenPackets = new HashMap<PacketType, List<Integer>>();
		
		lastAckReceived = 0;
		lastSeqReceived = 0;
		seqReceived = false;
	}

	@Override
	public void run() {
		cast.receiveDatagram();
	}
	
	public void send() {
		for (byte[] packet : sendingBuffer.getAllFromBuffer()) {
			DatagramPacket datagram = cast.makeDgramPacket(packet);
			cast.sendDatagram(datagram);
		}
	}
	
	public void receive() {
		byte[] packet;
		Packet p;
		while ((packet = receivingBuffer.getNext()) != null) {
			try {
				p = new Packet(packet);
			} catch (PacketFormatException e) { 
				continue;
			}
			
			PacketType type = p.getPacketType();
			if (seenPackets.containsKey(type) && seenPackets.get(type).contains((int) (p.getSeqNumber()))) {
				continue;
			}
			if (type != PacketType.ROUTING) {
				forwardPacket(p);
			}
			
			switch (type) {
				case TEXT:
					program.getUI().printText(p.getDataAsString());
					if (p.isAck()) {
						checkTextAck(p);
					} else {
						acknowledgeTextPacket(p);
					}
					break;
					
				case FILE:
					break;
					
				case ROUTING:
					routing.receivePacket(p);
					break;
					
				case SECURITY:
					break;
					
				default:
			}
		}
	}
	
	private void forwardPacket(Packet packet) {
		PacketType type = packet.getPacketType();
		int seq = (int) packet.getSeqNumber();
		
		if (!seenPackets.containsKey(type)) {
			seenPackets.put(type, new ArrayList<Integer>());
		}
		if (!seenPackets.get(type).contains(seq)) {
			seenPackets.get(type).add(seq);
			InetAddress sender = packet.getSenderAddress();
			try {
				if (!sender.equals(InetAddress.getLocalHost()) && !sender.isLoopbackAddress()) {
					sendingBuffer.forward(packet.pack());
					System.out.println("Forwarded message with SEQ: " + seq + " from address: " + sender);
				}
			} catch (UnknownHostException e) { }
		}
	}
	
	private void acknowledgeTextPacket(Packet packet) {
		short ack = packet.getSeqNumber();
		if (seqReceived) {
			if (lastSeqReceived + 1 == ack || (lastSeqReceived == Short.MAX_VALUE && ack == Short.MIN_VALUE)) {
				lastSeqReceived = ack;
			} else {
				ack = lastSeqReceived;
			}
		} else {
			seqReceived = true;
			lastSeqReceived = ack;
		}
		
		InetAddress dest = packet.getSenderAddress();
		PacketType type = packet.getPacketType();
		short seq = SequenceNumbers.get(type);
		
		Packet p = new Packet(type, Packet.ACK_FLAG, seq, ack, dest);
		//sendingBuffer.add(type, seq, p.pack());
	}
	
	private void checkTextAck(Packet packet) {
		//TODO Implement
	}
	
	public ReceivingBuffer getReceivingBuffer() {
		return receivingBuffer;
	}
	
	public SendingBuffer getSendingBuffer() {
		return sendingBuffer;
	}
	
	public Multicast getMulticast() {
		return cast;
	}
}
