package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import java.util.Arrays;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example to check the scroll for inner page.
 * @author Jean-Charles Roger
 */
public class MultiPageFieldDialog2 {

	
	
	public static void main(String[] args) {
		Display display = new Display();

		TextField textField = new TextField(null, BasicsUI.NO_INFO);
		ListField<String> listField = new ListField<String>(null, BasicsUI.NO_INFO);
		
		final CheckboxField choosePageField = new CheckboxField("Show text", false, BasicsUI.NO_INFO);
		final MultiPageField mainField = new MultiPageField(listField, textField);
		choosePageField.addListener(new NotificationListener() {
			public void notified(Notification notification) {
				if ( notification.name.equals(BasicsUI.NOTIFICATION_VALUE)) {
					mainField.setSelected(choosePageField.getValue() ? 1 : 0);
				}
			}
		});
		
		Shell shell = new Shell(display);
		FieldShellToolkit toolkit = new FieldShellToolkit(shell, "Scroll Test", BasicsUI.NONE, new CompositeField(choosePageField, mainField));
		toolkit.init();

		textField.setValue("a\nb\nc\nd\ne\nf\ng\nh\ni\nj\nk\nl\nm\nn\no\np\nq\nr\ns\nt\nu\nv\nw\nx\ny\nz");
		listField.setValue(Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15", "16","17","18","19","20","21","22","23","24","25","26"));

		shell.setSize(150, 250);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}

}
