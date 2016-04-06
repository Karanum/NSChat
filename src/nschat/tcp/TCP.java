package nschat.tcp;

/**
 * Used to create packets that can be send over the network
 * Use one tcp per connection for correct sequence numbers.
 * @author Bart Meyers
 *
 */
public class TCP {
	
	public static final byte ACK_FLAG = 1;
	private final int HEADERSIZE = 13;
	private SequenceNumber seq;
	
	/**
	 * Enum with 3 bit values on the three most significant bits for the packet type.
	 * @author Bart Meyers
	 *
	 */
	public enum PacketType {
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
	
	public TCP() {
		seq = new SequenceNumber();
	}
	
	private byte[] nextHeader(PacketType type, byte flags, short ack) {
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
		return header;
	}
	
	/**
	 * creates a packet.
	 * @param data Data that will be send
	 * @param type The type of packet
	 * @param flags the flags that need to be set
	 * @param ack the acknowledgment number
	 * @return the packet
	 */
	public byte[] nextPacket(String data, PacketType type, byte flags, short ack) {
		byte[] dataBytes = data.getBytes();
		byte[] packet = new byte[dataBytes.length + HEADERSIZE];
		byte[] header = nextHeader(type, flags, ack);
		System.arraycopy(dataBytes, 0, packet, HEADERSIZE, dataBytes.length);
		System.arraycopy(header, 0, packet, 0, HEADERSIZE);
		seq.increaseSeq();
		return packet;
	}	
}
