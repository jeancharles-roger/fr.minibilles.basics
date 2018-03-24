package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CDLSource2 {

	public static void main(String[] args) {
		
		
		final CDLSourceCode cdlSourceCode = new CDLSourceCode();
		final SourceCodeField field = new SourceCodeField();
		
		final SourceCodeFieldChangeHandler changeHandler = new SourceCodeFieldChangeHandler(field);
		field.setChangeHandler(changeHandler);
		
		field.setEditable(true);
		
		field.addAction(new Action.Stub("Search") {
			@Override
			public int run(ActionMonitor monitor) {
				field.openSearchAndReplaceShell();
				return STATUS_OK;
			}
		});
		
		field.addAction(new Action.Stub("Undo") {
			public int getVisibility() {
				return changeHandler.canUndo() ? VISIBILITY_ENABLE : VISIBILITY_DISABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				changeHandler.undo();
				return STATUS_OK;
			}
		});
		
		field.addAction(new Action.Stub("Redo") {
			public int getVisibility() {
				return changeHandler.canRedo() ? VISIBILITY_ENABLE : VISIBILITY_DISABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				changeHandler.redo();
				return STATUS_OK;
			}
		});
		
		field.addAction(new Action.Stub("CDL", Action.STYLE_DEFAULT | Action.STYLE_BOOLEAN_STATE ) {
			public boolean getState() {
				return field.getSourceCode() == cdlSourceCode;
			}
			
			public int run(ActionMonitor monitor) {
				SourceCode newSourceCode = null;
				if ( field.getSourceCode() != cdlSourceCode ) {
					newSourceCode = cdlSourceCode;
				}
				field.setSourceCode(newSourceCode);
				return STATUS_OK;
			}
			
		});
		
		Display display = Display.getDefault();
		Shell shell = FieldShellToolkit.createShell(display, "CDLSource2");
		
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "CDL SourceCodeField Test1", BasicsUI.NO_HEADER, field);
		toolkit.init();

		shell.setSize(640, 480);
		shell.open();
		while ( shell.isDisposed() == false ) {
			if ( display.readAndDispatch() == false ) display.sleep();
		}
	}
}
