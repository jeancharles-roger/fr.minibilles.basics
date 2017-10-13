package fr.minibilles.basics.ui;

import fr.minibilles.basics.notification.Notification;
import fr.minibilles.basics.notification.NotificationListener;
import fr.minibilles.basics.progress.ActionMonitor;
import fr.minibilles.basics.ui.action.Action;
import fr.minibilles.basics.ui.action.KeyCode;
import fr.minibilles.basics.ui.dialog.FieldShellToolkit;
import fr.minibilles.basics.ui.field.ChoiceField;
import fr.minibilles.basics.ui.field.CompositeField;
import fr.minibilles.basics.ui.field.ListField;
import fr.minibilles.basics.ui.field.MultiTabField;
import fr.minibilles.basics.ui.field.TextField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SmallApplication {

	private Shell shell;
	private FieldShellToolkit toolkit;
	private MultiTabField mainField;
	
	private void createFields() {
		mainField = new MultiTabField(null, BasicsUI.NONE);
		mainField.addTab(getField1(), null, true);
		mainField.addTab(getField2(), null, true);

		mainField.addStructureListener(new NotificationListener() {
			
			public void notified(Notification notification) {
				toolkit.getActionManager().refreshMenuBar(shell);
				
				if ( "close".equals(notification.name) ) {
					CompositeField field = (CompositeField) notification.newValue;
					System.out.println("Closed " + field.getLabel());
				}
			}
		});
	}
	
	private Action createEditMenuAction() {
		return new Action.Container("Edit",
					createCopyAction(),
					createPasteAction()
				);
	}
	
	private Action createCopyAction() {
		KeyCode code = new KeyCode(KeyCode.MOD1, 'c');
		return new Action.Stub("Copy", code) {
			
			@Override
			public int getVisibility() {
				return mainField.getSelected() == 1 ? VISIBILITY_ENABLE : VISIBILITY_DISABLE;
			}
			
			@Override
			public int run(ActionMonitor monitor) {
				System.out.println("Copy");
				return STATUS_OK;
			}
		};
	}
	
	private Action createPasteAction() {
		KeyCode code = new KeyCode(KeyCode.MOD1, 'v');
		return new Action.Stub("Paste", code) {
			@Override
			public int run(ActionMonitor monitor) {
				System.out.println("Paste");
				return STATUS_OK;
			}
		};
	}
	
	private Action createViewMenuAction() {
		return new Action.Container("View",
					createViewAction(1),
					createViewAction(2),
					createViewAction(3),
					createSeparatorAction("View 1x"),
					createViewAction(10),
					createViewAction(11),
					createViewAction(12),
					createViewAction(13)
				);
	}
	
	private Action createSeparatorAction(String label) {
		return new Action.Stub(label, Action.STYLE_MENU | Action.STYLE_SEPARATOR);
	}
	
	private Action createViewAction(final int  i) {
		return new Action.Stub("View"+i) {
			@Override
			public int run(ActionMonitor monitor) {
				System.out.println("View"+i);
				return STATUS_OK;
			}
		};
	}
	

	
	public void open(Display display) {
		
		// initializes application
		createFields();
		shell = FieldShellToolkit.createShell(display, "SmallApplication");
		toolkit = new FieldShellToolkit(shell, "SmallApplication", BasicsUI.NONE, mainField);
		toolkit.setMenuActions(createEditMenuAction(), createViewMenuAction());
		toolkit.init();
		
		// opens shell
		shell.open();
		
		// main loop
		while ( shell.isDisposed() == false ) {
			if ( display.readAndDispatch() == false ) {
				display.sleep();
			}
		}
	}
	
	

	private CompositeField getField1() {
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

	private CompositeField getField2() {
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

	public static void main(String[] args) {
		Display display = Display.getDefault();
		SmallApplication application = new SmallApplication();
		application.open(display);
	}
}
