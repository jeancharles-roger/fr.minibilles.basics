package fr.minibilles.basics.ui.dialog;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.ActionManager;
import fr.minibilles.basics.ui.controller.Controller;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.Field;
import fr.minibilles.basics.ui.field.VirtualMultiPageField;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A {@link FieldWizard} proposes a {@link Shell} with multiple pages.
 * This pages are accessible by <i>Next</i> and <i>Back</i>. A wizard is ended by <i>Finish</i> or <i>Cancel</i>.
 * <p>
 * A {@link FieldWizard} is constructed with a {@link VirtualMultiPageField}.
 *
 * @author Jean-Charles Roger
 */
public abstract class FieldWizard {

	private class InternalFieldShell extends FieldShellToolkit {

		public InternalFieldShell(Shell shell, String title, int style, Controller<?> controller) {
			super(shell, title, style, controller);
		}

		public InternalFieldShell(Shell shell, String title, int style, Field field) {
			super(shell, title, style, field);
		}
		
		@Override
		protected void createFields() {
			super.createFields();
			createButtonsBar(getShell());
		}
		
		@Override
		public Diagnostic validateAll() {
			lastDiagnostic = super.validateAll();
			ActionManager.refreshWidgets(buttonsBar);
			return lastDiagnostic;
		}
		
		@Override
		public void refresh() {
			super.refresh();
			ActionManager.refreshWidgets(buttonsBar);
		}
	}

	
	private final Shell shell;
	
	protected FieldShellToolkit fieldShell;
	private final VirtualMultiPageField multiPageField;

	private boolean canceled = true;
	private Diagnostic lastDiagnostic;

	private ActionManager buttonManager;
	private Composite buttonsBar;
	
	public FieldWizard(String shellTitle, String title, int style, int pageCount) {
		this(null, shellTitle, title, style, pageCount);
	}
	
	public FieldWizard(Shell parentShell, String shellTitle, String title, int style, int pageCount) {
		this.multiPageField = new VirtualMultiPageField(null, pageCount, BasicsUI.VALIDATE_ALL | BasicsUI.RESIZE_SHELL) {
			@Override
			protected CompositeField getField(int i) {
				return getPageField(i);
			}
		};
		this.shell = FieldShellToolkit.createShell(parentShell, shellTitle);
		this.fieldShell = new InternalFieldShell(shell, title, style, multiPageField);
		this.fieldShell.setButtonBarHeight(Basics.isMac() ? 40 : 30);
	}
	
	protected abstract CompositeField getPageField(int i);
	
	public void setPageCount(int value, boolean clear) {
		multiPageField.setFieldCount(value, clear);
	}
	
	public int getPageCount() {
		return multiPageField.getFieldCount();
	}
	
	
	protected VirtualMultiPageField getMultiPageField() {
		return multiPageField;
	}

	/** Creates the buttons for the dialog. */
	protected void createButtonsBar(final Shell dialogShell) {
		
		Label separator = new Label(dialogShell, SWT.HORIZONTAL | SWT.SEPARATOR );
		
		buttonManager = new ActionManager();
		List<Action> actions = Arrays.asList(getBackAction(), getNextAction(), getCancelAction(), getFinishAction());
		buttonsBar = buttonManager.createButtonBar(dialogShell, SWT.HORIZONTAL, actions);
		
		// set layout 
		
		FormData separatorFormData = new FormData();
		separatorFormData.left = new FormAttachment(0);
		separatorFormData.right = new FormAttachment(100);
		separatorFormData.bottom = new FormAttachment(buttonsBar);
		separator.setLayoutData(separatorFormData);
		
		FormData compositeLayoutData = new FormData();
		compositeLayoutData.right = new FormAttachment(100);
		compositeLayoutData.bottom = new FormAttachment(100);
		buttonsBar.setLayoutData(compositeLayoutData);

		//dialogShell.setDefaultButton(buttons[0]);
	}
	
	protected Action getBackAction() {
		return new Action.Stub("< Back") {
			@Override
			public int getVisibility() {
				return getMultiPageField().getSelected() > 0 ? VISIBILITY_ENABLE : VISIBILITY_DISABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				monitor.setTaskName(getLabel());
				monitor.begin(1);
				getMultiPageField().setSelected(getMultiPageField().getSelected() - 1);
				fieldShell.refresh();
				monitor.done();
				return STATUS_OK;
			}
		};
	};
	
	protected Action getNextAction() {
		return new Action.Stub("Next >") {
			@Override
			public int getVisibility() {
				return getMultiPageField().getSelected() < getMultiPageField().getFieldCount() - 1 ? VISIBILITY_ENABLE : VISIBILITY_DISABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				monitor.setTaskName(getLabel());
				monitor.begin(1);
				getMultiPageField().setSelected(getMultiPageField().getSelected() + 1);
				fieldShell.refresh();
				monitor.done();
				return STATUS_OK;
			}
		};
	};
	
	protected Action getCancelAction() {
		return new Action.Stub("Cancel") {
			@Override
			public int getVisibility() {
				return VISIBILITY_ENABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				monitor.setTaskName(getLabel());
				monitor.begin(1);
				canceled = true;
				shell.dispose();
				monitor.done();
				return STATUS_OK;
			}
		};
	};
	
	protected Action getFinishAction() {
		return new Action.Stub("Finish") {
			@Override
			public int getVisibility() {
				if ( lastDiagnostic == null ) return VISIBILITY_ENABLE;
				return lastDiagnostic.getLevel() == Diagnostic.ERROR ? VISIBILITY_DISABLE : VISIBILITY_ENABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				monitor.setTaskName(getLabel());
				monitor.begin(1);
				canceled = false;
				shell.dispose();
				monitor.done();
				return STATUS_OK;
			}
		};
	};
	
	public Image getBannerImage() {
		return fieldShell.getBannerImage();
	}

	public String getInitialMessage() {
		return fieldShell.getInitialMessage();
	}

	public void setBannerImage(Image bannerImage) {
		fieldShell.setBannerImage(bannerImage);
	}

	public void setInitialMessage(String initialMessage) {
		fieldShell.setInitialMessage(initialMessage);
	}

	
	/** Method called just before the wizard is opened.  */
	protected void preOpen() {
	}
	
	/** Method called just before the wizard is closed. */
	protected void preClose(boolean canceled) {
	}
	
	public boolean open() {
		fieldShell.init();
		preOpen();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() ) { shell.getDisplay().sleep(); }
		}
		preClose(canceled);
		
		Resources.releaseInstance(Resources.class);
		return canceled;
	}
}
