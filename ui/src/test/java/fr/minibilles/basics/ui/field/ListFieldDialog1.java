package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.error.CompoundValidator;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.Validator;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple {@link FieldDialog} with one {@link ListField}.
 * @author Jean-Charles Roger
 */
public class ListFieldDialog1 {

	public static void main(String[] args) {

		final TextField nameField = new TextField(null, "lolo", BasicsUI.NONE);
		nameField.setTooltip("Enter here the name to add in the list");
		
		final ListField<String> namesListField = new ListField<String>(null, Arrays.asList("toto", "toto1", "toto2"), BasicsUI.MULTI | BasicsUI.NO_INFO);
		namesListField.setTooltip("List of names");
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
				if ( !newValue.isEmpty()) {
					namesListField.setSingleSelection(newValue.get(0));
				}
				return STATUS_OK;
			}
			
			@Override
			public String getTooltip() {
				return "Removes " + namesListField.getMultipleSelection().size() + " elements(s) from the list.";
			}

		});
		namesListField.addAction(new Action.Stub(Action.STYLE_SEPARATOR | Action.STYLE_BUTTON));
		namesListField.addAction(new Action.Stub("Select All") {
			
			@Override
			public String getTooltip() {
				return "Selects all names in the list";
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setMultipleSelection(namesListField.getValue());
				return STATUS_OK;
			}
		});
		namesListField.addAction(new Action.Stub("Deselect All") {

			@Override
			public String getTooltip() {
				return "Deselects any name from the list";
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setMultipleSelection(null);
				return STATUS_OK;
			}
		});
		
		Validator<String> nullValidator = new Validator.Stub<String>(Diagnostic.ERROR, "Name is empty.") {
			public boolean isValid(String value) {
				return value != null && value.length() > 0;
			}
		};
		Validator<String> allReadyExistValidator = new Validator.Stub<String>(Diagnostic.WARNING, "Name already exists in the list.") {
			public boolean isValid(String value) {
				return !namesListField.getValue().contains(value);
			}
		};
		
		@SuppressWarnings("unchecked")
		final CompoundValidator<String> validator = new CompoundValidator<String>(nullValidator, allReadyExistValidator);
		nameField.setValidator(validator);

		
		FieldDialog dialog = new FieldDialog("Test", "ListFieldDialog1", BasicsUI.SHOW_HINTS, new CompositeField(nameField, namesListField));
		dialog.setInitialMessage("Hello");
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
	}

}
