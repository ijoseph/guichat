package main;

import java.io.IOException;

import server.ServerHandler;

import comm.RealConnectionHandler;

/**
 * Chat server runner.
 */
public class Server {

	public final static int DEFAULTPORT = 4444;
	/**
	 * Start a chat server.
	 */
	public static void main(String[] args) {
		ServerHandler chatServer = null;
		try {
			chatServer = new ServerHandler(new RealConnectionHandler(DEFAULTPORT));
		} catch (IOException e) {
			System.err.println("Unable to start server on port: " + DEFAULTPORT);
			e.printStackTrace();
			System.exit(0);
		}
		/**
		 * Create and start a thread to run the server.
		 */
		Thread serverThread = new Thread(chatServer);
		serverThread.start();
	}
}
