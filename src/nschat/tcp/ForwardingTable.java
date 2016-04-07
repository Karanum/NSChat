package nschat.tcp;

import java.util.HashMap;

public class ForwardingTable {
	
	// FORWARDING TABLE CONTAINS: DESTINATION, COST, VIA NEIGHBOUR!!!
	private HashMap<Byte, Long> forwardingTable;
	
	public ForwardingTable() {
		forwardingTable = new HashMap<Byte, Long>();		//TODO extend forwarding-table
	}
	
	public void addRoute(byte sender, long cost) {
		// Check if the route already exists in the forwarding table add if not, else compare cost.
		
		if (!forwardingTable.containsKey(sender)) {
			forwardingTable.put(sender, cost);
		} else {
			if (cost < forwardingTable.get(sender)) {
				forwardingTable.put(sender, cost);
			}
		}
	}
	
	
}
