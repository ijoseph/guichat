package tests;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import client.ClientController;
import client.ClientModel;
import client.gui.MainLoginWindow;
import client.gui.MainWindow;

import comm.Channel;
import comm.TestChannel;
import comm.TestChannelFactory;
import comm.TestConnectionHandler;

public class ClientGUITest {
	MainLoginWindow window;
	
	/**
	 * Sets up MainLoginWindow
	 */
	
	@Before public void loginWindowSetUp(){
    	window = new MainLoginWindow(); // Make new login window
    	ClientModel model = new ClientModel(window);   // Make associated client model
    	TestConnectionHandler handler = new TestConnectionHandler();
    	// TODO: create a test server with this handler here?
    	ClientController controller = new ClientController( model, new TestChannelFactory(handler) ); // Make controller    	
    	window.setController(controller);		    // set controller for main login window
    	window.setModel(model);					   // set model for main login window
    	window.setVisible(true);		
	}
	
	/**
	 * Basic test to see if login window MainLoginWindow was created correctly
	 */
	@Test public void loginWindowTest(){
    	Assert.assertNotNull(window);		
	}
	
	/**
	 * Test to see if main window is correctly created from login window 
	 */
	@Test public void mainWindowTest(){
    	MainWindow mainWindow = window.loginSucceeded(); // Simulate controller sending message to MainWindow reporting login success
    	Assert.assertNotNull(mainWindow);    	
	}

}
