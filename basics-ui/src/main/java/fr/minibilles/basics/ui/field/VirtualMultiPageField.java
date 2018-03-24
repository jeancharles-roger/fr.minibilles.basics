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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link VirtualMultiPageField} represents a field with multiple pages that can be shown.
 * @author Jean-Charles Roger
 *
 */
public abstract class VirtualMultiPageField extends AbstractField implements Structure {

	private Field[] fields;
	private Composite[] fieldComposites;
	private Point[] sizes;

	private int fieldCount;
	private int	oldSelection = -1;
	private int selected = 0;
	
	protected StackLayout layout;
	protected ScrolledComposite scrolledComposite;
	protected Composite stackComposite;
	
	protected final NotificationSupport.Stub structureNotificationSupport = new NotificationSupport.Stub(this);

	public VirtualMultiPageField() {
		this(null, BasicsUI.NONE);
	}
	
	public VirtualMultiPageField(String label, int style) {
		super(label, BasicsUI.NO_INFO | style);
	}
	
	public VirtualMultiPageField(String label, int fieldCount, int style) {
		this(label, style);
		setFieldCount(fieldCount, false);
	}

	public boolean addListener(NotificationListener listener) {
		boolean result = super.addListener(listener);
		if ( fields != null ) {
			for ( Field oneField: fields ) {
				if ( oneField != null ) oneField.addListener(listener);
			}
		}
		return result;
	}

	public boolean removeListener(NotificationListener listener) {
		boolean result = super.removeListener(listener);
		if ( fields != null ) {
			for ( Field oneField: fields ) {
				if ( oneField != null ) oneField.removeListener(listener);
			}
		}
		return result;
	}
	
	public boolean addStructureListener(NotificationListener listener) {
		return structureNotificationSupport.addListener(listener);
	}
	
	public boolean removeStructureListener(NotificationListener listener) {
		return structureNotificationSupport.removeListener(listener);
	}

	public void setFieldCount(int fieldCount, boolean clear) {
		this.fieldCount = fieldCount;
		
		Field[] oldFields = this.fields;
		Composite[] oldComposites = this.fieldComposites;
		Point[] oldSizes = this.sizes;
		
		this.fields = new Field[fieldCount];
		this.fieldComposites = new Composite[fieldCount];
		this.sizes = new Point[fieldCount];

		if ( oldFields != null && !clear ) {
			int i=0;
			while ( i<fieldComposites.length && i<oldComposites.length ) {
				fields[i] = oldFields[i];
				fieldComposites[i] = oldComposites[i];
				sizes[i] = oldSizes[i];
				i++;
			}
			while ( i<oldComposites.length ) {
				if ( oldComposites[i] != null ) oldComposites[i].dispose();
				i++;
			}
			updateSelection();
		}
	}
	
	@Override
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		createComposite(parent);
		createButtonBar(parent);
	}

	protected void createComposite(Composite parent) {
		
		boolean createScroll = hasStyle(BasicsUI.SCROLL);
		if ( createScroll ) {
			scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		} 
		
		Composite stackParent = createScroll ? scrolledComposite : parent;
		stackComposite = new Composite(stackParent, SWT.NONE);
		
		if ( createScroll ) {
			scrolledComposite.setContent(stackComposite);
			scrolledComposite.setExpandHorizontal(true);
			scrolledComposite.setExpandVertical(true);
		}
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.horizontalSpan = fieldHorizontalSpan();

		if ( createScroll ) {
			scrolledComposite.setLayoutData(layoutData);
			scrolledComposite.setLayout(new FillLayout());
		} else {
			stackComposite.setLayoutData(layoutData);
		}
		
		layout = new StackLayout();
		stackComposite.setLayout(layout);
		updateSelection();
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		return true;
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		for ( Field oneField: fields ) {
			if ( oneField != null ) oneField.setEnable(enable);
		}
	}
	
	@Override
	public void setActionExecuter(ActionExecuter executer) {
		super.setActionExecuter(executer);
		if ( fields != null ) {
			for ( Field oneField: fields ) {
				if (oneField != null ) ((AbstractField) oneField).setActionExecuter(executer);
			}
		}
	}
	
	@Override
	public void refreshButtonBar() {
		super.refreshButtonBar();
		for ( Field oneField: fields ) {
			if (oneField instanceof AbstractField ) ((AbstractField) oneField).refreshButtonBar();
		}
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		if ( hasStyle(BasicsUI.VALIDATE_ALL) ) {
			Diagnostic maxDiagnostic = null;
			int maxError = Diagnostic.INFO;
			for ( Field oneField: fields ) {
				if ( oneField != null) {
					Diagnostic diagnostic = oneField.validate();
					if ( diagnostic != null && diagnostic.getLevel() > maxError ) {
						maxDiagnostic = diagnostic;
						maxError = diagnostic.getLevel();
					}
				}
			}
			return maxDiagnostic;
		} else {
			if ( fields[getSelected()] != null ) return fields[getSelected()].validate();
			return super.getDiagnostic();
		}
	}
	
	public boolean activate() {
		if ( fields[getSelected()] != null ) return fields[getSelected()].activate();
		return false;
	}

	public int getFieldCount() {
		return fieldCount;
	}

	public int getSelected() {
		return selected;
	}


	public void setSelected(int selected) {
		if ( selected < 0 && selected >= fields.length ) return;
		this.oldSelection = this.selected;
		this.selected = selected;
		if ( stackComposite != null) updateSelection();
		structureNotificationSupport.fireNotification(Notification.TYPE_API, "selection");
	}

	protected void updateSelection() {
		if ( fields[selected] == null ) { 
			Field currentField = getField(selected);
			fields[selected] = currentField;
			
			// adds all listeners to new field
			for ( NotificationListener listener : notificationSupport.getListeners()) {
				currentField.addListener(listener);
			}
			// sets action executer for new field
			currentField.setActionExecuter(getActionExecuter());
			
			Composite composite = new Composite(stackComposite, SWT.NONE);
			attachFieldToWidget(composite);
			
			fieldComposites[selected] = composite;
			final GridLayout fieldLayout = createFieldLayout();
			fieldLayout.marginWidth = 0;
			composite.setLayout(fieldLayout);
			((AbstractField) currentField).createWidget(composite);
			
			composite.pack();
			sizes[selected] = composite.getSize();
		}

		if (hasStyle(BasicsUI.RESIZE_SHELL) && oldSelection > -1  && oldSelection < fieldCount ) {
			// resizes the shell to adjust to the new selection.
			int dx = sizes[selected].x - sizes[oldSelection].x;
			int dy = sizes[selected].y - sizes[oldSelection].y;
			Point shellSize = stackComposite.getShell().getSize();
			shellSize.x += dx;
			shellSize.y += dy;
			stackComposite.getShell().setSize(shellSize);
		}
		
		layout.topControl = fieldComposites[selected];
		if ( scrolledComposite != null ) {
			scrolledComposite.setMinSize(sizes[selected]);
		}
		stackComposite.layout();
	}

	protected abstract Field getField(int i);

}
