package nschat.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ForwardingTable {
	
	// MAP DESTINATION -> COST -> VIA NEIGHBOUR
	private Map<Integer, BasicRoute> forwardingTable;
	private BasicRoutingProtocol brp;
	private int myAddress;
	
	// MAP DESTINATION -> UNLINK_COOLDOWN
	private Map<Integer, Integer> unlinkedDests;
	private static final int UNLINK_COOLDOWN = 2;	
	
	private List<Integer> linkedSenders;
	private static final int INCREASE_RTT = 10;
	private static final int MAX_RTT = 20;

	
	public ForwardingTable(BasicRoutingProtocol brp, int myAddress) {
		this.forwardingTable = new HashMap<Integer, BasicRoute>();
		this.brp = brp;
		this.myAddress = myAddress;
		this.linkedSenders = new ArrayList<Integer>();
		unlinkedDests = new HashMap<Integer, Integer>();
	}
	

	/*
	 * Does several checks whether the given BasicRoute is useful, 
	 * and adds it into the forwardingTable if it is.
	 */
	private void addRoute(BasicRoute route) {
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
	
	/*
	 * Adds the sender of the BasicRoute to the forwardingTable and checks if the given 
	 * BasicRoutes are valid and adds them if so.
	 */
	private void tick(BasicRoute route, int routeSender) {
		int dest = route.getDestination();
		int linkCost = brp.getSenderRTT().get(routeSender);
		if (linkCost != -1) {
			addRoute(new BasicRoute(routeSender, linkCost, routeSender));	//ADD SENDER ROUTE
			
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
	
	/**
	 * Update the unlinkedDests list, lowering each destination's unlink_cooldown with 1,
	 * and removing it from the list if its unlink_cooldown is 0.
	 */
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
	
	/**
	 * Sets all route costs to -1 if the link to the node is broken.
	 */
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
	
	/**
	 * Removes the BasicRoute corresponding to the given destination from the forwardingTable.
	 * @param destination The destination of which the corresponding BasicRoute will be deleted
	 */
	public void removeRoute(Integer destination) {
		forwardingTable.remove(destination);
	}
	
	/**
	 * Returns the list of destinations, with the cost unequal to -1.
	 */
	public Collection<Integer> getDestinations() {
		Set<Integer> destinations = new HashSet<Integer>();
		for (BasicRoute route : getRoutes()) {
			if (route.getCost() != -1) {
				destinations.add(route.getDestination());
			}
		}
		return destinations;
	}
	
	/**
	 * Returns all BasicRoutes in the forwardingTable.
	 */
	public Collection<BasicRoute> getRoutes() {
		return forwardingTable.values();
	}
	
	/**
	 * Updates the forwardingTable with the given BasicRoutes, and adds the forwardingTable 
	 * to the sendingBuffer.
	 * @param routes BasicRoutes which will be used to update the forwardingTable
	 * @param routesSender The sender of the BasicRoute
	 */
	public void updateTable(List<BasicRoute> routes, int routesSender) {
		for (BasicRoute route : routes) {
			tick(route, routesSender);
		}	
		linkedSenders.add(routesSender);
	}

	/**
	 * Increases the cost of a destination in the forwardingTable if not heard from in a long time,
	 * if the cost goes above a certain amount, the cost is set to -1.
	 */
	public void setCost() {
		for (int sender : brp.getSenderRTT().keySet()) {
			if (!linkedSenders.contains(sender)) {
				brp.setRTT(sender, brp.getSenderRTT().get(sender) + INCREASE_RTT);
			}
		}
		
		for (int sender : brp.getSenderRTT().keySet()) {
			if (brp.getSenderRTT().get(sender) >= MAX_RTT) {
				brp.setRTT(sender, -1);
			}
		}
	}
	
	/**
	 * Returns the list of the linked senders.
	 */
	public List<Integer> getLinkedSenders() {
		return this.linkedSenders;
	}
	
	/**
	 * Clears the list of linked senders.
	 */
	public void clearLinkedSenders() {
		this.linkedSenders.clear();
	}
	
}

