package client.gui;

import client.TwoWayConversation;

/** 
 * @author Isaac
 */
public class TwoWayWindow extends IMWindow {
	private static final long serialVersionUID = 1786344280727411992L;
	private javax.swing.JLabel IMWithLabel;
	private javax.swing.JLabel participantLabel;
	

	/**
	 * Constructor
	 * @param username The other user with whom this conversation will take place
	 * @param myUsername The username of the currently logged in user
	 */
	public TwoWayWindow(String username, String myUsername) {
		super(myUsername);
		myDestinationName = username;
		this.setTitle("IM with " + username);
		participantLabel.setText(username);
		chatHistory = ""; // No message history
		chatHistoryPane.setEditable(false); // history is not editable
		chatHistoryPane.setContentType("text/html"); // history uses HTML markup for formatting
	}    

	@Override
	protected void initComponents() {
		// super.initComponents() already called in super constructor
		IMWithLabel = new javax.swing.JLabel();
		participantLabel = new javax.swing.JLabel();

		IMWithLabel.setText("IM With: ");
		//participantLabel.setText(" ");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(inputTextScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(chatHistoryScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
										.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
												.addComponent(IMWithLabel)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(participantLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)))
												.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
						.addGap(8, 8, 8)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(IMWithLabel)
								.addComponent(participantLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(chatHistoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(sendButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(inputTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE))
										.addContainerGap())
		);

		pack();



	}// </editor-fold>

	// Listeners
	
	@Override
	public void chatWindowClosed() {
		super.chatWindowClosed();
		controller.twoWayWindowClosed((TwoWayConversation) conversation);
	}
	

	@Override
	public void participantAbsentNotify(String username) {
		updateHistoryPane( "<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">User " +
			username + " has logged off.</font><br/>");
		
	}

	@Override
	public void participantPresentNotify(String username) {
		updateHistoryPane("<font size=\"3\" face=\"Helvetica\" color=\"#FF4500\">User " +
				username + " has logged on.</font><br/>");
	}
}
