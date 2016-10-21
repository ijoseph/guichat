package server;

import java.io.IOException;

import comm.Channel;
import comm.ConnectionHandler;


public class ServerHandler implements Runnable {
	
	private ConnectionHandler connectionHandler;
	
	public ServerHandler(ConnectionHandler connectionHandler) throws IOException {		
		this.connectionHandler = connectionHandler;
	}

	
	
	@Override
	public void run() {
		/**
		 * First, need to start thread that handles the queue.
		 */
		QueueHandler q = new QueueHandler();
		new Thread(q).start();
		
		/**
		 * Next, we need to wait for connections, and spawn a new thread every time someone connects.
		 */
		try {
			while (true) {
				// Wait until someone connects.
				Channel c = connectionHandler.awaitConnection();
				System.out.println("New Connection...");
				
				UserHandler handler = new UserHandler(c, q);
				Thread thread = new Thread(handler);
				thread.start();
			}
		} catch (IOException e) {
			System.err.println("Accept Failed.  Server Dead.");
			System.exit(1);
		}
	}
	

}
