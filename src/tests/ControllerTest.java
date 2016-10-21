package tests;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import client.ClientController;
import client.ClientModel;
import client.Conversation;
import client.ModelInconsistencyException;
import client.TwoWayConversation;
import client.Conversation.ErrorState;
import client.gui.MainLoginWindow;
import client.gui.TwoWayWindow;

import comm.ChannelFactory;
import comm.Packet;
import comm.TestChannelFactory;
import comm.TestConnectionHandler;
import comm.Packet.PacketType;

public class ControllerTest {
	private ClientController controller;
	private ClientModel model;
	private MainLoginWindow window;
	
	@Before
	public void setUp() throws ModelInconsistencyException {
    	window = new MainLoginWindow(); // Make new login window
    	model = new ClientModel(window);   // Make associated client model
    	/*TestConnectionHandler ch = new TestConnectionHandler();
    	ServerHandler server = new ServerHandler(ch);
    	ChannelFactory factory = new TestChannelFactory(ch);
    	*/
    	ChannelFactory factory = new TestChannelFactory(new TestConnectionHandler());
    	controller = new ClientController(model, factory); // Make controller    	
		window.setController(controller);		    // set controller for main login window
		window.setModel(model);					   // set model for main login window

    	// Simulate login
		controller.loginPressed("TestUser");
		
		Packet p = new Packet(PacketType.Ack);
		p.setStatus("OK");
		p.setType("login");
		p.setFor("Request");
		controller.receivedMessage(p);
		
	}
	@After
	public void takeDown(){
		controller = null;
		model = null;
		window = null;
	}
	
	/**
	 * Simulate server sending a login ack packet, and a list of currently online users.
	 * Makes sure that the appropriate changes are made to the model.
	 * @throws ModelInconsistencyException
	 */
	@Test
	public void testLoginPressed() throws ModelInconsistencyException {
		// Make sure we're connected after login
		Assert.assertTrue(model.isConnected());
		Assert.assertEquals("TestUser", model.getLoginName());
		Assert.assertFalse(window.isVisible());
	}

	@Test
	public void testLogoutPressed() throws ModelInconsistencyException {
		controller.logoutPressed();
		Assert.assertFalse(model.isConnected());
	}

	@Test
	public void testNotifications() throws ModelInconsistencyException {
		// Test receiving login notification
		Packet p = new Packet(PacketType.Notification);
		p.setType("login");
		p.addSource("TestUser");
		p.addSource("User2");
		
		controller.receivedMessage(p);
		Assert.assertEquals(2, model.getOnlineUsers().size());
		Assert.assertTrue(model.getOnlineUsers().contains("TestUser"));
		Assert.assertTrue(model.getOnlineUsers().contains("User2"));
		
		// Receiving logout notification 
		p = new Packet(PacketType.Notification);
		p.setType("logout");
		p.addSource("TestUser");
		
		controller.receivedMessage(p);
		Assert.assertFalse(model.getOnlineUsers().contains("TestUser"));
		
		// Chat notifications tested below
	}

	@Test
	public void testChatRooms() throws ModelInconsistencyException, InterruptedException {
		// Receiving joinchat ACK from new chat room
		controller.joinChatRoomRequested("yo");
		Packet p = new Packet(PacketType.Ack);
		p.setType("joinchat");
		p.setFor("Request");
		p.setRoom("yo");
		p.setStatus("CREATED");
		controller.receivedMessage(p);
		System.out.println(model.getChatRoomNames());
		Assert.assertTrue( model.getChatRoomNames().contains("yo") );
		
		// Receiving joinchat ACK from existing chat room
		controller.joinChatRoomRequested("sir");
		p = new Packet(PacketType.Ack);
		p.setType("joinchat");
		p.setRoom("sir");
		p.setStatus("OK");
		p.setFor("Request");
		controller.receivedMessage(p);
		Assert.assertTrue( model.getChatRoomNames().contains("sir") );
	}
	
	@Test
	public void testTwoWayChat() throws ModelInconsistencyException {
		controller.nameSelectedFromList("Isaac");
		Assert.assertTrue(model.get2WayNames().contains("Isaac"));
		// Make sure it doesn't add a second one when we click again
		controller.nameSelectedFromList("Isaac");
		int numIsaacs = 0;
		for( String s : model.get2WayNames() ) {
			if(s.equals("Isaac"))
				numIsaacs++;
		}
		Assert.assertEquals(1, numIsaacs);
		
		Conversation c = model.addConversation("Jesse");
		controller.sendImPressed("Message.", c);
		Assert.assertEquals(ErrorState.PENDING, c.getLastErrorState());
		
		Packet p = new Packet(PacketType.Ack);
		p.setFor("Message");
		p.setType("2way");
		p.setStatus("OK");
		p.setDest("Jesse");
		controller.receivedMessage(p);
		
		Assert.assertEquals(ErrorState.OK, c.getLastErrorState());
		// Testing whether the message got put into the chat window is a GUI task;
		// it is all only in the GUI
	}
}
