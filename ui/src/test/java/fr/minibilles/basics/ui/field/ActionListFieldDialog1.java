package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;

/**
 * Simple {@link FieldDialog} with a {@link ActionListField}.
 * @author Jean-Charles Roger
 */
public class ActionListFieldDialog1 {

	private static class ExampleAction extends Action.Stub {

		public ExampleAction(String label) {
			super(label);
		}
		
		@Override
		public int run(ActionMonitor monitor) {
			System.out.println("Execs: " + getLabel());
			return STATUS_OK;
		}
	}
	
	public static void main(String[] args) {

		ActionListField actionListField = new ActionListField();
		actionListField.addAction(new ExampleAction("One"));
		actionListField.addAction(new ExampleAction("Two"));
		actionListField.addAction(new ExampleAction("Three"));
		actionListField.addAction(new ExampleAction("Four"));
		actionListField.addAction(new Action.Container("Five", 
			new ExampleAction("Five.one"),
			new ExampleAction("Five.two"),
			new ExampleAction("Five.three")
		));
		
		FieldDialog dialog = new FieldDialog("Test", "ActionListFieldDialog1", BasicsUI.NONE, new CompositeField(actionListField));
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		
	}

}
