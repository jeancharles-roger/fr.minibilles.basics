package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import java.util.Random;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple {@link FieldDialog} with one {@link ConsoleField}.
 * @author Jean-Charles Roger
 */
public class ConsoleFieldDialog1 {

	public static void main(String[] args) {
		
		final ConsoleField consoleField = new ConsoleField();
		
		Shell shell = FieldShellToolkit.createShell("Test");
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "ConsoleFieldDialog1", BasicsUI.SHOW_HINTS, consoleField);
		toolkit.init();
		
		shell.open();
		
		final Display display = shell.getDisplay();
		
		final Random random = new Random();
		display.timerExec(1000, new Runnable() {
			
			int count = 1;
			
			public void run() {
				int color = SWT.COLOR_BLACK;
				if ( random.nextBoolean() ) {
					color += random.nextInt(15);
				}
				consoleField.log("Message logged n°" + count + ".\n", color, SWT.NONE);
				count++;
				
				consoleField.scrollToTheEnd();
				
				display.timerExec(1000, this);
			}
		});
		
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) display.sleep();
		}
		
		display.dispose();
		
	}
}
