package javaNK.util.graphics.components;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;

import javaNK.util.files.ImageHandler;

public class InteractiveIcon extends InteractiveComponent implements MouseListener
{
	private static final long serialVersionUID = -5905973344666262437L;
	
	private ImageIcon icon, selectedIcon;
	
	/**
	 * @param iconPath - The logical path of the icon to show
	 */
	public InteractiveIcon(String iconPath) {
		this(ImageHandler.loadIcon(iconPath));
	}
	
	/**
	 * @param icon - The icon object to show
	 */
	public InteractiveIcon(ImageIcon icon) {
		addMouseListener(this);
		this.icon = icon;
		setIcon(icon);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		if (hovered && selectedIcon != null) super.setIcon(selectedIcon);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseEntered(e);
		if (getIcon() != icon) super.setIcon(icon);
	}
	
	/**
	 * @param iconPath - The new icon's logical path
	 */
	public void setIcon(String iconPath) {
		setIcon(ImageHandler.loadIcon(iconPath));
	}
	
	/**
	 * @param iconPath - The new selected icon's logical path
	 */
	public void setHoverIcon(String iconPath) {
		setSelectedIcon(ImageHandler.loadIcon(iconPath));
	}
	
	/**
	 * @param icon - The new selected icon to show
	 */
	public void setSelectedIcon(ImageIcon icon) {
		selectedIcon = icon;
	}
}