package client;

import comm.Packet;
import comm.Packet.PacketType;


public class TwoWayConversation extends Conversation {
	String otherParticipant;
	
	public TwoWayConversation(String otherParticipant) {
		this.otherParticipant = otherParticipant;
		lastMessageStatus = ErrorState.OK;
	}
	
	public String getOtherParticipant() {
		return otherParticipant;
	}

	@Override
	public Packet generatePacket(String message) {
		Packet p = new Packet(PacketType.Message);
		p.setDest(otherParticipant);
		p.setContents(message);
		p.setType("2way");
		return p;
	}
}
