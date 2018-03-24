package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with one {@link RadioField}.
 * @author Jean-Charles Roger
 */
public class RadioFieldDialog1 {

	public static void main(String[] args) {

		final TextField nameField = new TextField(null, "toto", BasicsUI.NO_INFO);
		final RadioField genderField = new RadioField("Female", "Male", "Don't know");
		
		nameField.addListener(new NotificationListener() {
			
			public void notified(Notification notification) {
				genderField.setValue(nameField.getValue());
			}
		});
		
		FieldDialog dialog = new FieldDialog("Test", "RadioFieldDialog1", BasicsUI.NONE, new CompositeField(nameField, genderField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println("Hello " + nameField.getValue() + " of " + genderField.getValue() + ".");
		
	}

}
