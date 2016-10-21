package comm;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class RealChannelFactory extends ChannelFactory {

	private final String host;
	private final int port;
	
	public RealChannelFactory(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public Channel getNewChannel() throws UnknownHostException, IOException {
		return new RealChannel( new Socket(host, port) );
	}

}
