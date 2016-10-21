package tests;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import comm.Packet;
import comm.TestChannel;
import comm.Packet.PacketType;

import server.QueueHandler;
import server.UserHandler;

public class ServerTests {
	
	QueueHandler q;
	public ServerTests(){
		q = new QueueHandler();
		new Thread(q).start();
	}
	
	
	@Test
	public void testLogin() throws IOException{
		System.out.println("****Begin Login Test Record****");
		
		TestChannel c1 = new TestChannel(), c2 = new TestChannel();
		c1.connectTo(c2);
		UserHandler u = new UserHandler(c1, q);
		new Thread(u).start();
		
		/**
		 * First, login as test user and get ack packet back.
		 */
		Packet toSend = new Packet(PacketType.Request);
		toSend.setType("login");
		toSend.setName("TestUser");
		c2.sendMessage(toSend);
		Packet received = c2.readMessage();
		System.out.println("LoginTest: Expect login ack OK");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		/**
		 * Next, try to login with same username, and expect taken ack to be returned.
		 */
		TestChannel c3 = new TestChannel(), c4 = new TestChannel();
		c3.connectTo(c4);
		UserHandler u2 = new UserHandler(c3, q);
		new Thread(u2).start();
		
		c4.sendMessage(toSend);
		received = c4.readMessage();
		System.out.println("LoginTest: Expect login ack with taken username.");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("Username taken.", received.getStatus());
		
	}

	
	@Test
	public void test2WayChat() throws Exception{
		System.out.println("****Begin 2WayChat Test Record****");
		TestChannel c1 = new TestChannel(), c2 = new TestChannel();
		c1.connectTo(c2);
		UserHandler u = new UserHandler(c1, q);
		new Thread(u).start();
		
		/**
		 * First, login as test user and get ack packet back.
		 */
		Packet login = new Packet(PacketType.Request);
		login.setType("login");
		login.setName("testUser");
		c2.sendMessage(login);
		Packet received = c2.readMessage(); // The ack message
		System.out.println("2wayChatTest: User 1 login ack");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		received = c2.readMessage();   // The logged in users message
		System.out.println("2wayChatTest: User 1 logged in list");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		
		
		/**
		 * Next, login as second test user.
		 */
		Packet login2 = new Packet(PacketType.Request);
		login2.setName("testUser2");
		login2.setType("login");
		TestChannel c3 = new TestChannel(), c4 = new TestChannel();
		c3.connectTo(c4);
		UserHandler u2 = new UserHandler(c3, q);
		new Thread(u2).start();
		c4.sendMessage(login2);
		// Display the login ack for user 2.
		System.out.println("2wayChatTest: User 2 login ack");
		received = c4.readMessage();  // The ack packet
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		/**
		 * Look for expected notifications that login has occured.
		 */
		received = c2.readMessage();  // The login notification packet.
		System.out.println("2wayChatTest: User 1 login notify of 2");
		System.out.println(received);
		// Make sure we got the acknowledgement that testUser2 logged in.
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("testUser2", received.getFirstSource());
		
		System.out.println("2wayChatTest: User 2 logged in list");
		received = c4.readMessage();   // should be the login userlist
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		/**
		 * Now, send a 2way message between these users.
		 */
		Packet mess = new Packet(PacketType.Message);
		mess.setDest("testUser2");
		mess.setContents("HELLO");
		mess.setType("2way");
		c2.sendMessage(mess);

		received = c4.readMessage();
		System.out.println("2wayChatTest: User 1 sent message to 2");
		System.out.println(received);
		Assert.assertEquals(PacketType.Message, received.getPacketType());
		Assert.assertEquals("HELLO", received.getContents());
	}
	
	
	@Test
	public void testLogout() throws IOException{
		System.out.println("****Begin Logout Test Record****");
		TestChannel c1 = new TestChannel(), c2 = new TestChannel();
		c1.connectTo(c2);
		UserHandler u = new UserHandler(c1, q);
		new Thread(u).start();
		
		/**
		 * First, login as test user and get ack packet back.
		 */
		Packet login = new Packet(PacketType.Request);
		login.setType("login");
		login.setName("testUser");
		c2.sendMessage(login);
		Packet received = c2.readMessage(); // The ack message
		System.out.println("LogoutTest: User 1 login ack");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		received = c2.readMessage();   // The logged in users message
		System.out.println("LogoutTest: User 1 logged in list");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		
		
		/**
		 * Next, login as second test user.
		 */
		Packet login2 = new Packet(PacketType.Request);
		login2.setName("testUser2");
		login2.setType("login");
		TestChannel c3 = new TestChannel(), c4 = new TestChannel();
		c3.connectTo(c4);
		UserHandler u2 = new UserHandler(c3, q);
		new Thread(u2).start();
		c4.sendMessage(login2);
		// Display the login ack for user 2.
		System.out.println("LogoutTest: User 2 login ack");
		received = c4.readMessage();  // The ack packet
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		/**
		 * Look for expected notifications that login has occured.
		 */
		received = c2.readMessage();  // The login notification packet.
		System.out.println("LogoutTest: User 1 login notify of 2");
		System.out.println(received);
		// Make sure we got the acknowledgement that testUser2 logged in.
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("testUser2", received.getFirstSource());
		
		System.out.println("LogoutTest: User 2 logged in list");
		received = c4.readMessage();   // should be the login userlist
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		
		Packet logout = new Packet(PacketType.Request);
		logout.setType("logout");
		c2.sendMessage(logout);
		
		System.out.println("LogoutTest: User 2 logout notify of 1");
		received = c4.readMessage();   // should be the logout notification
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("logout", received.getType());
		Assert.assertEquals("testUser", received.getFirstSource());
		
	}
	
	@Test
	public void testChatRoom() throws IOException{
		System.out.println("****Begin ChatRoom Test Record****");
		TestChannel c1 = new TestChannel(), c2 = new TestChannel();
		c1.connectTo(c2);
		UserHandler u = new UserHandler(c1, q);
		new Thread(u).start();
		
		/**
		 * First, login as test user and get ack packet back.
		 */
		Packet login = new Packet(PacketType.Request);
		login.setType("login");
		login.setName("testUser");
		c2.sendMessage(login);
		Packet received = c2.readMessage(); // The ack message
		System.out.println("ChatRoomTest: User 1 login ack");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		received = c2.readMessage();   // The logged in users message
		System.out.println("ChatRoomTest: User 1 logged in list");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		
		
		/**
		 * Next, login as second test user.
		 */
		Packet login2 = new Packet(PacketType.Request);
		login2.setName("testUser2");
		login2.setType("login");
		TestChannel c3 = new TestChannel(), c4 = new TestChannel();
		c3.connectTo(c4);
		UserHandler u2 = new UserHandler(c3, q);
		new Thread(u2).start();
		c4.sendMessage(login2);
		// Display the login ack for user 2.
		System.out.println("ChatRoomTest: User 2 login ack");
		received = c4.readMessage();  // The ack packet
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("OK", received.getStatus());
		
		/**
		 * Look for expected notifications that login has occured.
		 */
		received = c2.readMessage();  // The login notification packet.
		System.out.println("ChatRoomTest: User 1 login notify of 2");
		System.out.println(received);
		// Make sure we got the acknowledgement that testUser2 logged in.
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("testUser2", received.getFirstSource());
		
		System.out.println("ChatRoomTest: User 2 logged in list");
		received = c4.readMessage();   // should be the login userlist
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("login", received.getType());
		
		/**
		 * Have second user create chat room named room101
		 */
		Packet chatRequest = new Packet(PacketType.Request);
		chatRequest.setType("joinroom");
		chatRequest.setRoom("room101");
		c4.sendMessage(chatRequest);
		
		received = c4.readMessage();
		System.out.println("ChatRoomTest: User 2 Chat Created Ack");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("joinroom", received.getType());
		Assert.assertEquals("CREATED", received.getStatus());
		
		/**
		 * Now have the other user enter the room as well.
		 */
		Packet chatRequest2 = new Packet(PacketType.Request);
		chatRequest2.setType("joinroom");
		chatRequest2.setRoom("room101");
		c2.sendMessage(chatRequest2);
		
		received = c2.readMessage();
		System.out.println("ChatRoomTest: User 1 Chat Room Joined Ack");
		System.out.println(received);
		Assert.assertEquals(PacketType.Ack, received.getPacketType());
		Assert.assertEquals("joinroom", received.getType());
		Assert.assertEquals("OK", received.getStatus());
		
		received = c2.readMessage();
		System.out.println("ChatRoomTest: User 1 Chat Room UserList");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("joinedroom", received.getType());
		
		received = c4.readMessage();
		System.out.println("ChatRoomTest: User 2 Notified that 1 joined room");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("joinedroom", received.getType());
		Assert.assertEquals("testUser", received.getFirstSource());
		
		/**
		 * Send message to the room.
		 */
		
		Packet roomMess = new Packet(PacketType.Message);
		roomMess.setContents("Hello Room");
		roomMess.setType("room");
		roomMess.setDest("room101");
		
		c2.sendMessage(roomMess);
		received = c4.readMessage();
		System.out.println("ChatRoomTest: User 2 Should get Room Message");
		System.out.println(received);
		Assert.assertEquals(PacketType.Message, received.getPacketType());
		Assert.assertEquals("room", received.getType());
		Assert.assertEquals("testUser", received.getFirstSource());
		Assert.assertEquals("Hello Room", received.getContents());
		
		/**
		 * Leave the room.
		 */
		Packet leaveRoom = new Packet(PacketType.Request);
		leaveRoom.setType("leaveroom");
		leaveRoom.setRoom("room101");
		c2.sendMessage(leaveRoom);
		
		received = c4.readMessage();
		System.out.println("ChatRoomTest: User 2 Should get Notified of leaving room");
		System.out.println(received);
		Assert.assertEquals(PacketType.Notification, received.getPacketType());
		Assert.assertEquals("leftroom", received.getType());
		Assert.assertEquals("testUser", received.getFirstSource());
		Assert.assertEquals("room101", received.getRoom());
	}
	
}
