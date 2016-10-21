package comm;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestChannelFactory extends ChannelFactory {
	private final TestConnectionHandler handler;
	
	public TestChannelFactory(TestConnectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public Channel getNewChannel() throws UnknownHostException, IOException {
		return handler.createConnection();
	}
}
