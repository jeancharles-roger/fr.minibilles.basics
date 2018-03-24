package ecore;

import ecore.EcoreVisitor;
import ecore.EModelElement;


public abstract class ENamedElement extends EModelElement {

	private String name;

	public ENamedElement() {
	}

	/**
	 * Gets name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 */
	public void setName(String newValue) {
		if (name == null ? newValue != null : (name.equals(newValue) == false)) {
			this.name= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public abstract void accept(EcoreVisitor visitor);
	

}

