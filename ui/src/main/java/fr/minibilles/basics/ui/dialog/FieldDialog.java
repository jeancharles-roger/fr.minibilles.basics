/**
 * 
 */
package fr.minibilles.basics.ui.dialog;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.controller.Controller;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.Field;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * This dialog box is constructed using a {@link CompositeField}.
 * @author Jean-Charles Roger
 */
public class FieldDialog {
	
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
			Diagnostic diagnostic = super.validateAll();
			buttons[0].setEnabled ( diagnostic == null || diagnostic.getLevel() < Diagnostic.ERROR );
			return diagnostic;
		}

	}
	
	public final static String [] okCancelButtons = new String [] { "Ok", "Cancel" };
	public final static String [] okButton = new String [] { "Ok" };
	
	private final Shell shell;
	
	private FieldShellToolkit fieldShell;
	
	private String [] buttonLabels = okCancelButtons;
	private Button[] buttons;
	private int selectedButton = -1;
	
	private Composite buttonsBar;
	
	public FieldDialog(Shell parentShell, String shellTitle, String title, int style, Field field) {
		this.shell = FieldShellToolkit.createShell(parentShell, shellTitle);
		this.fieldShell = new InternalFieldShell(shell, title, style, field);
		this.fieldShell.setButtonBarHeight(Basics.isMac() ? 40 : 30);
	}
	
	public FieldDialog(String shellTitle, String title, int style, Field field) {
		this(null, shellTitle, title, style, field);
	}
	
	public FieldDialog(Shell parentShell, String shellTitle, String title, int style, Controller<?> controller) {
		this.shell = FieldShellToolkit.createShell(parentShell, shellTitle);
		this.fieldShell = new InternalFieldShell(shell, title, style, controller);
		this.fieldShell.setButtonBarHeight(Basics.isMac() ? 40 : 30);
	}

	public FieldDialog(String shellTitle, String title, int style, Controller<?> controller) {
		this(null, shellTitle, title, style, controller);
	}
	
	/** @return the dialog shell. */
	public Shell getShell() {
		return shell;
	}
	
	/** @return the list of button's labels. */
	public String [] getButtonLabels() {
		return buttonLabels;
	}
	
	/** Sets the button's labels. */
	public void setButtonLabel(String [] buttonLabels) {
		this.buttonLabels = buttonLabels == null ? okCancelButtons : buttonLabels;
	}
		
	/** Creates the buttons for the dialog. */
	protected void createButtonsBar(final Shell dialogShell) {
		buttonsBar = new Composite(dialogShell, SWT.NONE);
	
		buttons = new Button[buttonLabels.length];
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for (int i=0; i < buttons.length; i++) {
					if ( buttons[i] == e.getSource() ) selectedButton = i;
				}
				preClose(selectedButton);
				dialogShell.dispose();
			}
		};
		
		// create controls
		for (int i=0; i<buttonLabels.length; i++ ) {
			Button button = new Button(buttonsBar, SWT.PUSH);
			button.setText(buttonLabels[i]);
			button.addSelectionListener(listener);
			buttons[i] = button;
		}

		// set layout 
		FormData compositeLayoutData = new FormData();
		compositeLayoutData.left = new FormAttachment(0);
		compositeLayoutData.right = new FormAttachment(100);
		compositeLayoutData.bottom = new FormAttachment(100);
		buttonsBar.setLayoutData(compositeLayoutData);
		
		final FormLayout buttonsBarLayout = new FormLayout();
		buttonsBarLayout.marginWidth = 5;
		buttonsBarLayout.marginTop = 5;
		buttonsBarLayout.marginBottom = 5;
		buttonsBar.setLayout(buttonsBarLayout);

		for ( int i=0; i<buttonLabels.length; i++ ) {
			FormData layoutData = new FormData(80,fieldShell.getButtonBarHeight() - 10);
			if ( i < buttonLabels.length-1 ) {
				layoutData.right = new FormAttachment(buttons[i+1]);
			} else {
				layoutData.right = new FormAttachment(100);
			}
			buttons[i].setLayoutData(layoutData);
		}
		
		dialogShell.setDefaultButton(buttons[0]);
	}
	
	
	
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

	/** Method called just before the dialog is opened.  */
	protected void preOpen() {
	}
	
	/** Method called just before the dialog is closed. */
	protected void preClose(int buttonIndex) {
	}
	
	public int open() {

		fieldShell.init();
		preOpen();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !shell.getDisplay().readAndDispatch() ) { shell.getDisplay().sleep(); }
		}
		preClose(selectedButton);
		
		Resources.releaseInstance(Resources.class);
		return selectedButton;
	}
}
