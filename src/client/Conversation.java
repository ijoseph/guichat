package client;

import client.gui.IMWindow;

import comm.Packet;

public abstract class Conversation {

	/**
	 * Local class to time out messages for conversations
	 * @author Jesse
	 *
	 */
	private class MessageTimeoutTimer implements Runnable {
		Conversation convo;
		
		public MessageTimeoutTimer(Conversation conversation) {
			convo = conversation;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(TIMEOUT);
				//  Make sure someone else didn't get in there first and change it
				if(convo.getLastErrorState().equals(ErrorState.PENDING))
					convo.setLastErrorState(ErrorState.UNKNOWN);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	private static final int TIMEOUT = 8000;
	private Thread lastTimerThread = null;
	
	public enum ErrorState {
		PENDING,
		OK,
		OFFLINE,
		UNKNOWN
	}
	
	protected ErrorState lastMessageStatus =  ErrorState.OK;
	protected IMWindow convoWindow;
	
	public void setLastErrorState(ErrorState state) {
		if(lastTimerThread != null) {
			lastTimerThread.interrupt();
			lastTimerThread = null;
		}
		lastMessageStatus = state;
		convoWindow.errorStatusChanged();
	}
	
	public ErrorState getLastErrorState() {
		return lastMessageStatus;
	}
	
	public void setWindow(IMWindow window) {
		convoWindow = window;
	}

	public void addMessage(Packet p) {
		convoWindow.receiveMessage( new PastMessage(p.getContents(), p.getFirstSource() ) );
		convoWindow.setVisible(true);   // Make sure window is still visible
	}
	
	public abstract Packet generatePacket(String message);
	
	public void startMessageTimer() {
		setLastErrorState(ErrorState.PENDING);
		lastTimerThread = new Thread(new MessageTimeoutTimer(this));
		lastTimerThread.start();
	}

	// Useful for testing
	public IMWindow getWindow() {
		return convoWindow;
	}
}
