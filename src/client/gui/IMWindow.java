package client.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import client.ClientController;
import client.Conversation;
import client.PastMessage;

public abstract class IMWindow extends JFrame {
	private static final long serialVersionUID = -6066589993303691125L;
	
	protected Conversation conversation; // associated conversation instance
	protected ClientController controller; //associated controller instance
	protected String chatHistory; // message history of window

	protected javax.swing.JLabel titleLabel;
	protected javax.swing.JTextPane inputTextPane;
	protected javax.swing.JScrollPane chatHistoryScrollPane;
	protected javax.swing.JScrollPane inputTextScrollPane;
	protected JWaitForTextButton sendButton;
	protected javax.swing.JTextPane chatHistoryPane; // message history pane of window
	protected String myUsername; // my user name
	protected String myDestinationName;
	
	public IMWindow(String myUsername) {
		this.myUsername = myUsername;
		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		titleLabel = new javax.swing.JLabel();
		chatHistoryScrollPane = new javax.swing.JScrollPane();
		chatHistoryPane = new javax.swing.JTextPane();
		inputTextScrollPane = new javax.swing.JScrollPane();
		inputTextPane = new javax.swing.JTextPane();
		sendButton = new JWaitForTextButton(inputTextPane);

		titleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText("KIDChat v. 1.0");

		chatHistoryScrollPane.setViewportView(chatHistoryPane);

		sendButton.setText("Send");
		sendButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendButtonActionPerformed(evt);
			}
		});

		inputTextScrollPane.setViewportView(inputTextPane);
		
		// Adding key typed listener to inputTextPane
		inputTextPane.addKeyListener(new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) { // enter pressed
				inputPaneKeyTypedListener(e);			
			}        	
		});
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				chatWindowClosed();
			}
		});
		
		initComponents();
		inputTextPane.requestFocus();
	}
	
	
	protected abstract void initComponents();


	/**
	 * Called upon an ACK message being received by the conversation (or lack thereof)
	 */

	public void errorStatusChanged(){		
		// Getting error status
		Conversation.ErrorState errorStatus = conversation.getLastErrorState();
		if (errorStatus == Conversation.ErrorState.OK){
			// Getting input text
			String inputText = inputTextPane.getText();
			// Clearing input text
			inputTextPane.setText("");
			inputTextPane.setEnabled(true);
			sendButton.processKeyPress();		
			// Adding to history
			updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"red\"><b>" + 
					escape(myUsername) + ": " + "</b></font><font size=\"3\" face=\"Helvetica\" color=\"black\">" +
					escape(inputText) + "</font><br/>");
			
		} else if (errorStatus == Conversation.ErrorState.OFFLINE){ // Offline 
			// reenable text pane, display error message
			// removing new line
			enableUnclearedInput();
			updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">" + myDestinationName + 
					" is not online or is unavailable. Could not send message.</font><br/>");
			
		} else if (errorStatus == Conversation.ErrorState.UNKNOWN){ // Unknown
			enableUnclearedInput();
			updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">" +  
					"Could not send message to " + myDestinationName + ".</font><br/>");
		}
		inputTextPane.requestFocus();
	}

	/**
	 * 
	 */
	protected void updateHistoryPane(String addendum) {
		chatHistory += addendum;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chatHistoryPane.setText(chatHistory);
			}
		});
	}

	/**
	 * Re-enables the input box without clearing the text from it
	 */
	private void enableUnclearedInput() {
		String inputPaneText = inputTextPane.getText();
		while (inputPaneText.endsWith("\n") || inputPaneText.endsWith("\r")){
			inputPaneText = inputPaneText.substring(0,inputPaneText.length()-1);
			inputTextPane.setText( inputPaneText );
		}
		inputTextPane.setEnabled(true);
		sendButton.processKeyPress();
	}
		
	protected static String escape(String s) {
		return s.replaceAll("<", "&lt;").replaceAll(">", "&gt;")
			.replaceAll("&", "&amp;");
	}
	
	/**
	 * Called when the send button is pressed
	 */
	private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
		sendInputToController(inputTextPane.getText());
	}
	/**
	 * Called when enter is pressed in the input pane
	 * @param e
	 */
	private void inputPaneKeyTypedListener(KeyEvent e){
		if (e.getKeyChar()=='\n' || e.getKeyChar() =='\r'){
			String inputPaneText = inputTextPane.getText();    		
			inputPaneText = inputPaneText.substring(0, inputPaneText.length()-2);
			sendInputToController(inputPaneText);
		}    	
	}

	// Controller interface methods
	/**
	 * Sends input message to controller for processing, then clears input text pane
	 */
	private void sendInputToController(String message){
		controller.sendImPressed(message, conversation);
		inputTextPane.setEnabled(false); // Disable text pane and button until message received
		sendButton.setEnabled(false);
	}    

	// Model interface methods
	/**
	 * Associates a conversation with this window
	 */
	public void associateConversation(Conversation convo){
		this.conversation = convo;
	}
	/**
	 * Associates a controller with this window 
	 */
	public void associateController(ClientController controller){
		this.controller = controller;
	}	
	/**
	 * Updates window with message received
	 */
	public void receiveMessage(PastMessage message){
		System.out.println("Received message: "  + message);
		// Add to chat history (using HTML formatting)		
		chatHistory += "<font size=\"3\" face=\"Helvetica\" color=\"blue\"><b>" + 
			escape(message.source) + ": " +  
			"</b></font><font size=\"3\" face=\"Helvetica\" color=\"black\">" +
			escape(message.contents) + "<br/>";		
			chatHistoryPane.setText(chatHistory);
	}
	
	public void chatWindowClosed() {
		inputTextPane.setText("");
	}
	
	public void clear() {
		chatHistory = "";
	}

	public abstract void participantPresentNotify(String username);

	public abstract void participantAbsentNotify(String username);
	
	/**
	 * Useful for testing.
	 * @return The entire chat log.
	 */
	public String getChatHistory() {
		return chatHistory;
	}
}
