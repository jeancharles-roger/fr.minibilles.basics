package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.dialog.FieldDialog;
import java.util.Arrays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * {@link FieldDialog} to check layout {@link ListField}.
 * @author Jean-Charles Roger
 */
public class ListFieldDialog4 {

	public static void main(String[] args) {
		final ListField<String> typeListField = new ListField<String>("Types", BasicsUI.CHECK);
		typeListField.setValue(Arrays.asList("Type1", "Type2", "Type3"));
		
		final TextField outputFileField = new TextField("Output", BasicsUI.NONE);
		outputFileField.addAction(new Action.Stub("\u2026") {
			@Override
			public int run(ActionMonitor monitor) {
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.txt", "*.cdl", "*"} );
				String file = dialog.open();
				if ( file == null ) return STATUS_CANCEL;
				outputFileField.setValue(file);
				return STATUS_OK;
			}
		});
		
		final CompositeField compositeField = new CompositeField(typeListField, outputFileField);
		final FieldDialog dialog = new FieldDialog("OBP", "Enumerate Type", BasicsUI.NONE, compositeField);
		dialog.open();
	}

}
