package nschat.routing;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForwardingTable {
	
	// FORWARDING TABLE CONTAINS: DESTINATION, COST, VIA NEIGHBOUR!!!
	private Map<Integer, BasicRoute> forwardingTable;
	private BasicRoutingProtocol brp;
	
	public ForwardingTable(BasicRoutingProtocol brp) {
		forwardingTable = new HashMap<Integer, BasicRoute>();		//TODO extend forwarding-table
		this.brp = brp;
	}
	
	public void addRoute(int destination, BasicRoute route) {
		// Check if the route already exists in the forwarding table add if not, else compare cost.
		
		if (!forwardingTable.containsKey(destination)) {
			forwardingTable.put(destination, route);
		} else {
			if (route.getCost() < forwardingTable.get(destination).getCost()) {
				forwardingTable.put(destination, route);
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
			BasicRoute newRoute = new BasicRoute(route.getDestination(), route.getCost() + linkCost, nextHop);
			addRoute(route.getDestination(), newRoute);
		}
	}
}
