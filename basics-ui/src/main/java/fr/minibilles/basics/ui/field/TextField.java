/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.PlatformUtil;
import fr.minibilles.basics.ui.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * A TextField allows to edit a text value.
 * @author Jean-Charles Roger
 *
 */
public class TextField extends AbstractField {

	protected Text textWidget;
	protected int nbLines = 1;
	
	protected String value;
	protected Validator<String> validator;
	
	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;
	
	public TextField(String label) {
		this(label, null, BasicsUI.NONE);
	}
	
	public TextField(String label, int style) {
		this(label, null, style);
	}
	
	public TextField(String label, String value, int style) {
		super(label, style);
		this.value = value;
	}

	public int getNbLines() {
		return nbLines;
	}
	
	public void setNbLines(int nbLines) {
		this.nbLines = nbLines;
	}
	
	public boolean isMultiline() {
		return getNbLines() > 1;
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
		createText(parent);
		createButtonBar(parent);
	}

	protected void createText(Composite parent) {
		// creates widget
		textWidget = new Text(parent, textStyle());
		attachFieldToWidget(textWidget);
		
		textWidget.setEnabled(isEnable());
		createMenu(textWidget);
		
		String description = getBackgroundColor();
		if ( description != null ) {
			textWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			textWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}
		
		if ( value != null ) {
			textWidget.setText(value);
		}
		
		textWidget.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String newText = textWidget.getText();
				newText = newText.length() > 0 ? PlatformUtil.platformToLogicString(newText) : null;
				if ( getValue() == null ? newText != null : !getValue().equals(newText) ) { 
					setValue(newText, Notification.TYPE_UI);
				}
			}
		});

		// sets layout
		GridData data = new GridData(SWT.FILL, isMultiline() ? SWT.FILL : SWT.CENTER, true, grabExcessVerticalSpace());
		data.minimumWidth = 120;
		double heuristicHeight = textWidget.getFont().getFontData()[0].getHeight() * 1.5 * getNbLines();
		data.minimumHeight = (int) heuristicHeight;
		data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = 1;
		textWidget.setLayoutData(data);
		
		fireWidgetCreation(textWidget);
	}

	/** @return Field text style for creation */
	protected int textStyle() {
		int style = SWT.LEFT | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
		style |= isMultiline() ? SWT.MULTI : SWT.SINGLE;
		style |= hasStyle(BasicsUI.READ_ONLY) ? SWT.READ_ONLY : SWT.NONE;
		style |= hasStyle(BasicsUI.SEARCH) ? SWT.SEARCH | SWT.ICON_CANCEL : SWT.NONE;
		return style;
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return isMultiline();
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( textWidget != null ) textWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( textWidget != null ) return textWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public String getValue()  {
		return value;
	}

	protected void setValue(String value, int type) {
		if ( value == this.value ) return;
		String oldValue = this.value;
		this.value = value;
		if ( textWidget != null && type != Notification.TYPE_UI ) {
			if ( !textWidget.getText().equals(value == null ? "" : value) ) {
				textWidget.setText(value == null ? "" : value);
			}
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, value, oldValue);
	}
	
	/** Sets the field's value */
	public void setValue(String value) {
		setValue(value, Notification.TYPE_API);
	}
	
	public int getIntValue() {
		try {
			return Integer.valueOf(value);
		} catch (Throwable e) {
			return 0;
		}
	}
	
	public void setIntValue(int value) {
		setValue(Integer.toString(value));
	}
	
	public double getDoubleValue() {
		try {
			return Double.valueOf(value);
		} catch (Throwable e) {
			return 0.0;
		}
	}
	
	public void setDoubleValue(double value) {
		setValue(Double.toString(value));
	}
	
	public Validator<String> getValidator() {
		return validator;
	}

	public void setValidator(Validator<String> validator) {
		this.validator = validator;
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( !isEnable() ) return null;
		if ( validator != null && !validator.isValid(getValue()) ) return validator.getDiagnostic();
		return super.getDiagnostic();
	}
	
}
