package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class InteractiveIcon extends InteractionObject.Stub {
	
	private final Image image;
	
	public InteractiveIcon(Element element, int type, float[] point, Image image) {
		super(element, type, Geometry.CENTER, point);
		this.image = image;
	}
	
	public void computeBounds(float[] result, DiagramContext context) {
		computeRectange(result);
		Geometry.expandRectangle(result, 3f, 3f);
	}

	private void computeRectange(float[] result) {
		result[0] = point[0];
		result[1] = point[1];
		result[2] = point[0];
		result[3] = point[1];
		final Rectangle bounds = image.getBounds();
		Geometry.expandRectangle(result, bounds.width/2f, bounds.height/2f);
	}
	
	public void display(GC gc, DiagramContext context) {
		float[] localPoint = Geometry.copyPoints(point);
		Transformation.transform(context.getTransformationMatrix(), localPoint);
	
		GcUtils.drawImageAligned(gc, image, localPoint[0], localPoint[1], Geometry.CENTER);
	}
	
	
	@Override
	public boolean hitTesting(float[] detectionPoint, DiagramContext context) {
		float[] rectangle = new float[4];
		computeBounds(rectangle, context);
 		return Geometry.rectangleContainsPoint(rectangle, detectionPoint);
	}
	
	@Override
	public Cursor getCursor(DiagramContext context) {
		return context.getResources().getSystemCursor(SWT.CURSOR_HAND);
	}
}
