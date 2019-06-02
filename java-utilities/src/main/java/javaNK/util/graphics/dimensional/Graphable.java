package javaNK.util.graphics.dimensional;
import java.awt.Dimension;
import javaNK.util.graphics.CustomGraphics;

public interface Graphable
{
	public void update(double delta);
	public void render(CustomGraphics g);
	public void setDimension(Dimension d);
	public Dimension getDimension();
	public Point getPoint();
	public void setX(double x);
	public void setY(double y);
	public double getX();
	public double getY();
}