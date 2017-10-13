/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A CheckboxField allows to edit a boolean value.
 * @author Jean-Charles Roger
 *
 */
public class CheckboxField extends AbstractField {

	protected Button checkWidget;
	
	protected boolean value;
	protected Validator<Boolean> validator;
	
	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;

	public CheckboxField(String label) {
		this(label, false, BasicsUI.NONE);
	}
	
	public CheckboxField(String label, int style) {
		this(label, false, style);
	}
	
	public CheckboxField(String label, boolean value, int style) {
		super(label, style);
		this.value = value;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createLabel(parent);
		createInfo(parent);
		createCheck(parent);
		createButtonBar(parent);
	}

	protected void createLabel(Composite parent) {
		if ( hasStyle(BasicsUI.RIGHT) ) {
			Label localLabel = new Label(parent, SWT.LEFT);
			attachFieldToWidget(localLabel);
			localLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
			fireWidgetCreation(localLabel);
		} else {
			super.createLabel(parent);
		}
	}

	
	protected void createCheck(Composite parent) {
		
		Composite checkComposite = new Composite(parent, SWT.NONE);
		attachFieldToWidget(checkComposite);
		checkComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace(), fieldHorizontalSpan() , 1));
		checkComposite.setLayout(new GridLayout(2, false));
		
		// creates widget
		checkWidget = new Button(checkComposite, checkStyle());
		checkWidget.setEnabled(isEnable());

		String description = getBackgroundColor();
		if ( description != null ) {
			checkWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			checkWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}
		
		checkWidget.setSelection(value);
		checkWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setValue(checkWidget.getSelection(), Notification.TYPE_UI);
				super.widgetSelected(e);
			}
		});
	
		if ( hasStyle(BasicsUI.RIGHT) ) {
			swtLabel = new Label(checkComposite, SWT.LEFT);
			swtLabel.setText(label);
			swtLabel.setToolTipText(getTooltip());
			attachFieldToWidget(swtLabel);
		}
			
		// sets layout
		checkWidget.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		fireWidgetCreation(checkWidget);
		
		if ( hasStyle(BasicsUI.RIGHT) ) {
			swtLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			fireWidgetCreation(swtLabel);
		}
		
	}

	/** @return Field check style for creation */
	protected int checkStyle() {
		int style = SWT.CHECK;
		style |= hasStyle(BasicsUI.READ_ONLY) ? SWT.READ_ONLY : SWT.NONE;
		return style;
	}
	
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( checkWidget != null ) checkWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( checkWidget != null ) return checkWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public boolean getValue()  {
		return value;
	}

	protected void setValue(boolean value, int type) {
		if ( value == this.value ) return;
		boolean oldValue = this.value;
		this.value = value;
		if ( checkWidget != null && type != Notification.TYPE_UI ) checkWidget.setSelection(value);
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, value, oldValue);
	}
	
	/** Sets the field's value */
	public void setValue(boolean value) {
		setValue(value, Notification.TYPE_API);
	}
	
	public Validator<Boolean> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Boolean> validator) {
		this.validator = validator;
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( !isEnable() ) return null;
		if ( validator != null && !validator.isValid(getValue()) ) return validator.getDiagnostic();
		return super.getDiagnostic();
	}
	
}
