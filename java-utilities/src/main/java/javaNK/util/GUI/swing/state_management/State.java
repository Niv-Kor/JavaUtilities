package javaNK.util.GUI.swing.state_management;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import javax.swing.JPanel;
import javaNK.util.GUI.swing.containers.Window;
import javaNK.util.files.FontHandler;
import javaNK.util.files.FontHandler.FontStyle;

public abstract class State
{
	public static final Font LABEL_FONT = FontHandler.load("Comfortaa", FontStyle.PLAIN, 17);
	
	protected JPanel[] panes;
	private int paneIndex;
	protected Window window;
	protected GridBagConstraints gridConstraints;
	
	/**
	 * @param window - The containing window of the state
	 * @param panesAmount - Amount of panels the state views
	 */
	public State(Window window, int panesAmount) {
		this.panes = new JPanel[panesAmount];
		this.paneIndex = 0;
		this.window = window;
		this.gridConstraints = new GridBagConstraints();
	}
	
	/**
	 * Create a panel of the state.
	 * 
	 * @param layout - The panel's layout model
	 * @param dim - The dimensions of the panel
	 * @param color - The panel's background color (if null, panel is transparent)
	 */
	protected void createPanel(LayoutManager layout, Dimension dim, Color color) {
		panes[paneIndex] = new JPanel(layout);
		createPanel(panes[paneIndex], dim, color);
	}
	
	/**
	 * Create a panel of the state, by supplying an existing panel.
	 * 
	 * @see createPanel(LayoutManager, Dimension, Color)
	 * @param other - The existing panel to add
	 */
	protected void createPanel(JPanel other, Dimension dim, Color color) {
		panes[paneIndex] = other;
		panes[paneIndex].setPreferredSize(dim);

		if (color != null) panes[paneIndex].setBackground(color);
		else panes[paneIndex].setOpaque(false);
		
		paneIndex++;
	}
	
	/**
	 * Insert the state's panels to the window that contains it.
	 * This method should normally be called by the window itself.
	 */
	public abstract void applyPanels();
	
	/**
	 * Insert a panel to the specified location in the window.
	 * 
	 * @param index - The index of the panel (from 'panes' array)
	 * @param location - BorderLayout constant
	 */
	protected void insertPanel(int index, String location) {
		insertPanel(panes[index], location);
	}
	
	/**
	 * @see insertPanel(int, String)
	 * @param index - The index of the panel (from 'panes' array)
	 * @param location - BorderLayout constant
	 */
	protected void insertPanel(JPanel panel, String location) {
		window.insertPanel(panel, location);
	}
	
	/**
	 * Add a component in a grid layout.
	 * 
	 * @param paneIndex - The index of the panel to add the component to
	 * @param c - The component to add
	 * @param x - X axis
	 * @param y - Y axis
	 */
	protected void addComponent(int paneIndex, Component c, int x, int y) {
		gridConstraints.gridx = x;
		gridConstraints.gridy = y;
		panes[paneIndex].add(c, gridConstraints);
	}
	
	/**
	 * @return an array of the state's JPanels.
	 */
	public JPanel[] getPanes() { return panes; }
	
	/**
	 * @return the window this state is applied on.
	 */
	public Window getWindow() { return window; }
	
	/**
	 * Paint on the window's main panel.
	 * 
	 * @param g - Graphics object to paint with
	 */
	public void paintComponent(Graphics g, Dimension windowDim) {}
}