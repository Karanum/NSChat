package nschat.multicasting;

import java.io.IOException;

public class Connection implements Runnable {
	
	ReceivingBuffer receivingBuffer; 
	SendingBuffer sendingBuffer;
	Multicast cast;
	
	public Connection() throws IOException {
		try {
			cast = new Multicast(receivingBuffer);
		} catch (IOException e) {
			throw e;
		}
		receivingBuffer = new ReceivingBuffer();
		sendingBuffer = new SendingBuffer();
	}

	@Override
	public void run() {
		cast.receiveDatagram();
	}
}
