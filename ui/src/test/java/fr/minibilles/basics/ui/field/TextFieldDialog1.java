package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.NumberValidator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with two {@link TextField}.
 * @author Jean-Charles Roger
 *
 */
public class TextFieldDialog1 {

	public static void main(String[] args) {

		final TextField nameField = new TextField("Name", "toto", BasicsUI.NONE);
		nameField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				if ( BasicsUI.NOTIFICATION_VALUE.equals(notification.name) ) {
					System.out.println("Name changed to " + nameField.getValue());
				}
			}
		});
		final TextField ageField = new TextField("Age", BasicsUI.NONE);
		ageField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				if ( BasicsUI.NOTIFICATION_VALUE.equals(notification.name) ) {
					System.out.println("Age changed to " + ageField.getValue());
				}
			}
		});
		ageField.setValidator(new NumberValidator(Diagnostic.ERROR, "Invalid age", Basics.POSITIVE));
		
		FieldDialog dialog = new FieldDialog("Test", "TextFieldDialog1", BasicsUI.NONE, new CompositeField(nameField, ageField));
		dialog.setInitialMessage("Toto fait du v�lo\net la sonnete ben elle d�gage.");
		
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println("Name is " + nameField.getValue());
		System.out.println("Age is " + ageField.getIntValue());
	}

}
