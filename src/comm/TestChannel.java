package comm;

import java.io.IOException;

public class TestChannel implements Channel {
	private TestChannel otherEnd = null;
	Packet nextPacket = null;

	@Override
	public synchronized Packet readMessage() throws IOException {
		if( !isConnected() )
			throw new IOException("Attempted to receive message over closed channel");
		try {
			while(nextPacket == null)
				wait();
			Packet p = nextPacket;
			nextPacket = null;
			return p;
		} catch (InterruptedException e) {
			throw new IOException("Interrupted while waiting for data");
		}		
	}

	@Override
	public void sendMessage(Packet message) throws IOException {
		if( !isConnected() ) 
			throw new IOException("Attempted to send message over closed channel");
		otherEnd.receiveMessage(message);
	}
	
	private synchronized void receiveMessage(Packet message) {
		nextPacket = message;
		notifyAll();
	}
	
	public void connectTo(TestChannel newOtherEnd) {
		setOtherSide(newOtherEnd);
		otherEnd.setOtherSide(this);
	}
	
	private void setOtherSide(TestChannel newOtherEnd) {
		otherEnd = newOtherEnd;
	}

	@Override
	public void close() throws IOException {
		if( isConnected() ) {
			otherEnd.setOtherSide(null);
			otherEnd.close();
		}
		setOtherSide(null);
	}

	@Override
	public boolean isConnected() {
		return otherEnd != null;
	}
}
