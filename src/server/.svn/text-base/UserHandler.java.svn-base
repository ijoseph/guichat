package server;

import java.io.IOException;

import comm.Channel;
import comm.Packet;
import comm.Packet.PacketType;

public class UserHandler implements Runnable {

	private String username;
	private Channel channel;

	private QueueHandler qHandler;
	
	
	public UserHandler(Channel c, QueueHandler q) throws IOException {
		this.channel = c;
		this.qHandler = q;
	}

	@Override
	public void run() {
		/**
		 * First, do login protocol within a try/catch block.
		 */
		try {            
			Packet login = channel.readMessage();
			// This function will parse the login string.
			// If it returns null, got unexpected message.
			Packet loginAckPacket = Packet.createAck("login", "Request", null, null); 
			
			if(login!=null && (username=login.getName())!=null) {
				if( qHandler.get(username)==null) { 
					qHandler.put(username, this);
					loginAckPacket.setStatus("OK");
					channel.sendMessage(loginAckPacket);
					Thread.sleep(50);  // In very rare cases, can get notification before ack, this prevents.
					qHandler.add(login);
				}
				else {
					loginAckPacket.setStatus("Username taken.");
					channel.sendMessage(loginAckPacket);
					// Need to close everything and end thread.
					channel.close();
					return;
				}
			} else {
				loginAckPacket.setStatus("UNKNOWN ERROR");
				channel.sendMessage(loginAckPacket);
				// Need to close everything and end thread.
				channel.close();
				return;
			}
		} catch(IOException e){
			System.err.println("IO1 Exception for user "+ username);
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		/**
		 * This next try/catch block contains the main loop, where our thread waits to receive messages
		 * from the user, and puts these in the queue.
		 */
		try {
			Packet nextMessage;
			while( (nextMessage = channel.readMessage())!=null ) {
				if(nextMessage.getPacketType()==PacketType.Request && nextMessage.getType().equals("logout")){
					break;
				}
				nextMessage.addSource(username);
				qHandler.add(nextMessage);
				
			}
		} catch (IOException e) {
			System.err.println("IO2 Exception for user "+ username);
			e.printStackTrace();
		} finally {
			try {
				// Attempt to close all resources.
				channel.close();
			} catch (IOException e) {
				System.err.println("IO3 Exception for user "+ username);
				e.printStackTrace();
			} finally {
				// Regardless of exceptions, we always print out disconnect,  remove user from map, and send notifications.
				Packet logout = new Packet(PacketType.Request);
				logout.addSource(username);
				logout.setType("logout");
				qHandler.add(logout);
				qHandler.remove(username);
			}	
		}
	}

	/**
	 * Called by the queue handler to forward a message to this user. 
	 * @param message
	 */
	public synchronized boolean sendMessage(Packet message){
		// This will write out to the socket with a message.
		try {
			channel.sendMessage(message);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
}
