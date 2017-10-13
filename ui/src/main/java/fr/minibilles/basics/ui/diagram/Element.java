package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.graphics.Image;

/**
 * <p>An element is a {@link Displayable} representation of a model's element</p>
 * 
 * @author Jean-Charles Roger (original from Didier Simoneau).
 */
public interface Element extends Displayable {

	/**
	 * @return the major model for this element.
	 */
	Object getModel();

	/**
	 * @return the additional model for this element.
	 */
	Object getModel2();

	/**
	 * @return the reference point for this element.
	 */
	float[] getPoint();

	void computeInteractionObjects(List<InteractionObject> result, int type, DiagramContext context);
	
	void hitTesting(List<Element> result, float[] detectionPoint, float[] detectionRectangle, int type, DiagramContext context);

	/**
	 * TODO comment
	 * @param interaction
	 *            the handle being clicked, or null if the element itself is
	 *            dragged.
	 * @param point the clicked point.
	 * @param count the number of click done, 1 is single click, 2 double click 
	 * 			  and so one if you can find use for more clicks.
	 * @param context
	 *            the current editing context
	 * @return true if click actually did something in the model.
	 */
	boolean click(InteractionObject interaction, float[] point, int count, DiagramContext context);

	/**
	 * TODO comment
	 * 
	 * @param interaction
	 *            the handle being dragged, or null if the element itself is
	 *            dragged.
	 * @param point
	 *            the position where this element is to be moved to; this is the
	 *            mouse position corrected with an offset that is different for
	 *            each moving object.
	 * @param step
	 *            the step counter; it is incremented each time this method is
	 *            called; at the end of the drag (on mouse release), this method
	 *            is called with step equals to -1; this allows to execute some
	 *            final code; this final call is executed within a transaction
	 * @param context
	 *            the current editing context
	 * @return true if move actually did something in the model.
	 * @see DiagramContext#getMousePoint() to get the exact mouse position.
	 */
	boolean move(float[] point, InteractionObject interaction, int step, DiagramContext context);

	/**
	 * Returns the list of actions to be presented when this element is selected.
	 * @param context the current editing context
	 * @return the action list
	 */
	public void computeActions(List<Action> result, DiagramContext context);

	/**
	 * Requests a tooltip information balloon. If this element can provide a
	 * tooltip, this method implementation should call context.displayToolTip()
	 * and return true, else it should return false.
	 * @param point the position of the mouse when the request as been detected
	 * @param context the current editing context
	 * @see DiagramContext#showBalloon(Image, String)
	 */
	public boolean toolTipRequest(float[] point, DiagramContext context);
	
	/**
	 * A stub implementation of interface {@link Element}, suitable as a base
	 * class for concrete implementations.
	 */
	public abstract class Stub implements Element {

		public Object getModel2() {
			return null;
		}

		public void computeInteractionObjects(List<InteractionObject> result, int type, DiagramContext context) { }

		public void hitTesting(List<Element> result, float[] detectionPoint, float[] detectionRectangle, int type, DiagramContext context) {
		}
		
		public boolean click(InteractionObject interaction, float[] point, int count, DiagramContext context) {
			return false;
		}

		public boolean move(float[] point, InteractionObject interaction, int step, DiagramContext context) {
			return false;
		}
		
		public void computeActions(List<Action> result, DiagramContext context) {
			
		}

		public boolean toolTipRequest(float[] point, DiagramContext context) {
			return false;
		}
		
		/** Return a string representation of the element, for debugging purposes. */
		@Override
		public String toString() {
			return getClass().getSimpleName() + Arrays.toString(getPoint()) + ' ' + (getModel() != null ? getModel().toString() : "(null)");
		}
		
}
	
}
