package tests;


import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import comm.Packet;
import comm.TestChannel;
import comm.Packet.PacketType;

public class CommTest {
	
	@Test
	public void testFakeChannel() throws IOException {
		TestChannel c1 = new TestChannel(), c2 = new TestChannel();
		c1.connectTo(c2);
		Packet sent = new Packet(PacketType.Message);
		sent.setContents("This is a message.");
		sent.setDest("Other");
		sent.setType("2way");
		c1.sendMessage( sent );
		Packet received = c2.readMessage();
		Assert.assertEquals(sent, received);
		c1.close();
		Assert.assertFalse(c1.isConnected());
		Assert.assertFalse(c2.isConnected());
		try {
			c1.sendMessage(new Packet(PacketType.Ack));
			Assert.assertTrue(false);	// Triggered if no exception thrown
		}
		catch(IOException e) {}
		try {
			c1.readMessage();
			Assert.assertTrue(false);	// Triggered if no exception thrown
		}
		catch(IOException e) {}
	}
}
