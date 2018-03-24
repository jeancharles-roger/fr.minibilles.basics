package ecore;

import ecore.EDataType;
import ecore.EStructuralFeature;
import ecore.EcoreVisitor;


public class EAttribute extends EStructuralFeature {

	private boolean iD;

	private transient EDataType eAttributeType;

	public EAttribute() {
	}

	/**
	 * Gets iD.
	 */
	public boolean isID() {
		return iD;
	}

	/**
	 * Sets iD.
	 */
	public void setID(boolean newValue) {
		if (iD != newValue) {
			this.iD= newValue;
		}
	}

	/**
	 * Gets eAttributeType.
	 */
	public EDataType getEAttributeType() {
		return eAttributeType;
	}

	/**
	 * Sets eAttributeType.
	 */
	public void setEAttributeType(EDataType newValue) {
		if (eAttributeType == null ? newValue != null : (eAttributeType.equals(newValue) == false)) {
			this.eAttributeType= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEAttribute(this);
	}

}

