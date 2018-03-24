package fr.minibilles.basics.model;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link ModelObject} which changes are recorded.
 * @author Jean-Charles Roger
 */
public class Person implements ModelObject {

	
	private String name;
	private String phoneNumber;
	
	private Person parent;
	private List<Person> children = new ArrayList<Person>();
	
	// record changes
	private ModelChangeRecorder changeHandler = null;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		getChangeRecorder().recordChangeAttribute(this, "name", this.name);
		this.name = name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		getChangeRecorder().recordChangeAttribute(this, "phoneNumber", this.phoneNumber);
		this.phoneNumber = phoneNumber;
	}
	
	public Person getParent() {
		return parent;
	}
	
	public int getChildrenCount() {
		return children.size();
	}
	
	public List<Person> getChildrenList() {
		return Collections.unmodifiableList(children);
	}
	
	public void addChildren(Person child) {
		addChildren(children.size(), child);
	}
	
	public void addChildren(int index, Person child) {
		getChangeRecorder().recordAddObject(this, "children", index);
		child.parent = child;
		children.add(index, child);
	}
	
	public void removeChildren(Person child) {
		removeChildren(children.indexOf(child));
	}
	
	public void removeChildren(int index) {
		if ( index < 0 ) return;
		Person child = children.remove(index);
		getChangeRecorder().recordRemoveObject(this, "children", index, child);
		child.parent = null;
	}
	
	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append(name);
		if ( phoneNumber != null ) { 	
			string.append("(");
			string.append(phoneNumber);
			string.append(")");
		}
		string.append(" children ");
		string.append(children);
		return string.toString();
	}
	
	public ChangeRecorder getChangeRecorder() {
		if ( parent != null ) return parent.getChangeRecorder();
		if ( changeHandler != null ) return changeHandler;
		return ChangeRecorder.Stub;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		Person brian = createBrian();
		System.out.println("At start: " + brian);
		
		// init change handler for brian
		// all children will inherit the change handler.
		brian.changeHandler = new ModelChangeRecorder();
		
		brian.getChangeRecorder().newOperation();
		brian.setPhoneNumber("0123-45-6789");
		brian.addChildren(createPerson("Susan"));
		System.out.println("Changed: " + brian);
		
		brian.getChangeRecorder().undo();
		System.out.println("Undone: " + brian);
		
	}
	
	public static Person createPerson(String name) {
		Person person = new Person();
		person.setName(name);
		return person;
	}
	public static Person createBrian() {
		Person brian = createPerson("Brian");
		brian.setPhoneNumber("0000-00-0000");
		
		brian.addChildren(createPerson("Steve"));
		brian.addChildren(createPerson("Norah"));
		return brian;
	}

}
