package ecore;

import ecore.EcoreVisitor;
import ecore.ENamedElement;
import ecore.EGenericType;
import ecore.EClassifier;


public abstract class ETypedElement extends ENamedElement {

	private boolean ordered = true;

	private boolean unique = true;

	private int lowerBound;

	private int upperBound = 1;

	private transient boolean many;

	private transient boolean required;

	private EClassifier eType;

	private EGenericType eGenericType;

	public ETypedElement() {
	}

	/**
	 * Gets ordered.
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * Sets ordered.
	 */
	public void setOrdered(boolean newValue) {
		if (ordered != newValue) {
			this.ordered= newValue;
		}
	}

	/**
	 * Gets unique.
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * Sets unique.
	 */
	public void setUnique(boolean newValue) {
		if (unique != newValue) {
			this.unique= newValue;
		}
	}

	/**
	 * Gets lowerBound.
	 */
	public int getLowerBound() {
		return lowerBound;
	}

	/**
	 * Sets lowerBound.
	 */
	public void setLowerBound(int newValue) {
		if (lowerBound != newValue) {
			this.lowerBound= newValue;
		}
	}

	/**
	 * Gets upperBound.
	 */
	public int getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets upperBound.
	 */
	public void setUpperBound(int newValue) {
		if (upperBound != newValue) {
			this.upperBound= newValue;
		}
	}

	/**
	 * Gets many.
	 */
	public boolean isMany() {
		return many;
	}

	/**
	 * Sets many.
	 */
	public void setMany(boolean newValue) {
		if (many != newValue) {
			this.many= newValue;
		}
	}

	/**
	 * Gets required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets required.
	 */
	public void setRequired(boolean newValue) {
		if (required != newValue) {
			this.required= newValue;
		}
	}

	/**
	 * Gets eType.
	 */
	public EClassifier getEType() {
		return eType;
	}

	/**
	 * Sets eType.
	 */
	public void setEType(EClassifier newValue) {
		if (eType == null ? newValue != null : (eType.equals(newValue) == false)) {
			this.eType= newValue;
		}
	}

	/**
	 * Gets eGenericType.
	 */
	public EGenericType getEGenericType() {
		return eGenericType;
	}

	/**
	 * Sets eGenericType.
	 */
	public void setEGenericType(EGenericType newValue) {
		if (eGenericType == null ? newValue != null : (eGenericType.equals(newValue) == false)) {
			this.eGenericType= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public abstract void accept(EcoreVisitor visitor);
	

}

