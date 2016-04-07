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
	
	public int getDest() {
		return dest;
	}
	
	public int getNextHop() {
		return next;
	}
	
	public void setCost(int cost) {
		this.cost = cost;
	}
}

