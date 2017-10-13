package fr.minibilles.basics.ui.diagram.geometry;

/**
 * Support for Manhattan connections. A Manhattan connection is represented by a
 * list of points, encoded as an array of floats. These points define a list of
 * connected segments that must be horizontal or vertical: this constraint
 * is enforced by the manipulation functions provided here.
 * 
 * @author dsimoneau
 * @deprecated Use {@link fr.minibilles.basics.geometry.Manhattan} instead
 */
public class Manhattan extends Polyline {

	public static boolean startsHorizontally(float[] connectionPoints) {
		float x1, y1, x2 = 0f, y2 = 0f;
		float bestLen = -1.0f;
		boolean hor = false, swap = true;
		for (int i = 0, j = 1; j < connectionPoints.length; i+=2, j+=2) {
			x1 = x2;
			y1 = y2;
			x2= connectionPoints[i];
			y2 = connectionPoints[j];
			if (i > 0) {
				float dx = Math.abs(x1 - x2);
				float dy = Math.abs(y1 - y2);
				float len = dx + dy;
				if (len > bestLen) {
					bestLen = dx + dy;
					hor = (dx >= dy)  == swap;
				}
				swap = !swap;
			}
		}
		return hor;
	}
	
	public static int segmentDirection(float[] connectionPoints, int segmentIndex) {
		int i = segmentIndex * 2;
		if (i < 0 || i + 3 >= connectionPoints.length) return -1; // out of range
		boolean hor = startsHorizontally(connectionPoints);
		if (segmentIndex % 2 == 1)  hor = !hor;
		float x1 = connectionPoints[i];
		float y1 = connectionPoints[i + 1];
		float x2 = connectionPoints[i + 2];
		float y2 = connectionPoints[i + 3];
		if (hor) {
			return x2 >= x1 ? Geometry.EAST : Geometry.WEST;
		} else {
			return y2 >= y1 ? Geometry.SOUTH : Geometry.NORTH;
		}
	}
	
	private static final float[] unusedRect = new float[4];
	
	public static void movePointTo(float[] connectionPoints, float[] point, int index) {
		movePointTo(connectionPoints, point[0], point[1], index, unusedRect);
	}
	
	public static void movePointTo(float[] connectionPoints, float x, float y, int index) {
		movePointTo(connectionPoints, x, y, index, unusedRect);
	}
	
	public static void movePointTo(float[] connectionPoints, float[] point, int index, float[] impactedRectangle) {
		movePointTo(connectionPoints, point[0], point[1], index, impactedRectangle);
	}
	
	public static void movePointTo(float[] connectionPoints, float x, float y, int index, float[] impactedRectangle) {
		int i = index * 2;
		boolean hor = startsHorizontally(connectionPoints); // segment at index
		if (index % 2 == 1) hor = ! hor;
		Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i], connectionPoints[i + 1]);
		connectionPoints[i] = x;
		connectionPoints[i + 1] = y;
		Geometry.rectangleMergeWithPoint(impactedRectangle, x, y);
		if (i - 2 >= 0) {
			Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i - 2], connectionPoints[i - 1]);
			if (!hor) connectionPoints[i - 1] = y;
			else connectionPoints[i - 2] = x;
		}
		if (i + 3 < connectionPoints.length) {
			Geometry.rectangleMergeWithPoint(impactedRectangle, connectionPoints[i + 2], connectionPoints[i + 3]);
			if (hor) connectionPoints[i + 3] = y;
			else connectionPoints[i + 2] = x;
		}
	}
	
	// ********** Adjusting algorithms **********
	
	/**
	 * Adjust both extremities of a given Manhattan polyline.
	 * @param polyline the points representing the connection
	 * @param point1 the point to which the start must be moved
	 * @param rect1 a reference rectangle used to compute the direction of the start segment
	 * @param point2 the point to which the end must be moved
	 * @param rect2 a reference rectangle used to compute the direction of the end segment
	 * @param offset the minimal length of the extremity segments
	 * @param impactedRect this rectangle is augmented with the zone impacted by the adjustment
	 * @return the resulting collection of points; this can be the same object as the polyline parameter, or a new array if the number of points is changed
	 */
	public static float[] adjustBothEnds(float[] polyline, float[] point1, float[] rect1, float[] point2, float[] rect2, float offset, float[] impactedRect) {
		polyline = adjustOneEnd(polyline, point1, rect1, offset, true, impactedRect);
		polyline = adjustOneEnd(polyline, point2, rect2, offset, false, impactedRect);
		return polyline;
	}
	
	/**
	 * Adjust one extremity of a given Manhattan polyline.
	 * @param polyline the points representing the connection
	 * @param point the point to which the extremity must be moved
	 * @param rectangle a reference rectangle used to compute the direction of the extremity segment
	 * @param offset the minimal length of the extremity segment
	 * @param start if true, the first point is adjusted, if false, the last point
	 * @param impactedRect this rectangle is augmented with the zone impacted by the adjustment
	 * @return the resulting collection of points; this can be the same object as the polyline parameter, or a new array if the number of points is changed
	 */
	public static float[] adjustOneEnd(float[] polyline, float[] point, float[] rectangle, float offset, boolean start, float[] impactedRect) {
		int numPoints = numPoints(polyline);
		int i1 = start ? 0 : numPoints - 1; // index of extremity point
		int i2 = i1 + (start ? +1 : -1); // index of next point
		float[] p1 = getPoint(polyline, i1); // extremity point
		float[] p2 = getPoint(polyline, i2); // next point
		
		if (Geometry.distanceSquared(point, p1) <= absoluteError * absoluteError) {
			return polyline; // nothing to do: don't touch this polyline
		}
		
		int rectSide = Geometry.positionOfPointRelativeToRectangle(point, rectangle);
		int segDir = Manhattan.segmentDirection(polyline, start ? 0 : numPoints - 2);
		if (!start) segDir = Geometry.oppositeDirection(segDir);
		
		if (!Geometry.isParallelTo(rectSide, segDir)) {
			return adjustOneEnd(addEndSegment(polyline, start), point, rectangle, offset, start, impactedRect);
		}
		
		movePointTo(polyline, point[0], point[1],  i1, impactedRect);
		p1 = getPoint(polyline, i1);
		p2 = getPoint(polyline, i2);
		float ux = Geometry.xDelta(rectSide);
		float uy = Geometry.yDelta(rectSide);
		float dotProd = ux * (p2[0] - p1[0]) + uy * (p2[1] - p1[1]);
		if (dotProd < offset /*|| Geometry.distanceSquared(p1, p2) == offset * offset*/ ) {
			movePointTo(polyline, point[0] + ux * offset, point[1] + uy * offset, i2, impactedRect);
		}
		return polyline;
	}
	
	// ********** Routing algorithms **********
	
	public static float[] route(float[] p1, int dir1, float offset1, float[] p2, int dir2, float offset2) {
		boolean dir1Horiz = Geometry.isParallelTo(dir1, Geometry.EAST);
		float[] u1 = Geometry.unitVector(dir1);
		if (Geometry.isParallelTo(dir1, dir2)) {
			if (dir1 == dir2) {
				// 4 points
				float[] points = new float[]{
						p1[0], p1[1],
						p1[0] + Geometry.xDelta(dir1) * offset1, p1[1] + Geometry.yDelta(dir1) * offset1,
						p2[0] + Geometry.xDelta(dir2) * offset2, p2[1] + Geometry.yDelta(dir2) * offset2,
						p2[0], p2[1]};
				if (Geometry.dotProduct(p1, u1) >= Geometry.dotProduct(p2, u1)) {
					if (dir1Horiz) points[4] = points[2];
					else points[5] = points[3];
				} else {
					if (dir1Horiz) points[2] = points[4];
					else points[3] = points[5];
				}
				return points;
			} else {
				float xm = (p1[0] + p2[0]) / 2f;
				float ym = (p1[1] + p2[1]) / 2f;
				if (Geometry.dotProduct(p1, u1) >= Geometry.dotProduct(p2, u1)) {
					// 6 points
					float[] points = new float[]{
							p1[0], p1[1],
							p1[0] + Geometry.xDelta(dir1) * offset1, p1[1] + Geometry.yDelta(dir1) * offset1,
							xm, ym,
							xm, ym,
							p2[0] + Geometry.xDelta(dir2) * offset2, p2[1] + Geometry.yDelta(dir2) * offset2,
							p2[0], p2[1]};
					if (dir1Horiz) {
						points[4] = points[2];
						points[6] = points[8];
					} else {
						points[5] = points[3];
						points[7] = points[9];
					}
					return points;
				} else {
					// 4 points
					if (dir1Horiz) {
						return new float[]{p1[0], p1[1], xm, p1[1], xm, p2[1], p2[0], p2[1]};
					} else {
						return new float[]{p1[0], p1[1], p1[0], ym, p2[0], ym,  p2[0], p2[1]};
					}
				}
			}
		} else {
			float[] u2 = Geometry.unitVector(dir2);
			if (Geometry.dotProduct(p1, u1) >= Geometry.dotProduct(p2, u1) || Geometry.dotProduct(p2, u2) >= Geometry.dotProduct(p1, u2)) {
				// 5 points
				float[] points = new float[]{
						p1[0], p1[1],
						p1[0] + Geometry.xDelta(dir1) * offset1, p1[1] + Geometry.yDelta(dir1) * offset1,
						0, 0,
						p2[0] + Geometry.xDelta(dir2) * offset2, p2[1] + Geometry.yDelta(dir2) * offset2,
						p2[0], p2[1]};
				if (dir1Horiz) {
					points[4] = points[2];
					points[5] = points[7];
				} else {
					points[4] = points[6];
					points[5] = points[3];
				}
				return points;
			} else {
				// 3 points
				return new float[]{
						p1[0], p1[1],
						dir1Horiz ? p2[0] : p1[0], dir1Horiz ? p1[1] : p2[1],
						p2[0], p2[1]};
			}
		}
		//TODO more routing option to do
	}
	
	public static float[] routeFor2Rects(float[] p1, float[] rect1, float offset1, float[] p2, float[] rect2, float offset2) {
		int dir1 = Geometry.positionOfPointRelativeToRectangle(p1, rect1);
		int dir2 = Geometry.positionOfPointRelativeToRectangle(p2, rect2);
		float[] points = route(p1, dir1, offset1, p2, dir2, offset2);
		if (Geometry.numPoints(points) == 6) {
			// tests if the middle segment (2) crosses one of the boxes:
			int segmentIndex = 2;
			if (segmentIntersectsRectangle(points, segmentIndex, rect1) || segmentIntersectsRectangle(points, segmentIndex, rect2)) {
				int dir = segmentDirection(points, segmentIndex);
				float xMax = Math.max(rect1[2], rect2[2]);
				float yMax = Math.max(rect1[3], rect2[3]);
				int iPoint = segmentIndex;
				float offset = Math.min(offset1, offset2);
				if (Geometry.isParallelTo(dir, Geometry.EAST)) Manhattan.movePointTo(points, points[iPoint*2], yMax + offset, iPoint);
				else Manhattan.movePointTo(points, xMax + offset, points[iPoint*2+1], iPoint);
			}
		}
		return points;
	}

	/** Use routeFor2Rects(float[] p1, float[] rect1, int offset1, float[] p2, float[] rect2, int offset2) instead. */
	@Deprecated
	public static float[] route(float[] p1, float[] rect1, float[] p2, float[] rect2) {
		return routeFor2Rects(p1, rect1, 10f, p2, rect2, 10f);
	}

	// ********** Adding removing points **********
	
	/**
	 * Returns a new connection with a given segment removed. It works in two
	 * steps: first, the two end points of the segment are both moved to the
	 * middle of the segment; then these two points are removed from the
	 * connection. This ensures that the manhattan routing is preserved.
	 * @param polyline
	 *            the points representing the connection
	 * @param segmentIndex
	 *            the index of the segment to be removed.
	 * @param impactedRect TODO
	 * @return the new collection of points
	 */
	public static float[] removeSegment(float[] polyline, int segmentIndex, float[] impactedRect) {
		int i = segmentIndex * 2;
		float x1 = polyline[i];
		float y1 = polyline[i+1];
		float x2 = polyline[i+2];
		float y2 = polyline[i+3];
		float xm = (x1 + x2) / 2f;
		float ym = (y1 + y2) / 2f;
		movePointTo(polyline, xm, ym, segmentIndex, impactedRect);
		movePointTo(polyline, xm, ym, segmentIndex + 1, impactedRect);
		float[] newConnectionPoints = new float[polyline.length - 4]; // removes two points
		for (int i1 = 0, i2 = 0; i1 < polyline.length; i1++, i2++) {
			if (i1 == i) i1 += 4;
			newConnectionPoints[i2] = polyline[i1];
		}
		return newConnectionPoints;
	}
	
	/**
	 * Simplifies a connection by removing segments shorter than threshold.
	 * Returns a new float array if points must be removed.
	 */
	public static float[] discardSmallSegments(float[] polyline, float threshold) {
		return discardSmallSegments(polyline, threshold, unusedRect);
	}
	
	/**
	 * Simplifies a connection by removing segments shorter than threshold.
	 * Returns a new float array if points must be removed.
	 */
	public static float[] discardSmallSegments(float[] polyline, float threshold, float[] impactedRect) {
		int numPoints = numPoints(polyline);
		if (numPoints <= 4) return polyline; // no attempt to simplify connections with less that 4 points
		int iSegment = findSmallSegment(polyline, threshold, 1);
		if (iSegment == -1) return polyline; // no small segment found
		if (iSegment == numPoints - 2) return polyline; // no attempt to simplify last segment
		float[] newConnectionPoints = removeSegment(polyline, iSegment, impactedRect);
		return discardSmallSegments(newConnectionPoints, threshold);
	}
	
}
