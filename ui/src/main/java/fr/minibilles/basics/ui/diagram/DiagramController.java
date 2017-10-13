package fr.minibilles.basics.ui.diagram;

import fr.minibilles.basics.geometry.Geometry;
import fr.minibilles.basics.geometry.Transformation;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.notification.NotificationSupport;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.ToolTipHandler;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.action.ActionManager;
import fr.minibilles.basics.ui.action.KeyCode;
import fr.minibilles.basics.ui.diagram.gc.GC;
import fr.minibilles.basics.ui.diagram.gc.GcUtils;
import fr.minibilles.basics.ui.diagram.gc.SWTGC;
import fr.minibilles.basics.ui.diagram.interaction.InteractionObject;
import fr.minibilles.basics.ui.diagram.interaction.Lasso;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

/**
 * {@link Diagram} controller.
 * Must not be Subclassed.
 * @author Jean-Charles Roger (original from Didier Simoneau).
 */
public final class DiagramController implements DiagramContext {
	
	/** If set to true, it will display the element bounds */
	private boolean debugBounds = false;
	
	/** The diagram being edited. */
	private Diagram<?> diagram;
	
	/** The control in which the diagram is displayed and edited. */
	private Control control;

	/** The composite that contains the controller actions */
	private Composite actionComposite;
	
	/** The application object that contains the diagram controller. */
	private Object container;
	
	/** The handler of SWT resources such as images, fonts, colors, etc... */
	private Resources uiResources;

	// TODO property change: zoom, scroll, selection
	private NotificationSupport.Stub notificationSupport = new NotificationSupport.Stub(this);
	
	/** Diagram action executer */
	private ActionExecuter executer = ActionExecuter.DEFAULT_EXECUTER;
	
	private final ActionManager actionManager = new ActionManager();
	
	public ActionExecuter getActionExecuter() {
		return executer;
	}
	
	public void setActionExecuter(ActionExecuter executer) {
		this.executer = executer;
		actionManager.setActionExecuter(executer);
	}
	
	public void addListener(NotificationListener listener) {
		notificationSupport.addListener(listener);
	}
	
	public void removePropertyChangeListener(NotificationListener listener) {
		notificationSupport.removeListener(listener);
	}
	
	private void fireProperty(int type, String name, Object oldValue, Object newValue) {
		notificationSupport.fireValueNotification(type, name, newValue, oldValue);
	}
	
	/**
	 * Constructs a new DiagramController
	 * 
	 * @param diagram the diagram to be displayed/edited
	 * @param destinationKind
	 *            the type of destination medium: one of
	 *            {@link DiagramContext#MAIN_VIEW},
	 *            TODO {@link DiagramContext#OUTLINE_VIEW},
	 *            {@link DiagramContext#PRINTER},
	 *            {@link DiagramContext#BITMAP_FILE},
	 *            {@link DiagramContext#VECTOR_FILE}
	 * @param control
	 *            the widget used to display/edit the diagram; typically a
	 *            {@link Canvas}; can be null, if the medium is not a widget,
	 *            e.g. a bitmap image
	 */
	public DiagramController(Diagram<?> diagram, int destinationKind, Control control) {
		this.diagram = diagram;
		this.destinationKind = destinationKind;
		this.uiResources = Resources.getInstance(diagram.getResourcesClass());
		resetZoom();
		updateTransformationMatrix();
		initControl(control);
	}
	
	public Diagram<?> getDiagram() {
		return diagram;
	}
	
	public Object getContainer() {
		return container;
	}
	
	public void setContainer(Object container) {
		this.container = container;
	}
	
	public ActionManager getActionManager() {
		return actionManager;
	}
	
	public Control getControl() {
		return control;
	}
	
	public void initControl(Control control) {
		this.control = control;
		if (control != null) {
			addPaintListener();
			addMouseListener();
			addKeyListener();
			if (control instanceof Scrollable) {
				addScrollListeners((Scrollable) control);
			}
			
			tooltipHandler.listenControl(control);
			actionManager.createPopupMenu(control, getRootAction());
			
			control.addListener(SWT.Dispose, new Listener() {
				public void handleEvent(Event event) {
					dispose();
				}
			});
		}
	}
	
	public void setActionComposite(Composite composite) {
		this.actionComposite = composite;
	}
	
	public void refreshCoolBar() {
		ActionManager.refreshWidgets(actionComposite);
	}
	
	public Resources getResources() {
		return uiResources;
	}
	
	// ******************** Painting ********************
	
	/** The type of medium this controller is drawing to. */
	private int destinationKind = MAIN_VIEW;
	
	/**
	 * Top left point of the visible part of the diagram, in diagram
	 * coordinates.
	 * <p>
	 * This field, together with the zoom field, define the current window of
	 * the editor. A change to any of these fields must be followed by a call to
	 * zoomOrScrollChanged() so that the transformationMatrix is recomputed
	 * and the display, scrolls, zoom combo and outline are refreshed.
	 */
	private float[] windowOrigin = new float[]{0f, 0f};
	
	/** Zoom factor */
	private float zoom = 1f;
	
	private float[] transformationMatrix;
	
	
	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public float[] getWindowOrigin() {
		return windowOrigin;
	}
	
	public void setWindowOrigin(float[] windowOrigin) {
		this.windowOrigin[0] = windowOrigin[0];
		this.windowOrigin[1] = windowOrigin[1];
	}
	
	/**
	 * The interaction objects contained in the diagram.
	 */
	private List<InteractionObject> interactionObjects = new ArrayList<InteractionObject>();
	
	public int getDestinationKind() {
		return destinationKind;
	}
	
	private void addPaintListener() {
		control.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent ev) {
				GC gc = new SWTGC(ev.gc);
				initializeGcTransform(gc);

				DiagramContext context = DiagramController.this;
				diagram.initializeGc(gc, context);
				diagram.displayBackground(gc, context);

				// TODO see if this paint method can refresh with no cost the diagram bounds.
				float[] clippingRectangle = GcUtils.fromSWTRectangle(gc.getClipping()); // get clipping from GC (in diagram coordinates)
				float[] boundingRectangle = new float[4];
				for (Element element : getDiagram().getElements()) {
					element.computeBounds(boundingRectangle, context);
					
					if ( debugBounds ) {
						gc.setForeground(getResources().getSystemColor(SWT.COLOR_RED));
						gc.setBackground(getResources().getSystemColor(SWT.COLOR_YELLOW));
						gc.setAlpha(128);
						GcUtils.drawRectangle(gc, boundingRectangle, true);
					}
					
					if (Geometry.rectangleIntersectsRectangle(boundingRectangle, clippingRectangle))  {
						getDiagram().resetGc(gc, context);
						element.display(gc, context);
					}
				}
				
				gc.setTransform(null); // use medium coordinates for interaction objects.
				for (Displayable interfactionObject : interactionObjects) {
					diagram.resetGc(gc, context);
					interfactionObject.display(gc, context);
				}
				Geometry.setNullRectangle(mergedDamagedRectangle);
			}
		});
	}
	
	private GC serviceGc;
	
	public GC getServiceGc() {
		if (serviceGc == null) {
			serviceGc = control != null ? new SWTGC(control) : new SWTGC(Display.getCurrent());
			diagram.initializeGc(serviceGc, this);
		}
		diagram.resetGc(serviceGc, this);
		return serviceGc;
	}
	
	public void invalidateRectangle(float[] damagedRectangle) {
		if ( control == null ) return;
		
		if (damagedRectangle == null) {
			Geometry.setLargestRectangle(mergedDamagedRectangle);
			control.redraw();
			return;
		}
		if (damagedRectangle[0] >= damagedRectangle[2] || damagedRectangle[1] >= damagedRectangle[3]) return;
		if (Geometry.rectangleContainsPoint(mergedDamagedRectangle, damagedRectangle[0], damagedRectangle[1])
				&& Geometry.rectangleContainsPoint(mergedDamagedRectangle, damagedRectangle[2], damagedRectangle[3])) return;
		Geometry.rectangleMergeWithRectangle(mergedDamagedRectangle, damagedRectangle);
		float x1 = Transformation.transformX(transformationMatrix, mergedDamagedRectangle[0], mergedDamagedRectangle[1]);
		float y1 = Transformation.transformY(transformationMatrix, mergedDamagedRectangle[0], mergedDamagedRectangle[1]);
		float x2 = Transformation.transformX(transformationMatrix, mergedDamagedRectangle[2], mergedDamagedRectangle[3]);
		float y2 = Transformation.transformY(transformationMatrix, mergedDamagedRectangle[2], mergedDamagedRectangle[3]);
		control.redraw(Math.round(x1) - 1, Math.round(y1) - 1, Math.round(x2 - x1) + 2, Math.round(y2 - y1) + 2, false);
	}
	
	private float[] mergedDamagedRectangle = new float[4];
	{ Geometry.setNullRectangle(mergedDamagedRectangle); }
	
	public void invalidate(Displayable damagedDisplayable) {
		if (damagedDisplayable == null) {
			invalidateRectangle(null);
			return;
		}
		float[] boundingRectangle = new float[4];
		damagedDisplayable.computeBounds(boundingRectangle, this);
		invalidateRectangle(boundingRectangle);
	}

	public <T> T findElementUnder(int type, float[] detectionPoint, Class<T> elementClass) {
		// searches for the first element to be hit from the last one to the first. 
		List<Element> hitElements = new ArrayList<Element>(); 
		final List<Element> elements = getDiagram().getElements();
		final float[] detectionRectangle = Geometry.rectangleFromPoints(detectionPoint, detectionPoint);
		Geometry.expandRectangle(detectionRectangle, 3f, 3f);
		for ( int i=elements.size()-1; i >=0; i-- ) {
			Element element = elements.get(i);
			element.hitTesting(hitElements, detectionPoint, detectionRectangle, type, this);
			if ( !hitElements.isEmpty() ) {
				Iterator<Element> iterator = hitElements.iterator();
				while ( iterator.hasNext() ) {
					Element current = iterator.next();
					if ( elementClass.isInstance(current) ) return elementClass.cast(current);
					iterator.remove();
				}
			}
		}
		return null;
	}

	public Element findElementUnder(int type, float[] detectionPoint) {
		return findElementUnder(type, detectionPoint, Element.class);
	}
	
	public void addInteractionObject(InteractionObject object) {
		interactionObjects.add(object);
	}
	
	public void removeInteractionObject(InteractionObject object) {
		interactionObjects.remove(object);
	}
	
	public void refreshElements(boolean rebuild) {
		if (rebuild) {
			getDiagram().build();
			findSelectionNewElements();
			hoveredElements.clear();
		}
		updateTransformationMatrix();
		invalidateRectangle(null);
		updateScrollBars();
	}
	
	public void refreshInteractions() {
		updateHoveredElements();
		updateSelectedInteractions();
		refreshCoolBar();
	}

	public float[] getTransformationMatrix() {
		return transformationMatrix;
	}

	private void updateTransformationMatrix() {
		float transformationScale = zoom;
		transformationMatrix = Transformation.newTransformation(transformationScale, transformationScale, windowOrigin, new float[2]);
	}

	/** Returns the control bounding rectangle, in medium coordinates. */
	private float[] getControlBounds() {
		Rectangle r = control instanceof Scrollable ? ((Scrollable) control).getClientArea() : control.getBounds();
		float[] controlBounds = new float[]{r.x, r.y, r.x + r.width, r.y + r.height};
		return controlBounds;
	}
	
	private void updateScrollBars() {
		if (control instanceof Scrollable) {
			Scrollable scrollable = (Scrollable) control;
			float[] diagramBounds = diagram.getElementsBounds(this);
			float[] controlBounds = getControlBounds();
			
			if ( Geometry.rectangleContainsPoint(diagramBounds, windowOrigin) == false ) {
				// window origin is out of diagram bounds move it inside
				windowOrigin[0] = diagramBounds[0];
				windowOrigin[1] = diagramBounds[1];
			}
			
			Transformation.inverseTransform(transformationMatrix, controlBounds);
			ScrollBar hBar = scrollable.getHorizontalBar();
			if (hBar != null) {
				hBar.setMinimum(Math.round(diagramBounds[0]));
				hBar.setMaximum(Math.round(diagramBounds[2]));
				hBar.setThumb(Math.round(controlBounds[2] - controlBounds[0]));
				hBar.setPageIncrement(hBar.getThumb());
				hBar.setIncrement(hBar.getThumb() / 10);
				hBar.setSelection(Math.round(controlBounds[0]));
			}
			ScrollBar vBar= scrollable.getVerticalBar();
			if (vBar != null) {
				vBar.setMinimum(Math.round(diagramBounds[1]));
				vBar.setMaximum(Math.round(diagramBounds[3]));
				vBar.setThumb(Math.round(controlBounds[3] - controlBounds[1]));
				vBar.setPageIncrement(vBar.getThumb());
				vBar.setIncrement(vBar.getThumb() / 10);
				vBar.setSelection(Math.round(controlBounds[1]));
			}
		}
	}

	/** Initializes the gc's transform to the transformationMatrix. This is to take current zoom and scroll into account. */
	public void initializeGcTransform(GC gc) {
		if (Transformation.isIdentity(transformationMatrix)) {
			gc.setTransform(null); // no need to use a transformation in this case
		}
		else {
			Transform transform = new Transform(gc.getDevice(), transformationMatrix);
			gc.setTransform(transform);
			transform.dispose();
		}
	}

	private void addScrollListeners(Scrollable canvas) {
		final ScrollBar hBar = canvas.getHorizontalBar();
		if (hBar != null) {
			hBar.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent ev) {
					windowOrigin[0] = hBar.getSelection();
					refreshElements(false);
				}
			});
		}
		final ScrollBar vBar = canvas.getVerticalBar();
		if (vBar != null) {
			vBar.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent ev) {
					windowOrigin[1] = vBar.getSelection();
					refreshElements(false);
				}
			});
		}
		if (hBar != null) {
			// Uses  shift+wheel to scroll horizontally:
			canvas.addMouseWheelListener(new MouseWheelListener() {
				public void mouseScrolled(MouseEvent ev) {
					if ((ev.stateMask & SWT.SHIFT) != 0) {
						boolean wheelUp = ev.count > 0;
						windowOrigin[0] = Math.round(hBar.getSelection() + (wheelUp ? - hBar.getIncrement() : hBar.getIncrement()));
						refreshElements(false);
					}
				}
			});
		}
		
		control.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent ev) {
				refreshElements(false);
			}
			public void controlResized(ControlEvent ev) {
				refreshElements(false);
			}
		});

	}
	
	public void resetZoom() {
		zoom = 1f;
	}

	private void applyZoomRatio(float ratio) {
		// applies ratio to zoom
		zoom = zoom * ratio;
	}
	
	public void increaseZoom() {
		applyZoomRatio(1.25f);
	}
	
	public void decreaseZoom() {
		applyZoomRatio(0.75f);
	}
	
	public Action createIncreaseZoomAction() {
		return new Action.Stub("+ Zoom", getResources().getImage("zoom_plus.gif")) {
			@Override
			public int run(ActionMonitor monitor) {
				increaseZoom();
				updateTransformationMatrix();
				return STATUS_OK;
			}
		};
	}
	
	public Action createDecreaseZoomAction() {
		return new Action.Stub("- Zoom", getResources().getImage("zoom_minus.gif")) {
			@Override
			public int run(ActionMonitor monitor) {
				decreaseZoom();
				updateTransformationMatrix();
				return STATUS_OK;
			}
		};
	}
	
	public Element getPrimarySelection() {
		if ( selectedElements.isEmpty() ) return null;
		return selectedElements.get(0);
	}

	public List<Element> getSelectedElements() {
		return selectedElements;
	}

	public boolean isSelected(Element element) {
		return selectedElements.contains(element);
	}

	public void setSelectedElements(Collection<Element> selectedElements) {
		setSelectedElements(selectedElements, true);
	}
	
	private void setSelectedElements(Collection<Element> selectedElements, boolean fireproperty) {
		this.selectedElements.clear();
		this.selectedElements.addAll(selectedElements);
		fireProperty(Notification.TYPE_API, BasicsUI.NOTIFICATION_SELECTION, null, this.selectedElements);
	}
	
	/** Hovered elements are pointed by the mouse while moving. */
	private ArrayList<Element> hoveredElements = new ArrayList<Element>();
	
	/** Hovered interaction objects appears for hovered objects */
	private ArrayList<InteractionObject> hoveredInteractions = new ArrayList<InteractionObject>();
	
	private float[] mousePoint = new float[2];
	
	public float[] getMousePoint() {
		return mousePoint;
	}
	
	/** Buttons used for move. */
	private boolean[] movingButtons = new boolean[] { false, true, false, false };

	public boolean isMovingButton(int number) {
		return movingButtons[number];
	}
	
	/**
	 * <p>Set for a button number if it triggers the move.</p> 
	 * <p>1 is left button, 2 middle and 3 right.</p>
	 */
	public void setMovingButton(int number, boolean moving) {
		movingButtons[number] = moving;
	}
	
	private float[] clickedMousePoint = new float[2];

	public float[] getClickedMousePoint() {
		return clickedMousePoint;
	}

	/** Clicked interactions are pointed when the mouse down event is caught. */
	private InteractionObject clickedInteraction = null;
	
	private float[] lassoRectangle = null;
	private Lasso lasso = null;
	
	private ArrayList<Element> selectedElements = new ArrayList<Element>();

	private ArrayList<Element> movingElements = new ArrayList<Element>();

	public List<Element> getMovingElements() {
		return movingElements;
	}
	
	/** Selected interaction objects appears for selected objects */
	private ArrayList<InteractionObject> selectionInteractions = new ArrayList<InteractionObject>();

	private boolean mouseButtonDown = false;
	
	/** Stores the moving element offset from click point */
	private ArrayList<float[]> movingElementOffsets = null;
	
	/** Stores the {@link InteractionObject} to use for move (if any) */
	private ArrayList<InteractionObject> movingElementInteractions = null;
	
	private int movingStep = -1;
	
	/** Updates hovered elements and interactions using current mousePoint. */
	private void updateHoveredElements() {
		ArrayList<Element> newHoveredElements = new ArrayList<Element>();
		// hitTesting for HOVER_EXIT for all hovered elements
		final float[] detectionRectangle = Geometry.rectangleFromPoints(mousePoint, mousePoint);

		for (Element element : hoveredElements ) {
			invalidate(element);
			element.hitTesting(newHoveredElements, mousePoint, detectionRectangle, HIT_HOVER_EXIT, DiagramController.this);
		}
		
		// hitTesting for HOVER_ENTER for not hovered elements
		for (Element element : getDiagram().getElements() ) {
			if ( !hoveredElements.contains(element) ) {
				element.hitTesting(newHoveredElements, mousePoint, detectionRectangle, HIT_HOVER_ENTER, DiagramController.this);
			}
		}
		hoveredElements = newHoveredElements;
		
		// computes new hovered interaction objects.
		ArrayList<InteractionObject> newHoveredInteraction = new ArrayList<InteractionObject>();
		for ( Element element : hoveredElements ) {
			element.computeInteractionObjects(newHoveredInteraction, HIT_HOVER_ENTER, DiagramController.this);
		}
		
		// invalidate old ones
		for ( InteractionObject interaction : hoveredInteractions ) invalidate(interaction);
		interactionObjects.removeAll(hoveredInteractions);
		
		hoveredInteractions = newHoveredInteraction;

		interactionObjects.addAll(hoveredInteractions);
		// invalidate new ones
		for ( InteractionObject interaction : hoveredInteractions ) invalidate(interaction);
	}

	private void addMouseListener() {
		control.addMouseMoveListener(new MouseMoveListener() {
			
			public void mouseMove(MouseEvent e) {
				mousePoint[0] = Transformation.inverseTransformX(transformationMatrix, e.x, e.y);
				mousePoint[1] = Transformation.inverseTransformY(transformationMatrix, e.x, e.y);

				if ( mouseButtonDown ) {
					if ( !movingElements.isEmpty() ) {
						movingStep++;
						
						if ( movingStep == 0 ) {
							prepareMove();
						}
						
						moveElements();
					} else {
						lassoRectangle = Geometry.rectangleFromPoints(clickedMousePoint, mousePoint);
						if ( lasso != null ) {
							invalidate(lasso);
							interactionObjects.remove(lasso);
						}

						lasso = new Lasso(lassoRectangle);
						interactionObjects.add(lasso);
						invalidate(lasso);
						
						computeSelectedElements(e);
						updateSelectedInteractions();
					}
				} else {
					updateHoveredElements();
					InteractionObject hoverInteraction = getInteractionUnder(mousePoint);
					control.setCursor(hoverInteraction != null ? hoverInteraction.getCursor(DiagramController.this) : null);
				}
			}
		});
		
		control.addMouseListener(new MouseAdapter() {
			
			public void mouseDown(MouseEvent e) {
				clickedMousePoint[0] = Transformation.inverseTransformX(transformationMatrix, e.x, e.y);
				clickedMousePoint[1] = Transformation.inverseTransformY(transformationMatrix, e.x, e.y);
				
				if ( isMovingButton(e.button) ) {
					mouseButtonDown = true;
				
					clickedInteraction = getInteractionUnder(clickedMousePoint);
					computeMovingElement(e);
				}

			}

			public void mouseUp(final MouseEvent e) {
				if ( isMovingButton(e.button) ) {
					mouseButtonDown = false;
					if ( movingStep >= 0 ) {
						// selected elements must be set before last move (with step -1)
						setSelectedElements(movingElements);
	
						// end moving 
						movingStep = -1;
						executer.executeAction(new Action.Stub("Moving", Action.STYLE_TRANSACTIONNAL) {
							@Override
							public int run(ActionMonitor monitor) {
								return moveElements() ? STATUS_OK : STATUS_CANCEL;
							}
						});
						
	
					} else {
						computeSelectedElements(e);
					}
					
					updateSelectedInteractions();
					
					cleanMove();
					// handle lasso
					if ( lasso != null ) {
						invalidate(lasso);
						interactionObjects.remove(lasso);
						lassoRectangle = null;
						lasso = null;
					}
					
					if ( e.count > 0 ) {
						executer.executeAction(new Action.Stub("Click", Action.STYLE_TRANSACTIONNAL) {
							@Override
							public int run(ActionMonitor monitor) {
								boolean click = false;
								Element alreadySendClick = null;
								if ( clickedInteraction != null ) {
									click |= clickedInteraction.click(mousePoint, e.count, DiagramController.this);
									alreadySendClick = clickedInteraction.getElement();
								}
								// send click to all selectedElements that haven't been click through an interaction object. 
								for ( Element element : selectedElements ) {
									if ( element != alreadySendClick ) {
										click |= element.click(null, mousePoint, e.count, DiagramController.this);
									}
								}
								click |= getDiagram().click(e.count, DiagramController.this);
								return click ? STATUS_OK : STATUS_CANCEL;
							}
						});
					}
				}
			}
		});
		
		// TODO implement the use of mouse wheel
	}
	
	private void computeMovingElement(MouseEvent event) {
		// constructs moving elements list.
		Element clickedElement = findElementUnder(HIT_SELECTION, clickedMousePoint);

		if ( clickedElement == null && clickedInteraction != null ) {
			clickedElement = clickedInteraction.getElement();
		}
		
		if ( clickedElement != null ) {
			final boolean keepPreviousSelection = (event.stateMask & SWT.MOD1) != 0;
			final boolean containsClicked = selectedElements.contains(clickedElement);
			if ( keepPreviousSelection || containsClicked ) {
				movingElements.addAll(selectedElements);
			}
			if ( !containsClicked )	movingElements.add(0, clickedElement);
		}
	}

	private void computeSelectedElements(MouseEvent e) {
		ArrayList<Element> newSelectedElements = new ArrayList<Element>();
		final boolean keepPrevious = (e.stateMask & SWT.MOD1) != 0;
		if ( keepPrevious ) newSelectedElements.addAll(getSelectedElements());

		if ( lasso != null ) {
			ArrayList<Element> lassoClickedElements = new ArrayList<Element>();
			for (Element element : getDiagram().getElements() ) {
				element.hitTesting(lassoClickedElements, null, lassoRectangle, HIT_SELECTION, DiagramController.this);
			}
			for ( Element clickedElement : lassoClickedElements) {
				computeSelectedElement(newSelectedElements, keepPrevious, clickedElement);
			}

		} else {
			Element clickedElement = findElementUnder(HIT_SELECTION, clickedMousePoint);
			computeSelectedElement(newSelectedElements, keepPrevious, clickedElement);
		}
		setSelectedElements(newSelectedElements);
	}

	private void computeSelectedElement(ArrayList<Element> result, final boolean keepPrevious, Element element) {
		if ( keepPrevious ) {
			if ( element != null ) {
				if ( result.contains(element) ) {
					result.remove(element);
				} else {
					result.add(0, element);
				}
			}
		} else {
			if ( element != null ) {
				result.add(element);
			}
		}
	}

	/**
	 * <p>Searches if selected elements still exist in diagram. If it's not the
	 * case, it tries to find the new elements from the model they represent. It
	 * assures that all selected elements are diagram elements.</p>
	 * <p>It's called by {@link #refreshElements(boolean)} (with rebuild at 
	 * true).</p>
	 */
	public void findSelectionNewElements() {
		final int size = getSelectedElements().size();
		Set<Element> newSelectedElements = new HashSet<Element>(size);
		for ( Element selected : getSelectedElements() ) {
			Element newSelected = getDiagram().findElement(selected.getClass(), selected.getModel(), selected.getModel2());
			if ( newSelected != null ) newSelectedElements.add(newSelected);
		}
		setSelectedElements(newSelectedElements, newSelectedElements.size() != size);
	}
	
	/** return the interaction object under point */
	private InteractionObject getInteractionUnder(float[] point) {
		for ( int i=interactionObjects.size() - 1; i>=0; i--) {
			InteractionObject interaction = interactionObjects.get(i);
			if ( interaction.hitTesting(point, DiagramController.this) ) {
				return interaction;
			}
		}
		return null;
	}

	private void updateSelectedInteractions() {
		// computes new hovered interaction objects.
		// first invalidate old ones
		for ( InteractionObject interaction : selectionInteractions ) invalidate(interaction);

		ArrayList<InteractionObject> newSelectedInteractions = new ArrayList<InteractionObject>();
		for ( Element element : selectedElements ) {
			element.computeInteractionObjects(newSelectedInteractions, HIT_SELECTION, DiagramController.this);
		}
		
		interactionObjects.removeAll(selectionInteractions);
		selectionInteractions = newSelectedInteractions;
		interactionObjects.addAll(selectionInteractions);
		
		// invalidate new ones
		for ( InteractionObject interaction : selectionInteractions ) invalidate(interaction);
	}
	
	private void prepareMove() {
	
		movingElementOffsets = new ArrayList<float[]>(movingElements.size());
		if ( clickedInteraction != null ) movingElementInteractions = new ArrayList<InteractionObject>();
		
		for ( Element element : movingElements ) {
			
			InteractionObject currentInteraction = null;
			
			if ( clickedInteraction != null ) {
				if ( element == clickedInteraction.getElement() ) {
					movingElementInteractions.add(clickedInteraction);
					currentInteraction = clickedInteraction;
				} else {
					for ( InteractionObject interaction : interactionObjects ) {
						if ( interaction.getElement() == element && 
							getDiagram().equivalentInteractions(interaction, clickedInteraction) ) {
							currentInteraction = interaction;
							break;
						}
					}
					movingElementInteractions.add(currentInteraction);
				}
			}
			
			float[] referencePoint = currentInteraction != null ? currentInteraction.getPoint(this) : element.getPoint();
			float[] offset = new float[] { 
					referencePoint[0] - mousePoint[0] ,
					referencePoint[1] - mousePoint[1]   
				};
			movingElementOffsets.add(offset);

		}
		
		// clear selection interaction while starts moving
		for ( InteractionObject interaction : selectionInteractions ) invalidate(interaction);
		interactionObjects.removeAll(selectionInteractions);
		selectionInteractions = new ArrayList<InteractionObject>();

		
	}
	
	private boolean moveElements() {
		boolean move = false;
		for (int i=0; i<movingElements.size(); i++ ) {
			final Element element = movingElements.get(i);
			final float[] offset = movingElementOffsets.get(i);
			final float[] point = new float[] { 
					mousePoint[0] + offset[0],
					mousePoint[1] + offset[1]
			};
			
			if ( movingElementInteractions != null ) {
				final InteractionObject interactionObject = movingElementInteractions.get(i);
				if ( interactionObject != null ) {
					move |= interactionObject.move(point, movingStep, this);
				}
			} else {
				move |= element.move(point, null, movingStep, this);
			}
		}
		move |= getDiagram().move(movingStep, this);
		return move;
	}
	
	private void cleanMove() {
		movingElements.clear();
		movingElementOffsets = null;
		movingElementInteractions = null;
	}
	
	// Action part
	private final Action rootAction = new Action.Stub(Action.STYLE_HIERARCHICAL) {
		public java.util.List<Action> getActions() {
			return getCurrentActionList();
		}
	};
	
	private void addKeyListener() {
		control.addKeyListener(new KeyListener() {
			
			public void keyReleased(final KeyEvent e) {
				final KeyCode code = KeyCode.fromEvent(e);
				executer.executeAction(new Action.Stub("CopyCutPaste", Action.STYLE_TRANSACTIONNAL) {
					@Override
					public int run(ActionMonitor monitor) {
						boolean done = e.doit;
						done = diagram.keyPressed(code, DiagramController.this);
						return done ? STATUS_OK : STATUS_CANCEL;
					}
				});
			}
			
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	
	private Action getRootAction() {
		return rootAction;
	}
	
	public List<Action> getCurrentActionList() {
		List<Action> actions = new ArrayList<Action>();
		Element element = findElementUnder(HIT_SELECTION, getClickedMousePoint());
		if (element != null ) {
			element.computeActions(actions, this);
		} else {
			getDiagram().computeActions(actions, this);
		}
		return actions;
	}
	
	public Action createSaveImageAction() {
		return new Action.Stub("Image\u2026", getResources().getImage("eclipse/print_edit.gif")) {
			@Override
			public int run(ActionMonitor monitor) {
				FileDialog dialog = new FileDialog(control.getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.png" });
				dialog.setFilterNames(new String[] { "PNG image" });
				String filename = dialog.open();
				if ( filename == null ) return STATUS_CANCEL;
				if ( !filename.endsWith(".png") ) filename += ".png";
				DiagramUtils.exportToImage(diagram, filename, 1f, SWT.IMAGE_PNG);
				return STATUS_OK;
			}
		};
	}
	
	// Tooltip part

	/** Handles the tooltips. */
	private ToolTipHandler tooltipHandler = new ToolTipHandler() {
		public boolean toolTipRequest(Control control, int x, int y) {
			float [] point = new float[2];
			point[0] = Transformation.inverseTransformX(transformationMatrix, x, y);
			point[1] = Transformation.inverseTransformY(transformationMatrix, x, y);
			
			// TODO check for element under (tooltip)
			Element element = findElementUnder(HIT_SELECTION, point);
			if ( element != null ) {
				return element.toolTipRequest(point, DiagramController.this);
			}
			return false;
		}
	};

	public void showBalloon(Image image, String text) {
		Color fg = getResources().getSystemColor(SWT.COLOR_BLACK);
		Color bg = getDiagram().getBackground(this);
		int x = Math.round(Transformation.transformX(transformationMatrix, mousePoint[0], mousePoint[1]));
		int y = Math.round(Transformation.transformY(transformationMatrix, mousePoint[0], mousePoint[1]));
		tooltipHandler.showBalloon(image, text, fg, bg, x,y);
	}
	
	public void setDebugBounds(boolean debugBounds) {
		this.debugBounds = debugBounds;
	}
	
	public void dispose() {
		if ( serviceGc != null ) serviceGc.dispose();
		Resources.releaseInstance(getDiagram().getResourcesClass());
	}
	
	
}
