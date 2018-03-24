package fr.minibilles.basics.ui.field.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FindAndReplaceShell {

	/** {@link StyledText} used for search */
	private final StyledText styledText;
	
	private Shell findAndReplaceShell;
	private Text findText;
	private Text replaceWithText;

	private Button ignoreCaseCheck;
	private Button wrapCheck;
	
	private Button findButton;
	private Button replaceFindButton;
	private Button replaceButton;
	private Button replaceAllButton;

	private Label messageLabel;
	
	public FindAndReplaceShell(StyledText styledText) {
		this.styledText = styledText;
	}
	
	public void open() {
		if ( findAndReplaceShell == null || findAndReplaceShell.isDisposed() ) {

			findAndReplaceShell = new Shell(styledText.getShell(), SWT.SHELL_TRIM);
			findAndReplaceShell.setText("Search and replace");
			findAndReplaceShell.setMinimumSize(260, 290);
			findAndReplaceShell.setSize(320, 290);
	
			Composite textComposite = new Composite(findAndReplaceShell, SWT.NONE);
			
			final Label findLabel = new Label(textComposite, SWT.NONE);
			findLabel.setText("Find:");
			findText = new Text(textComposite, SWT.BORDER);
			
			final Label replaceWithLabel = new Label(textComposite, SWT.NONE);
			replaceWithLabel.setText("Replace with:");
			replaceWithText = new Text(textComposite, SWT.BORDER);
			
			Group optionComposite = new Group(findAndReplaceShell, SWT.NONE);
			optionComposite.setText("Options");
			
			ignoreCaseCheck = new Button(optionComposite, SWT.CHECK);
			ignoreCaseCheck.setText("Ignore case");
			
			wrapCheck = new Button(optionComposite, SWT.CHECK);
			wrapCheck.setText("Wrap search");
			
			Composite buttonComposite = new Composite(findAndReplaceShell, SWT.NONE);
			
			findButton = new Button(buttonComposite, SWT.PUSH);
			findButton.setText("Find");
			
			replaceFindButton = new Button(buttonComposite, SWT.PUSH);
			replaceFindButton.setText("Replace/Find");
			
			replaceButton = new Button(buttonComposite, SWT.PUSH);
			replaceButton.setText("Replace");
			
			replaceAllButton = new Button(buttonComposite, SWT.PUSH);
			replaceAllButton.setText("Replace All");

			Composite messageCloseComposite = new Composite(findAndReplaceShell, SWT.NONE);

			messageLabel = new Label(messageCloseComposite, SWT.NONE);
			
			final Button closeButton = new Button(messageCloseComposite, SWT.PUSH);
			closeButton.setText("Close");
			
			findAndReplaceShell.setLayout(new GridLayout(1, false));
	
			GridLayout textCompositeLayout = new GridLayout(2, false);
			textCompositeLayout.horizontalSpacing = 10;
			textCompositeLayout.verticalSpacing = 10;
			textComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			textComposite.setLayout(textCompositeLayout);
	
			findLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			findText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			replaceWithLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
			replaceWithText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	
			optionComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			GridLayout optionCompositeLayout = new GridLayout(2, true);
			optionCompositeLayout.marginHeight = 15;
			optionComposite.setLayout(optionCompositeLayout);

			ignoreCaseCheck.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			wrapCheck.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			
			buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
			GridLayout buttonCompositeLayout = new GridLayout(2, true);;
			buttonComposite.setLayout(buttonCompositeLayout);
			
			findButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			replaceFindButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			replaceButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			replaceAllButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
			
			
			messageCloseComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			GridLayout messageCloseCompositeLayout = new GridLayout(2, false);
			messageCloseComposite.setLayout(messageCloseCompositeLayout);

			messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			closeButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	
			findButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					find();
				}
			});
	
			replaceFindButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if ( replace() ) find();
				}
			});
			
			replaceButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					replace();
				}
			});
			
			replaceAllButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int count = 0;
					while ( find() ) {
						count += 1;
						replace();
					}
					messageLabel.setText("Replaced "+ count +" occurence"+ (count > 1 ? "s" : "") +".");
				}
			});
			
			replaceButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					replace();
				}
			});
			
			closeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					findAndReplaceShell.dispose();
				}
			});
			
			findText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					findButton.setEnabled(findText.getText().length() > 0);
					replaceAllButton.setEnabled(findText.getText().length() > 0);
				}
			});
			
			findButton.setEnabled(false);
			replaceFindButton.setEnabled(false);
			replaceButton.setEnabled(false);
			replaceAllButton.setEnabled(false);

			findAndReplaceShell.setDefaultButton(findButton);
			findAndReplaceShell.open();
		} else {
			findAndReplaceShell.setActive();
		}
		
		String selection = styledText.getSelectionText();
		if ( selection != null && selection.length() > 0 ) {
			findText.setText(selection);
		}

 	}
	
	private boolean find() {
		boolean found = false;
		String toSearch = findText.getText();
		if ( toSearch != null && toSearch.length() > 0 ) {
			int currentOffset = styledText.getCaretOffset();
			
			String text = styledText.getText();
			if ( ignoreCaseCheck.getSelection() ) {
				text = text.toLowerCase();
				toSearch = toSearch.toLowerCase();
			}
			
			int searched = styledText.getText().indexOf(toSearch, currentOffset);
			
			if ( searched < 0 && wrapCheck.getSelection() ) {
				searched = styledText.getText().indexOf(toSearch);
			}
			
			if ( searched >= 0 ) {
				styledText.setSelection(searched, searched+toSearch.length());
				styledText.showSelection();
				found = true;
			}
			
		}
		
		// updates shell
		replaceFindButton.setEnabled(found);
		replaceButton.setEnabled(found);
		
		if ( found ) {
			messageLabel.setText("");
		} else {
			messageLabel.setText("String not found.");
		}
		
		return found;
	}

	private boolean replace() {
		boolean replaced = false;
		String toReplaceWith = replaceWithText.getText();
		int[] range = styledText.getSelectionRanges();
		if ( range.length == 2 && range[1] > 0 ) {
			if ( range.length == 2 ) {
				styledText.getContent().replaceTextRange(range[0], range[1], toReplaceWith);
				replaced = true;
			}
		}
		return replaced;
	}
}
