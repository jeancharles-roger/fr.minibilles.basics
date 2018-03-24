package ecore;

import ecore.EcoreVisitor;
import ecore.EClassifier;


public class EDataType extends EClassifier {

	private boolean serializable = true;

	public EDataType() {
	}

	/**
	 * Gets serializable.
	 */
	public boolean isSerializable() {
		return serializable;
	}

	/**
	 * Sets serializable.
	 */
	public void setSerializable(boolean newValue) {
		if (serializable != newValue) {
			this.serializable= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEDataType(this);
	}

}

