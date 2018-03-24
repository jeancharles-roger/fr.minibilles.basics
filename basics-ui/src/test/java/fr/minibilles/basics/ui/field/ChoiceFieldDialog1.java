package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple {@link FieldDialog} with one {@link ChoiceField}.
 * @author Jean-Charles Roger
 */
public class ChoiceFieldDialog1 {

	public static void main(String[] args) {

		final TextField nameField = new TextField(null, "toto", BasicsUI.NO_INFO);
		final ChoiceField<String> choiceField = new ChoiceField<String>("Choose", BasicsUI.NO_INFO);
		choiceField.setValue("two");
		choiceField.setRange(Arrays.asList("one", "two", "three", "four"));
		choiceField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				System.out.println(notification);
			}
		});
		
		
		nameField.addAction(new Action.Stub("+") {
			@Override
			public int getVisibility() {
				if ( nameField.getValue() != null && nameField.getValue().length() > 0 && !choiceField.getRange().contains(nameField.getValue()) ) {
					return VISIBILITY_ENABLE; 
				} else {
					return VISIBILITY_DISABLE;
				}
			}
			@Override
			public int run(ActionMonitor monitor) {
				List<String> newRange = new ArrayList<String>(choiceField.getRange());
				newRange.add(nameField.getValue());
				choiceField.setRange(newRange);
				choiceField.setValue(nameField.getValue());
				return STATUS_OK;
			}
		});
		FieldDialog dialog = new FieldDialog("Test", "ChoiceFieldDialog1", BasicsUI.NONE, new CompositeField(nameField, choiceField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		
	}

}
