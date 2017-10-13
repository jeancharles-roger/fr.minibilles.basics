/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.notification.NotificationSupport;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.ActionExecuter;
import fr.minibilles.basics.ui.controller.Controller;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


/**
 * A {@link MultiTabField} represents a field with multiple tabs.
 * @author Jean-Charles Roger
 *
 */
public class MultiTabField extends AbstractField implements Structure {

	/** Internal representation for a Tab. */
	private class Tab {
		Controller<?> controller;
		Field field;
		Image image;
		boolean closeable;
		CTabItem item;
		Composite composite;
		
		Tab(Controller<?> controller, Field field, Image image, boolean closeable) {
			this.controller = controller;
			this.field = field;
			this.image = image;
			this.closeable = closeable;
		}
	}
	
	/** Tab list */
	private final List<Tab> tabs = new ArrayList<Tab>();

	/** Index of selected tab */
	private int selected = 0;
	
	/** Tab widget */
	protected CTabFolder tabFolder;

	protected final NotificationSupport.Stub structureNotificationSupport = new NotificationSupport.Stub(this);

	public MultiTabField() {
		this(null, BasicsUI.NONE);
	}
	
	public MultiTabField(String label, int style) {
		super(label, BasicsUI.NO_INFO | style);
	}
	
	
	/**
	 * Returns an arrays of the {@link Controller} for each tab.
	 * A cell may be null.
	 */
	public Controller<?>[] getTabControllers() {
		Controller<?>[] result = new Controller<?>[tabs.size()];
		for (int i=0; i<tabs.size(); i++) {
			result[i] = tabs.get(i).controller;
		}
		return result;
	}

	/**
	 * Returns an arrays of {@link Field} for each tab.
	 */
	public Field[] getTabFields() {
		Field[] result = new Field[tabs.size()];
		for (int i=0; i<tabs.size(); i++) {
			result[i] = tabs.get(i).field;
		}
		return result;
	}
	

	/** 
	 * Returns the index of the given {@link Controller}, 
	 * -1 if doesn't exist in field. 
	 */
	public int indexOf(Controller<?> controller) {
		for (int i=0; i<tabs.size(); i++) {
			if ( controller == tabs.get(i).controller ) {
				return i;
			}
		}
		return -1;
	}
	
	/** 
	 * Returns the index of the given {@link Field}, 
	 * -1 if doesn't exist in field. 
	 */
	public int indexOf(Field field) {
		for (int i=0; i<tabs.size(); i++) {
			if ( field == tabs.get(i).field ) {
				return i;
			}
		}
		return -1;
	}

	/** Adds a tab composed by given controller, image and closeable attribute */
	public void addTab(Controller<?> controller, Image image, boolean closeable) {
		addTab(controller, image, closeable, -1);
	}
	
	/** Adds a tab composed by given controller, image, closeable attribute and index */
	public void addTab(Controller<?> controller, Image image, boolean closeable, int index) {
		if ( controller == null ) return;
		Field field = controller.createFields();
		field.addListener(controller.createListener(getActionExecuter()));
		controller.refreshFields();
		addTab(field, image, closeable, index);
	}
	
	/** Adds a tab composed by given field, image and closeable attribute */
	public void addTab(Field field, Image image, boolean closeable) {
		addTab(field, image, closeable, -1);
	}
	
	/** Adds a tab composed by given field, image, closeable attribute and index */
	public void addTab(Field field, Image image, boolean closeable, int index) {
		if ( field == null ) return;
		final Tab tab = new Tab(null, field, image, closeable);
		tabs.add( index < 0 ? tabs.size() : index, tab);
		if ( tabFolder != null && !tabFolder.isDisposed() ) {
			createCompositeForTab(tab, index < 0 ? tabFolder.getItemCount() : index);
		}
	}
	
	public void removeTab(Controller<?> controller) {
		removeTab(indexOf(controller), Notification.TYPE_API);
	}
	
	public void removeTab(Field field) {
		removeTab(indexOf(field), Notification.TYPE_API);
	}

	protected void removeTab(int index, int type) {
		Tab tab = tabs.get(index);
		if (tab != null && type != Notification.TYPE_UI ) {
			tab.item.dispose();
			tab.composite.dispose();
		} else {
			tabs.remove(index);
			final Object value = tab.controller == null ? tab.field : tab.controller;
			structureNotificationSupport.fireValueNotification(type, "close", value, null);
		}
	}
	
	public boolean activate() {
		final Tab currentTab = tabs.get(getSelected());
		if ( currentTab != null ) {
			return currentTab.field.activate();
		}
		return false;
	}

	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		for ( Tab tab : tabs) {
			tab.field.setEnable(enable);
		}
	}
	
	public boolean addListener(NotificationListener listener) {
		boolean result = super.addListener(listener);
		for ( Tab tab : tabs ) {
			result &= tab.field.addListener(listener);
		}
		return result;
	}

	public boolean removeListener(NotificationListener listener) {
		boolean result = true;
		for ( Tab tab : tabs ) {
			result &= tab.field.removeListener(listener);
		}
		return result;
	}
	
	public boolean addStructureListener(NotificationListener listener) {
		return structureNotificationSupport.addListener(listener);
	}
	
	public boolean removeStructureListener(NotificationListener listener) {
		return structureNotificationSupport.removeListener(listener);
	}
	
	@Override
	public void setActionExecuter(ActionExecuter executer) {
		super.setActionExecuter(executer);
		for ( Tab tab : tabs ) {
			((AbstractField) tab.field).setActionExecuter(executer);
		}
	}
	
	@Override
	public void refreshButtonBar() {
		super.refreshButtonBar();
		for ( Tab tab : tabs ) {
			((AbstractField) tab.field).refreshButtonBar();
		}
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( hasStyle(BasicsUI.VALIDATE_ALL) ) {
			Diagnostic maxDiagnostic = null;
			int maxError = Diagnostic.INFO;
			for ( Tab tab : tabs ) {
				if ( tab.field != null) {
					Diagnostic diagnostic = tab.field.validate();
					if ( diagnostic != null && diagnostic.getLevel() > maxError ) {
						maxDiagnostic = diagnostic;
						maxError = diagnostic.getLevel();
					}
				}
			}
			return maxDiagnostic;
		} else {
			if ( tabs.size() > 0 && tabs.get(getSelected()) != null ) {
				return tabs.get(getSelected()).field.validate();
			}
			return super.getDiagnostic();
		}
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createTabFolder(parent);
		createButtonBar(parent);
	}

	protected void createTabFolder(Composite parent) {
		tabFolder = new CTabFolder(parent, folderStyle());
		attachFieldToWidget(tabFolder);
		
		tabFolder.setBorderVisible(true);
		tabFolder.setSimple(false);
		
		Listener listener = new Listener() {
			public void handleEvent (Event event) {
				switch(event.type) {
				case SWT.Selection:
					setSelected(tabFolder.getSelectionIndex(), Notification.TYPE_UI);
					break;
				}
			}
		};
		tabFolder.addListener (SWT.Selection, listener);
	
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = fieldHorizontalSpan();
		tabFolder.setLayoutData(layoutData);

		for ( Tab tab : tabs ) {
			createCompositeForTab(tab); 
		}
		
		fireWidgetCreation(tabFolder);
		
		updateSelection();
	}

	private void createCompositeForTab(final Tab tab) {
		createCompositeForTab(tab, tabFolder.getItemCount());
	}
	
	private void createCompositeForTab(final Tab tab, int index) {
		tab.composite = new Composite(tabFolder, SWT.NONE);
		int style = tab.closeable ? SWT.CLOSE : SWT.NONE;
		tab.item = new CTabItem(tabFolder, style, index);
		if (tab.image != null) tab.item.setImage(tab.image);
		tab.item.setControl(tab.composite);
		if ( tab.field.getLabel() != null && tab.field.getLabel().length() > 0) {
			tab.item.setText(tab.field.getLabel());
		}

		final Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
					removeTab(event.index, Notification.TYPE_UI);
					tab.item.removeListener(SWT.Close, this);
					tab.item.removeListener(SWT.Dispose, this);
					break;
				}
			}
		};
		tab.item.addListener(SWT.Dispose, listener);
		
		tab.composite.setLayout(createFieldLayout());
		((AbstractField) tab.field).createWidget(tab.composite);
	}
	
	private int folderStyle() {
		return SWT.NONE;
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}
	
	public int getSelected() {
		return selected;
	}
	
	public void setSelected(Field field) {
		setSelected(indexOf(field));
	}
	
	public void setSelected(int selected) {
		setSelected(selected, Notification.TYPE_API);
	}
	
	protected void setSelected(int selected, int type) {
		if ( selected < 0 && selected >= tabs.size() ) return;
		int oldSelected = this.selected;
		this.selected = selected;
		if ( tabFolder != null && type != Notification.TYPE_UI ) {
			updateSelection();
		}
		structureNotificationSupport.fireValueNotification(type, BasicsUI.NOTIFICATION_SELECTION, this.selected, oldSelected);
	}

	protected void updateSelection() {
		tabFolder.setSelection(getSelected());
	}
	
}
