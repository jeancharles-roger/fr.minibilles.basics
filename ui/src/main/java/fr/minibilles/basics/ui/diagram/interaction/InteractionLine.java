package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;

public class InteractionLine extends InteractionObject.Stub implements InteractionObject {
	
	public InteractionLine(Element element, int type, int side, float[] source, float[] target) {
		super(element, type, side, new float[4]);
		setSource(source);
		setTarget(target);
	}

	public void setSource(float[] newSource) {
		point[0] = newSource[0];
		point[1] = newSource[1];
	}
	
	public void setTarget(float[] newTarget) {
		point[2] = newTarget[0];
		point[3] = newTarget[1];
	}
	
	public void display(GC gc, DiagramContext context) {
		float[] transformedPoints = Geometry.copyPoints(point);
		Transformation.transform(context.getTransformationMatrix(), transformedPoints);
		GcUtils.drawLine(gc, point[0], point[1], point[2], point[3]);
	}

	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.rectangleMergeWithRectangle(result, Geometry.rectangleFromPoints(point[0], point[1], point[2], point[3]));
		Geometry.expandRectangle(result, 4f, 4f);
	}

	
	
}
