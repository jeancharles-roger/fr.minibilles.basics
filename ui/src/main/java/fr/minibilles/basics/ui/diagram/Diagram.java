package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.KeyCode;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>Abstract class for all diagrams.</p>
 * 
 * @author Jean-Charles Roger (original from Didier Simoneau).
 */
public abstract class Diagram<M> {

	/** Diagram's model */
	protected M model;
	
	/**
	 * The list of the elements this diagram contains.
	 */
	private List<Element> elements = new ArrayList<Element>();
	
	/**
	 * Stores the {@link Element}s by their first model (see {@link Element#getModel()}). 
	 */
	private Map<Object, List<Element>> elementsCacheByModel = new HashMap<Object, List<Element>>();
	
	/**
	 * Caches the last computed diagram bounds. Diagram bounds is the merge of
	 * the bounds of its elements.
	 */
	private float[] elementsBounds = null;
	
	public List<Element> getElements() {
		return elements;
	}

	public M getModel() {
		return model;
	}
	
	public void setModel(M model) {
		this.model = model;
	}
	
	/** Clears the bounds cache. The next the bounds will be calculated. */
	public void invalidateBounds() {
		elementsBounds = null;
	}
	
	public void addElement(Element element) {
		addElement(elements.size(), element);
	}
	
	public void addElements(Collection<Element> c) {
		for (Element element : c) addElement(element);
	}
	
	public void addElement(int index, Element element) {
		elements.add(index, element);
		
		// puts the element in the cache
		final Object elementModel = element.getModel();
		List<Element> cachedElements = elementsCacheByModel.get(elementModel);
		if ( cachedElements == null ) {
			cachedElements = new ArrayList<Element>();
			elementsCacheByModel.put(elementModel, cachedElements);
		}
		cachedElements.add(element);
	}
	
	public <T> T findElement(Class<T> type, Object model1) {
		return findElement(type, model1, null);
	}
	
	public <T> T findElement(Class<T> type, Object model1, Object model2) {
		final List<Element> cachedElement = elementsCacheByModel.get(model1);
		if ( cachedElement != null ) {
			for ( Element element : cachedElement ) {
				if ( element.getModel() == model1 && element.getModel2() == model2 ) {
					if ( type.isInstance(element) ) {
						return type.cast(element);
					}
				}
			}
		}
		return null;
	}
	
	
	public void clearElements() {
		elements = new ArrayList<Element>();
		elementsCacheByModel = new HashMap<Object, List<Element>>();
	}
	
	public abstract void build();

	// ******************** Display methods ********************
	
	public void displayBackground(GC gc, DiagramContext context) {
		resetGc(gc, context);
		Rectangle clippingRect = gc.getClipping();
		gc.fillRectangle(clippingRect.x - 5, clippingRect.y - 5, clippingRect.width + 10, clippingRect.height + 10);
	}
	
	public float[] getElementsBounds(DiagramContext context) {
		if (elementsBounds == null) {
			// bounds were invalidated: recomputes them
			elementsBounds = new float[] { 0, 0, 0 , 0};
			//Geometry.setNullRectangle(elementsBounds);
			float[] elementBounds = new float[4];
			for (Element element : elements) {
				// merges the bounds of all elements:
				element.computeBounds(elementBounds, context);
				Geometry.rectangleMergeWithRectangle(elementsBounds, elementBounds);
			}
		}
		return elementsBounds;
	}

	/**
	 * Set the initial state of the GC. This is done only once when displaying a
	 * diagram and NOT before displaying each new element.
	 */
	public void initializeGc(GC gc, DiagramContext context) {
		gc.setAntialias(SWT.ON);
		gc.setFont(getStandardFont(context));
		gc.setLineCap(SWT.CAP_ROUND);
		gc.setLineJoin(SWT.JOIN_ROUND);
	}

	/**
	 * Restore the most commonly modified parameters of the GC. This is done before
	 * displaying a new element. This removes a little burden on the programmer
	 * when defining the display methods. All parameters that are not handled in
	 * this method MUST be restored explicitly to their entry state.
	 */
	public void resetGc(GC gc, DiagramContext context) {
		gc.setForeground(getForeground(context));
		gc.setBackground(getBackground(context));
		gc.setLineWidth(2); // 0.2 mm
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setAlpha(255);
	}

	/**
	 * Return the standard font used on the diagram.
	 */
	public Font getStandardFont(DiagramContext context) {
		return context.getResources().getSystemFont();
	}
	
	public Color getForeground(DiagramContext context) {
		return context.getResources().getSystemColor(SWT.COLOR_BLACK);
	}
	
	public Color getBackground(DiagramContext context) {
		return context.getResources().getSystemColor(SWT.COLOR_WHITE);
	}
	
	public Color getHandleColor(DiagramContext context) {
		return context.getResources().getSystemColor(SWT.COLOR_LIST_SELECTION);
	}
	
	
	/**
	 * Returns the class of SwtResource to be used with this diagram. Can be
	 * redefined in sub-classes of this Diagram if a subclass of Resources
	 * class is required.
	 */
	public Class<? extends Resources> getResourcesClass() {
		return Resources.class;
	}

	/**
	 * TODO do javadoc for {@link #click(int, DiagramContext)}
	 * @param count
	 * @param context
	 */
	public boolean click(int count, DiagramContext context) {
		return false;
	}
	
	/**
	 * TODO Review javadoc for {@link #move(int, DiagramContext)}
	 * Called during of a move/resize interaction, after all moving elements have been sent the move message.<br>
	 * This is a convenience place to update the model with the new graphical
	 * locations; this method is executed within a transaction. Moved elements
	 * can be obtained with context.getTouchedElements().
	 * @param step
	 * @param context
	 */
	public boolean move(int step, DiagramContext context) {
		return false;
	}

	public boolean keyPressed(KeyCode code, DiagramContext context) {
		return false;
	}
	
	public void computeActions(List<Action> result, DiagramContext context) {
	}


	/** Returns true if interaction objects are equivalent. */
	public boolean equivalentInteractions(InteractionObject first, InteractionObject second) {
		boolean result =  
			first.getType() == second.getType() &&
			first.getIndex() == second.getIndex();
		return result;
	}

}
