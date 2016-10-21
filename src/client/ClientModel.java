package client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.Conversation.ErrorState;
import client.gui.MainLoginWindow;
import client.gui.MainWindow;

import comm.Packet;
import comm.Packet.PacketType;

public class ClientModel {

	boolean connected = false;

	private Map<String, TwoWayConversation> twoWayConvos = new HashMap<String, TwoWayConversation>();
	private Map<String, ChatRoomConversation> chatRooms = new HashMap<String, ChatRoomConversation>();
	private MainLoginWindow loginWindow;
	private MainWindow mainWindow = null;

	private String loginName;
	private List<String> loggedInUsers;
	
	/**
	 * Constructs a new controller to process a series of events starting with the
	 * given login window
	 * @param window
	 */
	public ClientModel(MainLoginWindow window) {
		loginWindow = window;
		loggedInUsers = new LinkedList<String>(); 
	}
	
	/**
	 * @return Whether the client is currently connected to a server
	 */
	public boolean isConnected() {
		return connected;
	}
	
	public void failLogin(String errorMessage) {
		loginWindow.loginFailed(errorMessage);
	}
	
	/**
	 * Sets the connected state. If nowConnected is true, alerts the login window that
	 * login has succeeded and it should create a new MainWindow.
	 * @param nowConnected the new connected state
	 * @throws ModelInconsistencyException
	 */
	public void setConnected(boolean nowConnected) throws ModelInconsistencyException {
		connected = nowConnected;
		
		if(nowConnected) {
			if(mainWindow != null)
				throw new ModelInconsistencyException("MainWindow existed before login");
			mainWindow = loginWindow.loginSucceeded();
		}
		else {
			mainWindow = null;
			loggedInUsers.clear();
		}
		
	}
	
	/**
	 * Gets a list of users currently believed to be online.
	 * @return A list of usernames.
	 */
	public List<String> getOnlineUsers() {
		return loggedInUsers;
	}

	/**
	 * Adds a new two-way conversation with a particular person, if there 
	 * is no current conversation with that person. If there is an open conversation
	 * already, this brings that conversation's window to the front.s 
	 * @param name The username with with the new conversation will take place.
	 * @return 
	 */
	public TwoWayConversation addConversation(String name) {
		TwoWayConversation c = twoWayConvos.get(name);
		if(c == null) {
			c = new TwoWayConversation(name);
			twoWayConvos.put(name, c);
			c.setWindow( mainWindow.makeNewTwoWayWindow(c) );
		}
		else {
			c.convoWindow.setVisible(true);
			c.convoWindow.toFront();
		}
		return c;
	}
	
	/**
	 * Adds a new chat room conversation with the chat room specified, if the 
	 * client is not already in that room. If the client is already in the specified
	 * room, this brings that conversation's window to the front. 
	 * @param name The name of the new chat room.
	 */
	public ChatRoomConversation addChatRoom(String name) {
		System.out.println("Adding chat room named " + name);
		ChatRoomConversation c = chatRooms.get(name); 
		if(c == null) {
			c = new ChatRoomConversation(name);			
			chatRooms.put(name, c);			
			c.setWindow( mainWindow.makeNewChatRoomWindow(c) ); // signal view that chat room has been made
			c.addParticipant(loginName);
		}
		else {
			c.convoWindow.setVisible(true);
			c.convoWindow.toFront();
		}
		return c;
	}
	
	/**
	 * Records that the given two-way conversation has been closed.
	 * @param convo The conversation that has been closed.
	 */
	public void removeTwoWay(TwoWayConversation convo) {
		twoWayConvos.remove(convo);
	}

	/**
	 * Records that the given chat room conversation has been closed.
	 * @param convo The conversation that has been closed.
	 */
	public void removeChatRoom(ChatRoomConversation convo) {
		chatRooms.remove(convo);
	}
	
	/**
	 * Sets the username with which the client is currently logged in.
	 * @param loginName
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return The username with which the client is currently logged in.
	 */
	public String getLoginName() {
		return loginName;
	}
	
	/**
	 * React appropriately to an incoming message, routing it to the proper window.
	 * @param p The received message
	 * @throws ModelInconsistencyException
	 */
	public void processIncomingMessage(Packet p) throws ModelInconsistencyException {		
		try {
			if(p.getPacketType().equals(PacketType.Ack)) {
				processAck(p);
			}
			else if(p.getPacketType().equals(PacketType.Message)) {
				processMsg(p);
			}
			else { //p.getPacketType().equals(PacketType.Notification)
				processNotification(p);
			}
		}
		catch(NullPointerException e) {	// Indicates that one of the Packet properties we tried to get wasn't present
			//e.printStackTrace();
			System.err.println("WARNING: malformed packet discarded: " + p);
		}
	}

	private void processMsg(Packet p) {
		if( p.getType().equals("2way") ) {
			TwoWayConversation c = twoWayConvos.get( p.getFirstSource() );
			if(c == null)
				c = addConversation( p.getFirstSource() );
			
			c.addMessage(p);
		}
		else if (p.getType().equals("room")){			
			ChatRoomConversation c = chatRooms.get( p.getDest() );			
			if(c != null){
				c.addMessage(p);
			}else{
				System.err.println("WARNING: Received a message for a chat room that we're not in");
			}
			
		}else {
			System.err.println("WARNING: Dropping unwanted packet: " + p);
		}
	}

	private void processNotification(Packet p) {
		if( p.getType().equals("login") ) {
			for (String source:p.getSources()){
				if (!loggedInUsers.contains(source)){
					loggedInUsers.add(source);
				}

				// Display logon message if relevant
				TwoWayConversation c = twoWayConvos.get(source);
				if(c != null) {
					c.convoWindow.participantPresentNotify(source);
				}
			}
			mainWindow.updateOnlineUsers();
		}
		else if( p.getType().equals("logout") ) {
			for( String source : p.getSources() ) {
				loggedInUsers.remove(source);
				mainWindow.updateOnlineUsers();
	
				// Display logout message if relevant (chat room leaving will come in its own notification)
				TwoWayConversation c = twoWayConvos.get(source);
				if(c != null) {
					c.convoWindow.participantAbsentNotify(source);
				}
			}
		}
		else if( p.getType().equals("joinedroom") ) {
			ChatRoomConversation c = chatRooms.get(p.getRoom());
			if(c != null) {
				for(String s : p.getSources() ) {
					c.addParticipant(s);
				}
			}
		}
		else if (p.getType().equals("leftroom") ){
			System.out.println("Got a logout message");
			ChatRoomConversation c = chatRooms.get(p.getRoom());
			if(c != null) {
				for(String s : p.getSources() ) {
					c.removeParticipant(s);
				}
			}			
		} else{
			System.err.println("WARNING: Dropping unwanted packet: " + p);
		}
	}

	private void processAck(Packet p) throws ModelInconsistencyException {
		if( p.getFor().equals("Request") ) {
			if( p.getType().equals("login") ) {
				if( p.getStatus().equals("OK") ) {
					setConnected(true);
					twoWayConvos.clear();   // Make sure no old conversations from last login exist.
					chatRooms.clear();
				}
				else {
					loginWindow.loginFailed(p.getStatus());
				}
			}
			else {	// type == joinroom
				if( p.getStatus().equals("OK") || p.getStatus().equals("CREATED") ) {
					addChatRoom(p.getRoom());
				}
			}
		}
		else {	// p.getFor().equals("Message")
			Conversation c;
			if( p.getType().equals("2way") ) {
				c = twoWayConvos.get( p.getDest() );
			}
			else {	// p.getType().equals("room")
				c = chatRooms.get( p.getDest() );
			}
			
			// Make sure we got a valid destination conversation and that it's actually waiting for an ack
			if( c != null && c.getLastErrorState().equals(ErrorState.PENDING)) {
				ErrorState state;
				if( p.getStatus().equals("OK") ) {
					state = ErrorState.OK;
				}
				else if( p.getStatus().equals("OFFLINE") ) {
					state = ErrorState.OFFLINE;
				}
				else {
					state = ErrorState.UNKNOWN; 
				}
				c.setLastErrorState(state);
			}
			else {
				System.err.println("WARNING: Dropping unwanted packet: " + p);
			}
		}
	}
	
	/**
	 * For testing purposes
	 * @return A list of names of people we have 2-way conversations with
	 */
	public List<String> get2WayNames() {
		LinkedList<String> l = new LinkedList<String>();
		for(TwoWayConversation c : twoWayConvos.values() ) {
			l.add(c.getOtherParticipant());
		}
		return l;
	}

	/**
	 * For testing purposes
	 * @return A list of names of chat rooms we're in
	 */
	public List<String> getChatRoomNames() {
		LinkedList<String> l = new LinkedList<String>();
		for(ChatRoomConversation c : chatRooms.values() ) {
			l.add(c.getName());
		}
		return l;
	}
}
