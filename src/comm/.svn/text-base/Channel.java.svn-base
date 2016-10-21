package comm;

import java.io.IOException;

public interface Channel {
	public Packet readMessage() throws IOException;
	public void sendMessage(Packet message) throws IOException;
	public void close() throws IOException;
	public boolean isConnected();
}
