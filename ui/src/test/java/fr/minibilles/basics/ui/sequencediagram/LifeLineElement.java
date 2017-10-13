package fr.minibilles.basics.ui.sequencediagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import fr.minibilles.basics.ui.diagram.interaction.Handle;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import fr.minibilles.basics.ui.sequencediagram.model.LifeLine;
import java.util.List;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

public class LifeLineElement extends Element.Stub {

	public static final RGB backgroundRgb = new RGB(140, 210, 250);
	private float [] centerPoint;
	private float width;
	private float height;
	
	private LifeLine line;
	
	/** cache object for rectangle */
	private float[] headerRectangle;

	public LifeLineElement(LifeLine line, float[] point, float width, float height) {
		this.line = line;
		this.centerPoint = point;
		this.width = width;
		this.height = height;
		updateHeaderRectangle();
	}
	
	public LifeLine getModel() {
		return line;
	}
	
	public float getX() { return centerPoint[0]; }
	
	public float[] getPoint() {
		return centerPoint;
	}

	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.copyPoints(headerRectangle, result);
		result[3] += height;
		Geometry.expandRectangle(result, 5f, 5f);
	}


	private void updateHeaderRectangle() {
		if (headerRectangle == null ) headerRectangle = new float[4];
		headerRectangle[0] = centerPoint[0] - width/2f;
		headerRectangle[1] = centerPoint[1] - 20f;
		headerRectangle[2] = centerPoint[0] + width/2f;
		headerRectangle[3] = centerPoint[1] + 20f;
	}

	public void display(GC gc, DiagramContext context) {
		float[] headerRectangle = new float[4];
		headerRectangle[0] = centerPoint[0] - width/2f;
		headerRectangle[1] = centerPoint[1] - 20f;
		headerRectangle[2] = centerPoint[0] + width/2f;
		headerRectangle[3] = centerPoint[1] + 20f;
		gc.setBackground(context.getResources().getColor(backgroundRgb));
		GcUtils.drawRoundRectangle(gc, headerRectangle, 5f, 5f, true);
		
		gc.setClipping(GcUtils.toSWTRectangle(headerRectangle));
		GcUtils.drawStringAligned(gc, line.getLabel(), centerPoint[0], centerPoint[1], Geometry.CENTER);
		
		gc.setClipping((Rectangle) null);
		GcUtils.drawLine(gc, centerPoint[0], centerPoint[1] + 20, centerPoint[0], centerPoint[1] + 20 + height);
	}

	@Override
	public void hitTesting(List<Element> result, float[] detectionPoint, float[] detectionRectangle, int type, DiagramContext context) {
		if ( detectionRectangle != null ) {
			if ( Geometry.rectangleIntersectsRectangle(headerRectangle, detectionRectangle) ) result.add(this);
		} else {
			if ( Geometry.rectangleContainsPoint(headerRectangle, detectionPoint) ) result.add(this);
		}
	}
	
	@Override
	public void computeInteractionObjects(List<InteractionObject> result, int type, DiagramContext context) {
		if ( type == DiagramContext.HIT_SELECTION  ) {
			Handle.addRectangleHandles(result, this, headerRectangle, true, false);
		}
	}

	@Override
	public boolean move(float[] point, InteractionObject interaction, int step, DiagramContext context) {
		context.invalidate(this);
		Geometry.translatePointsBy(centerPoint, point[0] - centerPoint[0], 0f);
		updateHeaderRectangle();
		context.invalidate(this);
		((SequenceDiagram) context.getDiagram()).updateAll(context);

		boolean move = false;
		if ( step == - 1 )  {
			final float x = getX();
			if ( x != getModel().getX() ) {
				getModel().setX(x);
				move = true;
			}
		}
		return move;
	}
	
	@Override
	public boolean toolTipRequest(float[] point, DiagramContext context) {
		context.showBalloon(null, "Lifeline " + line.getLabel());
		return true;
	}
}
