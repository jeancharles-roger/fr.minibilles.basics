/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A {@link RadioField} allows to choose one string among a series.
 * It cannot contains either info nor label.
 * @author Jean-Charles Roger
 *
 */
public class RadioField extends AbstractField {

	protected Composite radioWidget;
	
	protected String value;
	protected final List<String> choices;

	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;
	
	public RadioField(String ... choices) {
		this(Arrays.asList(choices), BasicsUI.NO_INFO);
	}
	
	public RadioField(List<String> choices) {
		this(choices, BasicsUI.NO_INFO);
	}
	
	public RadioField(List<String> choices, int style) {
		super(null, style | BasicsUI.NO_INFO); 
		this.choices = choices;
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
		createRadio(parent);
		createButtonBar(parent);
	}

	protected void createRadio(Composite parent) {
		// creates widget
		radioWidget = new Composite(parent, SWT.NONE);
		attachFieldToWidget(radioWidget);
		
		radioWidget.setEnabled(isEnable());
		
		
		// selection listener for each toggle
		final Listener listener = new Listener () {
			public void handleEvent (Event e) {
				Control [] children = radioWidget.getChildren();
				for (int i=0; i<children.length; i++) {
					((Button) children [i]).setSelection(false);
				}
				final Button button = (Button) e.widget;
				button.setSelection(true);
				setValue(button.getText(), Notification.TYPE_UI);
			}
		};
		
		// creates toggle buttons
		for (String choice : choices ) {
			// sets start value as first choice if null
			if ( value == null ) value = choice;
			
			Button button = new Button(radioWidget, SWT.RADIO);
			button.setText(choice);
			button.addListener(SWT.Selection, listener);
			if (choice.equals(value)) button.setSelection (true);
		}
		

		String description = getBackgroundColor();
		if ( description != null ) {
			radioWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			radioWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}

		// sets layout
		radioWidget.setLayout(new FillLayout());

		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, grabExcessVerticalSpace());
		data.minimumWidth = 150;
		data.horizontalSpan = fieldHorizontalSpan();
		radioWidget.setLayoutData(data);
		
		fireWidgetCreation(radioWidget);
	}

	/** @return Field text style for creation */
	protected int comboStyle() {
		int style = SWT.READ_ONLY | SWT.BORDER;
		return style;
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( radioWidget != null ) radioWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( radioWidget != null ) return radioWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public String getValue()  {
		return value;
	}

	protected void setValue(String value, int type) {
		if ( value == this.value ) return;
		final String oldValue = this.value;
		this.value = value;
		if ( radioWidget != null && type != Notification.TYPE_UI ) {
			final int index = choices.indexOf(value);

			// selects given indexed button
			final Control[] children = radioWidget.getChildren();
			for (int i=0; i<children.length; i++) {
				((Button) children[i]).setSelection(i == index);
			}
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, this.value, oldValue);
	}
	
	/** Sets the field's value */
	public void setValue(String value) {
		setValue(value, Notification.TYPE_API);
	}
	
}
