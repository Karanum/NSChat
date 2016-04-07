package nschat.tcp;

import java.util.Arrays;

/**
 * Used to create packets that can be send over the network
 * @author Bart Meyers
 *
 */
public class TCP {
	
	public static final byte ACK_FLAG = 1;
	private static final int HEADERSIZE = 15;
	
	/**
	 * Enum with 3 bit values on the three most significant bits for the packet type.
	 * @author Bart Meyers
	 *
	 */
	public enum PacketType {
		UNDEFINED ((byte) 0),
		DATA ((byte) (5 << 5)),			// code: 101
		;
		private byte code;
		private PacketType(byte b) {
			this.code = b;
		}
		
		public byte getByte() {
			return code;
		}
	};
	
	private static byte[] nextHeader(PacketType type, SequenceNumberSet seq, byte flags, short ack) {
		byte[] header = new byte[HEADERSIZE];
		header[0] = (byte) (type.getByte() | flags);
		long time = System.currentTimeMillis();
		for (int i = 8; i > 0; i--) {
			header[i] = (byte) time;
			time = time >> 8;
		}
		header[9] = (byte) (seq.getSeq() >> 8);
		header[10] = (byte) seq.getSeq();
		header[11] = (byte) (ack >> 8);
		header[12] = (byte) ack;
		header[13] = 0;		//Source and destination needs to be added
		header[14] = 0;
		return header;
	}
	
	/**
	 * creates a packet.
	 * @param data Data that will be send
	 * @param type The type of packet
	 * @param seq The SequenceNumberSet to generate SEQ numbers with
	 * @param flags The flags that need to be set
	 * @param ack The acknowledgment number
	 * @return the packet
	 */
	public static byte[] nextPacket(String data, PacketType type, SequenceNumberSet seq, byte flags, short ack) {
		byte[] dataBytes = data.getBytes();
		byte[] packet = new byte[dataBytes.length + HEADERSIZE];
		byte[] header = nextHeader(type, seq, flags, ack);
		System.arraycopy(dataBytes, 0, packet, HEADERSIZE, dataBytes.length);
		System.arraycopy(header, 0, packet, 0, HEADERSIZE);
		seq.increaseSeq();
		return packet;
	}
	
	/**
	 * Returns whether the packet is valid (i.e. the header is complete)
	 * @param packet The packet to verify
	 * @return Whether the packet is valid
	 */
	public static boolean isValidPacket(byte[] packet) {
		return packet.length >= HEADERSIZE;
	}
	
	/**
	 * Returns the type of the packet
	 * @param packet The packet to extract from
	 * @return The PacketType of the packet
	 */
	public static PacketType getPacketType(byte[] packet) {
		if (packet.length >= HEADERSIZE) {
			byte type = (byte) (packet[0] >> 5);
			for (PacketType value : PacketType.values()) {
				if (value.getByte() == type) {
					return value;
				}
			}
		}
		return PacketType.UNDEFINED;
	}
	
	/**
	 * Checks whether the ACK flag of the packet has been set
	 * @param packet The packet to extract from
	 * @return Whether the packet is an ACK
	 */
	public static boolean isAck(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return false;
		}
		return (packet[0] & ACK_FLAG) != 0;
	}
	
	/**
	 * Returns the timestamp the packet was signed with
	 * @param packet The packet to extract from
	 * @return The timestamp of the packet
	 */
	public static long getTimestamp(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return 0L;
		}
		long time = 0L;
		for (int i = 1; i < 9; ++i) {
			time = (time | packet[i]);
			time = (time << 8);
		}
		return time;
	}
	
	/**
	 * Returns the sequence number of the packet
	 * @param packet The packet to extract from
	 * @return The SEQ of the packet
	 */
	public static short getSeqNumber(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return 0;
		}
		return (short) ((packet[9] << 8) + packet[10]);
	}
	
	/**
	 * Returns the acknowledgement number of the packet
	 * @param packet The packet to extract from
	 * @return The ACK of the packet
	 */
	public static short getAckNumber(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return 0;
		}
		return (short) ((packet[11] << 8) + packet[12]);
	}
	
	/**
	 * Returns the sender of the packet
	 * @param packet The packet to extract from
	 * @return The source ID of the packet
	 */
	public static byte getSender(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return 0;
		}
		return packet[13];
	}
	
	/**
	 * Returns the recipient of the packet
	 * @param packet The packet to extract from
	 * @return The destination ID of the packet
	 */
	public static byte getRecipient(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return 0;
		}
		return packet[14];
	}
	
	/**
	 * Returns the payload data of the packet
	 * @param packet The packet to extract from
	 * @return The data of the packet as a String
	 */
	public static String getDataAsString(byte[] packet) {
		if (packet.length < HEADERSIZE) {
			return "";
		}
		byte[] data = Arrays.copyOfRange(packet, 12, packet.length);
		return new String(data);
	}
}
