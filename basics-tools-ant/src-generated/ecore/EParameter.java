package ecore;

import ecore.EcoreVisitor;
import ecore.ETypedElement;
import ecore.EOperation;


public class EParameter extends ETypedElement {

	private transient EOperation eOperation;

	public EParameter() {
	}

	/**
	 * Gets eOperation.
	 */
	public EOperation getEOperation() {
		return eOperation;
	}

	/**
	 * Sets eOperation.
	 */
	public void setEOperation(EOperation newValue) {
		if (eOperation == null ? newValue != null : (eOperation.equals(newValue) == false)) {
			this.eOperation= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEParameter(this);
	}

}

