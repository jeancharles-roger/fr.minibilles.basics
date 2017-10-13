package fr.minibilles.basics.ui.diagram.interaction;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.diagram.DiagramContext;
import fr.minibilles.basics.ui.diagram.Displayable;
import fr.minibilles.basics.ui.diagram.Element;
import org.eclipse.swt.graphics.Cursor;

/**
 * 
 * @author Jean-Charles Roger (original from Didier Simoneau).
 */
public interface InteractionObject extends Displayable {

	public static final int TYPE_MOVE = 0;
	public static final int TYPE_ADD = 1;
	
	/** Referenced element */
	Element getElement();

	/** Type of interaction, it helps to create categories of interaction objects. */
	int getType();
	
	void setType(int type);
	
	/** 
	 * <p>Returns one of {@link Geometry} direction to indicate it's relative
	 * position from it's owning the element.</p>
	 */
	int getIndex();
	
	void setIndex(int index);
	
	/** Returns the position of the center of the handle, in diagram coordinates. */ 
	float[] getPoint(DiagramContext context);
	
	/** Returns true if the detection point hit this. */
	boolean hitTesting(float[] detectionPoint, DiagramContext context);

	/**  The interaction object has been clicked. */
	boolean click(float[] point, int count, DiagramContext context);

	/** Moving the interaction object. */
	boolean move(float[] point, int step, DiagramContext context);

	/** Tooltip requested for this */
	boolean toolTipRequest(float[] point, DiagramContext context);

	/** Returns the cursors for this (null if default) */
	Cursor getCursor(DiagramContext context);
	
	/** Simple stub */
	public abstract class Stub implements InteractionObject {

		private final Element element;
		private int type;
		private int index;
		
		protected float[] point;
		
		public Stub(Element element, int type, int side, float[] point) {
			this.element = element;
			this.type = type;
			this.index = side;
			this.point = point;
		}

		public float[] getPoint(DiagramContext context) {
			return point;
		}

		public Element getElement() {
			return element;
		}

		public int getType() {
			return type;
		}
		
		public void setType(int type) {
			this.type = type;
		}
		
		public int getIndex() {
			return index;
		}
		
		public void setIndex(int index) {
			this.index = index;
		}

		public boolean hitTesting(float[] detectionPoint, DiagramContext context) {
			return false;
		}

		public boolean click(float[] point, int count, DiagramContext context) {
			return element.click(this, point, count, context);
		}

		public boolean move(float[] point, int step, DiagramContext context) {
			return element.move(point, this, step, context);
		}
		
		public boolean toolTipRequest(float[] point, DiagramContext context) {
			return false;
		}
		
		public Cursor getCursor(DiagramContext context) {
			return null;
		}
	}
}
