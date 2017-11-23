package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;

/**
 * Simple interaction object to move point's of an element.
 * @author Jean-Charles Roger (original from Didier Simoneau)
 *
 */
public class Handle extends InteractionObject.Stub {
	
	final float halfSize = 4f;
	public Handle(Element element, int type, int side, float[] point) {
		super(element, type, side, point);
	}

	public void computeBounds(float[] result, DiagramContext context) {
		result[0] = point[0];
		result[1] = point[1];
		result[2] = point[0];
		result[3] = point[1];
		Geometry.expandRectangle(result, halfSize * 3, halfSize * 3);
	}
	
	public void display(GC gc, DiagramContext context) {
		float sx = Transformation.transformX(context.getTransformationMatrix(), point[0], point[1]);
		float sy = Transformation.transformY(context.getTransformationMatrix(), point[0], point[1]);

		Color color = context.getDiagram().getHandleColor(context);
		gc.setForeground(color);
		gc.setBackground(color);
		
		boolean first = getElement() == context.getPrimarySelection();
		GcUtils.drawRectangle(gc, sx - halfSize, sy -halfSize, sx + halfSize, sy + halfSize, first);
	}
	
	public boolean hitTesting(float[] detectionPoint, DiagramContext context) {
		float[] rectangle = new float[4];
		computeBounds(rectangle, context);
		return Geometry.rectangleContainsPoint(rectangle, detectionPoint);
	}

	@Override
	public Cursor getCursor(DiagramContext context) {
		int cursorId = SWT.CURSOR_HAND;
		switch (getIndex()) {
		case Geometry.NORTH_WEST:
			cursorId = SWT.CURSOR_SIZENW;
			break;
		case Geometry.NORTH:
			cursorId = SWT.CURSOR_SIZEN;
			break;
		case Geometry.NORTH_EAST:
			cursorId = SWT.CURSOR_SIZENE;
			break;
		case Geometry.EAST:
			cursorId = SWT.CURSOR_SIZEE;
			break;
		case Geometry.SOUTH_EAST:
			cursorId = SWT.CURSOR_SIZESE;
			break;
		case Geometry.SOUTH:
			cursorId = SWT.CURSOR_SIZES;
			break;
		case Geometry.SOUTH_WEST:
			cursorId = SWT.CURSOR_SIZESW;
			break;
		case Geometry.WEST:
			cursorId = SWT.CURSOR_SIZEW;
			break;
		}
		return context.getResources().getSystemCursor(cursorId);
	}
	
	/**
	 * Adds a set of handles to a rectangle element.
	 */
	public static void addRectangleHandles(List<InteractionObject> result, Element element, float[] rectangle, boolean onCorners, boolean onSides) {
		if (onCorners) {
			for ( int i = Geometry.SOUTH_EAST; i<= Geometry.NORTH_EAST; i += 2 /* normal direction without modulo */ ) {
				result.add(new Handle(element, TYPE_MOVE, i, Geometry.getRectanglePoint(rectangle, i)));
			}
		}
		if (onSides) {
			for ( int i = Geometry.EAST; i<= Geometry.NORTH; i += 2 /* normal direction without modulo */ ) {
				result.add(new Handle(element, TYPE_MOVE, i, Geometry.getRectanglePoint(rectangle, i)));
			}
		}
	}

	public static void addPolylineHandles(List<InteractionObject> handles, Element element, float[] polyline, boolean onExtremities, boolean creationHandle) {
		for (int i = 0, j = 1, pointIndex = 0; i < polyline.length; i+=2, j+=2, pointIndex++) {
			if (onExtremities || (0 < i && i < polyline.length - 2)) {
				handles.add(new Handle(element, TYPE_MOVE, pointIndex, Geometry.getPoint(polyline, i/2)));
			}
			if (creationHandle && i < polyline.length - 2) {
				Handle handle = new Handle(element, TYPE_ADD, pointIndex, new float[] { (polyline[i] + polyline[i+2]) / 2, (polyline[j] + polyline[j+2]) / 2} );
				handles.add(handle);
			}
		}
	}

	
}
