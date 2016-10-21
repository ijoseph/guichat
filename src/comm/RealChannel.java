package comm;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;



public class RealChannel implements Channel {
	private static final boolean VERBOSE = false;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public RealChannel(Socket socket) throws IOException {
		this.socket = socket;
		// Create reader and writer, connect them to socket.
		reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		writer = new PrintWriter(socket.getOutputStream(), true);
	}
	
	@Override
	public Packet readMessage() throws IOException {
		try {
			String msgStr = reader.readLine();
			if(VERBOSE) System.out.println(msgStr);
			if(msgStr == null)	// We've hit the end of the stream
				throw new EOFException();
			return XMLParser.parse(msgStr);
		}
		catch(SocketException e) {
			throw new IOException(e.getMessage());
		}
	}

	@Override
	public void sendMessage(Packet message) {
		writer.println(message.toXMLString());
	}

	@Override
	public void close() throws IOException {
		reader.close();
		writer.close();
		socket.close();
	}

	@Override
	public boolean isConnected() {
		return socket.isConnected();
	}

}
