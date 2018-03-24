/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * A CheckboxField allows to edit a boolean value.
 * @author Jean-Charles Roger
 *
 */
public class ActionListField extends AbstractField {

	public ActionListField() {
		this(null);
	}
	
	public ActionListField(String label) {
		this(label, BasicsUI.NONE);
	}
	
	public ActionListField(String label, int style) {
		super(label, style | BasicsUI.NO_INFO);
	}

	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createLabel(parent);
		createButtonBar(parent);
	}

	/**
	 * Creates the actions buttons
	 * SWT specific
	 */
	protected void createButtonBar(Composite parent) {
		if ( hasActionsStyled(Action.STYLE_BUTTON) ) {
			buttonBar = getActionManager().createButtonBar(parent, SWT.HORIZONTAL, actions);
			buttonBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, hasLabel() ? 3 : 4, 1));
			for ( Control child : buttonBar.getChildren() ) fireWidgetCreation(child);
		}
	}

	public boolean activate() {
		return false;
	}
}
