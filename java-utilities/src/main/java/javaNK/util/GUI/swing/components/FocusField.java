package javaNK.util.GUI.swing.components;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JPasswordField;

public class FocusField extends JPasswordField implements FocusListener
{
	private static final long serialVersionUID = -710257021583144498L;
	
	private String defaultMessage;
	private boolean isPassword;
	
	public FocusField() {
		this("");
	}
	
	public FocusField(String defMessage) {
		this.defaultMessage = new String(" " + defMessage);
		addFocusListener(this);
		setEchoChar((char) 0);
		focusLost(null);
	}
	
	@Override
	public String getText() {
		String text = getDecodedPassword();
		return (!text.equals(defaultMessage)) ? text : "";
	}
	
	public String getDecodedPassword() { return String.valueOf(super.getPassword()); }
	
	@Override
	public void focusGained(FocusEvent arg0) {
		if (getDecodedPassword().equals(defaultMessage)) {
			if (isPassword) setEchoChar('\u25CF');
			setText("");
			setForeground(Color.BLACK);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		 if (getDecodedPassword().equals("")) {
			if (isPassword) setEchoChar((char) 0);
			setText(defaultMessage);
			setForeground(Color.GRAY);
		 }
	}
	
	public void encode(boolean flag) { isPassword = flag; }
	
	public void clear() {
		setText("");
		focusLost(null);
	}
	
	public boolean isBlank() { return getText().equals(defaultMessage); }
}