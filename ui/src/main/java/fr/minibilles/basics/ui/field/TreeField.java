/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * A TreeField allows to present a tree of values.
 * @author Jean-Charles Roger
 *
 */
public abstract class TreeField<T> extends AbstractField {

	private final static String ELEMENT = "element";
	private final static String CHILDREN = "children";
	
	protected Tree treeWidget;
	protected TreeEditor treeEditor;
	protected int nbLines = 8;
	
	protected List<? extends T> value;
	protected List<? extends T> selection = Collections.emptyList();
	
	protected Validator<List<T>> validator;
	
	/** 
	 * String representing Background color for field.
	 */
	protected String backgroundColor;
	
	public TreeField(String label, int style) {
		this(label, null, style);
	}
	
	public TreeField(String label, List<T> value, int style) {
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
		createTree(parent);
		createButtonBar(parent);
	}

	protected void createTree(Composite parent) {
		// creates widget
		treeWidget = new Tree(parent, treeStyle());
		attachFieldToWidget(treeWidget);
		
		treeWidget.setEnabled(isEnable());
		createMenu(treeWidget);
		
		String description = getBackgroundColor();
		if ( description != null ) {
			treeWidget.setBackground(resources.getColor(Resources.getRGB(description)));
		} else if ( hasStyle(BasicsUI.READ_ONLY) ) {
			treeWidget.setBackground(resources.getSystemColor(READ_ONLY_COLOR));
		}

		if ( hasStyle(BasicsUI.ITEM_EDITABLE) ) {
			treeEditor = new TreeEditor(treeWidget);
			treeEditor.horizontalAlignment = SWT.LEFT;
			treeEditor.grabHorizontal = true;
			treeWidget.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent e) {
					treeEditor.minimumWidth = treeWidget.getSize().x;
				}
			});
		}
		
		Listener listener = new Listener() {
			public void handleEvent (Event event) {
				switch(event.type) {
				case SWT.Selection:
					updateFieldSelection();
					return;
					
				case SWT.SetData: 
					TreeItem item = (TreeItem) event.item;
					TreeItem parentItem = item.getParentItem();

					T element; // element associated to item
					if (parentItem == null) {
						// top-level: take from root list 'value"
						element = value.get(event.index); 
					} else {
						// child of parentItem
						@SuppressWarnings("unchecked")
						List<T> l = (List<T>) parentItem.getData(CHILDREN);
						element = l.get(event.index);
					}
					
					List<? extends T> childElements = getChildren(element);
					if (childElements == null) childElements = Collections.emptyList();
					
					item.setData(ELEMENT, element);
					item.setData(CHILDREN, childElements);
					item.setItemCount(childElements.size());

					item.setText(getText(element));
					item.setImage(getImage(element));
					return;
					
				case SWT.MouseDoubleClick:
					// case of table editor
					final T selection = getSingleSelection();
					final int _index = getValue().indexOf(selection);
					if ( _index < 0 ) break;
					final TreeItem _item = treeWidget.getItem(_index);
					
					// Clean up any previous editor control
					Control oldEditor = treeEditor.getEditor();
					if (oldEditor != null) oldEditor.dispose();
			
					// The control that will be the editor must be a child of the Table
					final Text newEditor = new Text(treeWidget, SWT.NONE);
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
					treeEditor.setEditor(newEditor, _item, 0);
				}
			}
		};
		
		treeWidget.addListener (SWT.SetData, listener);
		treeWidget.addListener (SWT.Selection, listener);
		if ( hasStyle(BasicsUI.ITEM_EDITABLE) ) {
			treeWidget.addListener(SWT.MouseDoubleClick, listener);
		}
		refresh();
		
		// sets layout
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
		data.minimumWidth = 80;
		data.minimumHeight = 80;
		data.horizontalSpan = fieldHorizontalSpan();
		data.verticalSpan = getNbLines();
		treeWidget.setLayoutData(data);
		
		fireWidgetCreation(treeWidget);
	}

	/** @return Field text style for creation */
	protected int treeStyle() {
		int style = SWT.VIRTUAL | SWT.BORDER | SWT.FULL_SELECTION;
		style |= hasStyle(BasicsUI.CHECK) ? SWT.CHECK : SWT.NONE;
		style |= hasStyle(BasicsUI.MULTI) ? SWT.MULTI : SWT.NONE;
		return style;
	}
	
	private void updateItem(final int index, final TreeItem item, final String text) {
		T newItem = fromText(text);
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
	
	@SuppressWarnings("unchecked")
	protected void updateFieldSelection() {
		TreeItem [] selectedItems = treeWidget.getSelection();
		List<T> newSelection = new ArrayList<T>(selectedItems.length);
		for ( TreeItem item : selectedItems) {
			newSelection.add((T) item.getData(ELEMENT));
		}
		setMultipleSelection(newSelection, Notification.TYPE_UI);
	}
	
	protected void updateWidgetSelection() {
		treeWidget.deselectAll();
		List<TreeItem> selectedItems = new ArrayList<TreeItem>();
		for ( T selected : getMultipleSelection() ) {
			List<T> path = new ArrayList<T>();
			T current = selected; 
			while (current != null ) {
				path.add(0, current);
				current = getParent(current);
			}
			
			TreeItem [] currentItems = treeWidget.getItems();
			TreeItem item = null;
			for ( T element : path ) {
				item = getItem(currentItems, element);
				if ( item == null ) break;
				currentItems = item.getItems();
			}
			if ( item != null ) selectedItems.add(item);
		}
		
		TreeItem [] selections = new TreeItem[selectedItems.size()];
		treeWidget.setSelection(selectedItems.toArray(selections));		
		treeWidget.showSelection();
	}
	
	private TreeItem getItem(TreeItem[] items, T data) {
		if ( items != null ) {
			for ( TreeItem item : items ) {
				// forces the item to calculates it's data.
				item.getText();
				
				if ( item.getData(ELEMENT).equals(data)) {
					return item;
				}
			}
		}
		return null;
	}

	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		if ( treeWidget != null ) treeWidget.setEnabled(enable);
	}
	
	public boolean activate() {
		if ( treeWidget != null ) return treeWidget.setFocus();
		return false;
	}
	
	/** @return the field's value */
	public List<T> getValue()  {
		if ( value == null ) return Collections.emptyList();
		return Collections.unmodifiableList(value);
	}

	protected void setValue(List<? extends T> value, int type) {
		if ( value == null ) value = Collections.emptyList();
		if ( value == this.value ) return;
		List<? extends T> oldValue = this.value;
		this.value = value;
		
		List<T> newSelection = new ArrayList<T>();
		for ( T current : selection) {
			if ( value.contains(current) ) newSelection.add(current);
		}
		setMultipleSelection(newSelection);
		
		if ( treeWidget != null && type != Notification.TYPE_UI ) {
			refresh();
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_VALUE, this.value, oldValue);
	}

	/** Refreshes the tree */
	public void refresh() {
		if ( treeWidget != null ) {
			treeWidget.clearAll(true);
			treeWidget.setItemCount(value.size());
			updateWidgetSelection();
		}
	}
	
	/** Sets the field's value */
	public void setValue(List<? extends T> value) {
		setValue(value, Notification.TYPE_API);
	}
	
	public T getSingleSelection() {
		return selection.isEmpty() ? null : selection.get(0);
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
		List<? extends T> oldSelection = this.selection;
		this.selection = selection;
		if ( treeWidget != null && type != Notification.TYPE_UI ) {
			updateWidgetSelection();
		}
		notificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_SELECTION, this.selection, oldSelection);
	}
	
	/** Sets the field's selection */
	public void setMultipleSelection(List<T> selection) {
		setMultipleSelection(selection, Notification.TYPE_API);
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
	 * @param text to convert.
	 * @return the converted text or null if it can't be converted.
	 */
	protected T fromText(String text) {
		return null;
	}
	
	public Image getImage(T element) {
		return null;
	}
	
	public abstract T getParent(T element);

	public abstract List<? extends T> getChildren(T element);
	
}
