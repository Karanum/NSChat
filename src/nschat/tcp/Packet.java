package nschat.tcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import nschat.exception.PacketFormatException;
import nschat.security.SymmetricEncryption;

/**
 * Packet data structure used for creating and reading data from packets.
 * @author Karanum
 *
 */
public class Packet {

	/**
	 * Enumeration of packet types.
	 * @author Bart Meyers
	 */
	public enum PacketType {
		UNDEFINED ((byte) 0),
		TEXT ((byte) (1 << 5)),
		FILE ((byte) (2 << 5)),
		ROUTING ((byte) (3 << 5)),
		SECURITY ((byte) (4 << 5));
		
		private byte code;
		private PacketType(byte b) {
			this.code = b;
		}
		
		/**
		 * Returns the byte representation of the packet type.
		 */
		public byte getByte() {
			return code;
		}
	};
	
	public static final byte ACK_FLAG = 1;
	public static final byte NEW_FLAG = 1<<1;
	private static final int HEADER_SIZE = 21;
	
	private PacketType type;
	private byte flags;
	private long timestamp;
	private short seq;
	private short ack;
	private InetAddress src;
	private InetAddress dest;
	//private Symetric enc = new Symetric();

	private byte[] data;
	
	/**
	 * Creates a new empty packet.
	 */
	public Packet() {
		this(PacketType.UNDEFINED, (byte) 0, (short) 0, (short) 0, null);
	}
	
	/**
	 * Creates a new packet with the specified parameters.
	 * @param type The type of the packet
	 * @param flags The flags of the packet added together
	 * @param seq The SEQ number of the packet
	 * @param ack The ACK number of the packet
	 * @param dest The destination address of the packet, or 0 for broadcasting
	 */
	public Packet(PacketType type, byte flags, short seq, short ack, InetAddress dest) {
		this.type = type;
		this.flags = flags;
		this.seq = seq;
		this.ack = ack;
		this.dest = dest;
		
		timestamp = System.currentTimeMillis();
		data = new byte[0];
		
		try {
			src = InetAddress.getLocalHost();	// Set to own IP oktnx
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new packet from existing packet bytes.
	 * @param packet The packet bytes to create the packet from
	 * @throws PacketFormatException Thrown if the packet bytes do not contain a complete header
	 */
	public Packet(byte[] packet) throws PacketFormatException {
		if (packet.length < HEADER_SIZE) {
			throw new PacketFormatException();
		}
		
		byte typeByte = (byte) (packet[0] & 0b11100000);
		this.type = PacketType.UNDEFINED;
		for (PacketType type : PacketType.values()) {
			if (type.getByte() == typeByte) {
				this.type = type;
			}
		}
		
		flags = (byte) (packet[0] & 0b11111);
		seq = (short) ((packet[9] << 8) | packet[10]);
		ack = (short) ((packet[11] << 8) | packet[12]);
		
		timestamp = 0;
		for (int i = 1; i < 9; ++i) {
			timestamp = (long) (timestamp << 8);
			timestamp = (long) (timestamp | (packet[i] & 0xff));
		}
		
		try {
			byte[] srcIp = new byte[4];
			System.arraycopy(packet, 13, srcIp, 0, 4);
			src = InetAddress.getByAddress(srcIp);
			
			byte[] destIp = new byte[4];
			System.arraycopy(packet, 17, destIp, 0, 4);
			dest = InetAddress.getByAddress(destIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		if (packet.length > HEADER_SIZE) {
			data = Arrays.copyOfRange(packet, HEADER_SIZE, packet.length);
		} else {
			data = new byte[0];
		}
	}
	
	/**
	 * Sets the packet type.
	 * @param type The type of the packet
	 */
	public void setPacketType(PacketType type) {
		this.type = type;
	}
	
	/**
	 * Sets the packet flags.
	 * @param flags The flags of the packet added together
	 */
	public void setFlags(byte flags) {
		this.flags = flags;
	}
	
	/**
	 * Sets the timestamp of the packet. Normally unnecessary since timestamps are automatically generated.
	 * @param timestamp The timestamp of the packet
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Sets the sequence number of the packet.
	 * @param seq The SEQ number of the packet
	 */
	public void setSeqNumber(short seq) {
		this.seq = seq;
	}
	
	/**
	 * Sets the acknowledgement number of the packet.
	 * @param ack The ACK number of the packet
	 */
	public void setAckNumber(short ack) {
		this.ack = ack;
	}
	
	/**
	 * Sets the recipient of the packet.
	 * @param dest The destination address of the packet
	 */
	public void setRecipient(InetAddress dest) {
		this.dest = dest;
	}
	
	/**
	 * Sets the payload data of the packet.
	 * @param data The packet data as a String
	 */
	public void setData(String data) {
		this.data = data.getBytes();
	}
	
	/**
	 * Sets the encrypted payload data of the packet.
	 * @param data The packet data as a String
	 */
	public void setData(String data, SymmetricEncryption enc) {
		this.data = enc.encrypt(data.getBytes(), seq);
	}
	
	/**
	 * Sets the payload data of the packet.
	 * @param data The packet data as bytes
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Sets the encrypted payload data of the packet.
	 * @param data The packet data as bytes
	 * @param enc The encryption system
	 */
	public void setData(byte[] data, SymmetricEncryption enc) {
		this.data = enc.encrypt(data, seq);
	}
	
	/**
	 * Returns the packet type.
	 */
	public PacketType getPacketType() {
		return type;
	}
	
	/**
	 * Returns whether the packet is an ACK packet.
	 */
	public boolean isAck() {
		return (flags & ACK_FLAG) != 0;
	}
	
	/**
	 * Returns whether the packet is a NEW packet.
	 */
	public boolean isNew() {
		return (flags & NEW_FLAG) != 0;
	}
	
	/**
	 * Returns the timestamp of the packet.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Returns the sequence number of the packet.
	 */
	public short getSeqNumber() {
		return seq;
	}
	
	/**
	 * Returns the acknowledgement number of the packet.
	 */
	public short getAckNumber() {
		return ack;
	}
	
	/**
	 * Returns the source address of the packet as an integer.
	 */
	public int getSender() {
		if (src == null) {
			return 0;
		}
		byte[] bytes = src.getAddress();
		return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
	}
	
	/**
	 * Returns the destination address of the packet as an integer.
	 */
	public int getRecipient() {
		if (dest == null) {
			return 0;
		}
		byte[] bytes = dest.getAddress();
		return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
	}
	
	/**
	 * Returns the packet payload data as a String.
	 */
	public String getDataAsString() {
		return new String(data);
	}
	
	/**
	 * Returns the packet payload data as a String.
	 */
	public String getDataAsString(SymmetricEncryption enc) {
		return new String(enc.decrypt(data, getSender(), seq));
	}
	
	/**
	 * Returns the packet payload data as bytes.
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * Returns the packet payload data as decrypted bytes.
	 * @param enc The encryption system
	 */
	public byte[] getData(SymmetricEncryption enc) {
		return enc.decrypt(data, getSender(), seq);
	}
	
	/**
	 * Converts the packet into a byte array so it can be sent.
	 * @return The packet in bytes
	 */
	public byte[] pack() {
		byte[] packet = new byte[HEADER_SIZE + data.length];
		packet[0] = (byte) (type.getByte() | flags);
		packet[9] = (byte) (seq >> 8);
		packet[10] = (byte) seq;
		packet[11] = (byte) (ack >> 8);
		packet[12] = (byte) ack;
		
		byte[] srcIp = src.getAddress();
		System.arraycopy(srcIp, 0, packet, 13, 4);
		
		if (dest != null) {
			byte[] destIp = dest.getAddress();
			System.arraycopy(destIp, 0, packet, 17, 4);
		} else {
			packet[17] = 0;
			packet[18] = 0;
			packet[19] = 0;
			packet[20] = 0;
		}
		
		long time = timestamp;
		for (int i = 8; i > 0; --i) {
			packet[i] = (byte) time;
			time = (time >> 8);
		}
		
		System.arraycopy(data, 0, packet, HEADER_SIZE, data.length);		
		return packet;
	}
	
	/**
	 * Returns the source address of the packet as an InteAddress.
	 * @return
	 */
	public InetAddress getSenderAddress() {
		return src;
	}
	
	/**
	 * Returns the destination address of the packet as an InetAddres.
	 * @return
	 */
	public InetAddress getRecipientAddress() {
		return dest;
	}
	
	/**
	 * Returns a string representation of the object.
	 */
	public String toString() {
		return String.format("\n\tType: %s\n\tFlags: %d\n\tTime: %d\n\tSrc: %d\n\tDst: %d\n\tSEQ: %d\n\tACK: %d\n\tData: %s", 
							type, flags, timestamp, getSender(), getRecipient(), seq, ack, getDataAsString());
	}
	
}
