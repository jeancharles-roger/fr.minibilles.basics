package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Element;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class Outliner extends InteractionObject.Stub {
	
	private final RGB color;
	private final RGB fillColor;
	private final boolean fill;
	private final int lineStyle;
	private final int alpha = 150;
	
	public Outliner(Element element, int type, float[] rectangle, RGB fillColor) {
		super(element, type, Geometry.CENTER, rectangle);
		this.color = fillColor;
		this.fillColor = fillColor;
		this.fill = true;
		this.lineStyle = SWT.LINE_SOLID;
	}
	
	public Outliner(Element element, int type, float[] rectangle, RGB color, int lineStyle) {
		super(element, type, Geometry.CENTER, rectangle);
		this.color = color;
		this.fillColor = null;
		this.fill = false;
		this.lineStyle = lineStyle;
	}
	
	public void computeBounds(float[] result, DiagramContext context) {
		Geometry.copyPoints(point, result);
		Geometry.expandRectangle(result, 3f, 3f);
	}
	
	public void display(GC gc, DiagramContext context) {
		float[] rectangle = Geometry.copyPoints(point);
		Transformation.transform(context.getTransformationMatrix(), rectangle);
		
		if ( color != null ) gc.setForeground(context.getResources().getColor(color));
		if ( fillColor != null ) gc.setBackground(context.getResources().getColor(fillColor));
		int oldAlpha = gc.getAlpha();
		gc.setAlpha(alpha);
		gc.setLineStyle(lineStyle);
		GcUtils.drawRoundRectangle(gc, rectangle, 10f, 10f, fill);
		gc.setAlpha(oldAlpha);
	}
	
}
