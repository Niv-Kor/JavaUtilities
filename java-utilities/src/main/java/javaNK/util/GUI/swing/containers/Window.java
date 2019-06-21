package javaNK.util.GUI.swing.containers;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javaNK.util.GUI.swing.state_management.State;

public abstract class Window extends JFrame
{
	protected static class MainPanel extends JPanel
	{
		private static final long serialVersionUID = 6314901980861191552L;
		
		private Window window;
		
		public MainPanel(Window window, Dimension dim) {
			super(new BorderLayout());
			setPreferredSize(dim);
			setBackground(window.getColor());
			this.window = window;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			State currentState = window.getCurrentState();
			if (currentState != null) currentState.paintComponent(g, getPreferredSize());
		}
	}
	
	private static final long serialVersionUID = 8576706402811297251L;
	
	protected MainPanel mainPanel;
	protected State currentState;
	
	/**
	 * @param title - The title of the window
	 * @param dim - Window's dimension
	 */
	public Window(String title) {
		super(title);
		init(getDimension());
	}
	
	/**
	 * @param title - The title of the window
	 * @param dim - Window's dimension
	 */
	public Window(String title, Dimension dim) {
		super(title);
		init(dim);
	}
	
	/**
	 * Initiate class members.
	 */
	private void init(Dimension dim) {
		setSize(dim);
		setResizable(false);
		
		this.mainPanel = new MainPanel(this, dim);
		add(mainPanel);
		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Insert a panel to the requested location in the window.
	 * The 'location' parameter will accept a BorderLayout constant if the panel uses 'BorderLayout' layout,
	 * or a GridBagConstraints object if the panel uses 'GridBagLayout' layout.
	 * 
	 * @param panel - The panel to insert
	 * @param location - Where should it be inserted
	 */
	public void insertPanel(JPanel panel, Object location) {
		try { mainPanel.add(panel, (String) location); }
		catch(IllegalArgumentException | ClassCastException e) {
			System.err.println("The location argument (\"" + location + "\") must be a BorderLayout constant.");
		}
	}
	
	/**
	 * Remove a panel from the window.
	 * 
	 * @param panel - The panel to remove
	 */
	public void removePanel(JPanel panel) {
		if (panel != null) mainPanel.remove(panel);
	}
	
	/**
	 * Change the state in the window.
	 * 
	 * @param currentState - The current state to remove
	 * @param newState - The new state to apply
	 */
	public void applyState(State newState) {
		//remove previous state if needed
		if (currentState != null) {
			JPanel[] oldPanes = currentState.getPanes();
			
			for (int i = 0; i < oldPanes.length; i++)
				removePanel(oldPanes[i]);
		}
		
		//apply new state
		newState.applyPanels();
		currentState = newState;
		setVisible(true);
	}
	
	/**
	 * @return the main panel.
	 */
	public JPanel getMainPanel() { return mainPanel; }
	
	/**
	 * @return the current state that's being displayed in the window.
	 */
	public State getCurrentState() { return currentState; }
	
	/**
	 * @return dimensions of the window.
	 */
	public abstract Dimension getDimension();
	
	/**
	 * @return the background color of the window.
	 */
	public abstract Color getColor();
	
	/**
	 * @return true if the window should be displayed the moment it's constructed.
	 */
	protected abstract boolean DisplayRightAway();
}