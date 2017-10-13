package fr.minibilles.basics.ui;

import fr.minibilles.basics.ui.field.ChoiceFieldDialog1;
import fr.minibilles.basics.ui.field.FieldWizard1;
import fr.minibilles.basics.ui.field.ListFieldDialog1;
import fr.minibilles.basics.ui.field.ListFieldDialog2;
import fr.minibilles.basics.ui.field.ListFieldDialog3;
import fr.minibilles.basics.ui.field.TextFieldDialog1;
import fr.minibilles.basics.ui.field.TextFieldDialog2;
import fr.minibilles.basics.ui.sequencediagram.SequenceDiagramApplication;

public class MainUI {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ChoiceFieldDialog1.main(args);
		FieldWizard1.main(args);
		ListFieldDialog1.main(args);
		ListFieldDialog2.main(args);
		ListFieldDialog3.main(args);
		TextFieldDialog1.main(args);
		TextFieldDialog2.main(args);
		SequenceDiagramApplication.main(args);
	}

}
