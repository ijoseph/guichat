package client.gui;

import client.ClientController;
import client.ClientModel;
import client.ModelInconsistencyException;

/** 
 * @author Isaac
 */
public class MainLoginWindow extends javax.swing.JFrame {
	private static final long serialVersionUID = -3340828862955999911L;
	// Variables declaration
	private JWaitForTextButton loginButton;
	private javax.swing.JLabel loginLabel;
	private javax.swing.JTextField usernameField;
	private javax.swing.JLabel usernameLabel;
	private ClientController controller; // controller associated with this login window
	private ClientModel clientModel;

	/** Creates new form MainLoginWindow */
	public MainLoginWindow() {
		super("KIDChat: Login");
		initComponents();
	}
	
	private void initComponents() {
		loginLabel = new javax.swing.JLabel();
		usernameLabel = new javax.swing.JLabel();
		usernameField = new javax.swing.JTextField();
		loginButton = new JWaitForTextButton(usernameField);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		loginLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		loginLabel.setText("Welcome to KIDChat!");

		usernameLabel.setText("Username:");

		usernameField.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				UsernameFieldActionPerformed(evt);
			}
		});

		loginButton.setText("Login");
		loginButton.setEnabled(false);
		loginButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				LoginButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(usernameLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(loginButton)
						.addContainerGap())
						.addComponent(loginLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(loginLabel)
						.addGap(18, 18, 18)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(usernameLabel)
								.addComponent(loginButton))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		pack();
	}// </editor-fold>

	// Listeners  
	/**
	 * Called when enter is pressed. Signals controller to create and send a login request.
	 */
	private void UsernameFieldActionPerformed(java.awt.event.ActionEvent evt) {
		if( loginButton.isEnabled() )
			userPressedLogin();
	}
	/**
	 * Called when "Login" button is pressed. Signals controller to create and send in a login request.
	 * @param evt
	 */
	private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		userPressedLogin();
	}

	// Controller interface methods
	/**
	 * Signals controller to create and send login request.
	 */
	private void userPressedLogin(){
		// Update view
		this.currentlyLoggingIn();		
		String username = usernameField.getText(); // Get username text from field
		try{
		controller.loginPressed(username);	
		} catch (ModelInconsistencyException e) {			
			controller.exitWithError(e.getMessage());
		}
	}
	
	/**
	 * Helper method;
	 * Changes window to reflect currently logging in status
	 */
	private void currentlyLoggingIn(){
		// Change label to display "logging in ... "
		loginLabel.setText("Logging in...");
		// Change text field and login button to be disabled
		usernameField.setEnabled(false);
		loginButton.setEnabled(false);
	}
	
	// Model interface methods	
	/**
	 * Notifies user that login failed and allows user to re-login 
	 */
	public void loginFailed(String status){
		// Change label to display (login failed; try again)
		loginLabel.setText(status);
		// Change text field and login button to be enabled
		usernameField.setText("");
		usernameField.setEnabled(true);
		loginButton.setEnabled(true);
	}
	
	/**	 
	 * If login succeeded, close this window and create a new main window.
	 * @return instance of main window
	 */
	public MainWindow loginSucceeded(){
		// Create new main window
		MainWindow main = new MainWindow(this, controller, clientModel);
		main.setMyUsername(usernameField.getText());
		// Hide this window and show new window
		main.setVisible(true);
		this.setVisible(false);
		return main;
	}

	// Main interface methods
	/**
	 * Sets controller
	 */
	public void setController(ClientController cont){
		this.controller = cont;
	}	
	/**
	 * Sets model
	 */
	public void setModel(ClientModel mod){
		this.clientModel = mod;
	}
	
	// View interface methods
	public void reset(){
		loginButton.setEnabled(true);
		usernameField.setEnabled(true);
		loginLabel.setText("Welcome to KIDChat!");
	}
}
