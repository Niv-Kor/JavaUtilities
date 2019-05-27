package java.util.graphics;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class InteractiveLabel extends InteractiveComponent implements MouseListener
{
	private static final long serialVersionUID = 3440174835250357589L;
	private final static Color DEF_HOVER_COLOR = Color.ORANGE;
	private final static Color DEF_SELECT_COLOR = new Color(88, 178, 255);
	
	private Color originColor, hoverColor, selectColor;
	private boolean clicked, enableSelection;
	
	public InteractiveLabel() {
		init();
	}
	
	/**
	 * @param text - Text to be shown
	 */
	public InteractiveLabel(String text) {
		super(text);
		init();
	}
	
	/**
	 * Initiate class members.
	 */
	private void init() {
		addMouseListener(this);
		hoverColor = DEF_HOVER_COLOR;
		selectColor = DEF_SELECT_COLOR;
		enableSelection = true;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		super.setForeground(hoverColor);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		if (!clicked || !enableSelection) super.setForeground(originColor);
		else super.setForeground(selectColor);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		clicked = !clicked;
		if (enableSelection) super.setForeground(selectColor);
	}
	
	/**
	 * Enable or disable the colorization of the label after a click.
	 * If disabled, the label will keep its color even if clicked.
	 * @param flag - True to enable or false to disable
	 */
	public void enableSelectionColor(boolean flag) {
		enableSelection = flag;
	}
	
	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		originColor = color;
	}
	
	/**
	 * Cancel the selection color of the label.
	 * Return it to its previous original color.
	 */
	public void release() {
		clicked = false;
		setForeground(originColor);
	}
	
	/**
	 * @return the color of the label when hovered by the cursor.
	 */
	public Color getHoverColor() { return hoverColor; }
	
	/**
	 * @return the color of the label after clicked.
	 */
	public Color getSelectColor() { return selectColor; }
	
	/**
	 * @param c - The color to paint the label with, upon hovering with the cursor
	 */
	public void setHoverColor(Color c) { hoverColor = c; }
	
	/**
	 * @param c - The color to paint the label with, after clicked
	 */
	public void setSelectColor(Color c) { selectColor = c; }
	
	/**
	 * @return true if the label is clicked or false otherwise.
	 */
	public boolean isClicked() { return clicked; }
}