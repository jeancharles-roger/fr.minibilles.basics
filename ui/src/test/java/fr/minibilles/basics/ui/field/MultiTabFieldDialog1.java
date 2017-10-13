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
 * Simple {@link FieldDialog} with one {@link MultiTabField}.
 * @author Jean-Charles Roger
 */
public class MultiTabFieldDialog1 {

	
	
	public static void main(String[] args) {

		final MultiTabField multiTab = new MultiTabField(null, BasicsUI.NONE);
		multiTab.addTab(getField1(), null, true);
		multiTab.addTab(getField2(), null, true);

		multiTab.addStructureListener(new NotificationListener() {
			
			public void notified(Notification notification) {
				if ( "close".equals(notification.name) ) {
					CompositeField field = (CompositeField) notification.newValue;
					System.out.println("Closed " + field.getLabel());
				}
			}
		});
		
		FieldDialog dialog = new FieldDialog("Test", "MultiTabFieldDialog1", BasicsUI.NONE, multiTab);
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		
	}

	public static CompositeField getField1() {
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
		
		return new CompositeField("ChoiceField", BasicsUI.NONE, nameField, choiceField);
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
		
		return new CompositeField("ListField", BasicsUI.NONE, nameField, namesListField);
	}

}
