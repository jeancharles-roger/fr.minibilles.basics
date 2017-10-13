package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import java.util.Collection;
import java.util.List;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * <p>A {@link DiagramContext} is given to all diagram methods.</p>
 * 
 * @author Jean-Charles Roger (original from Didier Simoneau).
 */
public interface DiagramContext {

	// possible values for hitTesting type
	final static int HIT_SELECTION = 0;
	final static int HIT_HOVER_ENTER = 1;
	final static int HIT_HOVER_EXIT = 2;
	
	// possible values for getDestinationKind()
	final static int MAIN_VIEW = 0;
	final static int OUTLINE_VIEW = 1;
	final static int PRINTER = 2;
	final static int BITMAP_FILE = 3;
	final static int VECTOR_FILE = 4;
	
	/** Returns the current diagram being displayed. */
	Diagram<?> getDiagram();
	
	/** The application object that contains the diagram context. */
	Object getContainer();
	
	/** Returns the current transformation matrix. */
	float[] getTransformationMatrix();
	
	/** Returns the SWT resource handler. */
	Resources getResources();
	
	/**
	 * Returns the current selection.
	 * @return the list of the currently selected elements
	 */
	List<Element> getSelectedElements();
	
	/**
	 * Returns the current primary selection. It is the last of the selected elements.
	 * @return the primary selection or null if no element is selected
	 */
	Element getPrimarySelection();
	
	/**
	 * Tests if a given element is currently selected within this
	 * DisplayingContext. This can be used within the display() methods to alter
	 * the display of some elements when these are selected.
	 * <p>
	 * It is expected to be faster than getSelectedElements().contains(element)
	 * thanks to the use of a HashSet.
	 */
	boolean isSelected(Element element);
	
	/**
	 * Specifies the medium type of the displaying been done. This can be used
	 * within the display() methods to alter the display of some elements
	 * depending on the destination display medium. way.
	 * <p>
	 * For example, in the outline view, we could choose not to display any
	 * text.
	 */
	int getDestinationKind();
	
	/**
	 * Returns a GC. This GC is initialized with the initial settings and the
	 * current transformation.
	 * <p>
	 * A typical use is for queries such as getting the extent of a string.
	 * 
	 * @return the GC
	 */
	GC getServiceGc();

	/**
	 * Returns the current zoom value. 100% is represented by the float value 1f.
	 * @return the current zoom
	 */
	//float getZoom();
	
	/**
	 * Causes the area specified by the given rectangle to be marked as needing
	 * to be redrawn.
	 * 
	 * @param damagedRectangle
	 *            the rectangle specifying the area to be redrawn; passing null
	 *            causes the whole control to be redrawn.
	 */
	void invalidateRectangle(float[] damagedRectangle);

	/**
	 * Causes the area specified by the bounds of the given object to be marked
	 * as needing to be redrawn.
	 * 
	 * @param damagedDisplayable
	 *            the object whose bounds specifies the area to be redrawn; passing null
	 *            causes the whole control to be redrawn.
	 */
	void invalidate(Displayable damagedDisplayable);

	/** Adds an {@link InteractionObject} to the diagram context. */
	void addInteractionObject(InteractionObject object);
	
	/** Removes an {@link InteractionObject} from the diagram context. */
	void removeInteractionObject(InteractionObject object);

	/**
	 * <p>Refreshes the diagram elements.</p>
	 * @param rebuild if true it will first rebuild the diagram.
	 */
	void refreshElements(boolean rebuild);
	
	/**
	 * <p>Refreshes the diagram interactions.</p>
	 */
	void refreshInteractions();
	
	/**
	 * Changes the current selection. Changes only the internal state of the
	 * controller. Does not refresh the handles and the display.
	 * @param selectedElements the new selection
	 */
	void setSelectedElements(Collection<Element> selectedElements);

	/**
	 * Returns the list of currently moving elements. Valid only during a move user interaction.
	 * @return the list of the currently moving elements
	 */
	List<Element> getMovingElements();
	
	Element findElementUnder(int type, float[] detectionPoint);
	
	<T> T findElementUnder(int type, float[] detectionPoint, Class<T> elementClass);
	
	/**
	 * Returns the current mouse position in diagram coordinates. This is the
	 * exact mouse position: no offset is applied.
	 */
	float[] getMousePoint();
	
	/**
	 * Returns the clicked position in diagram coordinates. This is the
	 * exact mouse position: no offset is applied.
	 */
	float[] getClickedMousePoint();

	/** Returns the SWT control in which this editing context is displaying. */
	Control getControl();
	
	void showBalloon(Image image, String text);
}
