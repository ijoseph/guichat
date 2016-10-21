/**
 * ClientController.java: a controller to manage the interactions between the GUI
 * of the chat client
 */

package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import client.Conversation.ErrorState;

import comm.Channel;
import comm.ChannelFactory;
import comm.Packet;
import comm.Packet.PacketType;

public class ClientController {
	
	/**
	 * Private class for running a thread that listens for new network messages
	 * and passes them on to the controller whenever it gets one
	 * @author Jesse
	 *
	 */
	private static final boolean VERBOSE = false;
	
	private class ClientMessageListener implements Runnable {
		private Channel serverChannel;
		private ClientController controller;
		
		public ClientMessageListener(Channel serverChannel, ClientController controller) {
			this.serverChannel = serverChannel;
			this.controller = controller;
		}

		@Override
		public void run() {
			while(true) {
				try {
					try {
						System.out.println("Waiting");
						Packet p = serverChannel.readMessage();
						if(VERBOSE) System.out.println("Client received " + p);
						controller.receivedMessage(p);
					} catch (IOException e) {
						// Occurs on connection problems, esp. if server closed connection because of bad logon
						controller.disconnected();
						return;
					}
				}
				catch(ModelInconsistencyException e) {
					e.printStackTrace();
					controller.exitWithError(e.getMessage());
				}
			}
		}	
	}
	
	private Channel serverChannel;
	private Thread listeningThread;
	private ClientModel model;
	private ChannelFactory channelFactory;
	
	/**
	 * Creates a new controller that updates a specified model and receives events from the GUI and network.
	 * @param model The model to update on events
	 * @param host The server host to connect to on login
	 * @param port The port on the host to which the controller should connect
	 */
	public ClientController(ClientModel model, ChannelFactory factory) {
		this.model = model;
		setChannel(null);
		this.channelFactory = factory;
	}
	

	/**
	 * Sets the channel that will be used for communicating with the server.
	 * @param serverChannel A channel to the server for the duration of this client session
	 */
	public void setChannel(Channel serverChannel) {
		this.serverChannel = serverChannel;
	}
	
	/**
	 * Login event: occurs when the login button is pressed on the login window.
	 * Connects to the server (if not already connected), and sends a login request.
	 * Also updates the model with the username.
	 * @throws ModelInconsistencyException
	 */
	public void loginPressed(String loginName) throws ModelInconsistencyException {
		try {
			serverChannel = channelFactory.getNewChannel();
			listeningThread = new Thread(new ClientMessageListener(serverChannel, this));
			listeningThread.start();
			Packet p = new Packet(PacketType.Request);
			p.setType("login");
			p.setName(loginName);
			serverChannel.sendMessage(p);
			System.out.println(serverChannel.isConnected());
			model.setLoginName(loginName);
		}
		catch(ConnectException e) {
			serverChannel = null;
			model.failLogin( e.getMessage() );
		} catch (UnknownHostException e) {
			serverChannel = null;
			model.failLogin( e.getMessage() );
		} catch (IOException e) {
			model.failLogin( e.getMessage() );
		}
	}
	
	/**
	 * Logout event: logout button was pressed on main window.
	 * @throws ModelInconsistencyException 
	 */
	public void logoutPressed() throws ModelInconsistencyException {
		try {
			Packet p = new Packet(PacketType.Request);
			p.setType("logout");
			serverChannel.sendMessage(p);
			serverChannel.close();
			serverChannel = null;
		} catch (IOException e) {
			e.printStackTrace();
			// Do nothing - presumably if we can't even disconnect,
			// we're no longer actually connected
		}
		model.setConnected(false);
		// listeningThread ought to stop on its own when it finds it can't read
	}
	
	/**
	 * Unrequested disconnect: lost server connection
	 * @throws ModelInconsistencyException 
	 */
	private void disconnected() throws ModelInconsistencyException {
		model.setConnected(false);
	}
	
	/**
	 * Received message event: a packet from the server came in over the network
	 * @param p The incoming message
	 * @throws ModelInconsistencyException 
	 */
	public void receivedMessage(Packet p) throws ModelInconsistencyException {
		model.processIncomingMessage(p);
	}
	
	/**
	 * Name selection event: a name on the online users list was selected to start a 2-way chat with
	 * @param name
	 */
	public void nameSelectedFromList(String name) {
		model.addConversation(name);
	}

	/**
	 * Send message event: the "Send" button in a 2-way conversation or
	 * chat window was pressed with a message to send
	 * @param message The message text typed into the chat window
	 * @param convo The conversation associated with the message
	 */
	public void sendImPressed(String message, Conversation convo) {
		Packet p = convo.generatePacket(message);
		try {
			serverChannel.sendMessage(p);
			convo.startMessageTimer();
		} catch (IOException e) {
			convo.setLastErrorState(ErrorState.UNKNOWN);
		}
	}
	
	/**
	 * The "Join" button was pressed in a "Join chat room" dialog
	 * @param roomName The name entered for the chat room to join
	 * @throws ModelInconsistencyException 
	 */
	public void joinChatRoomRequested(String roomName) throws ModelInconsistencyException {
		Packet p = new Packet(PacketType.Request);
		p.setType("joinroom");
		p.setRoom(roomName);
		try {
			serverChannel.sendMessage(p);
		} catch (IOException e) {
			Packet resp = new Packet(PacketType.Ack);
			resp.setFor("Request");
			resp.setType("joinroom");
			resp.setStatus("Failed to send join chat request");
			resp.setRoom(roomName);
			receivedMessage(resp);
		}
	}
	
	/**
	 * IM close event: a 2-way window was closed
	 * @param convo The conversation corresponding to the closed window
	 */
	public void twoWayWindowClosed(TwoWayConversation convo) {
		model.removeTwoWay(convo);
	}
	
	/**
	 * IM close event: a chat room window was closed.
	 * This event causes a leave chat room request.
	 * @param convo The conversation corresponding to the closed window
	 */
	public void chatRoomWindowClosed(ChatRoomConversation convo) {
		Packet p = new Packet(PacketType.Request);
		p.setType("leaveroom");
		p.setRoom(convo.getName());
		try {
			System.out.println("Sending closing message");
			serverChannel.sendMessage(p);
		} catch (IOException e) {
			// Doesn't seem like there's much we could do here, unless we want to keep trying to resend...
		}
		model.removeChatRoom(convo);
	}
	
	public void exitWithError(String error) {
		System.err.println("Exiting with error");
		JOptionPane.showMessageDialog(null, "Error", "An unrecoverable error occurred.  The application will now exit.", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
}
