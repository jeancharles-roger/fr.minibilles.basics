package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import fr.minibilles.basics.ui.diagram.interaction.Handle;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import fr.minibilles.basics.ui.diagram.interaction.Outliner;
import fr.minibilles.basics.ui.sequencediagram.model.LifeLine;
import fr.minibilles.basics.ui.sequencediagram.model.Pause;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class PauseElement extends Element.Stub {

	private float[] sourcePoint;
	private float[] targetPoint;
	private Pause pause;
	
	/** cache object for rectangle */
	private float[] rectangle;

	public PauseElement(Pause pause, float[] sourcePoint, float[] targetPoint) {
		this.pause = pause;
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		updateRectangle();
	}
	
	public Pause getModel() {
		return pause;
	}

	public float[] getPoint() {
		return sourcePoint;
	}

	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.copyPoints(rectangle, result);
	}

	private void updateRectangle() {
		if (rectangle == null ) rectangle = new float[4];
		if ( sourcePoint[0] < targetPoint[0]  ) {
			rectangle[0] = sourcePoint[0];
			rectangle[1] = sourcePoint[1];
			rectangle[2] = targetPoint[0];
			rectangle[3] = targetPoint[1];
		} else {
			rectangle[0] = targetPoint[0];
			rectangle[1] = targetPoint[1];
			rectangle[2] = sourcePoint[0];
			rectangle[3] = sourcePoint[1];
		}
		Geometry.expandRectangle(rectangle, 5f, 15f);
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
		gc.setForeground(context.getDiagram().getBackground(context));
		
		gc.setLineWidth(2);
		gc.setLineDash(new int[] { 3, 3 });
 		gc.setLineStyle(SWT.LINE_CUSTOM);

		for ( LifeLine line : pause.getSequence().getLifeLineList() ) {
			LifeLineElement element  = context.getDiagram().findElement(LifeLineElement.class, line);
			float x = element.getX();
			for ( float d=-2f; d<=2f; d+=1f ) {
				GcUtils.drawLine(gc, x+d, rectangle[1], x+d, rectangle[3]);
			}
		}
		gc.setLineStyle(SWT.LINE_SOLID);
		
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
	
}
