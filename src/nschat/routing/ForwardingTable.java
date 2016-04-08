package nschat.routing;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForwardingTable {
	
	// FORWARDING TABLE CONTAINS: DESTINATION, COST, VIA NEIGHBOUR!!!
	private Map<Integer, BasicRoute> forwardingTable;
	private BasicRoutingProtocol brp;
	private int myAddress;
	
	//MAP DESTINATION -> UNLINK_COOLDOWN
	private Map<Integer, Integer> unlinkedDests;
	private static final int UNLINK_COOLDOWN = 2;	
	
	public ForwardingTable(BasicRoutingProtocol brp, int myAddress) {
		forwardingTable = new HashMap<Integer, BasicRoute>();
		this.brp = brp;
		this.myAddress = myAddress;
	}
	
	//TODO finish
	public void addRoute(int dest, BasicRoute route) {
		if (dest == myAddress || route.getNextHop() == myAddress) {	// DO NOTHING
			return;
		} 
		if (!forwardingTable.containsKey(dest)) {		//DOESNT CONTAIN THE DESTINATION
			forwardingTable.put(dest, route);
		} else if (forwardingTable.containsKey(dest)) {		//CONTAINS THE DESTINATION
			
			BasicRoute oldRoute = forwardingTable.get(dest);
			if ((route.getCost() < oldRoute.getCost() && route.getCost() != -1)					
				   || oldRoute.getCost() == -1 || oldRoute.getNextHop() == route.getNextHop()) {
				forwardingTable.put(dest, route);
			}
		}
	}
	
	public void tick(int dest, BasicRoute route) {
		int linkCost = brp.getSenderRTT().get(dest);
		if (linkCost == -1) {
			addRoute(dest, route);
			
			
			for (int desti : forwardingTable.keySet()) {
				if (route.getNextHop() != myAddress && !unlinkedDests.containsKey(desti)) {
					
				}
			}
		}
		
	}
	
	public void removeRoute(Integer destination) {
		forwardingTable.remove(destination);
	}
	
	public Collection<BasicRoute> getRoutes() {
		return forwardingTable.values();
	}
	
	public void updateTable(List<BasicRoute> routes, int nextHop) {
		for (BasicRoute route : routes) {
			int linkCost = brp.getSenderRTT().get(nextHop);
			BasicRoute newRoute = new BasicRoute(route.getDestination(), 
					  route.getCost() + linkCost, nextHop);
			addRoute(route.getDestination(), newRoute);
		}
	}
	
}
