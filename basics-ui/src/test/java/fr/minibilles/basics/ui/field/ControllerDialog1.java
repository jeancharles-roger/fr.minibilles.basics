package fr.minibilles.basics.ui.field;

import fr.minibilles.basics.Basics;
import fr.minibilles.basics.error.Diagnostic;
import fr.minibilles.basics.error.NumberValidator;
import fr.minibilles.basics.ui.BasicsUI;
import fr.minibilles.basics.ui.controller.Controller;
import fr.minibilles.basics.ui.dialog.FieldDialog;

public class ControllerDialog1 {

	private static class Person {
		String name;
		int age;
		boolean male;
		
		public Person(String name, int age, boolean male) {
			super();
			this.name = name;
			this.age = age;
			this.male = male;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(name);
			builder.append( " is a ");
			builder.append( male ? "Male":"Female");
			builder.append( " of ");
			builder.append(age);
			builder.append( " years old.");
			return builder.toString();
		}
		
	}
	
	private static class PersonController extends Controller<Person> {
		
		protected TextField nameField;
		protected TextField ageField;

		@Override
		public CompositeField createFields() {
			nameField = new TextField("Name", "toto", BasicsUI.NONE);
			ageField = new TextField("Age", BasicsUI.NONE);
			ageField.setValidator(new NumberValidator(Diagnostic.ERROR, "Invalid age", Basics.POSITIVE));
			return new CompositeField(nameField, ageField);
		}
		
		@Override
		public void refreshFields() {
			nameField.setValue(getSubject().name);
			ageField.setIntValue(getSubject().age);
		}
		
		@Override
		public boolean updateSubject(Field field) {
			System.out.println("Updating");
			if ( field == nameField ) {
				getSubject().name = nameField.getValue();
				return true;
			}
			if ( field == ageField ) {
				getSubject().age = ageField.getIntValue();
				return true;
			}
			
			return false;
		}
	}
	
	public static void main(String[] args) {
	
		Person me = new Person("Jean-Charles", 30, true);
		PersonController controller = new PersonController();
		controller.setSubject(me);
		
		int style = /*BasicsUI.UPDATE_ON_CLOSE*/ BasicsUI.NONE;
		FieldDialog dialog = new FieldDialog("Test", "ControllerDialog1", style, controller);
		int resultButton = dialog.open();
		
		System.out.println("Button index is " + resultButton);
		System.out.println(me);
			
	}
}
