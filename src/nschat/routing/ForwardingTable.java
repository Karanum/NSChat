package nschat.routing;

import java.util.HashMap;
import java.util.Map;

public class ForwardingTable {
	
	// FORWARDING TABLE CONTAINS: DESTINATION, COST, VIA NEIGHBOUR!!!
	private Map<Integer, BasicRoute> forwardingTable;
	
	public ForwardingTable() {
		forwardingTable = new HashMap<Integer, BasicRoute>();		//TODO extend forwarding-table
	}
	
	public void addRoute(Integer destination, BasicRoute route) {
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
		if (forwardingTable.containsKey(destination)) {
			forwardingTable.remove(destination);
		}
	}
}
