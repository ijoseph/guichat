package comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RealConnectionHandler implements ConnectionHandler {
	private ServerSocket serverSocket;
	
	public RealConnectionHandler(int portNum) throws IOException {
		serverSocket = new ServerSocket(portNum);
	}
	
	@Override
	public Channel awaitConnection() throws IOException {
		Socket s = serverSocket.accept();
		return new RealChannel(s);
	}
}
