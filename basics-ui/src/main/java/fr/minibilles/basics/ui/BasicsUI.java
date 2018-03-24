/**
 * 
 */
package fr.minibilles.basics.ui;

import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.field.AbstractField;
import fr.minibilles.basics.ui.field.BorderField;
import fr.minibilles.basics.ui.field.CheckboxField;
import fr.minibilles.basics.ui.field.ChoiceField;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.ListField;
import fr.minibilles.basics.ui.field.MultiPageField;
import fr.minibilles.basics.ui.field.MultiTabField;
import fr.minibilles.basics.ui.field.TextField;
import fr.minibilles.basics.ui.field.VirtualMultiPageField;

/**
 * 
 * This class groups all the styles used in the UI library.
 * @author Jean-Charles Roger
 *
 */
public class BasicsUI {

	/** No style. */
	public final static int NONE = 0;
	
	/** 
	 * The field can't be edited.
	 * Used by:
	 * <ul>
	 * <li>{@link AbstractField}</li>
	 * </ul>
	 */
	public final static int READ_ONLY = 1<<0;

	/** 
	 * The field will not contains information mark which shows the diagnostic.
	 * Used by:
	 * <ul>
	 * <li>{@link AbstractField}</li>
	 * </ul>
	 */
	public final static int NO_INFO = 1<<1;
	
	
	/** 
	 * Field is a search field.
	 * Used by:
	 * <ul>
	 * <li>{@link TextField}</li>
	 * </ul>
	 */
	public final static int SEARCH = 1<<2;
	
	/** 
	 * Field has multi selection..
	 * Used by:
	 * <ul>
	 * <li>{@link ListField}</li>
	 * </ul>
	 */
	public final static int MULTI = 1<<3;
	
	/** 
	 * {@link CompositeField} is shown in a group.
	 * Used by:
	 * <ul>
	 * <li>{@link CompositeField}</li>
	 * <li>{@link BorderField}</li>
	 * </ul>
	 */
	public final static int GROUP = 1<<4;
	
	/** 
	 * The {@link ChoiceField} proposes a null value.
	 * Used by:
	 * <ul>
	 * <li>{@link ChoiceField}</li>
	 * </ul>
	 */
	public final static int OPTIONAL = 1<<5;
	
	/** 
	 * Validates all the fields, not only the selected one.
	 * Used by:
	 * <ul>
	 * <li>{@link MultiTabField}</li>
	 * <li>{@link VirtualMultiPageField}</li>
	 * </ul>
	 */
	public final static int VALIDATE_ALL = 1<<6;
	
	
	/** 
	 * Adds check box for field.
	 * Used by:
	 * <ul>
	 * <li>{@link ListField}</li>
	 * </ul>
	 */
	public final static int CHECK = 1<<7;
	
	/** 
	 * The field items are editable.
	 * Used by:
	 * <ul>
	 * <li>{@link ListField}</li>
	 * <li>TODO {@link ChoiceField}</li>
	 * </ul>
	 */
	public final static int ITEM_EDITABLE = 1<<8;
	
	/** 
	 * The field can resize the shell if needed.
	 * Used by:
	 * <ul>
	 * <li>{@link VirtualMultiPageField}</li>
	 * <li>{@link MultiPageField}</li>
	 * </ul>
	 */
	public final static int RESIZE_SHELL = 1<<9;
	
	
	/** 
	 * Left property
	 * Used by:
	 * <ul>
	 * <li>{@link CheckboxField}</li>
	 * </ul>
	 */
	public final static int LEFT = 1<<10;
	
	/** 
	 * Center property
	 * Used by:
	 * <ul>
	 * <li>{@link CheckboxField}</li>
	 * </ul>
	 */
	public final static int CENTER = 1<<11;
	
	/** 
	 * Right property
	 * Used by:
	 * <ul>
	 * <li>{@link CheckboxField}</li>
	 * </ul>
	 */
	public final static int RIGHT = 1<<12;

	/** 
	 * Forces button bar to be horizontal.
	 * Used by:
	 * <ul>
	 * <li>{@link AbstractField}</li>
	 * </ul>
	 */
	public final static int HORIZONTAL = 1<<13;
	
	/**
	 * Insert a scrolled composite for children.
	 * Used by:
	 * <ul>
	 * <li>{@link VirtualMultiPageField}</li>
	 * <li>{@link MultiPageField}</li>
	 * </ul>
	 */
	public final static int SCROLL = 1<<14;
	
	/** 
	 * Don't validate fields on each changes.
	 * Used by:
	 * <ul>
	 * <li>{@link FieldDialog}</li>
	 * </ul>
	 */
	public final static int NO_AUTOMATIC_VALIDATION = 1<<0;
	
	
	/** 
	 * Show the active field tooltip in a text a top of the dialog.
	 * Used by:
	 * <ul>
	 * <li>{@link FieldDialog}</li>
	 * </ul>
	 */
	public final static int SHOW_HINTS = 1<<1;

	/** 
	 * Don't show the shell header.
	 * Used by:
	 * <ul>
	 * <li>{@link FieldDialog}</li>
	 * </ul>
	 */
	public final static int NO_HEADER = 1<<2;
	
	/** 
	 * Only update subject when closing dialog on button 0.
	 * Used by:
	 * <ul>
	 * <li>{@link ControllerDialog}</li>
	 * </ul>
	 */
	// TODO
	//public final static int UPDATE_ON_CLOSE = 8;
	
	/** Notification for value */
	public static final String NOTIFICATION_VALUE = "value"; 			//$NON-NLS-1$

	/** Notification for selection */
	public static final String NOTIFICATION_SELECTION = "selection"; 	//$NON-NLS-1$

	/** Notification for check */
	public static final String NOTIFICATION_CHECK = "check"; 	//$NON-NLS-1$

	/** Notification for range */
	public static final String NOTIFICATION_RANGE = "range";	 		//$NON-NLS-1$

	/** Notification for widget creation */
	public static final String NOTIFICATION_WIDGET_CREATION = "widgetCreation";	//$NON-NLS-1$

	/** Notification for widget enabling */
	public static final String NOTIFICATION_ENABLE = "enable";	 		//$NON-NLS-1$
	
}
