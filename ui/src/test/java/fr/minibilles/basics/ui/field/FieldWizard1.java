package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.dialog.FieldWizard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple {@link FieldDialog} with one {@link VirtualMultiPageField}.
 * @author Jean-Charles Roger
 */
public class FieldWizard1 {

	
	
	public static void main(String[] args) {

		FieldWizard dialog = new FieldWizard("Test", "FieldWizard1", BasicsUI.SHOW_HINTS, 2) {
			@Override
			protected CompositeField getPageField(int i) {
				switch (i) {
				case 0: 
					return getField1();
				default:
					return getField2();
				}
			}
		};
		boolean canceled = dialog.open();
		
		System.out.println("Is canceled: " + canceled);
		
	}

	public static CompositeField getField1() {
		final TextField nameField = new TextField("Name", "toto", BasicsUI.NO_INFO);
		nameField.setTooltip("Enter person's name here.");
		
		final ChoiceField<String> choiceField = new ChoiceField<String>("Choose", BasicsUI.NO_INFO);
		choiceField.setTooltip("Choose a number.");
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
			@Override
			public String getTooltip() {
				return "Adds " + nameField.getValue() + " to the list.";
			}
		});
		
		return new CompositeField(nameField, choiceField);
	}

	public static CompositeField getField2() {
		final TextField nameField = new TextField(null, "toto", BasicsUI.NO_INFO);
		final ListField<String> namesListField = new ListField<String>(null, Arrays.asList("toto", "toto1", "toto2"), BasicsUI.MULTI | BasicsUI.NO_INFO);
		namesListField.addAction(new Action.Stub("+") {
			@Override
			public int getVisibility() {
				if ( nameField.getValue() != null && nameField.getValue().length() > 0 && !namesListField.getValue().contains(nameField.getValue())) {
					return VISIBILITY_ENABLE;
				} else {
					return VISIBILITY_DISABLE;
				}
			}
			@Override
			public int run(ActionMonitor monitor) {
				List<String> newValue = new ArrayList<String>(namesListField.getValue());
				newValue.add(nameField.getValue());
				namesListField.setValue(newValue);
				return STATUS_OK;
			}
			
			@Override
			public String getTooltip() {
				return "Adds " + nameField.getValue() + " to the list.";
			}

		});
		namesListField.addAction(new Action.Stub("-") {
			@Override
			public int getVisibility() {
				return namesListField.getMultipleSelection().isEmpty() ? VISIBILITY_DISABLE : VISIBILITY_ENABLE;
			}
			@Override
			public int run(ActionMonitor monitor) {
				List<String> newValue = new ArrayList<String>(namesListField.getValue());
				newValue.removeAll(namesListField.getMultipleSelection());
				namesListField.setValue(newValue);
				return STATUS_OK;
			}
			
			@Override
			public String getTooltip() {
				return "Removes " + namesListField.getMultipleSelection().size() + " elements(s) from the list.";
			}

		});
		namesListField.addAction(new Action.Stub("Select All") {
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setMultipleSelection(namesListField.getValue());
				return STATUS_OK;
			}
		});
		namesListField.addAction(new Action.Stub("Deselect All") {
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setMultipleSelection(null);
				return STATUS_OK;
			}
		});
		
		CompositeField compositeField = new CompositeField(nameField, namesListField);
		return compositeField;
	}

}
