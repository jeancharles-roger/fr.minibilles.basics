/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link ChoiceField} allows to choose one value among a list.
 * @author Jean-Charles Roger
 *
 */
public class ChoiceField<T> extends AbstractField {

	private static String EMPTY = "(None)";
	
	protected Combo comboWidget;
	
	protected T value;
	protected List<T> range = Collections.emptyList();
	protected Validator<T> validator;
	
	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;
	
	public ChoiceField(String label, int style) {
		this(label, null, null, style);
	}
	
	public ChoiceField(String label, T value, List<T> range, int style) {
		super(label, style); 
		setValue(value);
		setRange(range);
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
		createCombo(parent);
		createButtonBar(parent);
	}

	protected void createCombo(Composite parent) {
		// creates widget
		comboWidget = new Combo(parent, comboStyle());
		attachFieldToWidget(comboWidget);
		
		comboWidget.setEnabled(isEnable());

		String description = getBackgroundColor();
		if ( description != null ) {
			comboWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			comboWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}

		comboWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = comboWidget.getSelectionIndex();
				T selection;
				if ( selectionIndex < 0 || selectionIndex >= range.size() ) {
					selection = null;
				} else {
					selection = range.get(selectionIndex);
				}
				setValue(selection, Notification.TYPE_UI);
			}
		});
		updateWidgetRange();
		
		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, grabExcessVerticalSpace());
		data.minimumWidth = 150;
		data.horizontalSpan = fieldHorizontalSpan();
		comboWidget.setLayoutData(data);
		
		fireWidgetCreation(comboWidget);
	}

	/** @return Field text style for creation */
	protected int comboStyle() {
		int style = SWT.READ_ONLY | SWT.BORDER;
		return style;
	}
	
	
	protected void updateWidgetRange() {
		String [] items = new String [range.size() +  (hasStyle(BasicsUI.OPTIONAL) ? 1 : 0)];
		int selected = -1;
		for ( int i=0; i < range.size(); i++ ) {
			T element = range.get(i);
			if ( value != null && value.equals(element) ) selected = i;
			items[i] = getText(element);
		}
		if ( hasStyle(BasicsUI.OPTIONAL) ) {
			items[items.length - 1] = EMPTY;
			if ( value == null ) selected = items.length - 1;
		}
		comboWidget.setItems(items);
		comboWidget.select(selected);
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( comboWidget != null ) comboWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( comboWidget != null ) return comboWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public T getValue()  {
		return value;
	}

	protected void setValue(T value, int type) {
		if ( value == this.value ) return;
		T oldValue = this.value;
		this.value = value;
		if ( comboWidget != null && type != Notification.TYPE_UI ) {
			if ( value == null ) {
				if ( hasStyle(BasicsUI.OPTIONAL) ) comboWidget.select(range.size());
			} else {
				comboWidget.select(range.indexOf(value));
			}
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, this.value, oldValue);
	}
	
	/** Sets the field's value */
	public void setValue(T value) {
		setValue(value, Notification.TYPE_API);
	}
	
	
	/** @return the field's range */
	public List<T> getRange()  {
		return Collections.unmodifiableList(range);
	}
	
	/** Sets the field's selection */
	public void setRange(List<T> range) {
		if ( range == null ) range = Collections.emptyList();
		if ( range.equals(this.range) ) return;
		this.range = range;
		if ( comboWidget != null ) {
			updateWidgetRange();
		}
	}
	
	public Validator<T> getValidator() {
		return validator;
	}

	public void setValidator(Validator<T> validator) {
		this.validator = validator;
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( !isEnable() ) return null;
		if ( validator != null && !validator.isValid(getValue()) ) return validator.getDiagnostic();
		return super.getDiagnostic();
	}
	
	public String getText(T element) {
		return element.toString();
	}
	
}
