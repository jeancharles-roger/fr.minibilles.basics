package fr.minibilles.basics.ui.diagram.gc;

import fr.minibilles.basics.geometry.Geometry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;


/** 
 * Gc utility functions
 * @author Didier Simoneau, Jean-Charles Roger.
 */
public class GcUtils  {

	/**
	 * Draws a line.
	 * @param gc the GC to draw with.
	 * @param source the source point.
	 * @param target the target point.
	 */
	public static void drawLine(GC gc, float[] source, float[] target) {
		drawLine(gc, source[0], source[1], target[0], target[1]);
	}
	
	/**
	 * Draws a line
	 * @param gc the GC to draw with.
	 * @param x1 the x of source point.
	 * @param y1 the y of source point.
	 * @param x2 the x of target point.
	 * @param y2 the y of target point.
	 */
	public static void drawLine(GC gc, float x1, float y1, float x2, float y2) {
		gc.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	/**
	 * Draws an oval.
	 * @param gc the GC to draw with.
	 * @param x upper left x coordinates
	 * @param y upper left y coordinates
	 * @param width the oval width
	 * @param height the oval height
	 * @param fill if true, the figure is filled with the background color 
	 */
	public static void drawOval(GC gc, float x, float y, float width, float height, boolean fill) {
		if (fill) {
			gc.fillOval((int) x, (int) y,(int) width, (int) height);
		}
		gc.drawOval((int) x, (int) y,(int) width, (int) height);
	}

	
	/**
	 * Draws a rectangular box.
	 * @param gc the GC to draw with
	 * @param rectangle an array of 4 floats representing x1, y1, x2, y2
	 * @param fill if true, the figure is filled with the background color
	 */
	public static void drawRectangle(GC gc, float[] rectangle, boolean fill) {
		drawRectangle(gc, rectangle[0], rectangle[1], rectangle[2], rectangle[3], fill);
	}

	/**
	 * Draws a rectangular box.
	 * @param gc the GC to draw with
	 * @param x1 left coordinate
	 * @param y1 top coordinate
	 * @param x2 right coordinate
	 * @param y2 bottom coordinate
	 * @param fill if false, the figure is filled with the background color
	 */
	public static void drawRectangle(GC gc, float x1, float y1, float x2, float y2, boolean fill) {
		int x = Math.round(x1);
		int y = Math.round(y1);
		int w = Math.round(x2 - x1);
		int h = Math.round(y2 - y1);
		if (fill) gc.fillRectangle(x, y, w, h);
		gc.drawRectangle(x, y, w, h);
	}
	
	public static void drawRoundRectangle(GC gc, float[] rectangle, float arcWidth, float arcHeight, boolean fill) {
		drawRoundRectangle(gc, rectangle[0], rectangle[1], rectangle[2], rectangle[3], arcWidth, arcHeight, fill);
	}

	public static void drawRoundRectangle(GC gc, float x1, float y1, float x2,
			float y2, float arcWidth, float arcHeight, boolean fill) {
		int x = Math.round(x1);
		int y = Math.round(y1);
		int w = Math.round(x2 - x1);
		int h = Math.round(y2 - y1);
		if (fill) gc.fillRoundRectangle(x, y, w, h, (int) arcWidth, (int) arcHeight);
		gc.drawRoundRectangle(x, y, w, h, (int) arcWidth, (int) arcHeight);
	}
	
	/**
	 * Draws a triangle within a given rectangle.
	 * 
	 * @param gc
	 *            the GC to draw with
	 * @param rectangle
	 *            an array of 4 floats representing x1, y1, x2, y2
	 * @param fill
	 *            if false, the figure is filled with the background color
	 */
	public static void drawTriangle(GC gc, float[] rectangle, int direction, boolean fill) {
		drawTriangle(gc, rectangle[0], rectangle[1], rectangle[2], rectangle[3], direction, fill);
	}
	
	/**
	 * Draws a triangle within a given rectangle.
	 * @param gc the GC to draw with
	 * @param x1 left coordinate
	 * @param y1 top coordinate
	 * @param x2 right coordinate
	 * @param y2 bottom coordinate
	 * @param fill  if false, the figure is filled with the background color
	 */
	public static void drawTriangle(GC gc, float x1, float y1, float x2, float y2, int direction, boolean fill) {
		int normalDir = Geometry.normalDirection(direction);
		float x = Geometry.getRectanglePointX(x1, y1, x2, y2, direction);
		float y = Geometry.getRectanglePointY(x1, y1, x2, y2, direction);
		float base2, height;
		if (Geometry.isParallelTo(direction, Geometry.WEST)) {
			base2 = (y2 - y1) / 2f; height = x2 - x1;
		}
		else {
			base2 = (x2 - x1) / 2f; height = y2 - y1;
		}
		int[] points = new int[6];
		points[0] = Math.round(x); // tip
		points[1] = Math.round(y);
		points[2] = Math.round(x - Geometry.xDelta(direction) * height + Geometry.xDelta(normalDir) * base2);
		points[3] = Math.round(y - Geometry.yDelta(direction) * height + Geometry.yDelta(normalDir) * base2);
		points[4] = Math.round(x - Geometry.xDelta(direction) * height - Geometry.xDelta(normalDir) * base2);
		points[5] = Math.round(y - Geometry.yDelta(direction) * height - Geometry.yDelta(normalDir) * base2);
		if (fill) gc.fillPolygon(points);
		gc.drawPolygon(points);
	}

	public static void drawTriangle2(GC gc, float x, float y, float base, float height, int direction, boolean fill) {
		int normalDir = Geometry.normalDirection(direction);
		float base2 = base / 2f;
		int[] points = new int[6];
		points[0] = Math.round(x); // tip
		points[1] = Math.round(y);
		points[2] = Math.round(x - Geometry.xDelta(direction) * height + Geometry.xDelta(normalDir) * base2);
		points[3] = Math.round(y - Geometry.yDelta(direction) * height + Geometry.yDelta(normalDir) * base2);
		points[4] = Math.round(x - Geometry.xDelta(direction) * height - Geometry.xDelta(normalDir) * base2);
		points[5] = Math.round(y - Geometry.yDelta(direction) * height - Geometry.yDelta(normalDir) * base2);
		if (fill) gc.fillPolygon(points);
		gc.drawPolygon(points);
	}


	public static void drawPolyline(GC gc, float[] polyline, boolean fill) {
		int[] points = new int[polyline.length];
		for (int i = 0; i < polyline.length; i++) {
			points[i] = Math.round(polyline[i]);
		}
		if ( fill ) gc.fillPolygon(points);
		gc.drawPolyline(points);
	}
	
	public static void drawBeveledConnection(GC gc, float[] polyline, float delta) {
		float x = 0f, y = 0f;
		float deltaOver2 = delta / 2f;
		for (int i = 0, j = 1; i < polyline.length; i+=2, j+=2) {
			boolean first = i ==0;
			boolean last = i == polyline.length - 2;
			if (first) {
				x = polyline[i];
				y = polyline[j];
			}
			if (!first && !last) {
				float dx1 = polyline[i - 2] - polyline[i];
				float dy1 = polyline[j - 2] - polyline[j];
				float len1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
				dx1 = Math.abs(dx1) >= deltaOver2 ? dx1 * delta / len1 : dx1 / 2f;
				dy1 = Math.abs(dy1) >= deltaOver2 ? dy1 * delta / len1 : dy1 / 2f;
				float dx2 = polyline[i + 2] - polyline[i];
				float dy2 = polyline[j + 2] - polyline[j];
				float len2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
				dx2 = Math.abs(dx2) >= deltaOver2 ? dx2 * delta / len2 : dx2 / 2f;
				dy2 = Math.abs(dy2) >= deltaOver2 ? dy2 * delta / len2 : dy2 / 2f;
				float x1 = polyline[i] + dx1;
				float y1 = polyline[j] + dy1;
				gc.drawLine(Math.round(x), Math.round(y), Math.round(x1), Math.round(y1));
				x = polyline[i] + dx2;
				y = polyline[j] + dy2;
				gc.drawLine(Math.round(x1), Math.round(y1), Math.round(x), Math.round(y));
			}
			if (last) {
				gc.drawLine(Math.round(x), Math.round(y), Math.round(polyline[i]), Math.round(polyline[j]));
			}
		}
	}
	
	public static void drawRoundedConnection(GC gc, float[] polyline, float delta) {
		float x = 0f, y = 0f;
		float delta2 = delta * 2f;
		Path path = new Path(gc.getDevice());
		for (int i = 0, j = 1; i < polyline.length; i+=2, j+=2) {
			boolean first = i ==0;
			boolean last = i == polyline.length - 2;
			if (first) {
				x = polyline[i];
				y = polyline[j];
				path.moveTo(x, y);
			}
			if (!first && !last) {
				float dx1 = polyline[i - 2] - polyline[i];
				float dy1 = polyline[j - 2] - polyline[j];
				float len1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
				dx1 = Math.abs(dx1) >= delta2 ? dx1 * delta / len1 : dx1 / 2f;
				dy1 = Math.abs(dy1) >= delta2 ? dy1 * delta / len1 : dy1 / 2f;
				float dx2 = polyline[i + 2] - polyline[i];
				float dy2 = polyline[j + 2] - polyline[j];
				float len2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
				dx2 = Math.abs(dx2) >= delta2 ? dx2 * delta / len2 : dx2 / 2f;
				dy2 = Math.abs(dy2) >= delta2 ? dy2 * delta / len2 : dy2 / 2f;
				float x1 = polyline[i] + dx1;
				float y1 = polyline[j] + dy1;
				path.lineTo(x1, y1);
				x = polyline[i] + dx2;
				y = polyline[j] + dy2;
				path.quadTo(polyline[i], polyline[j], x, y);
			}
			if (last) {
				path.lineTo(Math.round(polyline[i]), Math.round(polyline[j]));
			}
		}
		gc.drawPath(path);
		path.dispose();
	}

	/**
	 * Displays a string at a given point with a given alignment.
	 * @param gc the GC
	 * @param string the text to be displayed
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param alignment any of the 8 directions (EAST, SOUTH_EAST ...) or the special constant CENTER.
	 */
	public static void drawStringAligned(GC gc, String string, float x, float y, int alignment) {
		if (alignment == Geometry.NORTH_WEST) {
			gc.drawString(string, Math.round(x), Math.round(y), true);
			return;
		}
		Point extent = gc.stringExtent(string);
		float px = Geometry.getRectanglePointX(0, 0, extent.x, extent.y, alignment);
		float py = Geometry.getRectanglePointY(0, 0, extent.x, extent.y, alignment);
		gc.drawString(string, Math.round(x - px), Math.round(y - py), true);
	}
	
	/**
	 * Displays a string at a given point with a given alignment.
	 * @param gc the GC
	 * @param text the text to be displayed
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param alignment any of the 8 directions (EAST, SOUTH_EAST ...) or the special constant CENTER.
	 */
	public static void drawTextAligned(GC gc, String text, float x, float y, int alignment) {
		if (alignment == Geometry.NORTH_WEST) {
			gc.drawText(text, Math.round(x), Math.round(y), true);
			return;
		}
		Point extent = gc.textExtent(text);
		float px = Geometry.getRectanglePointX(0, 0, extent.x, extent.y, alignment);
		float py = Geometry.getRectanglePointY(0, 0, extent.x, extent.y, alignment);
		gc.drawText(text, Math.round(x - px), Math.round(y - py), true);
	}
	
	/**
	 * Displays an image at a given point with a given alignment.
	 * @param gc the GC
	 * @param image the text to be displayed
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param alignment any of the 8 directions (EAST, SOUTH_EAST ...) or the special constant CENTER.
	 */
	public static void drawImageAligned(GC gc, Image image, float x, float y, int alignment) {
		if (alignment == Geometry.NORTH_WEST) {
			gc.drawImage(image, Math.round(x), Math.round(y));
			return;
		}
		Rectangle bounds = image.getBounds();
		float px = Geometry.getRectanglePointX(0, 0, bounds.width, bounds.height, alignment);
		float py = Geometry.getRectanglePointY(0, 0, bounds.width, bounds.height, alignment);
		gc.drawImage(image, Math.round(x - px), Math.round(y - py));
	}
	
	/**
	 * Displays a string at a given point with a given rotation and alignment.
	 * @param gc the GC
	 * @param string the text to be displayed
	 * @param x x coordinate of the point
	 * @param y y coordinate of the point
	 * @param angle the rotation angle, in degrees, positive is clockwise
	 * @param alignment any of the 8 directions (EAST, SOUTH_EAST ...) or the special constant CENTER.
	 */
	public static void drawStringRotatedAligned(GC gc, String string, float x, float y, float angle, int alignment) {
		Transform transform = new Transform(gc.getDevice());
		gc.getTransform(transform);
		transform.translate(Math.round(x), Math.round(y));
		transform.rotate(angle);
		gc.setTransform(transform);
		GcUtils.drawStringAligned(gc, string, 0, 0, alignment);
		transform.rotate(- angle);
		transform.translate(- Math.round(x), - Math.round(y));
		gc.setTransform(transform);
		transform.dispose();
	}
	
	/**
	 * Computes the bounding rectangle of a displayed string. Result is placed into r.
	 */
	public static void getStringAlignedRectangle(GC gc, String string, float x, float y, int alignment, float[] r) {
		Point extent = gc.stringExtent(string);
		if (alignment == Geometry.NORTH_WEST) {
			r[0] = Math.round(x);
			r[1] = Math.round(y);
		} else {
			float px = Geometry.getRectanglePointX(0, 0, extent.x, extent.y, alignment);
			float py = Geometry.getRectanglePointY(0, 0, extent.x, extent.y, alignment);
			r[0] = Math.round(x - px);
			r[1] = Math.round(y - py);
		}
		r[2] = r[0] + extent.x;
		r[3] = r[1] + extent.y;
	}
	
	/**
	 * Computes the bounding rectangle of a displayed text. Result is placed into r.
	 */
	public static void getTextAlignedRectangle(GC gc, String text, float x, float y, int alignment, float[] r) {
		Point extent = gc.textExtent(text);
		if (alignment == Geometry.NORTH_WEST) {
			r[0] = Math.round(x);
			r[1] = Math.round(y);
		} else {
			float px = Geometry.getRectanglePointX(0, 0, extent.x, extent.y, alignment);
			float py = Geometry.getRectanglePointY(0, 0, extent.x, extent.y, alignment);
			r[0] = Math.round(x - px);
			r[1] = Math.round(y - py);
		}
		r[2] = r[0] + extent.x;
		r[3] = r[1] + extent.y;
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
