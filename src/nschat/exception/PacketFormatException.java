package nschat.exception;

/**
 * Exception thrown when trying to initialize a packet with an incorrect header.
 * @author Karanum
 */
public class PacketFormatException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The header of the provided packet is invalid";
	}
}
