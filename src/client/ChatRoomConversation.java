package client;

import java.util.ArrayList;
import java.util.List;

import client.gui.ChatRoomWindow;

import comm.Packet;
import comm.Packet.PacketType;

public class ChatRoomConversation extends Conversation {

	String name;
	private List<String> currentParticipants;
	
	public ChatRoomConversation(String name) {
		this.name = name;
		currentParticipants = new ArrayList<String>();
		lastMessageStatus=ErrorState.OK;
	}
	
	public void addParticipant(String participantName) {		
		if (!currentParticipants.contains(participantName)){
			currentParticipants.add(participantName);
			convoWindow.participantPresentNotify(participantName);
		}
		((ChatRoomWindow) convoWindow).updateParticipants();
	}
	
	public boolean removeParticipant(String participantName) {
		System.out.println("REMOVING PARTICIPANT " + participantName);
		boolean participantPresennt = currentParticipants.remove(participantName); 
		convoWindow.participantAbsentNotify(participantName);
		((ChatRoomWindow) convoWindow).updateParticipants();
		return participantPresennt;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getParticipants() {
		return currentParticipants;
	}

	@Override
	public Packet generatePacket(String message) {
		Packet p = new Packet(PacketType.Message);
		p.setDest(name);
		p.setContents(message);
		p.setType("room");
		
		return p;
	}
}
