package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.util.Arrays;

/**
 * Simple {@link FieldDialog} with one checked {@link ListField}.
 * @author Jean-Charles Roger
 */
public class ListFieldDialog2 {

	public static void main(String[] args) {

		final ListField<String> namesListField = new ListField<String>(null, Arrays.asList("toto", "toto1", "toto2"), BasicsUI.MULTI | BasicsUI.NO_INFO | BasicsUI.CHECK );
		namesListField.setTooltip("List of names");
		namesListField.addAction(new Action.Stub("Check All") {
			
			@Override
			public String getTooltip() {
				return "Checks all names in the list";
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setChecked(namesListField.getValue());
				return STATUS_OK;
			}
		});
		namesListField.addAction(new Action.Stub("Uncheck All") {

			@Override
			public String getTooltip() {
				return "Unchecks any name from the list";
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				namesListField.setChecked(null);
				return STATUS_OK;
			}
		});
		
		FieldDialog dialog = new FieldDialog("Test", "ListFieldDialog2", BasicsUI.NONE, new CompositeField(namesListField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println("Selected names: " + namesListField.getMultipleSelection());
		System.out.println("Checked names: " + namesListField.getChecked());
	}

}
