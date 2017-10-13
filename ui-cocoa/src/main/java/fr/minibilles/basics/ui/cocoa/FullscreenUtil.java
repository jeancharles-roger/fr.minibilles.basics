package fr.minibilles.basics.ui.cocoa;

import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.widgets.Shell;

public class FullscreenUtil {
	
	public static void setupFullscreen(Shell shell) {
		NSWindow nswindow = shell.view.window();
		// TODO to fix
		//nswindow.setCollectionBehavior(1 << 7);
	}
}
