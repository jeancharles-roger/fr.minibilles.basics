package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import fr.minibilles.basics.ui.diagram.interaction.Handle;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import fr.minibilles.basics.ui.diagram.interaction.Outliner;
import fr.minibilles.basics.ui.sequencediagram.model.Message;
import java.util.List;
import org.eclipse.swt.graphics.RGB;

public class MessageElement extends Element.Stub {

	private float[] sourcePoint;
	private float[] targetPoint;
	private Message message;
	
	/** cache object for rectangle */
	private float[] rectangle;

	public MessageElement(Message message, float[] sourcePoint, float[] targetPoint) {
		this.message = message;
		this.sourcePoint = sourcePoint;
		this.targetPoint = targetPoint;
		updateRectangle();
	}
	
	public Message getModel() {
		return message;
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
		GcUtils.drawLine(gc, sourcePoint, targetPoint);
		
		int direction = sourcePoint[0] < targetPoint[0] ? Geometry.EAST : Geometry.WEST;
		float size = 10f;
		int normalDir = Geometry.normalDirection(direction);

		int left = Math.round(targetPoint[0] - Geometry.xDelta(direction) * size);
		int right = Math.round(targetPoint[0]);
		int top = Math.round(targetPoint[1] - Geometry.yDelta(normalDir) * size/2f);
		int center = Math.round(targetPoint[1]);
		int bottom = Math.round(targetPoint[1] + Geometry.yDelta(normalDir) * size/2f);
		gc.drawLine(left, top, right, center);
		gc.drawLine(left, bottom, right, center);

		GcUtils.drawStringAligned(gc, message.getLabel(), (sourcePoint[0] + targetPoint[0])/2, (sourcePoint[1] + targetPoint[1])/2, Geometry.SOUTH);
	}

	@Override
	public void computeInteractionObjects(List<InteractionObject> result, int type, DiagramContext context) {
		if ( type == DiagramContext.HIT_HOVER_ENTER  ) {
			float[] outlinerRectangle = Geometry.copyPoints(rectangle);
			Geometry.expandRectangle(outlinerRectangle, 5f, 10f);
			result.add(new Outliner(this, 0, outlinerRectangle,  new RGB(200, 200, 200)));
		} else {
			// FIXME calculate direction for handle
			result.add(new Handle(this, 0, Geometry.EAST, sourcePoint));
			result.add(new Handle(this, 0, Geometry.WEST, targetPoint));
		}
	}
	
	public void update(DiagramContext context) {
		LifeLineElement source = context.getDiagram().findElement(LifeLineElement.class, message.getSource());
		LifeLineElement target = context.getDiagram().findElement(LifeLineElement.class, message.getTarget());
		
		context.invalidate(this);
		sourcePoint[0] = source.getX();
		targetPoint[0] = target.getX();
		updateRectangle();
		context.invalidate(this);
	}
}
