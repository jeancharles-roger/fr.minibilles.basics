package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import org.eclipse.swt.SWT;

public class Lasso extends InteractionObject.Stub {


	public Lasso(float[] rectangle) {
		super(null, 0, Geometry.CENTER, rectangle);
	}

	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.copyPoints(point, result);
		Geometry.expandRectangle(result, 4f, 4f);
	}

	public void display(GC gc, DiagramContext context) {
		gc.setLineStyle(SWT.LINE_DOT);
		gc.setLineWidth(0);
		float[] rectangle = Geometry.copyPoints(point);
		Transformation.transform(context.getTransformationMatrix(), rectangle);
		GcUtils.drawRectangle(gc, rectangle, false);
	}

}
