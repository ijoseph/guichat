package client.gui;

/**
 *
 * @author Isaac
 */
@SuppressWarnings("serial")
public class ChatRoomNameDialog extends javax.swing.JDialog {    
	private javax.swing.JLabel LoginLabel;
	private javax.swing.JTextField chatRoomNameField;
	private JWaitForTextButton enterCreateButton;
	private MainWindow mainWindow;


	/** Creates new form ChatRoomNameDialog */
	public ChatRoomNameDialog(MainWindow mainWindow) {
		this.mainWindow = mainWindow; // sets associated main window
		initComponents();
	}

	private void initComponents() {

		chatRoomNameField = new javax.swing.JTextField();
		enterCreateButton = new JWaitForTextButton(chatRoomNameField);
		LoginLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		chatRoomNameField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				chatRoomNameFieldActionPerformed(evt);
			}
		});

		enterCreateButton.setText("Enter/Create");
		enterCreateButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				enterCreateButtonActionPerformed(evt);
			}
		});

		LoginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		LoginLabel.setText("Enter chat room name below.");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(chatRoomNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(enterCreateButton)
						.addContainerGap())
						.addComponent(LoginLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 77, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(LoginLabel)
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(chatRoomNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(enterCreateButton))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}// </editor-fold>

	// Listeners; updates main window with name of new chat room
	private void chatRoomNameFieldActionPerformed(java.awt.event.ActionEvent evt) {
		sendChatName(chatRoomNameField.getText());
	}

	private void enterCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {
		sendChatName(chatRoomNameField.getText());
	}

	// View interface method
	private void sendChatName(String name){
		mainWindow.createChatRoom(name);
		this.dispose(); // get rid of this dialog
	}


}
