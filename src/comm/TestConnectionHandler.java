package comm;

import java.io.IOException;

public class TestConnectionHandler implements ConnectionHandler {
	private TestChannel nextServerChannel = null;

	@Override
	public synchronized Channel awaitConnection() throws IOException {
		try {
			while(nextServerChannel == null)
				wait();
			TestChannel t = nextServerChannel;
			nextServerChannel = null;
			return t;
		} catch (InterruptedException e) {
			throw new IOException("Interrupted while awaiting connections");
		}
	}
	
	public synchronized Channel createConnection() {
		nextServerChannel = new TestChannel();
		TestChannel c = new TestChannel();
		c.connectTo(nextServerChannel);
		
		notifyAll();
		return c;
	}
}
