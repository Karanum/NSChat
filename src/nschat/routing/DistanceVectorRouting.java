package nschat.routing;

public class DistanceVectorRouting {

	private double RTT;
	
	public double getRRT() {
		return RTT;
	}
	
	public void setRTT() {
		double sendTime = System.currentTimeMillis();
		//when receiving ack;
		double receiveTime = System.currentTimeMillis();
		RTT = receiveTime - sendTime;
	}
}
