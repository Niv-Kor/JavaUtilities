package javaNK.util.GUI.geometric_2D;
import java.awt.Dimension;

import javaNK.util.GUI.QuillPen;

public interface Graphable
{
	public void update(double delta);
	public void render(QuillPen g);
	public void setDimension(Dimension d);
	public Dimension getDimension();
	public Point getPoint();
	public void setX(double x);
	public void setY(double y);
	public double getX();
	public double getY();
}