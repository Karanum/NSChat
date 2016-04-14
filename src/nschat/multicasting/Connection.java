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
import nschat.tcp.AckHandler;
import nschat.tcp.AckList;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;
import nschat.tcp.Timeout;

public class Connection implements Runnable {
	
	private ReceivingBuffer receivingBuffer; 
	private SendingBuffer sendingBuffer;
	private BasicRoutingProtocol routing;
	private Multicast cast;
	private Program program;
	private FileHandler fileManager;
	private Timeout timeout;
	
	private Map<PacketType, Map<Integer, List<Integer>>> seenPackets;
	
	public Connection(Program program) throws IOException {
		receivingBuffer = new ReceivingBuffer();
		sendingBuffer = new SendingBuffer(this);
		try {
			cast = new Multicast(receivingBuffer);
		} catch (IOException e) {
			throw e;
		} 
		
		this.program = program;
		routing = new BasicRoutingProtocol(sendingBuffer);
		fileManager = new FileHandler(this);
		timeout = new Timeout(this);
		seenPackets = new HashMap<PacketType, Map<Integer, List<Integer>>>();
	}

	@Override
	public void run() {
		cast.receiveDatagram();
	}
	
	public void send() {
		for (byte[] packet : sendingBuffer.getAllFromBuffer()) {
			DatagramPacket datagram = cast.makeDgramPacket(packet);
			cast.sendDatagram(datagram);
			
			//packet.hashCode();
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
			
			if (Program.DEBUG) {
				System.out.println("Received packet: " + p.toString());
			}
						
			PacketType type = p.getPacketType();
			if (seenPackets.containsKey(type)) {
				int sender = p.getSender();
				
				if (seenPackets.get(type).containsKey(sender) &&
						seenPackets.get(type).get(sender).contains((int) p.getSeqNumber())) {
					continue;
				}
			}
			if (type != PacketType.ROUTING && type != PacketType.UNDEFINED) {
				forwardPacket(p);
				if (p.isAck()) {
					checkAck(p);
					continue;
				} else {
					acknowledgePacket(p);
				}
			}
			
			switch (type) {
				case TEXT:
					program.getUI().printText(p.getDataAsString(/*program.getSecurity()*/)); //TODO enable encryption...

					break;
					
				case FILE:
					fileManager.receiveFile(p);
					//program.getUI().printImage(p.get)
					break;
					
				case ROUTING:
					routing.receivePacket(p);
					break;
					
				case SECURITY:
					getProgram().getSecurity().IVReceived(p);
					break;
					
				default:
			}
		}
	}
	
	private void forwardPacket(Packet packet) {
		PacketType type = packet.getPacketType();
		int sender = packet.getSender();
		int seq = (int) packet.getSeqNumber();
		
		if (!seenPackets.containsKey(type)) {
			seenPackets.put(type, new HashMap<Integer, List<Integer>>());
		}
		if (!seenPackets.get(type).containsKey(sender)) {
			seenPackets.get(type).put(sender, new ArrayList<Integer>());
		}
		
		List<Integer> list = seenPackets.get(type).get(sender);
		if (!list.contains(seq)) {
			list.add(seq);
			if (list.size() > 50) {
				list.remove(0);
			}
			
			InetAddress senderAddr = packet.getSenderAddress();
			try {				
				if (!senderAddr.equals(InetAddress.getLocalHost()) && !senderAddr.isLoopbackAddress()) {
					sendingBuffer.add(packet.pack());
					if (Program.DEBUG) {
						System.out.println("Forwarded message with SEQ: " + seq + " from address: " + senderAddr);
					}
				}
			} catch (UnknownHostException e) { }
		}
	}
	
	private void acknowledgePacket(Packet packet) {
		PacketType type = packet.getPacketType();
		Packet ack = AckHandler.getHandler(type).createAck(packet).orElse(null);
		if (ack != null) {
			sendingBuffer.add(ack.pack());
			if (Program.DEBUG) {
				System.out.println("ACKing packet: " + ack.toString());
			}
		}
	}
	
	private void checkAck(Packet packet) {
		PacketType type = packet.getPacketType();
		AckHandler.getHandler(type).handleAck(this, packet);
		
		AckList ackList = AckList.getInstance(packet.getPacketType(), packet.getAckNumber());
		if (ackList != null) {
			ackList.setAcknowledged(packet.getSender());
		}
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
	
	public FileHandler getFileHandler() {
		return fileManager;
	}
	
	public Program getProgram() {
		return program;
	}
	
	public Timeout getTimeout() {
		return timeout;
	}
	
	public BasicRoutingProtocol getRouting() {
		return routing;
	}
}
