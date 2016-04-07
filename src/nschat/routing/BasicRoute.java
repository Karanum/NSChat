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
		
		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (dest >> (32 - (i+1)*8));
		}
		for (int i = 4; i < 8; i++) {
			bytes[i] = (byte) (cost >> (32 - (i-3)*8));
		}
		for (int i = 8; i < 12; i++) {
			bytes[i] = (byte) (next >> (32 - (i-7)*8));
		}
		return bytes;
	}
}

