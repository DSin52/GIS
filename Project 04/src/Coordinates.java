import java.util.ArrayList;

/**
 * This class represents the Coordinate object that implmenets comparable
 * interface. It is used as the object to be inserted into the quadtree.
 * 
 */
public class Coordinates implements Compare2D<Coordinates> {

	private long xcoord;
	private long ycoord;
	ArrayList<Long> offsets;

	/**
	 * Default constructor that sets up coordinates of GISRecord and also has a
	 * list containing all GISRecords with same location.
	 * 
	 * @param x
	 * @param y
	 */
	public Coordinates(long x, long y) {
		xcoord = x;
		ycoord = y;
		offsets = new ArrayList<Long>();
	}

	/**
	 * Returns information on all records in this object Returns the
	 * x-coordinate field of the user data object.
	 * 
	 * @return x
	 */
	public long getX() {
		return xcoord;
	}

	/**
	 * Returns y coordinate
	 * 
	 * @return y
	 */
	public long getY() {
		return ycoord;
	}

	/**
	 * Returns offset list.
	 * 
	 * @return offset list
	 */
	public ArrayList<Long> getList() {
		return offsets;
	}

	/**
	 * Returns indicator of the direction to the user data object from the
	 * location (X, Y) specified by the parameters. The indicators are defined
	 * in the enumeration Direction, and are used as follows:
	 * 
	 * NE: vector from (X, Y) to user data object has a direction in the range
	 * [0, 90) degrees (relative to the positive horizontal axis NW: same as
	 * above, but direction is in the range [90, 180) SW: same as above, but
	 * direction is in the range [180, 270) SE: same as above, but direction is
	 * in the range [270, 360) NOQUADRANT: location of user object is equal to
	 * (X, Y)
	 * 
	 * @param X
	 *            x coordinate
	 * @param Y
	 *            y coordinate
	 * @return Direction from those coordinates
	 */

	public Direction directionFrom(long X, long Y) {
		Coordinates userDataObj = new Coordinates(X, Y);
		if (getX() - X < userDataObj.getX()) {
			if (getY() - Y > userDataObj.getY()) {
				return Direction.NW;
			} else {
				return Direction.SW;
			}
		} else if (getX() > userDataObj.getX()) {
			if (getY() - Y >= userDataObj.getY()) {
				return Direction.NE;
			} else {
				return Direction.SE;
			}
		} else if (getX() - X == userDataObj.getX()) {
			if (getY() - Y > userDataObj.getY()) {
				return Direction.NW;
			} else {
				return Direction.SE;
			}
		} else if (getY() - Y == userDataObj.getX()) {
			if (getX() - X > userDataObj.getY()) {
				return Direction.NE;
			} else {
				return Direction.SW;
			}
		} else {
			return Direction.NOQUADRANT;
		}
	}

	/**
	 * Returns indicator of which quadrant of the rectangle specified by the
	 * parameters that user data object lies in. The indicators are defined in
	 * the enumeration Direction, and are used as follows, relative to the
	 * center of the rectangle:
	 * 
	 * NE: user data object lies in NE quadrant, including non-negative x-axis,
	 * but not the positive y-axis NW: user data object lies in the NW quadrant,
	 * including the positive y-axis, but not the negative x-axis SW: user data
	 * object lies in the SW quadrant, including the negative x-axis, but not
	 * the negative y-axis SE: user data object lies in the SE quadrant,
	 * including the negative y-axis, but not the positive x-axis NOQUADRANT:
	 * user data object lies outside the specified rectangle
	 * 
	 * @param xLo
	 *            minimum x value
	 * @param xHi
	 *            maximum x value
	 * @param yLo
	 *            minimum y value
	 * @param yHi
	 *            maximum y value
	 * @return Direction of quadrant
	 */

	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi) {
		if (!inBox(xLo, xHi, yLo, yHi)) {
			return Direction.NOQUADRANT;
		} else if ((getX() == ((xLo + xHi) / 2) && getY() == ((yLo + yHi) / 2))
				|| ((getX() > ((xLo + xHi) / 2) && getY() >= ((yLo + yHi) / 2)))) {
			return Direction.NE;
		} else if (getX() < ((xLo + xHi) / 2)) {
			if (getY() <= ((yLo + yHi) / 2)) {
				return Direction.SW;
			} else {
				return Direction.NW;
			}
		} else if (getX() > ((xLo + xHi) / 2) && getY() < ((yLo + yHi) / 2)) {
			return Direction.SE;
		} else if (getX() == ((xLo + xHi) / 2)) {
			if (getY() > ((yLo + yHi) / 2)) {
				return Direction.NW;
			} else {
				return Direction.SE;
			}
		} else {
			if (getY() == ((yLo + yHi) / 2) && getX() < ((xLo + xHi) / 2)) {
				return Direction.SW;
			} else {
				return Direction.SE;
			}
		}
	}

	/**
	 * Returns true iff the user data object lies within or on the boundaries of
	 * the rectangle specified by the parameters. of the rectangle specified by
	 * the parameters.
	 * 
	 * @param xLo
	 *            minimum x value
	 * @param xHi
	 *            maximum x value
	 * @param yLo
	 *            minimum y value
	 * @param yHi
	 *            maximum y value
	 * @return if inside box
	 */
	public boolean inBox(double xLo, double xHi, double yLo, double yHi) {
		if (getX() < xLo || getX() > xHi || getY() < yLo || getY() > yHi) {
			return false;
		}
		return true;
	}

	/**
	 * Returns x and y coordinates of object.
	 * 
	 * @return x and y coordinates
	 */
	public String getCoords() {
		return "(" + getX() + ", " + getY() + ")";
	}

	/**
	 * Returns all information about objects in the offset list.
	 * 
	 * @return representation of object
	 */
	public String toString() {
		String allRecordInfo = "";
		for (int i = 0; i < offsets.size(); i++) {
			allRecordInfo += offsets.get(i).toString() + "\n";
		}
		return allRecordInfo;
	}

	/**
	 * Overrides the user data object's inherited equals() method with an
	 * appropriate definition; it is necessary to place this in the interface
	 * that is used as a bound on the type parameter for the generic spatial
	 * structure, otherwise the compiler will bind to Object.equals(), which
	 * will almost certainly be inappropriate.
	 * 
	 * @param o
	 *            object to compare to
	 * @return true if equal, false otherwise
	 */
	public boolean equals(Object o) {
		Coordinates otherObject = (Coordinates) o;
		if (this.getX() == otherObject.getX()
				&& this.getY() == otherObject.getY()) {
			return true;
		}
		return false;
	}

}
