package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.NumberValidator;
import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.Resources;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with two {@link TextField}.
 * @author Jean-Charles Roger
 *
 */
public class TextFieldDialog2 {

	public static void main(String[] args) {

		Resources resources = Resources.getInstance(Resources.class);
		final TextField nameField = new TextField("Name", "toto", BasicsUI.NONE);
		nameField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				System.out.println("Name changed to " + nameField.getValue());
			}
		});
		nameField.addAction(new Action.Stub("Essai") {
			@Override
			public int run(ActionMonitor monitor) {
				System.out.println("Hello world");
				return STATUS_OK;
			}
		});
		
		final TextField ageField = new TextField("Age", BasicsUI.NONE);
		ageField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				System.out.println("Age changed to " + ageField.getValue());
				ageField.refreshButtonBar();
			}
		});
		ageField.setValidator(new NumberValidator(Diagnostic.ERROR, "Invalid age", Basics.POSITIVE));
		ageField.addAction(new Action.Stub("Essai") {
			@Override
			public int getVisibility() {
				if (ageField.getIntValue() > 100 ) return VISIBILITY_HIDDEN;
				if (ageField.getIntValue() > 50 ) return VISIBILITY_DISABLE;
				return VISIBILITY_ENABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				System.out.println("Hello world");
				return STATUS_OK;
			}
		});
		
		final TextField multiTextField = new TextField("Blabla", BasicsUI.NO_INFO);
		multiTextField.setNbLines(3);
		
		final TextField readonlyTextField = new TextField("Readonly", BasicsUI.NO_INFO | BasicsUI.READ_ONLY);
		

		
		final CheckboxField testField = new CheckboxField("True or false");
		
		FieldDialog dialog = new FieldDialog("Test", "TextFieldDialog2", BasicsUI.SHOW_HINTS | BasicsUI.NO_HEADER, new CompositeField(nameField, multiTextField, readonlyTextField, ageField, testField));
		dialog.setInitialMessage("Toto fait du vélo\net la sonnete ben elle dégage.");
		dialog.setBannerImage(resources.getImage("banners/trash_empty.png"));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println("Name is " + nameField.getValue());
		System.out.println("Age is " + ageField.getIntValue());
		
		Resources.releaseInstance(Resources.class);
	}

}
