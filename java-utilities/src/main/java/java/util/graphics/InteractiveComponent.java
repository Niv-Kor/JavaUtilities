package java.util.graphics;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Callable;

import javax.swing.Icon;
import javax.swing.JLabel;

public abstract class InteractiveComponent extends JLabel implements MouseListener
{
	private static final long serialVersionUID = 6779388611159602543L;
	
	protected Callable<?> func;
	protected boolean hovered, hoverEnabled, funcEnabled;
	
	protected InteractiveComponent() {
		init();
	}
	
	/**
	 * @param text - The text to show in the component
	 */
	protected InteractiveComponent(String text) {
		super(text);
		init();
	}
	
	/**
	 * @param icon - The icon to show in the component
	 */
	protected InteractiveComponent(Icon icon) {
		super(icon);
		init();
	}
	
	/**
	 * Initiate class members.
	 */
	private void init() {
		this.hoverEnabled = true;
		this.funcEnabled = true;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (func != null) {
			try { func.call(); }
			catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	@Override
	public void mouseEntered(MouseEvent e) { hovered = true; }
	
	@Override
	public void mouseExited(MouseEvent e) { hovered = false; }
	
	/**
	 * @return true if the cursor is hovering above the component.
	 */
	public boolean isHovered() { return false; }
	
	/**
	 * @return the function that runs upon a mouse press.
	 */
	public Callable<?> getFunction() { return func; }
	
	/**
	 * @param function - The function to run upon a mouse press
	 */
	public void setFunction(Callable<?> function) { func = function; }
	
	/**
	 * @param flag - True to enable component changes when hovered with the mouse,
	 * 				 or false to disable
	 */
	public void enableHover(boolean flag) { hoverEnabled = flag; }
	
	/**
	 * @param flag - True to enable functionality upon a click, or false to disable
	 */
	public void enableFunction(boolean flag) { funcEnabled = flag; }
}