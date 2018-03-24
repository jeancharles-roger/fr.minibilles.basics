/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Field that creates a separator.s
 * @author Jean-Charles Roger
 *
 */
public class SeparatorField extends AbstractField {

	public SeparatorField() {
		super(null, BasicsUI.NO_INFO);
	}
	
	public boolean activate() {
		return false;
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, fieldHorizontalSpan(), 1));
	}

}
