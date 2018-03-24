package fr.minibilles.basics.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * This manager handle the detection of mouse hovering over one or many control,
 * and provides utilities to open balloon windows.
 * 
 * @author dsimoneau, Jean-Charles Roger
 */
public class ToolTipHandler {
		
	/** Control in which the last tool-tip detection occurred. */
	protected Control detectedControl = null;
	
	/** Position of the mouse cursor where the last tool-tip detection occurred. */
	protected int detectedPosX;
	protected int detectedPosY;

	/** Currently active tool-tip, or null. */
	protected Shell currentToolTipShell = null;
	
	/** Installs the required listeners on a given control. */
	public void listenControl(final Control control) {
		control.setToolTipText("");
		Listener toolTipListener = new Listener() {
			public void handleEvent(Event ev) {
				switch (ev.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (currentToolTipShell == null) break;
					currentToolTipShell.dispose();
					currentToolTipShell = null;
					detectedControl = null;
					break;
				}
				case SWT.MouseHover: {
					detectedControl = control;
					detectedPosX = ev.x;
					detectedPosY = ev.y;
					toolTipRequest(control, ev.x, ev.y);
				}
				}
			}
		};
		control.addListener(SWT.Dispose, toolTipListener);
		control.addListener(SWT.KeyDown, toolTipListener);
		control.addListener(SWT.MouseMove, toolTipListener);
		control.addListener(SWT.MouseHover, toolTipListener);
	}
	
	/**
	 * This method is called when a hovering is detected on a listened control.
	 * To be redefined in a sub-class to take the appropriate action.
	 */
	public boolean toolTipRequest(Control control, int x, int y) {
		return false;
	}
	
	/** Opens  a balloon displaying the given string. */
	public void showBalloon(String text) {
		showBalloon(
				null,
				text,
				detectedControl.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND),
				detectedControl.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND), 
				detectedPosX,
				detectedPosY + 24);
	}
	
	/** Opens  a balloon displaying the given image and string; colors and position can also be specified. */
	public void showBalloon(Image image, String text, Color foregroundColor, Color backgroundColor, int x, int y) {
		if (currentToolTipShell != null && !currentToolTipShell.isDisposed()) currentToolTipShell.dispose();
		currentToolTipShell = new Shell(detectedControl.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		currentToolTipShell.setBackground(backgroundColor);
		RowLayout layout = new RowLayout();
		layout.marginWidth = 2;
		currentToolTipShell.setLayout(layout);
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MouseDown:
					currentToolTipShell.dispose();
					break;
				case SWT.MouseExit:
					currentToolTipShell.dispose();
					break;
				}
			}
		};
		if (image != null) {
			Label imageLabel = new Label(currentToolTipShell, SWT.NONE);
			imageLabel.setForeground(foregroundColor);
			imageLabel.setBackground(backgroundColor);
			imageLabel.setImage(image);
			imageLabel.addListener(SWT.MouseExit, labelListener);
			imageLabel.addListener(SWT.MouseDown, labelListener);
		}
		if (text != null) {
			Label textLabel = new Label(currentToolTipShell, SWT.NONE);
			textLabel.setForeground(foregroundColor);
			textLabel.setBackground(backgroundColor);
			textLabel.setText(text);
			textLabel.addListener(SWT.MouseExit, labelListener);
			textLabel.addListener(SWT.MouseDown, labelListener);
		}
		Point size = currentToolTipShell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point pt = detectedControl.toDisplay(x, y);
		currentToolTipShell.setBounds(pt.x, pt.y, size.x, size.y);
		currentToolTipShell.setVisible(true);
	}

}
