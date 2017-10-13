package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import fr.minibilles.basics.ui.diagram.interaction.Handle;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import fr.minibilles.basics.ui.diagram.interaction.Outliner;
import fr.minibilles.basics.ui.sequencediagram.model.HorizontalLine;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class HorizontalLineElement extends Element.Stub {

	private float[] sourcePoint;
	private float[] targetPoint;
	private HorizontalLine hline;
	
	/** cache object for rectangle */
	private float[] rectangle;

	public HorizontalLineElement(HorizontalLine hline, float[] sourcePoint, float[] targetPoint) {
		this.hline = hline;
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		updateRectangle();
	}
	
	public HorizontalLine getModel() {
		return hline;
	}

	public float[] getPoint() {
		return sourcePoint;
	}

	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.copyPoints(rectangle, result);
	}

	private void updateRectangle() {
		if (rectangle == null ) rectangle = new float[4];
		rectangle[0] = sourcePoint[0];
		rectangle[1] = sourcePoint[1];
		rectangle[2] = targetPoint[0];
		rectangle[3] = targetPoint[1];
		Geometry.expandRectangle(rectangle, 5f, 5f);
	}

	@Override
	public void hitTesting(List<Element> result, float[] detectionPoint, float[] detectionRectangle, int type, DiagramContext context) {
		if ( detectionRectangle != null ) {
			if ( Geometry.rectangleIntersectsRectangle(rectangle, detectionRectangle) ) result.add(this);
		} else {
			if ( Geometry.rectangleContainsPoint(rectangle, detectionPoint) ) result.add(this);
		}
	}
	
	public void display(GC gc, DiagramContext context) {
		gc.setLineWidth(1);
		gc.setLineDash(new int [] {5, 5});
		gc.setLineStyle(SWT.LINE_CUSTOM);
		GcUtils.drawLine(gc, sourcePoint, targetPoint);
		gc.setLineStyle(SWT.LINE_SOLID);
		
		gc.setBackground(context.getResources().getSystemColor(SWT.COLOR_BLACK));
		GcUtils.drawStringAligned(gc, hline.getLabel(), (sourcePoint[0] + targetPoint[0])/2, (sourcePoint[1] + targetPoint[1])/2, Geometry.SOUTH);
	}

	@Override
	public void computeInteractionObjects(List<InteractionObject> result, int type, DiagramContext context) {
		if ( type == DiagramContext.HIT_HOVER_ENTER  ) {
			float[] outlinerRectangle = Geometry.copyPoints(rectangle);
			Geometry.expandRectangle(outlinerRectangle, 5f, 10f);
			result.add(new Outliner(this, 0, outlinerRectangle,  new RGB(200, 200, 200)));
		} else {
			result.add(new Handle(this, 0, Geometry.EAST, sourcePoint));
			result.add(new Handle(this, 0, Geometry.WEST, targetPoint));
		}
	}
	
	public void update(DiagramContext context, float minX, float maxX) {
		context.invalidate(this);
		sourcePoint[0] = minX - 50f;
		targetPoint[0] = maxX + 50f;
		updateRectangle();
		context.invalidate(this);
	}
}
