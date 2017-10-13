package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.NotificationSupport;
import fr.minibilles.basics.ui.action.ActionExecuter;

/**
 * Common interface for any field.
 * @author Jean-Charles Roger
 *
 */
public interface Field extends NotificationSupport {

	/** Field label */
	String getLabel();
	
	/** @return true if field is enable */
	boolean isEnable();
	
	/** Changes enable state */
	void setEnable(boolean enable);
	
	/** @return true if the field has the given style. */
	boolean hasStyle(int mask);
	
	/** @return the field's tooltip.*/
	String getTooltip();
	
	/** Sets the focus to the field, returns true if it accepts it */
	boolean activate();
	
	/** Validates this, if needed it shows an image with the message as tooltip. */
	Diagnostic validate();
	
	/** @return the diagnostic if value is not valid, null otherwise */
	Diagnostic getDiagnostic();
	
	/** @return true if field can be expanded in vertical dimension */
	boolean grabExcessVerticalSpace();
	
	/** @return field {@link ActionExecuter}. */
	ActionExecuter getActionExecuter();
	
	/** Sets the {@link ActionExecuter}. */
	void setActionExecuter(ActionExecuter actionExecuter);
	
}
