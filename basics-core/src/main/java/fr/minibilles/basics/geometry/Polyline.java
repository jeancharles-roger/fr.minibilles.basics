package fr.minibilles.basics.geometry;

/**
 * Support for polyline connections. A polyline connection is represented by a
 * list of points, encoded as an array of floats. These points define a list of
 * connected segments that can have arbitrary orientation.
 * 
 * @author dsimoneau
 */
public class Polyline {
	
	/** A small epsilon value, for comparisons of floats. */
	public final static float absoluteError = 0.01f;
	
	// ********** Accessing and queries **********
	
	/** Returns the number of points of the polyline. */
	public static int numPoints(float[] points) {
		return points.length / 2;
	}
	
	/** Returns the nth point of the polyline. First point index is 0. */
	public static float[] getPoint(float[] points, int index) {
		int i = index * 2;
		return new float[]{points[i], points[i + 1]};
	}
	
	/** Returns the middle point of the nth segment of the polyline. First segment index is 0. */
	public static float[] getSegmentMiddlePoint(float[] points, int index) {
		int i = index * 2;
		return new float[]{(points[i] + points[i + 2]) / 2, (points[i + 1] +  points[i + 3]) / 2};
	}

	/**
	 * Returns the square of the length of a given segment.
	 * @param polyline the points representing the connection
	 * @param segmentIndex the index of the segment
	 * @return the length squared
	 */
	public static float segmentLengthSquared(float[] polyline, int segmentIndex) {
		int i = segmentIndex * 2;
		float dx = polyline[i + 2] - polyline[i];
		float dy = polyline[i + 3] - polyline[i + 1];
		return dx * dx + dy * dy;
	}
	
	/**
	 * Tests if a rectangle contains some points of a polyline.
	 * @param polyline the points representing the connection
	 * @param rectangle the rectangle
	 * @return true if the rectangle intersects the polyline, else false
	 */
	public static boolean intersectsRectangle(float[] polyline, float[] rectangle) {
		int numSegments = Geometry.numPoints(polyline) - 1;
		for (int iSegment = 0; iSegment < numSegments; iSegment++) {
			if (segmentIntersectsRectangle(polyline, iSegment, rectangle)) return true;
		}
		return false;
	}
	
	/**
	 * Tests if a rectangle contains some points of a given segment which is part of a polyline
	 * @param polyline polyline the points representing the connection
	 * @param iSegment the segment index
	 * @param rectangle the rectangle
	 * @return true if the rectangle intersects the segment, else false
	 */
	public static boolean segmentIntersectsRectangle(float[] polyline, int iSegment, float[] rectangle) {
		int i = iSegment * 2;
		return segmentIntersectsRectangle(polyline[i], polyline[i + 1], polyline[i + 2], polyline[i + 3], rectangle);
	}
	
	/**
	 * Tests if a rectangle contains some points of a given segment.
	 * @param x1 the x of the segment first extremity
	 * @param y1 the y of the segment first extremity
	 * @param x2 the x of the segment second extremity
	 * @param y2 the y of the segment second extremity
	 * @param rectangle the rectangle
	 * @return true if the rectangle intersects the segment, else false
	 */
	public static boolean segmentIntersectsRectangle(float x1, float y1, float x2, float y2, float[] rectangle) {
		// Test for trivial exclusion:
		if (x1 < rectangle[0] && x2 < rectangle[0] || x1 > rectangle[2] && x2 > rectangle[2]
		         || y1 < rectangle[1] && y2 < rectangle[1] || y1 > rectangle[3] && y2 > rectangle[3]) return false;
		// Test for trivial inclusion:
		if (Geometry.rectangleContainsPoint(rectangle, x1, y1)) return true;
		if (Geometry.rectangleContainsPoint(rectangle, x2, y2)) return true;
		// Divide and conquer:
		float xm = (x1 + x2) / 2f;
		float ym = (y1 + y2) / 2f;
		// TODO this recursion seems to loop sometimes
		return segmentIntersectsRectangle(x1, y1, xm, ym, rectangle) || segmentIntersectsRectangle(xm, ym, x2, y2, rectangle);
	}
	
	/**
	 * Project a given point on a connection: returns a point of the polyline that is the nearest to the given point.
	 * @param polyline the points representing the connection
	 * @param point the point to project on the connection
	 * @return the projected point
	 */
	public static float[] projectPoint(float[] polyline, float[] point) {
		int numSegments = Geometry.numPoints(polyline) - 1;
		float bestDistSquared = Float.MAX_VALUE;
		float[] bestPoint = null;
		for (int iSegment = 0; iSegment < numSegments; iSegment++) {
			int i = iSegment * 2;
			float[] p = Geometry.projectPointOnSegment(point[0], point[1], polyline[i], polyline[i + 1], polyline[i + 2], polyline[i + 3]);
			float distSquared = Geometry.distanceSquared(point, p);
			if (distSquared < bestDistSquared) {
				bestDistSquared = distSquared;
				bestPoint = p;
			}
		}
		return bestPoint;
	}
	
	/**
	 * Project a given point on a connection: returns a point of the polyline that is the nearest to the given point.
	 * @param point the point to project on the connection, it will be changed by method.
	 * @param polyline  the points representing the connection
	 * @return the index of the segment where the point has been projected on the polyline.
	 */
	public static int projectPointOnPolyline(float[] point, float[] polyline) {
		int numSegments = Geometry.numPoints(polyline) - 1;
		float bestDistSquared = Float.MAX_VALUE;
		float[] bestPoint = null;
		int projectedSegment = -1;
		for (int iSegment = 0; iSegment < numSegments; iSegment++) {
			int i = iSegment * 2;
			float[] p = Geometry.projectPointOnSegment(point[0], point[1], polyline[i], polyline[i + 1], polyline[i + 2], polyline[i + 3]);
			float distSquared = Geometry.distanceSquared(point, p);
			if (distSquared < bestDistSquared) {
				bestDistSquared = distSquared;
				bestPoint = p;
				projectedSegment = iSegment;
			}
		}
		Geometry.copyPoints(bestPoint, point);
		return projectedSegment;
	}
	/**
	 * Detects segments shorter than a given threshold.
	 * @param polyline the points representing the connection
	 * @param threshold the threshold
	 * @param startIndex the index of the first segment to be checked
	 * @return the index of the first segment whose length is less or equal that threshold, or -1 if none is found
	 */
	public static int findSmallSegment(float[] polyline, float threshold, int startIndex) {
		float threshold2 = threshold * threshold;
		float x1, y1, x2 = 0f, y2 = 0f;
		for (int i = startIndex * 2, j = i + 1; j < polyline.length; i+=2, j+=2) {
			x1 = x2;
			y1 = y2;
			x2= polyline[i];
			y2 = polyline[j];
			float dx = x2 - x1;
			float dy = y2 - y1;
			if (i > 0 && dx*dx + dy*dy <= threshold2) {
				return (i - 2) / 2;
			}
		}
		return -1;
	}
	
	// ********** Moving points **********
	
	public static void movePointTo(float[] connectionPoints, float[] point, int index, float[] impactedRectangle) {
		movePointTo(connectionPoints, point[0], point[1], index, impactedRectangle);
	}
	
	public static void movePointTo(float[] connectionPoints, float x, float y, int index, float[] impactedRectangle) {
		int i = index * 2;
		Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i], connectionPoints[i + 1]);
		connectionPoints[i] = x;
		connectionPoints[i + 1] = y;
		Geometry.rectangleMergeWithPoint(impactedRectangle, x, y);
		if (i - 2 >= 0) {
			Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i - 2], connectionPoints[i - 1]);
		}
		if (i + 3 < connectionPoints.length) {
			Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i + 2], connectionPoints[i + 3]);
		}
	}
	
	// ********** Adding removing points **********
	
	/**
	 * Returns a new connection with a single point added at an extremity
	 * of a connection.
	 * @param polyline the points representing the connection
	 * @param start if true, adds a point at the start; if false, adds a point at the end.
	 * @return the new collection of points
	 */
	public static float[] addEndSegment(float[] polyline, boolean start) {
		int length = polyline.length;
		float[] newPolyline = new float[length + 2];
		System.arraycopy(polyline, 0, newPolyline, start ? 2 : 0, length);
		if (start) {
			newPolyline[0] = polyline[0];
			newPolyline[1] = polyline[1];
		} else {
			newPolyline[length] = polyline[length - 2];
			newPolyline[length + 1] = polyline[length - 1];
		}
		return newPolyline;
	}
	
	/**
	 * Returns a new connection with a single point removed at an extremity
	 * of a connection.
	 * @param polyline the points representing the connection
	 * @param start if true, removes a point at the start; if false, removes a point at the end.
	 * @return the new collection of points
	 */
	public static float[] removeEndSegment(float[] polyline, boolean start) {
		int length = polyline.length;
		float[] newPolyline = new float[length - 2];
		System.arraycopy(polyline, start ? 2 : 0, newPolyline, 0, length - 2);
		return newPolyline;
	}
	
	/**
	 * Returns a new connection with one additional point added at the middle
	 * of a given segment.
	 * @param polyline the points representing the connection
	 * @param segmentIndex the index of the segment on which the new point is created.
	 * @return the new collection of points
	 */
	public static float[] createPointOnSegment(float[] polyline, int segmentIndex) {
		float[] newPolyline = new float[polyline.length + 2]; // make room for one point
		int newPointXIndex = segmentIndex * 2 + 2; // index of x of new point
		float newPointX = (polyline[newPointXIndex - 2] + polyline[newPointXIndex]) / 2f;
		float newPointY = (polyline[newPointXIndex - 1] + polyline[newPointXIndex + 1]) / 2f;
		for (int i1 = 0, i2 = 0; i1 < polyline.length; i1++, i2++) {
			if (i1 == newPointXIndex) {
				newPolyline[i2++] = newPointX;
				newPolyline[i2++] = newPointY;
			}
			newPolyline[i2] = polyline[i1];
		}
		return newPolyline;
	}
	
	/**
	 * Returns a new connection with two additional points added at the middle
	 * of a given segment.
	 * @param polyline the points representing the connection
	 * @param segmentIndex the index of the segment on which the new pair of points is created.
	 * @return the new collection of points
	 */
	public static float[] createPointPairOnSegment(float[] polyline, int segmentIndex) {
		float[] newPolyline = new float[polyline.length + 4]; // make room for two points
		int newPointXIndex = segmentIndex * 2 + 2; // index of x of new point
		float newPointX = (polyline[newPointXIndex - 2] + polyline[newPointXIndex]) / 2f;
		float newPointY = (polyline[newPointXIndex - 1] + polyline[newPointXIndex + 1]) / 2f;
		for (int i1 = 0, i2 = 0; i1 < polyline.length; i1++, i2++) {
			if (i1 == newPointXIndex) {
				newPolyline[i2++] = newPointX;
				newPolyline[i2++] = newPointY;
				newPolyline[i2++] = newPointX;
				newPolyline[i2++] = newPointY;
			}
			newPolyline[i2] = polyline[i1];
		}
		return newPolyline;
	}

}
