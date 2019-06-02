package javaNK.util.graphics.dimensional;

import javaNK.util.math.RNG;

/**
 * This enum represents a vector in the space
 * 
 * @author Niv Kor
 *
 */
public enum Vector {
	NONE(0),
	UP(-1),
	DOWN(1),
	LEFT(-1),
	RIGHT(1);
	
	private int vector;
	
	private Vector(int vector) {
		this.vector = vector;
	}
	
	/**
	 * @return the opposite direction vector.
	 */
	public Vector oppose() {
		switch (name()) {
			case "UP": return Vector.DOWN;
			case "DOWN": return Vector.UP;
			case "LEFT": return Vector.RIGHT;
			case "RIGHT": return Vector.LEFT;
			default: return Vector.NONE;
		}
	}
	
	/**
	 * Generate a direction, pointing to the general vector's direction, but slightly off.
	 * 
	 * @return a generated vector.
	 */
	public double generate() {
		if (vector < 0) return RNG.generateDouble(-1, -0.001);
		else if (vector > 0) return RNG.generateDouble(0.001, 1);
		else return 0;
	}
	
	/**
	 * @return a vector that's 90 degrees to the direction.
	 */
	public int straight() { return vector; }
}