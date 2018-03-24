package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import java.io.PrintWriter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple {@link FieldDialog} with one {@link ConsoleField}.
 * @author Jean-Charles Roger
 */
public class ConsoleFieldDialog2 {

	public static void main(String[] args) {
		final int n = 100000;
		
		final ConsoleField consoleField = new ConsoleField();
		
		Shell shell = FieldShellToolkit.createShell("Test");
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "ConsoleFieldDialog2", BasicsUI.SHOW_HINTS, consoleField);
		toolkit.init();
		
		shell.open();
		
		final Display display = shell.getDisplay();
		
		final PrintWriter writer = consoleField.createPrintWriter(false);
		display.asyncExec(new Runnable() {
			int count = 0;
			public void run() {
				writer.println("Write a line");
				count += 1;
				
				if ( count < n ) {
					display.asyncExec(this);
				} else {

					writer.flush();
					writer.close();
				}
			}
		});
		
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		
		display.dispose();
		
	}
}
