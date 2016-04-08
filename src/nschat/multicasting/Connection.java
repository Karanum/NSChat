package nschat.multicasting;

import java.io.IOException;
import java.net.DatagramPacket;

import nschat.Program;
import nschat.exception.PacketFormatException;
import nschat.routing.BasicRoutingProtocol;
import nschat.tcp.Packet;
import nschat.tcp.Packet.PacketType;

public class Connection implements Runnable {
	
	ReceivingBuffer receivingBuffer; 
	SendingBuffer sendingBuffer;
	BasicRoutingProtocol routing;
	Multicast cast;
	Program program;
	
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
			switch (type) {
				case TEXT:
					program.getUI().printText(p.getDataAsString());
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
	
	public ReceivingBuffer getReceivingBuffer() {
		return receivingBuffer;
	}
	
	public SendingBuffer getSendingBuffer() {
		return sendingBuffer;
	}
}
