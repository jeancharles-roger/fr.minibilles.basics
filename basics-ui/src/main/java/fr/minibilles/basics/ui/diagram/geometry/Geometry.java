package fr.minibilles.basics.ui.diagram.geometry;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>Set of geometry functions.<p>
 * <ul>
 * <li>A point is an array of 2 float <code>new float[] { x, y }</code></li>
 * <li>A rectangle is an array of 4 float:
 * <ul>
 * <li><code>new float[] { left, top, right, bottom }</code> or</li>
 * <li><code>new float[] { x1, y1, x2, y2 }</code> (which are the same).</li>
 * </ul>
 * with <code>left &lt; right</code>  (<code>x1 &lt; x2</code>) and <code>top &lt; bottom</code> (<code>y1 &lt; y2</code>).
 * </li>
 * </ul>
 * @author Jean-Charles Roger (original from Didier Simoneau)
 * @deprecated Use {@link fr.minibilles.basics.geometry.Geometry} instead
 */
public class Geometry {

	public final static float sqrt2over2 = (float) Math.sqrt(2.0) / 2f;
	
	// ********** Direction functions **********

	// General purpose direction enumeration.
	// Note that an equivalent angle in degrees can be obtained by multiplying the constant by 45.
	// Typical usage is:
	// - direction of a vector
	// - position of a point relative to a rectangle (EAST, NORTH, WEST, SOUTH)
	// - location of a handle on a box

	public final static int EAST = 0;
	public final static int SOUTH_EAST = 1;
	public final static int SOUTH = 2;
	public final static int SOUTH_WEST = 3;
	public final static int WEST = 4;
	public final static int NORTH_WEST = 5;
	public final static int NORTH = 6;
	public final static int NORTH_EAST = 7;
	
	/** Specifies the center of a rectangle. Used only in specific functions.  */
	public final static int CENTER = -1;
	
	/** Returns the unit vector pointing to direction. */
	public static float[] unitVector(int direction) {
		return new float[]{xDelta(direction), yDelta(direction)};
	}
	
	/** Returns the x coordinate of a unit vector pointing to direction. */
	public static float xDelta(int direction) {
		switch (direction) {
		case EAST:
			return 1f;
		case SOUTH_EAST:
		case NORTH_EAST:
			return sqrt2over2;
		case WEST:
			return -1f;
		case SOUTH_WEST:
		case NORTH_WEST:
			return -sqrt2over2;
		default:
			return 0f;
		}
	}
	
	/** Returns the y coordinate of a unit vector pointing to direction. */
	public static float yDelta(int direction) {
		switch (direction) {
		case SOUTH:
			return 1f;
		case SOUTH_EAST:
		case SOUTH_WEST:
			return sqrt2over2;
		case NORTH:
			return -1f;
		case NORTH_EAST:
		case NORTH_WEST:
			return -sqrt2over2;
		default:
			return 0f;
		}
	}
	
	/** Return direction rotated +90degrees. */
	public static int normalDirection(int direction) {
		return (direction + 2) % 8;
	}
	
	/** Return direction rotated 180degrees. */
	public static int oppositeDirection(int direction) {
		return (direction + 4) % 8;
	}
	
	/** Returns true if the two directions are parallel. */
	public static boolean isParallelTo(int direction1, int direction2) {
		return Math.abs(direction1 - direction2) % 4 == 0;
	}
	
	/** Returns true if the two directions are perpendicular. */
	public static boolean isPerpendicularTo(int direction1, int direction2) {
		return Math.abs(direction1 - direction2) % 4 == 2;
	}
	
	// ********** Point/Vector functions **********
	
	/** Computes the square of the distance between two points. */
	public static float distanceSquared(float[] point1, float[] point2) {
		float dx = point1[0] - point2[0];
		float dy = point1[1] - point2[1];
		return dx * dx + dy * dy;
	}
	
	/** Computes the dot product (scalar product) of two vectors. */
	public static float dotProduct(float[] vector1, float[] vector2) {
		return vector1[0] * vector2[0] + vector1[1] * vector2[1];
	}
	
	/**
	 * Returns one of the four quadrants EAST, SOUTH, WEST or NORTH representing
	 * the position of a given point relative to a reference point.
	 * @param pointX the x of the point
	 * @param pointY the y of the point
	 * @param refPointX the x of the origin of the reference point
	 * @param refPointY the y of the origin of the reference point
	 */
	public static int positionOfPointRelativeToPoint(float pointX, float pointY, float refPointX, float refPointY) {
		float x = pointX - refPointX;
		float y = pointY - refPointY;
		if (x >= y) {
			if (x >= -y) return EAST;
			else return NORTH;
		} else {
			if (x >= -y) return SOUTH;
			else return WEST;
		}
	}
	
	/**
	 * Project a given point on a segment specified by two points: returns a
	 * point of the segment that is the nearest to the given point.
	 * 
	 * @param xp the x of the point to project
	 * @param yp the y of the point to project
	 * @param x1 the x of the segment first extremity
	 * @param y1 the y of the segment first extremity
	 * @param x2 the x of the segment second extremity
	 * @param y2 the y of the segment second extremity
	 * @return the projected point
	 */
	public static float[] projectPointOnSegment(float xp, float yp, float x1, float y1, float x2, float y2) {
		float x12 = x2 - x1;
		float y12 = y2 - y1;
		float dist12Squared = x12 * x12 + y12 * y12;
		if (dist12Squared <= 0f) return new float[]{x1, y1};
		float x1p = xp - x1;
		float y1p = yp - y1;
		float r = (x1p * x12 + y1p * y12) / dist12Squared;
		if (r <= 0f ) return new float[]{x1, y1};
		if (r >= 1f ) return new float[]{x2, y2};
		return new float[]{x1 +  x12 * r, y1 + y12 * r};
	}
	
	// ********** Rectangle functions **********
	
	/** Creates a valid rectangle from this two points */
	public static float[] rectangleFromPoints(float[] p1, float[] p2) {
		return new float[] {
				Math.min(p1[0], p2[0]),
				Math.min(p1[1], p2[1]),
				Math.max(p1[0], p2[0]),
				Math.max(p1[1], p2[1])
		};
	}

	/** Creates a valid rectangle from this two points */
	public static float[] rectangleFromPoints(float x1, float y1, float x2, float y2) {
		return new float[] {
				Math.min(x1, x2),
				Math.min(y1, y2),
				Math.max(x1, x2),
				Math.max(y1, y2)
		};
	}
	
	public static boolean rectangleIntersectsRectangle(float[] r1, float[] r2) {
		return r1[0] <= r2[2] && r2[0] <= r1[2] 
		                                     && r1[1] <= r2[3] && r2[1] <= r1[3];
	}

	public static float[] getRectangleIntersection(float[] r1, float[] r2) {
		  float[] result = new float[4];
		  result[0] = Math.max(r1[0], r2[0]);
		  result[1] = Math.max(r1[1], r2[1]);
		  result[2] = Math.min(r1[2], r2[2]);
		  result[3] = Math.min(r1[3], r2[3]);
		  return result;
	 }
	
	public static boolean rectangleContainsPoint(float[] rectangle, float x, float y) {
		return rectangle[0] <= x && x <= rectangle[2] && rectangle[1] <= y && y <= rectangle[3];
	}

	public static boolean rectangleContainsPoint(float[] rectangle, float[] point) {
		return rectangleContainsPoint(rectangle, point[0], point[1]);
	}
	
	/** Tests if rectangle contains rectangle2. */
	public static boolean rectangleContainsRectangle(float[] rectangle, float[] rectangle2) {
		return rectangleContainsPoint(rectangle, rectangle2[0], rectangle2[1])
			&& rectangleContainsPoint(rectangle, rectangle2[2], rectangle2[3]);
	}
	
	public static void rectangleMergeWithPoint(float[] rectangle, float[] point) {
		rectangleMergeWithPoint(rectangle, point[0], point[1]);
	}
	
	public static void rectangleMergeWithPoint(float[] rectangle, float x, float y) {
		if (x < rectangle[0]) rectangle[0] = x;
		if (x > rectangle[2]) rectangle[2] = x;
		if (y < rectangle[1]) rectangle[1] = y;
		if (y > rectangle[3]) rectangle[3] = y;
	}
	
	public static void rectangleMergeWithRectangle(float[] rectangle, float[] rect) {
		if (rect[0] < rectangle[0]) rectangle[0] = rect[0];
		if (rect[2] > rectangle[2]) rectangle[2] = rect[2];
		if (rect[1] < rectangle[1]) rectangle[1] = rect[1];
		if (rect[3] > rectangle[3]) rectangle[3] = rect[3];
	}
	
	/**
	 * Returns one of the four quadrants EAST, SOUTH, WEST or NORTH representing
	 * the position of a given point relative to a reference rectangle.
	 * @param pointX the x of the point
	 * @param pointY the y of the point
	 * @param x1 the x of the origin of the reference rectangle
	 * @param y1 the y of the origin of the reference rectangle
	 * @param x2 the x of the corner of the reference rectangle
	 * @param y2 the y of the corner of the reference rectangle
	 */
	public static int positionOfPointRelativeToRectangle(float pointX, float pointY, float x1, float y1, float x2, float y2) {
		float w = x2 - x1;
		float h = y2 - y1;
		float d = Math.min(w, h) / 2f;
		int q1 = positionOfPointRelativeToPoint(pointX, pointY, x1 + d, y1 + d);
		int q2 = positionOfPointRelativeToPoint(pointX, pointY, x2 - d, y2 - d);
		if (q1 == q2) return q1;
		if (w > h) {
			if (pointY > y1 + d) return SOUTH;
			else return NORTH;
		} else {
			if (pointX > x1 + d) return EAST;
			else return WEST;
		}
	}
	
	/**
	 * Returns one of the four quadrants EAST, SOUTH, WEST or NORTH representing
	 * the position of a given point relative to a reference rectangle.
	 * @param point the point
	 * @param refRectangle the reference rectangle
	 */
	public static int positionOfPointRelativeToRectangle(float[] point, float[] refRectangle) {
		return positionOfPointRelativeToRectangle(point[0], point[1], refRectangle[0], refRectangle[1], refRectangle[2], refRectangle[3]);
	}
	
	public static float getRectanglePointX(float x1, float y1, float x2, float y2, int direction) {
		if (direction == WEST || direction == NORTH_WEST || direction == SOUTH_WEST) return x1;
		if (direction == EAST || direction == NORTH_EAST || direction == SOUTH_EAST) return x2;
		return  (x1 + x2) / 2f;
	}
	
	public static float getRectanglePointY(float x1, float y1, float x2, float y2, int direction) {
		if (direction == NORTH || direction == NORTH_EAST || direction == NORTH_WEST) return y1;
		if (direction == SOUTH || direction == SOUTH_EAST || direction == SOUTH_WEST) return y2;
		return (y1 + y2) / 2f;
	}
	
	/** Returns a rectangle point. direction can be any of the 8 directions (EAST, SOUTH_EAST ...) or the special constant CENTER. */
	public static float[] getRectanglePoint(float[] rectangle, int direction) {
		return new float[] {
				getRectanglePointX(rectangle[0], rectangle[1], rectangle[2], rectangle[3], direction),
				getRectanglePointY(rectangle[0], rectangle[1], rectangle[2], rectangle[3], direction) };
	}

	/**
	 * Returns a new rectangle of a given width and height, so that the point
	 * designated by the direction specifier coincides with a given point. In
	 * other words, r being the returned rectangle, getRectanglePoint(r,
	 * direction) is identical to new float[]{x, y}.
	 * 
	 * @param width
	 *            the width of the returned rectangle
	 * @param height
	 *            the height of the returned rectangle
	 * @param x
	 *            the x coordinate of the point
	 * @param y
	 *            the y coordinate of the point
	 * @param direction
	 *            specifies which point of the rectangle must be aligned with
	 *            the given point; can be any of the 8 directions (EAST,
	 *            SOUTH_EAST ...) or the special constant CENTER.
	 */
	public static float[] rectangleAlignedOn(float width, float height, float x, float y, int direction) {
		float rx = x - getRectanglePointX(0, 0, width, height, direction);
		float ry = y - getRectanglePointY(0, 0, width, height, direction);
		return new float[]{rx, ry, rx + width, ry + height};
	}
	
	public static void moveRectanglePointTo(float[] rectangle, float x, float y, int location) {
		if (location == WEST || location == NORTH_WEST || location == SOUTH_WEST) rectangle[0] = x;
		if (location == EAST || location == NORTH_EAST || location == SOUTH_EAST) rectangle[2] = x;
		if (location == NORTH || location == NORTH_EAST || location == NORTH_WEST) rectangle[1] = y;
		if (location == SOUTH || location == SOUTH_EAST || location == SOUTH_WEST) rectangle[3] = y;
	}
	
	public static void moveRectanglePointTo(float[] rectangle, float x, float y, int location, float minWidth, float minHeight) {
		if (location == WEST || location == NORTH_WEST || location == SOUTH_WEST) rectangle[0] = Math.min(x, rectangle[2] - minWidth);
		if (location == EAST || location == NORTH_EAST || location == SOUTH_EAST) rectangle[2] = Math.max(x, rectangle[0] + minWidth);
		if (location == NORTH || location == NORTH_EAST || location == NORTH_WEST) rectangle[1] = Math.min(y, rectangle[3] - minHeight);
		if (location == SOUTH || location == SOUTH_EAST || location == SOUTH_WEST) rectangle[3] = Math.max(y, rectangle[1] + minHeight);
	}
	
	public static void moveRectanglePointBy(float[] rectangle, float dx, float dy, int location, float minWidth, float minHeight) {
		if (location == WEST || location == NORTH_WEST || location == SOUTH_WEST) rectangle[0] = Math.min(rectangle[0] + dx, rectangle[2] - minWidth);
		if (location == EAST || location == NORTH_EAST || location == SOUTH_EAST) rectangle[2] = Math.max(rectangle[2] + dx, rectangle[0] + minWidth);
		if (location == NORTH || location == NORTH_EAST || location == NORTH_WEST) rectangle[1] = Math.min(rectangle[1] + dy, rectangle[3] - minHeight);
		if (location == SOUTH || location == SOUTH_EAST || location == SOUTH_WEST) rectangle[3] = Math.max(rectangle[3] + dy, rectangle[1] + minHeight);
	}
	
	public static void expandRectangle(float[] rectangle, float dx, float dy) {
		rectangle[0] -= dx;
		rectangle[1] -= dy;
		rectangle[2] += dx;
		rectangle[3] += dy;
	}

	/**
	 * Projects a point on a rectangle. The projected point is the nearest point
	 * on the rectangle.
	 * @param point
	 *            the point to be projected; after return, this point is
	 *            modified to contain the result of the projection
	 * @param rectangle
	 *            the rectangle
	 */
	public static void projectPointOnRectangle(float[] point, float[] rectangle) {
		int direction = positionOfPointRelativeToRectangle(point, rectangle);
		switch (direction) {
		case EAST:
			point[0] = rectangle[2];
			point[1] = Math.max(rectangle[1], Math.min(point[1], rectangle[3]));
			break;
		case SOUTH:
			point[0] = Math.max(rectangle[0], Math.min(point[0], rectangle[2]));
			point[1] = rectangle[3];
			break;
		case WEST:
			point[0] = rectangle[0];
			point[1] = Math.max(rectangle[1], Math.min(point[1], rectangle[3]));
			break;
		case NORTH:
			point[0] = Math.max(rectangle[0], Math.min(point[0], rectangle[2]));
			point[1] = rectangle[1];
			break;
		}
	}
	
	/**
	 * Projects a point on a rectangle (alternate version). The projected point is the intersection of
	 * the rectangle and the line joining the given point to the center of the
	 * rectangle.
	 * @param point
	 *            the point to be projected; after return, this point is
	 *            modified to contain the result of the projection
	 * @param rectangle
	 *            the rectangle
	 */
	public static void projectPointOnRectangleAlt(float[] point, float[] rectangle) {
		float cx = (rectangle[0] + rectangle[2]) / 2f;
		float cy = (rectangle[1] + rectangle[3]) / 2f;
		float radx = (rectangle[2] - rectangle[0]) / 2f;
		if (radx == 0) radx = 1;
		float rady = (rectangle[3] - rectangle[1]) / 2f;
		if (rady == 0) rady = 1;
		float x = (point[0] - cx) / radx;
		float y = (point[1] - cy) / rady;
		if (x + y >= 0) {
			if (x - y > 0) { // EAST quadrant
				point[1] = (x != 0 ? y / x : 0) * rady + cy;
				point[0] = rectangle[2];
			} else { // SOUTH quadrant
				point[0] = (y != 0 ? x / y : 0) * radx + cx;
				point[1] = rectangle[3];
			}
		} else {
			if (x - y > 0) { // NORTH quadrant
				point[0] = - (y != 0 ? x / y : 0) * radx + cx;
				point[1] = rectangle[1];
			} else { // WEST quadrant
				point[1] = - (x != 0 ? y / x : 0) * rady + cy;
				point[0] = rectangle[0];
			}
		}
	}
	
	/**
	 * Projects a point on an ellipse. The projected point is the intersection of
	 * the ellipse and the line joining the given point to the center of the
	 * ellipse.
	 * @param point
	 *            the point to be projected; after return, this point is
	 *            modified to contain the result of the projection
	 * @param rectangle
	 *            the rectangle in which the ellipse fits
	 */
	public static void projectPointOnEllipse(float[] point, float[] rectangle) {
		float cx = (rectangle[0] + rectangle[2]) / 2f;
		float cy = (rectangle[1] + rectangle[3]) / 2f;
		float radx = (rectangle[2] - rectangle[0]) / 2f;
		float rady = (rectangle[3] - rectangle[1]) / 2f;
		float x = (point[0] - cx) / radx;
		float y = (point[1] - cy) / rady;
		float d = (float) Math.sqrt(x * x + y * y);
		x /= d;
		y /= d;
		point[0] = x * radx + cx;
		point[1] = y * rady + cy;
	}

	/**
	 * Answer a Point, delta, such that the origin of the rectangle translated
     * by delta will lie within container, and the translated rectangle's corner
     * will also lie within container, if it will fit .
	 */
	public static float[] amountToTranslateWithin(float [] rectangle, float[] container) {
		float [] delta  = new float[2];
		if ( rectangle[2] > container[2] ) delta[0] = container[2] - rectangle[2];
		if ( rectangle[3] > container[3] ) delta[1] = container[3] - rectangle[3];
		if ( rectangle[0] + delta[0] < container[0] ) delta[0] = container[0] - rectangle[0];
		if ( rectangle[1] + delta[1] < container[1] ) delta[1] = container[1] - rectangle[1];
		return delta;
	}
	
	/** Assigns a null rectangle to the provided parameter. A null rectangle is a suitable initial rect for merge operations. */
	public static void setNullRectangle(float[] r) {
		r[0] = Float.POSITIVE_INFINITY;
		r[1] = Float.POSITIVE_INFINITY;
		r[2] = Float.NEGATIVE_INFINITY;
		r[3] = Float.NEGATIVE_INFINITY;
	}
	
	/** Assigns a very large rectangle to the provided parameter. */
	public static void setLargestRectangle(float[] r) {
		r[0] = Float.NEGATIVE_INFINITY;
		r[1] = Float.NEGATIVE_INFINITY;
		r[2] = Float.POSITIVE_INFINITY;
		r[3] = Float.POSITIVE_INFINITY;
	}
	
	// ********** General point list functions **********
	
	/** Returns the number of points of a given point list. */
	public static int numPoints(float[] points) {
		return points.length / 2;
	}
	
	/** Returns a copy of the parameter. */
	public static float[] copyPoints(float[] points) {
		float[] newArray = new float[points.length];
		System.arraycopy(points, 0, newArray, 0, points.length);
		return newArray;
	}
	
	/** Copies the points of the first parameter into the second parameter. */
	public static void copyPoints(float[] sourcePoints, float[] destinationPoints) {
		for (int i = 0; i < sourcePoints.length; i++) {
			destinationPoints[i] = sourcePoints[i];
		}
	}
	
	/** General method copy of points */
	public static void copyPoints(float[] sourcePoints, int startIndex, int length, float[] destinationPoints, int destinationIndex) {
		for ( int i = startIndex * 2, j=0; i< (length + startIndex) * 2; i++,j++) {
			destinationPoints[j + (destinationIndex *2)] = sourcePoints[i];
		}
	}
	
	
	/**
	 * Returns the nth point of a point list.
	 * @param points the point list
	 * @param index the index of the point; indexes starts at 0
	 * @return the requested point; this is a new allocated array
	 */
	public static float[] getPoint(float[] points, int index) {
		int i = index * 2;
		return new float[]{points[i], points[i + 1]};
	}
	
	/**
	 * Replaces the nth point of a point list by a given point.
	 * @param points the point list
	 * @param point the new point
	 * @param index the index of the point; indexes starts at 0
	 */
	public static void movePointTo(float[] points, float[] point, int index) {
		int i = index * 2;
		points[i] = point[0];
		points[i + 1] = point[1];
	}
	
	public static void computeBoundingRectangle(float[] points, float[] resultRectangle) {
		setNullRectangle(resultRectangle);
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			rectangleMergeWithPoint(resultRectangle, points[i], points[j]);
		}
	}
	
	public static void translatePointsBy(float[] points, float dx, float dy) {
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			points[i] += dx;
			points[j] += dy;
		}
	}
	
	public static void translatePointsBy(float[] points, float dx, float dy, float[] impactedRectangle) {
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			rectangleMergeWithPoint(impactedRectangle, points[i], points[j]);
			points[i] += dx;
			points[j] += dy;
			rectangleMergeWithPoint(impactedRectangle, points[i], points[j]);
		}
	}
	
	public static void translatePointsTo(float[] points, float x, float y) {
		translatePointsBy(points, x - points[0], y - points[1]);
	}

	public static void gridPoints(float[] points, float gridX, float gridY) {
		for (int i = 0, j = 1; j < points.length; i+=2, j+=2) {
			points[i] = Math.round((points[i]) / gridX) * gridX;
			points[j] = Math.round((points[j]) / gridY) * gridY;
		}
	}

	// ********** SWT data types conversion functions **********

	public static float[] fromSWTPoint(Point point) {
		return new float[]{point.x, point.y};
	}
	
	public static float[] fromSWTRectangle(Rectangle rect) {
		return new float[]{rect.x, rect.y, rect.x + rect.width, rect.y + rect.height};
	}
	
	public static Rectangle toSWTRectangle(float[] rect) {
		return new Rectangle(Math.round(rect[0]), Math.round(rect[1]), Math.round(rect[2] - rect[0]), Math.round(rect[3] - rect[1]));
	}
	
	public static Point toSWTPoint(float[] point) {
		return new Point(Math.round(point[0]), Math.round(point[1]));
	}
}
