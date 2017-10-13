package fr.minibilles.basics.ui.field.text;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CDLSource1 {

	public static void main(String[] args) {
		
		final SourceCodeField field = new SourceCodeField(new CDLSourceCode());
		field.setEditable(true);
		
		field.addAction(new Action.Stub("Search") {
			@Override
			public int run(ActionMonitor monitor) {
				field.openSearchAndReplaceShell();
				return STATUS_OK;
			}
		});
		
		Display display = Display.getDefault();
		Shell shell = FieldShellToolkit.createShell(display, "CDLSource1");
		
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "CDL SourceCodeField Test1", BasicsUI.NO_HEADER, field);
		toolkit.init();

		shell.setSize(640, 480);
		shell.open();
		while ( shell.isDisposed() == false ) {
			if ( display.readAndDispatch() == false ) display.sleep();
		}
	}
}
