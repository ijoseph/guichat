package comm;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class XMLParser {

	public static Packet parse(String s){
		try{
			if(s==null) return null;
			/**
			 * First, this code intializes the java built in xml parser for the string s.
			 */
			DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(s));
			Document doc = db.parse(is);

			String rootName = doc.getDocumentElement().getNodeName();
			
			Packet parsed=null;
			/**
			 * Next, we parse the top level element to determine the type of packet.
			 */
			if(rootName.equals("Request")){
				parsed = new Packet(Packet.PacketType.Request);
			}else if(rootName.equals("Message")){
				parsed = new Packet(Packet.PacketType.Message);
			} else if(rootName.equals("Ack")){
				parsed = new Packet(Packet.PacketType.Ack);
			}else if(rootName.equals("Notification")){
				parsed = new Packet(Packet.PacketType.Notification);
			}else return null;
			
			
			
			/**
			 * Finally, we parse all of the possible attributes and sub elements of a transmission,
			 * and put them into the packet being parsed.
			 */
			String type = doc.getDocumentElement().getAttribute("type");
			parsed.setType(type);

			String responseFor = doc.getDocumentElement().getAttribute("for");
			parsed.setFor(responseFor);
			
			
			CharacterData d;
			NodeList nodes = doc.getElementsByTagName("name");
			if(nodes.item(0)!=null){
				d = (CharacterData)nodes.item(0).getFirstChild();
				String name = d.getData();
				parsed.setName(name);
			}
			nodes = doc.getElementsByTagName("room");
			if(nodes.item(0)!=null){
				d = (CharacterData)nodes.item(0).getFirstChild();
				String room = d.getData();
				parsed.setRoom(room);
			}
			nodes = doc.getElementsByTagName("contents");
			if(nodes.item(0)!=null){
				d = (CharacterData)nodes.item(0).getFirstChild();
				String contents = d.getData();
				parsed.setContents(contents);
			}
			nodes = doc.getElementsByTagName("dest");
			if(nodes.item(0)!=null){
				d = (CharacterData)nodes.item(0).getFirstChild();
				String dest = d.getData();
				parsed.setDest(dest);
			}
			nodes = doc.getElementsByTagName("status");
			if(nodes.item(0)!=null){
				d = (CharacterData)nodes.item(0).getFirstChild();
				String status = d.getData();
				parsed.setStatus(status);
			}
			nodes = doc.getElementsByTagName("source");
			for(int i=0; i<nodes.getLength(); i++){
				d = (CharacterData)nodes.item(i).getFirstChild();
				String source = d.getData();
				parsed.addSource(source);
			}
			return parsed;


		} catch(Exception e) {
			System.err.println("WARNING: XML parsing error");
			System.out.println("Received: " + s);
			return null;
		}
	}
}
