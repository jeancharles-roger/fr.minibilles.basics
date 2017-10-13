/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.ActionExecuter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * A ListField allows to edit a list of values.
 * @author Jean-Charles Roger
 *
 */
public class ListField<T> extends AbstractField {

	protected Table tableWidget;
	protected TableEditor tableEditor;
	protected int nbLines = 3;
	
	protected List<T> value;
	protected List<T> selection = Collections.emptyList();
	protected List<T> checked = Collections.emptyList();
	
	protected Validator<List<T>> validator;
	
	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;
	
	public ListField(String label, int style) {
		this(label, null, style);
	}
	
	public ListField(String label, List<T> value, int style) {
		super(label, style);
		setValue(value);
	}

	public int getNbLines() {
		return nbLines;
	}
	
	public void setNbLines(int nbLines) {
		this.nbLines = nbLines;
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
		createList(parent);
		createButtonBar(parent);
	}

	protected void createList(Composite parent) {
		// creates widget
		tableWidget = new Table(parent, tableStyle());
		attachFieldToWidget(tableWidget);
		
		tableWidget.setEnabled(isEnable());
		createMenu(tableWidget);
		
		String description = getBackgroundColor();
		if ( description != null ) {
			tableWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			tableWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}

		if ( hasStyle(BasicsUI.ITEM_EDITABLE) ) {
			tableEditor = new TableEditor(tableWidget);
			tableEditor.horizontalAlignment = SWT.LEFT;
			tableEditor.grabHorizontal = true;
			tableWidget.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					tableEditor.minimumWidth = tableWidget.getSize().x;
				}
			});
		}
		
		Listener listener = new Listener() {
			public void handleEvent (Event event) {
				switch(event.type) {
				case SWT.SetData: 
					TableItem item = (TableItem) event.item;
					int index = tableWidget.indexOf (item);
					T element = value.get(index);
					item.setText(getText(element));
					item.setImage(getImage(element));
					if ( hasStyle(BasicsUI.CHECK) ) {
						item.setChecked(checked.contains(element));
					}
					return;
				case SWT.Selection:
					updateFieldChecked();
					updateFieldSelection();
					return;
				case SWT.MouseDoubleClick:
					// case of table editor
					final T selection = getSingleSelection();
					final int _index = getValue().indexOf(selection);
					if ( _index < 0 ) break;
					final TableItem _item = tableWidget.getItem(_index);
					
					// Clean up any previous editor control
					Control oldEditor = tableEditor.getEditor();
					if (oldEditor != null) oldEditor.dispose();
			
					// The control that will be the editor must be a child of the Table
					final Text newEditor = new Text(tableWidget, SWT.NONE);
					newEditor.setText(getText(selection));
					Listener textListener = new Listener () {
						public void handleEvent (final Event e) {
							boolean update = true;
							if ( e.type == SWT.Traverse ) {
								switch (e.detail) {
								case SWT.TRAVERSE_ESCAPE:
									update = false;
								case SWT.TRAVERSE_RETURN:
									e.doit = false;
									break;
								default:
									return;
								}
							}
							if ( update ) updateItem(_index, _item, newEditor.getText());
							newEditor.dispose ();
						}
					};
					newEditor.addListener (SWT.FocusOut, textListener);
					newEditor.addListener (SWT.Traverse, textListener);
					
					newEditor.selectAll();
					newEditor.setFocus();
					tableEditor.setEditor(newEditor, _item, 0);
				}
			}
		};
		tableWidget.addListener (SWT.SetData, listener);
		tableWidget.addListener (SWT.Selection, listener);
		if ( hasStyle(BasicsUI.ITEM_EDITABLE) ) {
			tableWidget.addListener(SWT.MouseDoubleClick, listener);
		}
		tableWidget.setItemCount(value.size());
		updateWidgetSelection();
		
		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
		data.widthHint = 80;
        data.heightHint = getNbLines() * tableWidget.getItemHeight() + tableWidget.getHeaderHeight();
        data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = 1;
		tableWidget.setLayoutData(data);
		
		
		fireWidgetCreation(tableWidget);
	}

	/** @return Field text style for creation */
	protected int tableStyle() {
		int style = SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION;
		style |= hasStyle(BasicsUI.CHECK) ? SWT.CHECK : SWT.NONE;
		style |= hasStyle(BasicsUI.MULTI) ? SWT.MULTI : SWT.SINGLE;
		return style;
	}
	
	private void updateItem(final int index, final TableItem item, final String text) {
		T edited = value.get(index);
		T newItem = fromText(edited, text);
		if ( newItem != null ) {
			item.setText(text);
			ArrayList<T> newValue = new ArrayList<T>(value);
			newValue.remove(index);
			newValue.add(index, newItem);
			setValue(newValue, Notification.TYPE_UI);
			updateFieldSelection();
		}
	}

	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}
	
	protected void updateFieldSelection() {
		int [] indices = tableWidget.getSelectionIndices();
		List<T> newSelection = new ArrayList<T>();
		for ( int i=0; i<indices.length; i++ ) {
			newSelection.add(value.get(indices[i]));
		}
		setMultipleSelection(newSelection, Notification.TYPE_UI);
	}
	
	protected void updateWidgetSelection() {
		tableWidget.deselectAll();
		int [] indices = new int [selection.size()];
		for ( int i=0; i<indices.length; i++ ) {
			indices[i] = value.indexOf(selection.get(i));
		}
		tableWidget.select(indices);
		tableWidget.showSelection();
	}
	
	protected void updateFieldChecked() {
		List<T> newChecked = new ArrayList<T>();
		final TableItem[] items = tableWidget.getItems();
		for ( int i=0; i<items.length; i++ ) {
			if (items[i].getChecked() ) newChecked.add(value.get(i));
		}
		setChecked(newChecked, Notification.TYPE_UI);
	}
	
	protected void updateWidgetChecked() {
		tableWidget.deselectAll();
		final TableItem[] items = tableWidget.getItems();
		for ( int i=0; i<items.length; i++ ) {
			items[i].setChecked( checked.contains(value.get(i) ) );
		}
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( tableWidget != null ) tableWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( tableWidget != null ) return tableWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public List<T> getValue()  {
		return Collections.unmodifiableList(value);
	}

	protected void setValue(List<T> value, int type) {
		if ( value == null ) value = Collections.emptyList();
		if ( value.equals(this.value) ) return;
		List<T> oldValue = this.value;
		this.value = new ArrayList<T>(value);
		
		List<T> newSelection = new ArrayList<T>();
		for ( T current : selection) {
			if ( value.contains(current) ) newSelection.add(current);
		}
		setMultipleSelection(newSelection);

		if ( type != Notification.TYPE_UI ) refresh();
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, this.value, oldValue);
	}
	
	/** Sets the field's value */
	public void setValue(List<T> value) {
		setValue(value, Notification.TYPE_API);
	}
	
	/** Refreshes the list */
	public void refresh() {
		if ( tableWidget != null ) {
			tableWidget.clearAll();
			tableWidget.setItemCount(value.size());
		}
	}

	public T getSingleSelection() {
		return selection.isEmpty() ? null : selection.get(0);
	}

	public int getSingleSelectionIndex() {
	    return tableWidget.getSelectionIndex();
    }
	
	public void setSingleSelection(T newSelection) {
		setMultipleSelection(Collections.singletonList(newSelection));
	}
	
	/** @return the field's selection */
	public List<T> getMultipleSelection()  {
		return Collections.unmodifiableList(selection);
	}
	
	protected void setMultipleSelection(List<T> selection, int type) {
		if ( selection == null ) selection = Collections.emptyList();
		List<T> oldSelection = this.selection;
		this.selection = selection;
		if ( tableWidget != null && type != Notification.TYPE_UI ) {
			updateWidgetSelection();
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_SELECTION, this.selection, oldSelection);
	}
	
	/** Sets the field's selection */
	public void setMultipleSelection(List<T> selection) {
		setMultipleSelection(selection, Notification.TYPE_API);
	}
	
	/** @return the field's checked elements from value. */
	public List<T> getChecked()  {
		return Collections.unmodifiableList(checked);
	}
	
	protected void setChecked(List<T> checked, int type) {
		if ( checked == null ) checked = Collections.emptyList();
		List<T> oldChecked = this.checked;
		this.checked = checked;
		if ( tableWidget != null && type != Notification.TYPE_UI ) {
			updateWidgetChecked();
			updateWidgetSelection();
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_CHECK, this.checked, oldChecked);
	}
	
	/** Sets the field's checked elements in value. */
	public void setChecked(List<T> checked) {
		setChecked(checked, Notification.TYPE_API);
	}
	
	public Validator<List<T>> getValidator() {
		return validator;
	}

	public void setValidator(Validator<List<T>> validator) {
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
	
	/**
	 * <p>Used when field is {@link BasicsUI#ITEM_EDITABLE} to convert a text to T.</p>
	 * @param edited edited object before modification.
	 * @param text to convert.
	 * @return the converted text or null if it can't be converted.
	 */
	protected T fromText(T edited, String text) {
		return null;
	}
	
	public Image getImage(T element) {
		return null;
	}
	
	@Override
	public void setActionExecuter(ActionExecuter executer) {
		super.setActionExecuter(executer);
	}
}
