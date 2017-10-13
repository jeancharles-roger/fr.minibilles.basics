package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import fr.minibilles.basics.ui.field.swt.GridCompositeField;

public class GridCompositeDialog1 {
	public static void main(String[] args) {
		TextField nameField = new TextField("Name");
		TextField secondNameField = new TextField("Second name");
		
		TextField descriptionField = new TextField("Description");
		descriptionField.setNbLines(3);
		
		GridCompositeField compositeField = new GridCompositeField();
		compositeField.setNumColumns(2);
		compositeField.addField(new CompositeField(nameField, secondNameField));
		compositeField.addField(descriptionField);
		
		FieldDialog dialog = new FieldDialog("Test", "GridCompositeDialog1", BasicsUI.NONE, compositeField);
		dialog.open();
		
	}
}
