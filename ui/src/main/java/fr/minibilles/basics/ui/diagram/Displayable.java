/**
 * 
 */
package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.ui.diagram.gc.GC;

/**
 * <p>Displayable object.</p>
 * @author Jean-Charles Roger (original from Didier Simoneau)
 */
public interface Displayable {

	/**
	 * <p>Display this on given GC.</p>
	 * 
	 * @param gc {@link GC} where to display this.
	 * @param context the {@link DiagramContext} to provide information on context.
	 */
	public void display(GC gc, DiagramContext context);
	
	/**
	 * Compute bounding rectangle for this.
	 * @param result not initialized array of 4 floats must contain the result.
	 * @param context the {@link DiagramContext} to provide information on context.
	 */
	public void computeBounds(float[] result, DiagramContext context);

}
