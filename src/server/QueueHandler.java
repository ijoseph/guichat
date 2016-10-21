package server;

import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import comm.Packet;
import comm.Packet.PacketType;

public class QueueHandler extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Use BlockingQueue, which works with concurrency by waiting to add until there is room,
	 * and waiting to remove until there is an element in the queue.
	 */
	private BlockingQueue<Packet> queue = new ArrayBlockingQueue<Packet>(5);
	/**
	 * Need to have a synchronized map for multiple thread access.
	 * One map for usernames to user handlers, another map for chat room names to sets
	 * of usernames.
	 */
	private Map<String, UserHandler> users = Collections.synchronizedMap(new HashMap<String, UserHandler>());
	private Map<String, Set<String>> chatRooms = Collections.synchronizedMap(new HashMap<String, Set<String>>());

	private final DefaultListModel textDisplayModel;
	private final DefaultListModel usersDisplayModel;
	private final DefaultListModel chatRoomDisplayModel;
	private final DefaultListModel inChatRoomDisplayModel;
	
	private final JList chattext;
	
	public QueueHandler(){
		super("KID CHAT SERVER 1.0");

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cp = this.getContentPane();

		double size[][] =
		{{5, TableLayout.PREFERRED, 5, TableLayout.FILL, 5, TableLayout.FILL, 5,
			TableLayout.FILL, 5, TableLayout.FILL, 5},  // Columns
			{5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, TableLayout.FILL, 5}}; // Rows

		cp.setLayout(new TableLayout(size));

		cp.add(new JLabel("Server Log: "), "3, 1, 3, 1");
		cp.add(new JLabel("Users Logged In: "), "5, 1, 5, 1");
		cp.add(new JLabel("Chat Rooms: "), "7, 1, 7, 1");
		cp.add(new JLabel("In Selected Room: "), "9, 1, 9, 1");
		
		textDisplayModel = new DefaultListModel();
		JList text = new JList(textDisplayModel);
		cp.add(new JScrollPane(text), "3, 4, 3, 4");
		
		usersDisplayModel = new DefaultListModel();
		JList usertext = new JList(usersDisplayModel);
		cp.add(new JScrollPane(usertext), "5, 4, 5, 4");

		chatRoomDisplayModel = new DefaultListModel();
		chattext = new JList(chatRoomDisplayModel);
		chattext.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//TODO: add listener to the JList, another pane to show the members of the selected
		chattext.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				String selected = (String) chattext.getSelectedValue();
				inChatRoomDisplayModel.clear();
				if(selected==null || selected.isEmpty()) return;
				for(String s : chatRooms.get(selected)){
					inChatRoomDisplayModel.addElement(s);
				}
			}
		});
		cp.add(new JScrollPane(chattext), "7, 4, 7, 4");
		
			
		inChatRoomDisplayModel = new DefaultListModel();
		JList inChatRoom = new JList(inChatRoomDisplayModel);
		cp.add(new JScrollPane(inChatRoom), "9, 4, 9, 4");

		this.pack();
	}



	@Override
	public void run() {
		this.setVisible(true);
		display("Server Initialized");
		while(true){
			try{
			Packet curMessage = get();  // never null, ensured by user handler.
			if(curMessage.getPacketType().equals(PacketType.Request)){
				
				if(curMessage.getType().equals("login")){
					login(curMessage);
				}else if(curMessage.getType().equals("logout")){
					logout(curMessage);			
				}else if(curMessage.getType().equals("joinroom")){
					joinRoom(curMessage);
				}else if(curMessage.getType().equals("leaveroom")){
					leaveRoom(curMessage);
				}

			}else{  // Received a message
				/**
				 * Pass messages in appropriate fashion.
				 * TODO: pass messages that go to chat rooms properly
				 */
				if(curMessage.getPacketType().equals(PacketType.Message)){
					if(curMessage.getType().equals("2way")){
						send2Way(curMessage);
					}else if(curMessage.getType().equals("room")){
						sendChat(curMessage);
					}
				}
			}
			}catch(Exception e){
				// In case of exceptions when processing packets
				System.err.println("Server Got Bad Packet");
				e.printStackTrace();
			}
		}
	}

	/**
	 * For users logging in or out of the server, we send notifications out to all other users.
	 */
	private void login(Packet curMessage){
		display("New User Logged in: " + curMessage.getName());
		usersDisplayModel.addElement(curMessage.getName());					

		Packet notify = new Packet(PacketType.Notification);
		notify.setType("login");
		notify.addSource(curMessage.getName());
		sendAll(notify, curMessage.getName());  // Everyone notified except yourself.


		///// Code here to send the user that just logged in the list of users
		Packet loggedIn = new Packet(PacketType.Notification);
		loggedIn.setType("login");
		for(String s : users.keySet()){
			loggedIn.addSource(s);
		}
		UserHandler u = null;
		while(u==null) u=get(curMessage.getName());
		
		u.sendMessage(loggedIn);
		/////
		return;
	}

	/**
	 * When a user logs out, have to make sure they are removed from all
	 * possible rooms and that models are updated.
	 * This is challenging.  Can't change the chatRooms list as it is being looped through,
	 * so we make a set of packets to process and then process them afterwards
	 */
	private void logout(Packet curMessage){
		String name = curMessage.getFirstSource();
		// First get set of all the rooms we're in
		Set<Packet> roomPackets = new HashSet<Packet>();
		synchronized(chatRooms){
			for(String s : chatRooms.keySet()){
				if(chatRooms.get(s).contains(name)){
					Packet p = new Packet(PacketType.Request);
					p.setType("leaveroom");
					p.addSource(name);
					p.setRoom(s);
					roomPackets.add(p);
				}
			}
		}
		// Then, go trough this set of packets created and process each of them.
		for(Packet p : roomPackets){
			leaveRoom(p);
		}

		Packet notify = new Packet(PacketType.Notification);
		notify.setType("logout");
		notify.addSource(curMessage.getFirstSource());
		sendAll(notify, curMessage.getName());   // Everyone notified except yourself.

		display("User Disconnected: " + curMessage.getFirstSource());
		usersDisplayModel.removeElement(curMessage.getFirstSource());
		return;
	}

	/**
	 * To join room, have cases, either it exists or it doesn't.
	 * @param curMessage
	 * @throws InterruptedException 
	 */
	private void joinRoom(Packet curMessage) throws InterruptedException{
		String roomName = curMessage.getRoom();
		String source = curMessage.getFirstSource();
		Packet joinAck = Packet.createAck("joinroom", "Request", null, null);
		Set<String> room;
		synchronized(chatRooms){
			room=chatRooms.get(roomName);
		}
		/**
		 * If room doesn't exist, create and add the current user to it.
		 * Don't need to send out notifications cause just one user in it.
		 */
		if(room==null){
			room = new HashSet<String>();
			room.add(source);
			synchronized(chatRooms){
				chatRooms.put(roomName, room);
			}
			display("New chat room created: " + roomName);
			display("User " + source + " joined "+ roomName);
			chatRoomDisplayModel.addElement(roomName);

			joinAck.setStatus("CREATED");
			joinAck.setRoom(roomName);
			get(source).sendMessage(joinAck);
			return;
		}else{
			/**
			 * If room does exist, add self to the room.
			 * Need to send notifications of joining the room to user in room.
			 */
			room.add(source); // add user to room*
			display("User " + source + " joined "+ roomName);

			joinAck.setStatus("OK");
			joinAck.setRoom(roomName);
			get(source).sendMessage(joinAck);
			Thread.sleep(50);  // In very rare cases, can get notification before ack, this prevents.
			Packet inRoom = new Packet(PacketType.Notification);
			inRoom.setType("joinedroom");
			inRoom.setRoom(roomName);
			for(String s : room){
				inRoom.addSource(s);
			}
			get(source).sendMessage(inRoom);

			Packet joinNotify = new Packet(PacketType.Notification);
			joinNotify.setType("joinedroom");
			joinNotify.setRoom(roomName);
			joinNotify.addSource(source);
			sendRoom(joinNotify, roomName, source);
			refreshRoom();
			return;
		}

	}

	/**
	 * To leave room, first check if room exists.  If does, remove self from the set.
	 * If the room is now empty, kill the room.
	 * Need to send notifications to the room that someone just left it.
	 */
	private void leaveRoom(Packet curMessage){
		String roomName = curMessage.getRoom();
		Set<String> room;
		synchronized(chatRooms){
			room = chatRooms.get(roomName);
		}
		if(room==null) return;
		else{
			room.remove(curMessage.getFirstSource());
			display("User " + curMessage.getFirstSource() + " left " + roomName);

			Packet leftNotify = new Packet(PacketType.Notification);
			leftNotify.setType("leftroom");
			leftNotify.setRoom(roomName);
			leftNotify.addSource(curMessage.getFirstSource());
			sendRoom(leftNotify, roomName, curMessage.getFirstSource());
			/**
			 * Send notification first, then remove chat room from the map.
			 */
			if(room.isEmpty()){
				synchronized(chatRooms){
					chatRooms.remove(roomName);
				}
				display("Room " + roomName + " is gone.");
				chatRoomDisplayModel.removeElement(roomName);
			}
			refreshRoom();
			return;

		}
	}

	/**
	 * Sends a 2way messsage to appropriate dest, and sends appropriate ack back.
	 * @param curMessage
	 */
	private void send2Way(Packet curMessage){
		UserHandler u = get(curMessage.getDest());
		if(u!=null){
			display("Message Sent: " + curMessage.getFirstSource() + " to " + curMessage.getDest());
			u.sendMessage(curMessage);
			Packet ack = Packet.createAck("2way", "Message", "OK", curMessage.getDest());
			get(curMessage.getFirstSource()).sendMessage(ack); return;
		}else{
			display("Message Fail: " + curMessage.getFirstSource() + " to " + curMessage.getDest());
			Packet ack = Packet.createAck("2way", "Message", "OFFLINE", curMessage.getDest());
			get(curMessage.getFirstSource()).sendMessage(ack); return;
		}
	}
	
	/**
	 * TODO: Need to get the room that this message is being routed to, check if the user is a member of that room,
	 * and then send the message to that room if so, sending the appropriate acks back as well.
	 * @param curMessage
	 */
	private void sendChat(Packet curMessage){
		String room = curMessage.getDest();
		Packet ack = Packet.createAck("room", "Message", null, room);
		String source = curMessage.getFirstSource();
		if(room==null){ // If no room specified
			display("RoomNull Fail: " + source);
			ack.setStatus("FAIL");
			get(source).sendMessage(ack);
		}
		else{  
			Set<String> inRoom = chatRooms.get(room);
			if(inRoom==null || !inRoom.contains(source)){  // If room doesn't exist, or user not in room.
				display("RoomNotAllowed: " + source + " to " + room);
				ack.setStatus("FAIL");
				get(source).sendMessage(ack);
			}else{   // Otherwise, we're good, room exists and in it, send message onwards.
				display("RoomMessSend: " + source + " to " + room);
				sendRoom(curMessage, room, curMessage.getFirstSource());
				ack.setStatus("OK");
				get(source).sendMessage(ack);
			}
		}
	}


	/**
	 * Sends the given packet p to all currently logged in users, except for one.
	 * @param p
	 */
	private void sendAll(Packet p, String except){
		synchronized(users){
			for(String s : users.keySet()){
				if(s.equals(except)) continue;
				users.get(s).sendMessage(p);
			}
		}
	}

	/**
	 * Sends the given packet to all users in the room.
	 * @param p
	 * @param room
	 */
	private void sendRoom(Packet p, String room, String except){
		Set<String> roomSet;
		synchronized(chatRooms){
			roomSet = chatRooms.get(room);
		}
		for(String s : roomSet){
			if(s.equals(except)) continue;
			get(s).sendMessage(p);
		}
	}

	/**
	 * Add a packet to the queue.  Called by each UserHandler thread.
	 * @param p
	 */
	public void add(Packet p){
		try {
			queue.put(p);
		} catch (InterruptedException e) {
			System.err.println("Queue interrupted");
			e.printStackTrace();
		}
	}

	/**
	 * Get next packet from queue.  Called within this class to read incoming messages.
	 * @return
	 */
	private Packet get(){
		try {
			return queue.take();
		} catch (InterruptedException e) {
			System.err.println("Queue Interrupted");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Registers a new user with the queue handler so message can be forwarded to them.
	 * @param s
	 * @param u
	 */
	public void put(String s, UserHandler u){
		synchronized(users){
			users.put(s, u);
		}
	}
	/**
	 * Gets associated user handler with a username, used in user handler to see if
	 * name is taken.
	 * @param s
	 * @return
	 */
	public UserHandler get(String s){
		synchronized(users){
			return users.get(s);
		}
	}
	/**
	 * Used by user handler to remove itself from the map on disconnect.
	 * @param username
	 */
	public void remove(String username) {
		synchronized(users){
			users.remove(username);
		}
	}

	/**
	 * Displays server messages properly.
	 * Need fancy runnable thing because of race conditions within swing.
	 * @param s
	 */
	private void display(final String s){
		Runnable updateDisplay = new Runnable(){
			public void run(){
				textDisplayModel.addElement(s);
			}
		};
		SwingUtilities.invokeLater(updateDisplay);
	}
	
	/**
	 * Use this to refresh what is displayed whenever someone joins or leaves from a room.
	 */
	private void refreshRoom(){
		Runnable refresh = new Runnable(){
			public void run(){
				String selected = (String) chattext.getSelectedValue();
				inChatRoomDisplayModel.clear();
				if(selected==null || selected.isEmpty()) return;
				for(String s : chatRooms.get(selected)){
					inChatRoomDisplayModel.addElement(s);
				}
			}
		};
		SwingUtilities.invokeLater(refresh);
	}
}
