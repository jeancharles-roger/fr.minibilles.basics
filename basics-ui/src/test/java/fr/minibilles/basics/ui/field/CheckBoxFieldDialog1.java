package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with some {@link CheckboxField}.
 * @author Jean-Charles Roger
 */
public class CheckBoxFieldDialog1 {

	public static void main(String[] args) {

		CheckboxField field1 = new CheckboxField("Check 1");
		CheckboxField field2 = new CheckboxField("Check 1", BasicsUI.RIGHT);
		
		FieldDialog dialog = new FieldDialog("Test", "CheckBoxFieldDialog1", BasicsUI.NONE, new CompositeField(field1, field2));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		
	}

}
