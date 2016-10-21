package comm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Packet {
	/**
	 * Enum for possible types of packets.
	 * @author Mike
	 *
	 */
	public enum PacketType {
		Request,
		Ack,
		Message,
		Notification
	}


	PacketType packetType;
	String username;
	String status;
	List<String> source;
	String type;
	String dest;
	String contents;
	String room;
	String responseFor;
	
	/**
	 * We've included a couple of different constructors here for convenience.
	 */
	public Packet(PacketType packetType, String username,String status,	String source,
			String type,String dest,String contents,String room, String responseFor){
		this.packetType=packetType;
		this.username=username;
		this.status=status;
		this.source=new ArrayList<String>();
		if(source != null)
			this.source.add(source);
		this.type=type;
		this.dest=dest;
		this.contents=contents;
		this.room=room;
		this.responseFor=responseFor;
	}
	public Packet(PacketType packetType){
		this(packetType, null, null, null, null, null, null, null, null);
	}
	
	/**
	 * Creates an Ack packet with given parameters.
	 * @param type
	 * @param responseFor
	 * @param status
	 * @param dest
	 * @return
	 */
	public static Packet createAck(String type, String responseFor, String status, String dest){
		return new Packet(PacketType.Ack, null, status, null, type, dest, null, null, responseFor);
	}
	
	/**
	 * We have getter methods for all of the fields held by a packet.
	 * @return
	 */
	public PacketType getPacketType() {
		return packetType;
	}

	public String getName(){
		return username;
	}
	public String getStatus(){
		return status;
	}
	public String getFirstSource(){
		Iterator<String> iter = source.iterator();
		if(!iter.hasNext())
			return null;
		return iter.next();
	}
	public String getType(){
		return type;
	}
	public String getDest(){
		return dest;
	}
	public String getContents(){
		return contents;
	}
	public String getRoom(){
		return room;
	}
	public String getFor(){
		return responseFor;
	}
	public List<String> getSources() {
		return source;
	}

	/**
	 * Also setter methods for all fields of a packet.
	 * @param s
	 */
	public void setName(String s){
		this.username=s;
	}
	public void setStatus(String s){
		this.status=s;
	}
	public void addSource(String s){
		this.source.add(s);
	}
	public void setType(String s){
		this.type=s;
	}
	public void setDest(String s){
		this.dest=s;
	}
	public void setContents(String s){
		this.contents=s;
	}
	public void setRoom(String s){
		this.room=s;
	}
	public void setFor(String s){
		this.responseFor = s;
	}


	/**
	 * Used to display to console.
	 */
	@Override
	public String toString(){
		return packetType.toString()+"\n"+
		"Username: " + username + "\n"+
		"Status: " + status + "\n"+
		"Sources: " + source + "\n"+
		"Type: " + type + "\n"+
		"Dest: " + dest + "\n"+
		"Contents: " + contents + "\n"+
		"Room: " + room + "\n"+
		"For: " + responseFor + "\n";
	}
	
	/**
	 * @return
	 * Returns an XML string that contains all of the information for the current packet.
	 */
	public String toXMLString(){
		String xml = "";
		xml = xml + "<" + packetType.toString();
		if(type!=null){
			xml = xml + " type=" + "\"" + escape(type) + "\"";
		}
		if(responseFor!=null){
			xml = xml + " for=" + "\"" + escape(responseFor) + "\"";
		}
		xml = xml+">";

		if(username!=null){
			xml = xml+"<name>" + escape(username) + "</name>";
		}
		if(status!=null){
			xml = xml+"<status>" + escape(status) + "</status>";
		}
		if(dest!=null){
			xml = xml+"<dest>" + escape(dest) + "</dest>";
		}
		if(contents!=null){
			xml = xml +"<contents>" + escape(contents) + "</contents>";	
		}
		if(room!=null){
			xml = xml+"<room>" + escape(room) + "</room>";
		}
		if(!source.isEmpty() && source.get(0)!=null){
			for(String s : source){
				xml = xml + "<source>" + escape(s) + "</source>";
			}
		}

		xml = xml + "</" + packetType.toString() + ">";

		return xml;
	}
	
	/**
	 * Returns a new string, so that certain strings do not generate XML parser exceptions.
	 * @param s
	 * @return
	 */
	private static String escape(String s) {
		return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
			.replaceAll("&", "&amp;").replaceAll("\'", "&apos;");
	}
	
	/**
	 * Used to test equality of packets.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) return true;
		if (!(other instanceof Packet)) return false;
		Packet p = (Packet)other;
		return p.contents.equals(this.contents) && p.dest.equals(this.dest)
			&& p.packetType.equals(this.packetType) && p.responseFor.equals(this.responseFor)
			&& p.room.equals(this.room) && p.source.equals(this.source)
			&& p.status.equals(this.status) && p.type.equals(this.type)
			&& p.username.equals(this.username);
			
	}
}
