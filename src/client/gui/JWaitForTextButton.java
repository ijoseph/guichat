package client.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.text.JTextComponent;

public class JWaitForTextButton extends JButton {
	private static final long serialVersionUID = 2787919919042307065L;
	
	private JTextComponent textField;
	
	public JWaitForTextButton(JTextComponent textField) {
		this.textField = textField;
		processKeyPress();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyevent) {
				processKeyPress();
			}			
		});
	}
	
	public void processKeyPress() {
		setEnabled( textField.getText().length() > 0 );
	}

}
