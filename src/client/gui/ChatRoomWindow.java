package client.gui;

import java.util.List;

import javax.swing.DefaultListModel;

import client.ChatRoomConversation;

/**
 * @author Isaac
 */
public class ChatRoomWindow extends IMWindow {
	private static final long serialVersionUID = 3337543071430680323L;
	// Variables declaration - do not modify
	private javax.swing.JList chatMemberList;
	private javax.swing.JScrollPane chatMemberScrollPane;
	private javax.swing.JLabel chatRoomLabel;
	private javax.swing.JLabel chatRoomNameDispLabel;
	private javax.swing.JLabel chatRoomParticipantsLabel;	
	private DefaultListModel chatMemberListModel; // List model for online chat room participants

	/** Creates new form ChatRoomWindow */
	public ChatRoomWindow(String myUsername, String chatRoomName) {
		super(myUsername);
		myDestinationName = chatRoomName;
		chatHistory = ""; // No message history
		chatHistoryPane.setEditable(false); // history is not editable
		chatHistoryPane.setContentType("text/html"); // history uses HTML markup for formatting		
		this.setTitle("Chat: "+ chatRoomName);	
		this.chatRoomNameDispLabel.setText(chatRoomName);
	}


	protected void initComponents() {		
		chatRoomLabel = new javax.swing.JLabel();
		chatRoomNameDispLabel = new javax.swing.JLabel();
		chatMemberScrollPane = new javax.swing.JScrollPane();
		chatMemberList = new javax.swing.JList(chatMemberListModel = new DefaultListModel());
		chatRoomParticipantsLabel = new javax.swing.JLabel();

		inputTextScrollPane.setViewportView(inputTextPane);


		chatHistoryScrollPane.setViewportView(chatHistoryPane);

		titleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setText("KIDChat v. 1.0");

		chatRoomLabel.setText("Chat room:");

		chatRoomNameDispLabel.setText(" ");

		chatMemberScrollPane.setViewportView(chatMemberList);

		chatRoomParticipantsLabel.setText("Participants:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
										.addContainerGap())
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
																.addComponent(inputTextScrollPane)
																.addComponent(chatHistoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
																.addGroup(layout.createSequentialGroup()
																		.addComponent(chatRoomLabel)
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(chatRoomNameDispLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)))
																		.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(layout.createSequentialGroup()
																						.addComponent(chatRoomParticipantsLabel)
																						.addGap(60, 60, 60))
																						.addGroup(layout.createSequentialGroup()
																								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
																										.addComponent(chatMemberScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
																										.addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
																										.addContainerGap())))))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(chatRoomLabel)
								.addComponent(chatRoomNameDispLabel)
								.addComponent(chatRoomParticipantsLabel))
								.addGap(13, 13, 13)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(chatHistoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(chatMemberScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
												.addComponent(inputTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
												.addContainerGap())
		);

		pack();
	}// </editor-fold>

	// Model interface method
	
	/**
	 * updates participants in chat 
	 * @param participantName 
	 */
	public void updateParticipants() {		
		List<String> participantList = ((ChatRoomConversation) conversation).getParticipants(); // Get participant list		
		chatMemberListModel.clear();
		for (String participant: participantList){
			chatMemberListModel.addElement(participant);
		}
	}

	@Override
	public void participantAbsentNotify(String username) {
		updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">User " +
			username + " has left the room.</font><br/>");
	}


	@Override
	public void participantPresentNotify(String username) {
		updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">User " +
				username + " has joined the room.</font><br/>");
	}
	
	@Override
	public void chatWindowClosed() {
		super.chatWindowClosed();
		clear();
		controller.chatRoomWindowClosed(((ChatRoomConversation) conversation));
	}
}
