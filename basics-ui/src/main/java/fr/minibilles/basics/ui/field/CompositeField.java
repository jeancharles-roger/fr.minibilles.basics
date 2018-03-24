/**
 * 
 */
package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.notification.NotificationSupport;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionExecuter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * A {@link CompositeField} is a {@link Field} container.
 * @author Jean-Charles Roger
 *
 */
public class CompositeField extends AbstractField implements Structure {

	protected final List<Field> fieldList = new ArrayList<Field>();
	protected final NotificationSupport.Stub structureNotificationSupport = new NotificationSupport.Stub(this);

	public CompositeField(Field ...fields) {
		this(null, fields);
	}
	
	public CompositeField(String label, Field ...fields) {
		this(label, BasicsUI.NONE, fields);
	}
	
	public CompositeField(String label, int style, Field ... fields) {
		super(label, style);
		if ( fields != null ) {
			for ( Field oneField : fields ) fieldList.add(oneField);
		}
	}

	public void addField(Field field) {
		fieldList.add(field);
	}
	
	/** Returns a unmodifiable list of children fields. */
	public List<Field> getChildren() {
		return Collections.unmodifiableList(fieldList);
	}
	
	public boolean addListener(NotificationListener listener) {
		boolean result = super.addListener(listener);
		for ( Field oneField: fieldList ) {
			result &= oneField.addListener(listener);
		}
		return result;
	}

	public boolean removeListener(NotificationListener listener) {
		boolean result = true;
		for ( Field oneField: fieldList ) {
			result &= oneField.removeListener(listener);
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
	public void createWidget(Composite parent) {
		super.createWidget(parent);
		
		if ( hasActionsStyled(Action.STYLE_BUTTON) ) {
			parent = createActionComposite(parent);
		}
		
		Group group = null;
		if ( hasStyle(BasicsUI.GROUP) ) {
			group = new Group(parent, SWT.SHADOW_IN);
			if ( hasLabel() ) group.setText(getLabel());
			
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, grabExcessVerticalSpace());
			layoutData.horizontalSpan = 4;
			group.setLayoutData(layoutData);
			
			GridLayout layout = createFieldLayout();
			layout.marginWidth = 0;
			group.setLayout(layout);
			
		}
		
		for ( Field oneField: fieldList ) {
			((AbstractField) oneField).createWidget(group != null ? group : parent);
		}
	}
	
	protected Composite createActionComposite(Composite parent) {
		Composite buttonContainer = new Composite(parent, style);
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,4));
		buttonContainer.setLayout(new GridLayout(2, false));

		Composite composite = new Composite(buttonContainer, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout fieldLayout = createFieldLayout();
		fieldLayout.marginWidth = 0;
		composite.setLayout(fieldLayout);
		
		createButtonBar(buttonContainer);
		
		return composite;
	}
	
	@Override
	public boolean grabExcessVerticalSpace() {
		boolean result = false;
		for ( Field oneField: fieldList ) result |= oneField.grabExcessVerticalSpace();
		return result;
	}
	
	public boolean activate() {
		for ( Field oneField: fieldList ) {
			if ( oneField.activate() ) return true;
		}
		return false;
	}
	
	@Override
	public void setEnable(boolean enable) {
		super.setEnable(enable);
		for ( Field oneField: fieldList ) {
			oneField.setEnable(enable);
		}
	}
	
	@Override
	public Diagnostic validate() {
		Diagnostic maxDiagnostic = null;
		int maxError = Diagnostic.INFO;
		for ( Field oneField: fieldList ) {
			 Diagnostic diagnostic = oneField.validate();
			 if ( diagnostic != null && diagnostic.getLevel() > maxError ) {
				 maxDiagnostic = diagnostic;
				 maxError = diagnostic.getLevel();
			 }
		}
		return maxDiagnostic;
	}
	
	@Override
	public Diagnostic getDiagnostic() {
		Diagnostic maxDiagnostic = null;
		int maxError = Diagnostic.INFO;
		for ( Field oneField: fieldList ) {
			 Diagnostic diagnostic = oneField.getDiagnostic();
			 if ( diagnostic != null && diagnostic.getLevel() > maxError ) {
				 maxDiagnostic = diagnostic;
				 maxError = diagnostic.getLevel();
			 }
		}
		return maxDiagnostic;
	}

	@Override
	public void setActionExecuter(ActionExecuter executer) {
		super.setActionExecuter(executer);
		for ( Field oneField: fieldList ) {
			((AbstractField) oneField).setActionExecuter(executer);
		}
	}
	
	@Override
	public void refreshButtonBar() {
		super.refreshButtonBar();
		for ( Field oneField: fieldList ) {
			((AbstractField) oneField).refreshButtonBar();
		}
	}
}
