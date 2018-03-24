package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.util.Arrays;

/**
 * Simple {@link FieldDialog} with one checked {@link ListField}.
 * @author Jean-Charles Roger
 */
public class ListFieldDialog3 {

	public static void main(String[] args) {

		final ListField<String> namesListField = new ListField<String>(null, Arrays.asList("toto", "toto1", "toto2"), BasicsUI.ITEM_EDITABLE | BasicsUI.NO_INFO ) {
			@Override
			protected String fromText(String edited, String text) {
				return text;
			}
		};
	
		FieldDialog dialog = new FieldDialog("Test", "ListFieldDialog3", BasicsUI.NONE, new CompositeField(namesListField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println("Names: " + namesListField.getValue());
	}

}
