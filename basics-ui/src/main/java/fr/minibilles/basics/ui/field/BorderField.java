package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

public class BorderField extends CompositeField {

	private final Field center;
	private Composite centerComposite;
	private int weightX;
	private int weightY;
	
	private Field west;
	private int westWeight;
	private Composite westComposite;
	
	private Field east;
	private int eastWeight;
	private Composite eastComposite;
	
	private Field north;
	private int northWeight;
	private Composite northComposite;
	
	private Field south;
	private int southWeight;
	private Composite southComposite;
	
	private Composite borderComposite;
	private SashForm horizontalSashForm;
	private SashForm verticalSashForm;
	
	private Field maximized = null;

	// fields for smooth maximization (with timer)
	
	/** Number of step to be done */
	private final static int STEP_COUNT = 8;
	/** Time between two steps */
	private final static int STEP_TIME = 25;
	
	private Display display;
	
	/** True when animation is going on. */
	private boolean animating = false;
	
	/** Current step */
	private int step = 0;
	
	/** Saved weights to be restored when finished */
	private int[] indexesToGrow = null;
	private int[][] originalWeights = null;
	
	private SashForm[] maximizedSashForms = null;
	private Composite[] maximizedComposites = null;
	
	private final Runnable maximizationRunnable = new Runnable() {
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// applies maximization at start when maximized is null
			if ( step == 0 && maximized == null ) {

				for ( int i=0; i<maximizedSashForms.length; i++ ) {
					final SashForm sashForm = maximizedSashForms[i];

					// first stores indexesToGrow for animation
					for (int j=0; j<sashForm.getChildren().length; j++ ) {
						if (sashForm.getChildren()[j] == sashForm.getMaximizedControl() ) {
							indexesToGrow[i] = j;
							break;
						}
					}

					// restores weights
					sashForm.setWeights(originalWeights[i]);
					// restores maximized
					sashForm.setMaximizedControl(maximizedComposites[i]);
				}
			}
			
			if ( step < STEP_COUNT ) {
				// computes weights for smooth animation 
				int sashCount = indexesToGrow.length;
				for ( int i=0; i<sashCount; i++) {
					// selects weights
					int[] weights = originalWeights[i];
					int weightCount = weights.length;
					int[] newWeights = new int[weightCount];
					
					// computes total weights
					int total = 0;
					for ( int j=0; j<weightCount; j++) {
						total += weights[j];
					}
					
					for ( int j=0; j<weightCount; j++) {

						if ( maximized == null ) {
							// returns weights to original
							if ( j == indexesToGrow[i] ) {
								// shrinks
								int delta = step*((total-weights[j])/STEP_COUNT);
								newWeights[j] = total - delta;
								
							} else {
								// grows
								int delta = Math.max(1, step*(weights[j]/STEP_COUNT));
								newWeights[j] =  delta;
							}
							
							
						} else {
							// transforms weights to maximize indexesToGrow[i] 
							int delta = (step*(weights[j]/STEP_COUNT));
							if ( j == indexesToGrow[i] ) {
								// grows
								newWeights[j] = weights[j] + delta;
								
							} else {
								// shrinks
								newWeights[j] = weights[j] - delta;
							}
						}
					}
					// sets new weights for animation
					maximizedSashForms[i].setWeights(newWeights);
				}
				
				// animation is running
				step += 1;
				
				// continues for next step
				display.timerExec(STEP_TIME, this);
			} else {
				endAnimation();
			}
		}
	};
	
	private Runnable animationEndCallback = null;
	
	public BorderField(int style, Field center) {
		this(style, 100, 100, center);
	}
	
	public BorderField(String label, int style, Field center) {
		this(label, style, 100, 100, center);
	}
	
	public BorderField(int style, int weightX, int weightY, Field center) {
		this(null, style, weightX, weightY, center);
	}
	
	public BorderField(String label, int style, int weightX, int weightY, Field center) {
		super(label, style);
		this.center = center;
		this.weightX = weightX;
		this.weightY = weightY;
		
		addField(center);
	}
	
	public boolean activate() {
		if ( maximized != null ) return maximized.activate();
		return center.activate();
	}
	
	public Field getCenter() {
		return center;
	}
	
	public Field getWest() {
		return west;
	}
	
	/**
	 * Sets west field.
	 * @param west field to set
	 * @param weight weight of the field among the whole shell in percent.
	 */
	public void setWest(Field west, int weight) {
		if ( this.west != null ) fieldList.remove(this.west);
		addField(west);
		this.west = west;
		this.westWeight = weight;
		
	}
	
	public Field getEast() {
		return east;
	}
	
	/**
	 * Sets east field.
	 * @param east field to set
	 * @param weight weight of the field among the whole shell in percent.
	 */
	public void setEast(Field east, int weight) {
		if ( this.east != null ) fieldList.remove(this.east);
		addField(east);
		this.east = east;
		this.eastWeight = weight;
	}
	
	public Field getNorth() {
		return north;
	}
	
	/**
	 * Sets north field.
	 * @param north field to set
	 * @param weight weight of the field among the whole shell in percent.
	 */
	public void setNorth(Field north, int weight) {
		if ( this.north != null ) fieldList.remove(this.north);
		addField(north);
		this.north = north;
		this.northWeight = weight;
	}
	
	public Field getSouth() {
		return south;
	}
	
	/**
	 * Sets south field.
	 * @param south field to set
	 * @param weight weight of the field among the whole shell in percent.
	 */
	public void setSouth(Field south, int weight) {
		if ( this.south != null ) fieldList.remove(this.south);
		addField(south);
		this.south = south;
		this.southWeight = weight;
	}
	
	@Override
	public void createWidget(Composite parent) {
		// stores display
		display = parent.getDisplay();
		
		addDisposeListener(parent);
		
		if ( hasActionsStyled(Action.STYLE_BUTTON) ) {
			parent = createActionComposite(parent);
		}
		

		borderComposite = new Composite(parent, SWT.NONE);
		final GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		borderComposite.setLayoutData(layoutData);
		borderComposite.setLayout(new FillLayout());
		
		if( north != null || south != null ) {
			verticalSashForm = createSashForm(borderComposite, SWT.VERTICAL);
		}	
			
		if (north != null ) northComposite = createComposite(verticalSashForm, north);

		if ( west != null || east != null ) { 
			horizontalSashForm = createSashForm(verticalSashForm == null ? borderComposite : verticalSashForm, SWT.HORIZONTAL);
		}
		
		if (west != null ) westComposite = createComposite(horizontalSashForm, west);
		
		Composite centerParent = horizontalSashForm;
		if ( centerParent == null ) centerParent = verticalSashForm;
		if ( centerParent == null ) centerParent = borderComposite;
		
		centerComposite = createComposite(centerParent, center);

		if (east != null ) eastComposite = createComposite(horizontalSashForm, east);
		if (south != null ) southComposite = createComposite(verticalSashForm, south);
		
		// compute proportions
		if ( verticalSashForm != null ) {
			int size = 1 + (north != null ? 1 :0 ) +  (south != null ? 1 :0 );
			int [] weights = new int[size];
			int current = 0;
			if ( north != null ) weights[current++] = northWeight;
			weights[current++] = weightY - northWeight - southWeight;
			if ( south != null ) weights[current++] = southWeight;
			verticalSashForm.setWeights(weights);
		}
		
		if ( horizontalSashForm != null ) {
			int size = 1 + (east != null ? 1 :0 ) +  (west != null ? 1 :0 );
			int [] weights = new int[size];
			int current = 0;
			if ( west != null ) weights[current++] = westWeight;
			weights[current++] = weightX - eastWeight - westWeight;
			if ( east != null ) weights[current++] = eastWeight;
			horizontalSashForm.setWeights(weights);
		}
		
		applyMaximized();
	}

	private SashForm createSashForm(Composite parent, int style) {
		SashForm newSashForm = new SashForm(parent, style | SWT.SMOOTH);
		newSashForm.setSashWidth(4);
		newSashForm.setLayout(new FillLayout());
		return newSashForm;

	}
	
	private Composite createComposite(Composite parent, Field field) {
		Composite newComposite = null;
		if ( hasStyle(BasicsUI.GROUP) ) {
			newComposite = new Group(parent, SWT.NONE);
		} else {
			newComposite = new Composite(parent, SWT.NONE);
		}
		final GridLayout layout = createFieldLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		newComposite.setLayout(layout);
		((AbstractField) field).createWidget(newComposite);
		return newComposite;
	}
	
	
	public Field getMaximized() {
		return maximized;
	}

	public void setMaximized(Field maximized) {
		if ( this.maximized != maximized ) {
			this.maximized = maximized;
			applyMaximized();
		}
	}
	
	public Runnable getAnimationEndCallback() {
		return animationEndCallback;
	}
	
	public void setAnimationEndCallback(Runnable animationEndCallback) {
		this.animationEndCallback = animationEndCallback;
	}
	
	private void initializeAnimation(int count) {
		if ( animating ) {
			endAnimation();
		}
		
		indexesToGrow = new int[count];
		originalWeights = new int[count][];
		maximizedSashForms = new SashForm[count];
		maximizedComposites= new Composite[count];
		step = 0;
		animating = true;
	}
	
	private void setAnimationInfo(int index, SashForm sashForm, Composite composite) {
		maximizedSashForms[index] = sashForm;
		maximizedComposites[index] = composite;
		
		// searches for index
		indexesToGrow[index] = -1;
		for (int i=0; i<sashForm.getChildren().length; i++ ) {
			if (sashForm.getChildren()[i] == composite ) {
				indexesToGrow[index] = i;
				break;
			}
		}
		
		originalWeights[index] = sashForm.getWeights();
	}
	
	private void startAnimation() {
		display.timerExec(STEP_TIME, maximizationRunnable);
	}
	
	private void endAnimation() {
		// animation is ending
		animating = false;
		
		// ends maximizing (even if it was done before)
		for ( int i=0; i<maximizedSashForms.length; i++ ) {
			// restores weights
			maximizedSashForms[i].setWeights(originalWeights[i]);
			// restores maximized
			maximizedSashForms[i].setMaximizedControl(maximizedComposites[i]);
		}
		
		// resets animations informations
		indexesToGrow = null;
		originalWeights = null;
		maximizedSashForms = null;
		maximizedComposites = null;
		
		// post to display animation end callback if exists
		if ( animationEndCallback != null ) {
			display.asyncExec(animationEndCallback);
		}
	}

	private void applyMaximized() {
		if ( centerComposite == null ) return;
		
		if ( maximized == null ) {
			int count = (verticalSashForm == null ? 0 : 1) + (horizontalSashForm == null ? 0 : 1);
			initializeAnimation(count);
			int current = 0;
			if (verticalSashForm != null ) {
				setAnimationInfo(current, verticalSashForm, null);
				current += 1;
			}
			if (horizontalSashForm != null ) {
				setAnimationInfo(current, horizontalSashForm, null);
				current += 1;
			}
			startAnimation();
			
		} else {
			if ( maximized == north ) {
				initializeAnimation(1);
				// vertical can't be null
				setAnimationInfo(0, verticalSashForm, northComposite);
				startAnimation();
				
			} else if ( maximized == south ) {
				initializeAnimation(1);
				// vertical can't be null
				setAnimationInfo(0, verticalSashForm, southComposite);
				startAnimation();
				
			} else if ( maximized == west ) {
				// horizontal can't be null
				initializeAnimation(2);
				setAnimationInfo(0, verticalSashForm, horizontalSashForm);
				setAnimationInfo(1, horizontalSashForm, westComposite);
				startAnimation();
				
			} else if ( maximized == east ) {
				// horizontal can't be null
				initializeAnimation(2);
				setAnimationInfo(0, verticalSashForm, horizontalSashForm);
				setAnimationInfo(1, horizontalSashForm, eastComposite);
				startAnimation();
				
			} else if ( maximized == center ) {

				// maximized is center, maximized all form needed
				if ( horizontalSashForm != null && verticalSashForm != null ) {
					initializeAnimation(2);
					setAnimationInfo(0, verticalSashForm, horizontalSashForm);
					setAnimationInfo(1, horizontalSashForm, centerComposite);
					startAnimation();
					
				} else if ( horizontalSashForm != null ) {
					initializeAnimation(1);
					setAnimationInfo(0, horizontalSashForm, centerComposite);
					startAnimation();
					
				} else if ( verticalSashForm != null ) {
					initializeAnimation(1);
					setAnimationInfo(0, verticalSashForm, centerComposite);
					startAnimation();
				}
				
			} else {
				// maximized is unknown
				setMaximized(null);
			}
			
		}
	}
	
}
