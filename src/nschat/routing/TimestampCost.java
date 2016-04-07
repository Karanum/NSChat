package nschat.routing;

public class TimestampCost {

	private long start;
	private long end;
	
	public TimestampCost(long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	public long getRTT() {
		
		
		
		return 0;
	}
	
	public long getLinkCost(long rtt) {
	
		double sendTime = System.currentTimeMillis();
		
		//WAit for ACK
		
		double receiveTime = System.currentTimeMillis();
		
		double roundtriptime = receiveTime - sendTime;
		
		return 0;
	}
}
