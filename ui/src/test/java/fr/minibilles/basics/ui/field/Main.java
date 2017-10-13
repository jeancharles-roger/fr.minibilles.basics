package fr.minibilles.basics.ui.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Main {

	
	public static void main(String[] args) {
		
		final Display display = Display.getDefault();
		
		final Shell findAndReplaceShell = new Shell(display, SWT.SHELL_TRIM);
		findAndReplaceShell.setText("Search and replace");
		findAndReplaceShell.setMinimumSize(150, 100);
		findAndReplaceShell.setSize(250, 130);

		Composite textComposite = new Composite(findAndReplaceShell, SWT.NONE);
		
		final Label findLabel = new Label(textComposite, SWT.NONE);
		findLabel.setText("Find:");
		final Text findText = new Text(textComposite, SWT.NONE);

		final Label replaceWithLabel = new Label(textComposite, SWT.NONE);
		replaceWithLabel.setText("Replace with:");
		final Text replaceWithText = new Text(textComposite, SWT.NONE);
		
		Composite buttonComposite = new Composite(findAndReplaceShell, SWT.NONE);
		
		final Button findButton = new Button(buttonComposite, SWT.PUSH);
		findButton.setText("Find");
		
		final Button closeButton = new Button(buttonComposite, SWT.PUSH);
		closeButton.setText("Close");
		
		findAndReplaceShell.setLayout(new GridLayout(1, false));

		GridLayout textCompositeLayout = new GridLayout(2, false);
		textCompositeLayout.horizontalSpacing = 10;
		textCompositeLayout.verticalSpacing = 10;
		textComposite.setLayout(textCompositeLayout);
		textComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		buttonComposite.setLayout(new GridLayout(2, true));
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		
		findLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		findText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		replaceWithLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		replaceWithText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		findButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				findAndReplaceShell.dispose();
			}
		});
		
		findAndReplaceShell.open();

		while (findAndReplaceShell.isDisposed() == false ) {
			if ( display.readAndDispatch() == false ) display.sleep();
		}
		
	}
}
