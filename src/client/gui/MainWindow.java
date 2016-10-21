package client.gui;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.ChatRoomConversation;
import client.ClientController;
import client.ClientModel;
import client.ModelInconsistencyException;
import client.TwoWayConversation;
/** 
 * @author Isaac
 */
public class MainWindow extends javax.swing.JFrame {
	private static final long serialVersionUID = -6696186956869070718L;
	// Variables declaration - do not modify
	private javax.swing.JButton IMButton; // IM button
	private javax.swing.JLabel titleLabel; // Label for title
	private javax.swing.JList onlineUserList; // Buddy list GUI List 
	private DefaultListModel onlineUserListModel; // Buddy list list (updates GUI buddyList)
	private javax.swing.JScrollPane jScrollPane1; // Scroll pane for buddy list
	private javax.swing.JSeparator jSeparator1; // Separator for the status bar
	private javax.swing.JButton joinChatButton; // Join chat button
	private javax.swing.JButton logoutButton; // Log out button
	private javax.swing.JLabel statusLabel; // Label for status bar
	private javax.swing.JLabel usernameLabel; // Label for user name
	private MainLoginWindow loginWindow; //Associated login window used for logging out
	private ClientController controller; // Associated client controller
	private ClientModel model;
	private String selectedOnlineUser; // Online user selected in Buddy list (used if IM button is pressed)
	private String myUsername; // my user name
	private boolean userIsSelected; // Boolean used to update if an online user is selected in the buddy list
	private javax.swing.JLabel myUsernameLabel; // Label whose sole purpose is to display the username of the currently logged in user
	

	/** Creates new form MainWindow */
	public MainWindow(MainLoginWindow loginWindow, ClientController controller, ClientModel model) {
		super("KIDChat"); // Naming this window
		this.loginWindow = loginWindow; // Associated login window
		this.controller = controller; // client controller
		this.model = model;
		initComponents();
		// Configuring buddy list 
		onlineUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userIsSelected = false;
		// Formatting labels
		usernameLabel.setText("Online Users");
		usernameLabel.setFont(new Font("Helvetica",Font.PLAIN,11));
		titleLabel.setFont(new Font("Helvetica", Font.BOLD,13));
		statusLabel.setFont(new Font("Helvetica", Font.PLAIN,11));
		myUsernameLabel.setHorizontalAlignment(JLabel.CENTER);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowevent) {
				setVisible(false);	// Hide window right away even before sending logout
				sendLogoutToController();
			}
		});
		
		onlineUserList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() > 1) {
					sendNewIMToController((String) onlineUserList.getSelectedValue());
				}
			}
		});
	}

	private void initComponents() {

		titleLabel = new javax.swing.JLabel();
		statusLabel = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		IMButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		onlineUserList = new javax.swing.JList(onlineUserListModel = new DefaultListModel());
		joinChatButton = new javax.swing.JButton();
		logoutButton = new javax.swing.JButton();
		usernameLabel = new javax.swing.JLabel();
		myUsernameLabel = new javax.swing.JLabel();
		/*
		 * Adding list selection listeners
		 */
		onlineUserList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged (ListSelectionEvent e){
				listSelectionActionPerformed(e);        		
			}

		});


		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusTraversalPolicyProvider(true);

        titleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("KIDChat v. 1.0");

        statusLabel.setText(" ");

        IMButton.setText("IM");
        IMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IMButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 7));
        jScrollPane1.setViewportView(onlineUserList);

        joinChatButton.setText("Join Chat Room");
        joinChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinChatButtonActionPerformed(evt);
            }
        });

        logoutButton.setText("Logout");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        myUsernameLabel.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(myUsernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(IMButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(joinChatButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(myUsernameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IMButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(joinChatButton, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(statusLabel))
        );

        pack();
	}// </editor-fold>

	// Listeners
	/**
	 * Listener for the Join Chat Room button. Creates new window and updates UI and Model through controller.
	 */
	private void joinChatButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
		// Create a new ChatWindow name dialog window
		new ChatRoomNameDialog(this).setVisible(true);
	}                                              
	/**
	 * Listener for the IM button. Creates new window and updates UI and Model through controller.
	 * @param evt
	 */
	private void IMButtonActionPerformed(java.awt.event.ActionEvent evt) {                                     
		if (userIsSelected){
			sendNewIMToController(selectedOnlineUser); // update model through controller
		}else{
			statusLabel.setText("Select an online user");
		}

	}    
	/**
	 * Listener for the selection of a buddy in the online users buddy list
	 * @param evt
	 */
	private void listSelectionActionPerformed(ListSelectionEvent evt){
		if (onlineUserList.getSelectedIndex()!= -1){
			selectedOnlineUser = (String) onlineUserListModel.get(onlineUserList.getSelectedIndex()); // Updating selected online user
			statusLabel.setText(selectedOnlineUser); 
			userIsSelected = true;
		}else{
			statusLabel.setText("Online Users");
			userIsSelected = false;
		}

	}
	/**
	 * Listener for logout button; logs out, calls the relevant method on the 
	 * controller, then sets the login window visible
	 */
	private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
		// Make main window invisible
		this.setVisible(false);   	
		// Call controller to reset model to original state
		this.sendLogoutToController();
		loginWindow.reset();
		loginWindow.setVisible(true); // set login window visible		
	}    


	// Controller interface methods
	/**
	 * Handles logout button pressed by sending logout info to controller
	 */
	private void sendLogoutToController(){
		try {
			controller.logoutPressed();
		} catch (ModelInconsistencyException e) {
			// Exit with error:
			controller.exitWithError("Fatal progam data error. Closing program.");
		}    	
	}

	/**
	 * Handles IM button by sending info to controller (username known)
	 */
	private void sendNewIMToController(String username){
		// Alert controller of new IM, which makes a new conversation object or puts this conversation on top
		controller.nameSelectedFromList(username); 
	}

	// Model interface methods
	/**
	 * Updates the buddy list with the correct online users from the model
	 */
	public void updateOnlineUsers(){
		onlineUserListModel.clear();
		for (String username: model.getOnlineUsers()){
			onlineUserListModel.addElement(username);
		}
	}
	/**
	 * Makes and displays a new two way window with associated conversation in response to request by ClientModel 
	 */
	public TwoWayWindow makeNewTwoWayWindow(TwoWayConversation convo){		
		TwoWayWindow newWindow = new TwoWayWindow(convo.getOtherParticipant(), myUsername);
		newWindow.associateConversation(convo);
		newWindow.associateController(controller);
		newWindow.setVisible(true);
		return newWindow;
	}
	
	/**
	 * Makes and displays a chat room window with associated conversation in response to request by ClientModel
	 * @param convo
	 * @return
	 */
	public ChatRoomWindow makeNewChatRoomWindow(ChatRoomConversation convo){
		ChatRoomWindow chatRoomWindow = new ChatRoomWindow(myUsername, convo.getName());		
		chatRoomWindow.associateConversation(convo);
		chatRoomWindow.associateController(controller);
		chatRoomWindow.setVisible(true); // creates new chat room window
		return chatRoomWindow;
	}
	
	// View interface methods (used for preloading main window)
	
	public void setMyUsername(String myUsername){
		this.myUsername = myUsername;
		myUsernameLabel.setText("Logged in as: " + myUsername);
	}
	
	/**
	 * Creates a new chat room window, called by the dialog
	 * @param name
	 */
	public void createChatRoom(String name){
		try {
			controller.joinChatRoomRequested(name);
		} catch (ModelInconsistencyException e) {			
			System.err.println("Create chat room failed");
		} // calling model to add chat room
	}

}
