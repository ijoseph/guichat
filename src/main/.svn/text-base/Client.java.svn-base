package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import client.ClientController;
import client.ClientModel;
import client.gui.MainLoginWindow;

import comm.ChannelFactory;
import comm.RealChannelFactory;

/**
 * GUI chat client runner.
 */
public class Client {
	
	public final static int DEFAULT_PORT = 4444;
	public final static String DEFAULT_HOST = "localhost";
    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) {
    	
    	String host = DEFAULT_HOST;
    	// In case of a command line argument, use this as the hostname.
    	if(args.length>0) host = args[0];
    	
    	// Try to set L&F; don't worry too much if we can't
        try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		} catch (UnsupportedLookAndFeelException e) {
		}
    	MainLoginWindow window = new MainLoginWindow(); // Make new login window
    	ClientModel model = new ClientModel(window);   // Make associated client model
    	ChannelFactory factory = new RealChannelFactory(host, DEFAULT_PORT);
    	ClientController controller = new ClientController(model, factory); // Make controller    	
		window.setController(controller);		    // set controller for main login window
		window.setModel(model);					   // set model for main login window
		window.setVisible(true);		
    }
}
