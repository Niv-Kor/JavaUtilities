package javaNK.util.graphics.dimensional;
import java.awt.geom.Point2D;

/**
 * This class represents a point in space.
 * 
 * @author Niv Kor
 */
public class Point extends Point2D
{
	private double x, y, z;
	
	/**
	 * Construct a 3D point.
	 * 
	 * @param x - X value of the point
	 * @param y - Y value of the point
	 * @param z - Z value of the point
	 */
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Construct a 2D point (Z is 0).
	 * 
	 * @param x - X value of the point
	 * @param y - Y value of the point
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0;
	}
	
	/**
	 * Construct a point in the origin of Euclidean space.
	 */
	public Point() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	/**
	 * Construct a point by copying another point's values,
	 * and moving each of them by a fixed delta.
	 * 
	 * @param other - The point to copy from
	 * @param dx - The length to add to the X value
	 * @param dy - The length to add to the Y value
	 * @param dz - The length to add to the Z value
	 */
	public Point(Point other, double dx, double dy, double dz) {
		this.x = other.x + dx;
		this.y = other.y + dy;
		this.z = other.z + dz;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param other - The point to copy from
	 */
	public Point(Point other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	/**
	 * @param other - The other point
	 * @return true if both of the points' X values are equal.
	 */
	public boolean equalsX(Point other) {
		return this.x == other.x;
	}
	
	/**
	 * @param other - The other point
	 * @return true if both of the points' Y values are equal.
	 */
	public boolean equalsY(Point other) {
		return this.y == other.y;
	}
	
	/**
	 * @param other - The other point
	 * @return true if both of the points' X values are equal.
	 */
	public boolean equalsZ(Point other) {
		return this.z == other.z;
	}
	
	/**
	 * @param other - The other point
	 * @return true if both of the points are equal (by all values).
	 */
	public boolean equals(Point other) {
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}
	
	/**
	 * Check if the point is larger than another point,
	 * taking into consideration only one of the axles.
	 * 
	 * @param other - The other point
	 * @param axis - The axis to take into consideration
	 * @param delta - An allowed deviation, that the point will not be considered larger if not bypassing it 
	 * @return true if the point is larger than the other point
	 */
	public boolean largerThan(Point other, Axis axis, double delta) {
		switch(axis) {
			case X: return this.x > other.x + delta;
			case Y: return this.y > other.y + delta;
			case Z: return this.z > other.z + delta;
			default: System.err.println("Invalid input."); return false;
		}
	}
	
	/**
	 * Check if the point is smaller than another point,
	 * taking into consideration only one of the axles.
	 * 
	 * @param other - The other point
	 * @param axis - The axis to take into consideration
	 * @param delta - An allowed deviation, that the point will not be considered smaller if not bypassing it 
	 * @return true if the point is smaller than the other point
	 */
	public boolean smallerThan(Point other, Axis axis, double delta) {
		switch(axis) {
			case X: return this.x < other.x - delta;
			case Y: return this.y < other.y - delta;
			case Z: return this.z < other.z - delta;
			default: System.err.println("Invalid input."); return false;
		}
	}
	
	/**
	 * Check if the point is whithin the range of another point.
	 * 
	 * @param other - The other point
	 * @param epsilon - The epsilon range (added to both sides)
	 * @return true if the point is whithin the range of the other.
	 */
	public boolean whithinRange(Point other, double epsilon) {
		return withinRange(other, Axis.X, epsilon)
			&& withinRange(other, Axis.Y, epsilon)
			&& withinRange(other, Axis.Z, epsilon);
	}
	
	/**
	 * Check if the point is whithin the range of another point.
	 * 
	 * @param other - The other point
	 * @param axis - The axis to take into consideration
	 * @param epsilon - The epsilon range (added to both sides)
	 * @return true if the point is whithin the range of the other.
	 */
	public boolean withinRange(Point other, Axis axis, double epsilon) {
		switch(axis) {
			case X: return this.x <= other.x + epsilon && this.x >= other.x - epsilon;
			case Y: return this.y <= other.y + epsilon && this.y >= other.y - epsilon;
			case Z: return this.z <= other.z + epsilon && this.z >= other.z - epsilon;
			default: return false;
		}
	}
	
	/**
	 * Get the distance of a point from another point, looking at only one axis.
	 * 
	 * @param other - The other point
	 * @param axis - The axis to look at
	 * @return the distance between the two points, considering only the specified axis.
	 */
	public double distance(Point other, Axis axis) {
		switch(axis) {
			case X: return Math.abs(this.x - other.x);
			case Y: return Math.abs(this.y - other.y);
			case Z: return Math.abs(this.z - other.z);
			default: return 0;
		}
	}
	
	/**
	 * Get the distance of a point from another point.
	 * 
	 * @param other - The other point
	 * @return the distance between the two points.
	 */
	public double distance(Point other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
	}
	
	/**
	 * Get a point that's right in the middle between this one and another one.
	 * 
	 * @param other - The other point
	 * @return a point that's in the middle of the two points.
	 */
	public Point getMidBetween(Point other) {
		double x = (this.x > other.x) ? this.x : other.x;
		double y = (this.y > other.y) ? this.y : other.y;
		return new Point(x - distance(other, Axis.X) / 2, y - distance(other, Axis.Y) / 2);
	}
	
	/**
	 * @return the X value.
	 */
	public double getX() { return x; }
	
	/**
	 * @return the Y value.
	 */
	public double getY() { return y; }
	
	/**
	 * @return the Z value.
	 */
	public double getZ() { return z; }
	
	/**
	 * @param x - The new X value
	 */
	public void setX(double x) { this.x = x; }
	
	/**
	 * @param y - The new Y value
	 */
	public void setY(double y) { this.y = y; }
	
	/**
	 * @param z - The new Z value
	 */
	public void setZ(double z) { this.y = z; }
	
	@Override
	public void setLocation(double x, double y) {
		setX(x);
		setY(y);
	}
	
	@Override
	public String toString() { return new String("(" + x + "," + y + "," + z + ")"); }
}