package nschat.multicasting;

import java.io.IOException;

public class Connection implements Runnable {
	
	ReceivingBuffer receivingBuffer; 
	SendingBuffer sendingBuffer;
	Multicast cast;
	
	public Connection() {
		try {
			cast = new Multicast(receivingBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//UI.error("Port already in use restart your system!");
		}
		receivingBuffer = new ReceivingBuffer();
		sendingBuffer = new SendingBuffer();
	}

	@Override
	public void run() {
		cast.receiveDatagram();
	}
}
