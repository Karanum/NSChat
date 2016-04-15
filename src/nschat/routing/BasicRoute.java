package nschat.routing;

public class BasicRoute {

	private int cost;
	private int dest;
	private int next;
	
	public BasicRoute(int dest, int cost, int next) {
		this.cost = cost;
		this.dest = dest;
		this.next = next;
	}
	
	/**
	 * Returns the cost value of the BasicRoute.
	 */
	public int getCost() {
		return cost;
	}
	
	/**
	 * Returns the destination of the BasicRoute.
	 */
	public int getDestination() {
		return dest;
	}
	
	/**
	 * Returns the next hop of the BasicRoute.
	 */
	public int getNextHop() {
		return next;
	}
	
	/**
	 * Sets the BasicRoute's cost with the given cost value.
	 * @param cost The cost the BasicRoute will have
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * Converts the BasicRoute into a byte array and then returns it.
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[12];
		bytes[0] = (byte) (dest >> 24);
		bytes[1] = (byte) (dest >> 16);
		bytes[2] = (byte) (dest >> 8);
		bytes[3] = (byte) dest;
		bytes[4] = (byte) (cost >> 24);
		bytes[5] = (byte) (cost >> 16);
		bytes[6] = (byte) (cost >> 8);
		bytes[7] = (byte) cost;
		bytes[8] = (byte) (next >> 24);
		bytes[9] = (byte) (next >> 16);
		bytes[10] = (byte) (next >> 8);
		bytes[11] = (byte) next;
		return bytes;
	}
	

}

