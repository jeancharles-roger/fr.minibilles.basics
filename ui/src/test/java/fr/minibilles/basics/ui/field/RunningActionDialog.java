package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with a running action.
 * @author Jean-Charles Roger
 *
 */
public class RunningActionDialog {

	public static void main(String[] args) {
		final boolean[] running = new boolean[1];
		final TextField nameField = new TextField("Name", "true", BasicsUI.NONE);
		nameField.addAction(new Action.Stub("Test") {
			@Override
			public int getVisibility() {
				return running[0] ? VISIBILITY_RUNNING : VISIBILITY_ENABLE;
			}
		});
		nameField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				final String value = nameField.getValue();
				if ( value != null) {
					running[0] = Boolean.parseBoolean(value);
				} else {
					running[0] = false;
				}
			}
		});
		
		FieldDialog dialog = new FieldDialog("Test", "RunningActionDialog", BasicsUI.NONE, new CompositeField(nameField));
		dialog.open();
	}

}
