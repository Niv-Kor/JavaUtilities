package javaNK.util.GUI.geometric_2D;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

import javaNK.util.GUI.QuillPen;

/**
 * This class represents a shape on the plane, having both size and location.
 * A collide is able to tell if another collider touches it.
 * There are two ways to construct a Collider object,
 * one is to create a rectangular, more simple shape,
 * and the other is to create a complex shape that follows an image's outlines.
 * 
 * @author Niv Kor
 */
public class Collider implements Graphable
{
	protected Dimension dim;
	protected Polygon polygon;
	protected Polygon[] masks;
	protected Point point, initPoint;
	protected boolean hidden;
	
	/**
	 * Construct an object of Collider using an image.
	 * 
	 * There are 2 options, that will be chosen by the value of the "trace" argument:
	 * Option #1 - Create an object using the image's rectangular dimensions.
	 * 			   "trace" should be false.
	 * 
	 * Option #2 - Create an object using the image's real outlines, excluding white space behind it.
	 * 			   "trace" should be true.
	 * 			   To use this option, the image must be painted with ONLY black and white,
	 * 			   while the image itself is black and the space behind it is white.
	 * 			   Gray pixels might change the expected result.
	 * 
	 * @param image - The image to constuct the collider over
	 * @param trace - see the explanation above about this argument
	 */
	public Collider(BufferedImage image, boolean trace) {
		initByImagePreference(image, new Point(), trace);
	}
	
	/**
	 * @see Collider(BufferedImage, boolean)
	 * @param point - The point on the plain, where the collider will be
	 */
	public Collider(BufferedImage image, Point point, boolean trace) {
		initByImagePreference(image, point, trace);
	}
	
	/**
	 * @param point - The point on the plain, where the collider will be
	 * @param dim - The dimension of the collider
	 */
	public Collider(Point point, Dimension dim) {
		initRectangular(point, dim);
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param other - The other collider to copy from
	 */
	public Collider(Collider other) {
		this.dim = new Dimension(other.dim);
		this.point = new Point(other.point);
		this.initPoint = new Point(other.initPoint);
		this.polygon = new Polygon(other.polygon.xpoints,
								   other.polygon.ypoints,
								   other.polygon.npoints);
		
		createMasks();
	}
	
	/**
	 * Initiate the collider, using the arguments passed by the constructors.
	 * 
	 * @see constructors
	 */
	protected void initByImagePreference(BufferedImage image, Point point, boolean trace) {
		if (trace) initPolygonial(image, point);
		else initRectangular(point, new Dimension(image.getWidth(), image.getHeight()));
	}
	
	/**
	 * Initiate a non-rectangular collider
	 * 
	 * @see constructors
	 */
	protected void initPolygonial(BufferedImage image, Point point) {
		BinaryImageTracer bit = new BinaryImageTracer(image);
		this.polygon = bit.getPolygon();
		this.initPoint = bit.getInitPoint();
		this.point = new Point(point);
		this.dim = new Dimension(image.getWidth(), image.getHeight());

		createMasks();
	}
	
	/**
	 * Initiate a rectangular collider
	 * 
	 * @see constructors
	 */
	protected void initRectangular(Point point, Dimension dim) {
		int np = 4;
		int[] xp = new int[np];
		int[] yp = new int[np];
		
		xp[0] = (int) Math.round(point.getX());
		yp[0] = (int) Math.round(point.getY());
		
		xp[1] = (int) Math.round(point.getX() + dim.width);
		yp[1] = (int) Math.round(point.getY());
		
		xp[2] = (int) Math.round(point.getX() + dim.width);
		yp[2] = (int) Math.round(point.getY() + dim.height);
		
		xp[3] = (int) Math.round(point.getX());
		yp[3] = (int) Math.round(point.getY() + dim.height);
		
		this.dim = new Dimension(dim);
		this.point = new Point(point);
		this.initPoint = new Point();
		this.polygon = new Polygon(xp, yp, np);
		
		createMasks();
	}
	
	/**
	 * apply another collider's properties to this collider.
	 * 
	 * @param other - The other collider to copy from
	 */
	public void copy(Collider other) {
		this.dim = new Dimension(other.dim);
		this.point = new Point(other.point);
		this.initPoint = new Point(other.initPoint);
		this.polygon = new Polygon(other.polygon.xpoints,
								   other.polygon.ypoints,
								   other.polygon.npoints);
		
		createMasks();
	}
	
	/**
	 * Erase this collider.
	 * Set all of its points to (0, 0).
	 */
	public void erase() {
		int[] x = new int[0], y = new int[0];
		this.polygon = new Polygon(x, y, 0);
	}
	
	/**
	 * Initiate the collider's masks.
	 * Masks are used to identify an interaction between two colliders.
	 */
	protected void createMasks() {
		int n = 8, w = Math.abs(getDimension().width), h = Math.abs(getDimension().height);
		int[] x = new int[n], y = new int[n];
		
		this.masks = new Polygon[4];
		
		//up
		x[0] = 0;
		y[0] = 0;
		
		x[1] = w;
		y[1] = 0;
		
		x[2] = w;
		y[2] = (int) Math.round(4 * h / 5);
		
		x[3] = (int) Math.round(14 * w / 15);
		y[3] = (int) Math.round(4 * h / 5);
		
		x[4] = (int) Math.round(14 * w / 15);
		y[4] = (int) Math.round(h / 2);
		
		x[5] = (int) Math.round(w / 5);
		y[5] = (int) Math.round(h / 2);
		
		x[6] = (int) Math.round(w / 5);
		y[6] = (int) Math.round(4 * h / 5);
		
		x[7] = 0;
		y[7] = (int) Math.round(4 * h / 5);
		
		masks[0] = new Polygon(x, y, n);
		
		//left
		x[0] = 0;
		y[0] = 0;
		
		x[1] = (int) Math.round(4 * w / 5);
		y[1] = 0;
		
		x[2] = (int) Math.round(4 * w / 5);
		y[2] = (int) Math.round(h / 15);
		
		x[3] = (int) Math.round(w / 2);
		y[3] = (int) Math.round(h / 15);
		
		x[4] = (int) Math.round(w / 2);
		y[4] = (int) Math.round(14 * h / 15);
		
		x[5] = (int) Math.round(4 * w / 5);
		y[5] = (int) Math.round(14 * h / 15);
		
		x[6] = (int) Math.round(4 * w / 5);
		y[6] = h;
		
		x[7] = 0;
		y[7] = h;
		
		masks[1] = new Polygon(x, y, n);
		
		//down
		x[0] = 0;
		y[0] = (int) Math.round(h / 5);
		
		x[1] = (int) Math.round(w / 15);
		y[1] = (int) Math.round(h / 5);
		
		x[2] = (int) Math.round(w / 15);
		y[2] = (int) Math.round(h / 2);
		
		x[3] = (int) Math.round(14 * w / 15);
		y[3] = (int) Math.round(h / 2);
		
		x[4] = (int) Math.round(14 * w / 15);
		y[4] = (int) Math.round(h / 5);
		
		x[5] = w;
		y[5] = (int) Math.round(h / 5);
		
		x[6] = w;
		y[6] = h;
		
		x[7] = 0;
		y[7] = h;
		
		masks[2] = new Polygon(x, y, n);
		
		//right
		x[0] = (int) Math.round(w / 5);
		y[0] = 0;
		
		x[1] = w;
		y[1] = 0;
		
		x[2] = w;
		y[2] = h;
		
		x[3] = (int) Math.round(w / 5);
		y[3] = h;
		
		x[4] = (int) Math.round(w / 5);
		y[4] = (int) Math.round(14 * h / 15);
		
		x[5] = (int) Math.round(w / 2);
		y[5] = (int) Math.round(14 * h / 15);
		
		x[6] = (int) Math.round(w / 2);
		y[6] = (int) Math.round(h / 15);
		
		x[7] = (int) Math.round(w / 5);
		y[7] = (int) Math.round(h / 15);
		
		masks[3] = new Polygon(x, y, n);
		updateCoordinates();
	}
	
	/**
	 * Check if the collider touches another collider.
	 * 
	 * @param other - The other collider
	 * @return true if both of the colliders touch eachother.
	 */
	public boolean touch(Collider other) {
		if (isHidden()) return false;
		
		Area a = new Area(getPolygon());
		Area b = new Area(other.getPolygon());
		
		a.intersect(b);
		return !a.isEmpty();
	}
	
	/**
	 * Check if the collider touches another collider in a specific direction.
	 * 
	 * @param other - The other collider
	 * @param direction - The direction to check (applied to the current collider)
	 * @return true if both of the colliders touch eachother in the specified direction.
	 */
	public boolean touch(Collider other, Vector direction) {
		if (isHidden()) return false;
		
		Area a, b, c, d;
		Polygon opposeMaskA, opposeMaskB;
		
		//move mask to the right spot
		opposeMaskA = getMask(direction);
		opposeMaskB = other.getMask(direction.oppose());
		
		a = new Area(getPolygon());
		b = new Area(other.getPolygon());
		c = new Area(opposeMaskA);
		d = new Area(opposeMaskB);
		
		a.intersect(b);
		
		if (!a.isEmpty()) {
			//intersect with both a and b's opposite direction masks
			a.intersect(c);
			a.intersect(d);
			
			//empty means they touched the correct side
			return a.isEmpty();
		}
		else return false;
	}
	
	/**
	 * Update the coordinates of the collider's masks, based on recent changes.
	 */
	protected void updateMaskCoordinates() {
		int w = Math.abs(getDimension().width);
		int h = Math.abs(getDimension().height);
		int x, y;
		
		for (int i = 0; i < masks.length; i++) {
			x = masks[i].xpoints[0];
			y = masks[i].ypoints[0];
			masks[i].translate(-x, -y);
			
			switch (i) {
				case 0: { //up
					x = 0;
					y = 0;
					break;
				}
				case 1: { //left
					x = 0;
					y = 0;
					break;
				}
				case 2: { //down
					x = 0;
					y = (int) Math.round(h / 5);
					break;
				}
				case 3: { //right
					x = (int) Math.round(w / 5);
					y = 0;
					break;
				}
			}
			
			masks[i].translate((int) getX() + x, (int) getY() + y);
		}
	}
	
	/**
	 * @param direction - The direction of the mask
	 * @return the collider's mask of the specified direction.
	 */
	protected Polygon getMask(Vector direction) {
		int opposeMaskIndex;
		
		switch (direction) {
			case UP: opposeMaskIndex = 0; break;
			case LEFT: opposeMaskIndex = 1; break;
			case DOWN: opposeMaskIndex = 2; break;
			case RIGHT: opposeMaskIndex = 3; break;
			default: {
				int[] x = new int[0];
				int[] y = new int[0];
				return new Polygon(x, y, 0);
			}
		}
		
		return masks[opposeMaskIndex];
	}
	
	@Override
	public void setX(double x) {
		if (polygon.npoints == 0) return;
		
		point.setX(x);
		updateCoordinates();
	}
	
	@Override
	public void setY(double y) {
		if (polygon.npoints == 0) return;
		
		point.setY(y);
		updateCoordinates();
	}
	
	/**
	 * Update the coordinates of the collider, based on recent changes.
	 */
	protected void updateCoordinates() {
		//primary point
		if (polygon.npoints > 0) {
			int px = polygon.xpoints[0];
			int py = polygon.ypoints[0];
			
			polygon.translate(-px, -py);
			polygon.translate((int) (getX() + initPoint.getX()),
							  (int) (getY() + initPoint.getY()));
		}
		
		//masks
		int w = Math.abs(getDimension().width);
		int h = Math.abs(getDimension().height);
		int x, y;
		
		for (int i = 0; i < masks.length; i++) {
			x = masks[i].xpoints[0];
			y = masks[i].ypoints[0];
			masks[i].translate(-x, -y);
			
			switch (i) {
				case 0: { //up
					x = 0;
					y = 0;
					break;
				}
				case 1: { //left
					x = 0;
					y = 0;
					break;
				}
				case 2: { //down
					x = 0;
					y = (int) Math.round(h / 5);
					break;
				}
				case 3: { //right
					x = (int) Math.round(w / 5);
					y = 0;
					break;
				}
			}
			
			masks[i].translate((int) getX() + x, (int) getY() + y);
		}
	}
	
	/**
	 * Hide the collider (erase it temporarily).
	 * Can be undone later, while restoring the collider's last coordinates.
	 * 
	 * @param flag - True to hide or false to unhide
	 */
	public void hide(boolean flag) { hidden = flag; }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point A of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point A.
	 */
	public Point getA() { return new Point(getX(), getY()); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point B of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point B.
	 */
	public Point getB() { return new Point(getX() + dim.width, getY()); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point C of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point C.
	 */
	public Point getC() { return new Point(getX() + dim.width, getY() + dim.height); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point D of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point D.
	 */
	public Point getD() { return new Point(getX(), getY() + dim.height); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point E of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point E.
	 */
	public Point getE() { return new Point(getA().getMidBetween(getB())); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point F of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point F.
	 */
	public Point getF() { return new Point(getB().getMidBetween(getC())); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point G of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point G.
	 */
	public Point getG() { return new Point(getC().getMidBetween(getD())); }
	
	/**
	 * A		E		 B
	 *   ----------------
	 *   |				|
	 *   |				|
	 * H |				| F	
	 *   |				|
	 *   |				|
	 *   ----------------
	 * D		G		 C
	 * 
	 * Get point H of the collider.
	 * Works best with rectangular colliders.
	 * 
	 * @return point H.
	 */
	public Point getH() { return new Point(getD().getMidBetween(getA())); }
	
	/**
	 * @return true if the collider is hidder or false otherwise.
	 */
	public boolean isHidden() { return hidden; }
	
	@Override
	public void setDimension(Dimension d) {}
	
	@Override
	public Point getPoint() { return point; }
	
	@Override
	public double getX() { return point.getX(); }
	
	@Override
	public double getY() { return point.getY(); }
	
	@Override
	public Dimension getDimension() { return dim; }
	
	/**
	 * @return a Polygon object with the collider's properties.
	 */
	public Polygon getPolygon() { return polygon; }
	
	@Override
	public void update(double delta) {}
	
	@Override
	public void render(QuillPen g) {}
}