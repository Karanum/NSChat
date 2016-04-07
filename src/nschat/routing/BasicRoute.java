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
	
	public int getCost() {
		return cost;
	}
	
	public int getDestination() {
		return dest;
	}
	
	public int getNextHop() {
		return next;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
	
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

