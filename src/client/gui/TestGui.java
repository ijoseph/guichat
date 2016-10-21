package client.gui;

import info.clearthought.layout.TableLayout;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import comm.Channel;
import comm.Packet;
import comm.RealChannel;
import comm.XMLParser;

/**
 * This was used only in the initial stages of coding, as a bare bones system for sending messages
 * to and from the server.
 * @author Mike
 *
 */
public class TestGui extends JFrame {

	private static final long serialVersionUID = 1L;
	private final JTextField message;
	private final JButton b;

	private static Channel channel;

	private static DefaultListModel outputDisplayModel; 
	/**
	 * Make a TestGUI window.
	 */
	public TestGui() {
		super("KID Chat Test GUI");

		// call System.exit() when user closes the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cp = this.getContentPane();
		/**
		 * Setup the layout model for the window.
		 */
		double size[][] =
		{{5, TableLayout.PREFERRED, 5, TableLayout.FILL, 5, TableLayout.PREFERRED, 5},  // Columns
				{5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, TableLayout.FILL, 5}}; // Rows

		cp.setLayout(new TableLayout(size));

		cp.add(new JLabel("Message: "), "1, 1, 1, 1");

		cp.add(message = new JTextField(20), "3, 1, 3, 1");

		/**
		 * Action listener to send messages received to the server.
		 * @author Mike
		 *
		 */
		class Send implements ActionListener{
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Send the message in the text field to the server
				String s = message.getText();
				try {
					channel.sendMessage(XMLParser.parse(s));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		message.addActionListener(new Send());

		/**
		 * Add display of the console.
		 */
		outputDisplayModel = new DefaultListModel();
		JList console = new JList(outputDisplayModel);
		cp.add(new JScrollPane(console), "3, 4, 3, 4");

		/**
		 * Add send button
		 */
		b = new JButton("Send");
		cp.add(b, "5, 1, 5, 1");
		b.addActionListener(new Send());

		this.pack();
	}

	/**
	 * Main method.  Makes and displays a TestGui.
	 * @param args Command-line arguments.  Ignored.
	 */
	public static void main(String[] args) {



		// In general, Swing objects should only be accessed from
		// the event-handling thread -- not from the main thread
		// or other threads you create yourself.  SwingUtilities.invokeLater()
		// is a standard idiom for switching to the event-handling thread.
		try {
			channel = new RealChannel(new Socket("localhost", 4444));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run () {
				// Make and display the WordFinder window.
				new TestGui().setVisible(true);
			}
		});
		try{
			while(true){
				final Packet p = channel.readMessage();
				final String out = p.toXMLString();
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						outputDisplayModel.addElement(out);
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
