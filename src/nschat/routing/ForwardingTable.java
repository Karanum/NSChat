package nschat.routing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
	
	
	public void addRoute(BasicRoute route) {
		int dest = route.getDestination();
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
	
	public void tick(BasicRoute route, int routeSender) {
		int dest = route.getDestination();
		int linkCost = brp.getSenderRTT().get(routeSender);
		if (linkCost != -1) {
			addRoute(new BasicRoute(routeSender, linkCost, routeSender));
			
			for (int desti : forwardingTable.keySet()) {
				if (route.getNextHop() != myAddress && !unlinkedDests.containsKey(desti)) {
					int cost = route.getCost();
					if (cost != -1) {
						cost += linkCost;
					} else if (forwardingTable.containsKey(dest) 
							  && forwardingTable.get(dest).getCost() != -1) {
						unlinkedDests.put(dest, UNLINK_COOLDOWN);
					}
					addRoute(new BasicRoute(dest, cost, routeSender));
				}
			}
		}
	}
	
	public void updateUnlinkedDests() {
		Iterator<Integer> iter = unlinkedDests.keySet().iterator();
		while (iter.hasNext()) {
			int destination = iter.next();
			unlinkedDests.put(destination, unlinkedDests.get(destination) - 1);
			if (unlinkedDests.get(destination) <= 0) {
				iter.remove();
				unlinkedDests.remove(destination);
			}
		}
	}
	
	public void clearUnseenRoutes() {
		for (int dest : forwardingTable.keySet()) {
			int nextHop = forwardingTable.get(dest).getNextHop();
			if (nextHop != myAddress && brp.getSenderRTT().get(nextHop) == -1) {
				BasicRoute destRoute = forwardingTable.get(dest);
				destRoute.setCost(-1);
				forwardingTable.put(dest, destRoute);
			}
		}
	}
	
	public void removeRoute(Integer destination) {
		forwardingTable.remove(destination);
	}
	
	public Collection<BasicRoute> getRoutes() {
		return forwardingTable.values();
	}
	
	public void updateTable(List<BasicRoute> routes, int routesSender) {
		for (BasicRoute route : routes) {
//			int linkCost = brp.getSenderRTT().get(routesSender);
//			BasicRoute newRoute = new BasicRoute(route.getDestination(), 
//					  route.getCost() + linkCost, routesSender);	//linkcost should not be here
//			tick(newRoute, routesSender);
			tick(route, routesSender);
		}
		clearUnseenRoutes();
		brp.sendPacket();
		updateUnlinkedDests();
	}
	
}
