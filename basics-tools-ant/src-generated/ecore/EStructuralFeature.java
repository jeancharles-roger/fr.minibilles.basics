package ecore;

import ecore.EcoreVisitor;
import ecore.ETypedElement;
import ecore.EClass;


public abstract class EStructuralFeature extends ETypedElement {

	private boolean changeable = true;

	private boolean volatile_;

	private boolean transient_;

	private String defaultValueLiteral;

	private transient Object defaultValue;

	private boolean unsettable;

	private boolean derived;

	private transient EClass eContainingClass;

	public EStructuralFeature() {
	}

	/**
	 * Gets changeable.
	 */
	public boolean isChangeable() {
		return changeable;
	}

	/**
	 * Sets changeable.
	 */
	public void setChangeable(boolean newValue) {
		if (changeable != newValue) {
			this.changeable= newValue;
		}
	}

	/**
	 * Gets volatile.
	 */
	public boolean isVolatile() {
		return volatile_;
	}

	/**
	 * Sets volatile.
	 */
	public void setVolatile(boolean newValue) {
		if (volatile_ != newValue) {
			this.volatile_= newValue;
		}
	}

	/**
	 * Gets transient.
	 */
	public boolean isTransient() {
		return transient_;
	}

	/**
	 * Sets transient.
	 */
	public void setTransient(boolean newValue) {
		if (transient_ != newValue) {
			this.transient_= newValue;
		}
	}

	/**
	 * Gets defaultValueLiteral.
	 */
	public String getDefaultValueLiteral() {
		return defaultValueLiteral;
	}

	/**
	 * Sets defaultValueLiteral.
	 */
	public void setDefaultValueLiteral(String newValue) {
		if (defaultValueLiteral == null ? newValue != null : (defaultValueLiteral.equals(newValue) == false)) {
			this.defaultValueLiteral= newValue;
		}
	}

	/**
	 * Gets defaultValue.
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets defaultValue.
	 */
	public void setDefaultValue(Object newValue) {
		if (defaultValue == null ? newValue != null : (defaultValue.equals(newValue) == false)) {
			this.defaultValue= newValue;
		}
	}

	/**
	 * Gets unsettable.
	 */
	public boolean isUnsettable() {
		return unsettable;
	}

	/**
	 * Sets unsettable.
	 */
	public void setUnsettable(boolean newValue) {
		if (unsettable != newValue) {
			this.unsettable= newValue;
		}
	}

	/**
	 * Gets derived.
	 */
	public boolean isDerived() {
		return derived;
	}

	/**
	 * Sets derived.
	 */
	public void setDerived(boolean newValue) {
		if (derived != newValue) {
			this.derived= newValue;
		}
	}

	/**
	 * Gets eContainingClass.
	 */
	public EClass getEContainingClass() {
		return eContainingClass;
	}

	/**
	 * Sets eContainingClass.
	 */
	public void setEContainingClass(EClass newValue) {
		if (eContainingClass == null ? newValue != null : (eContainingClass.equals(newValue) == false)) {
			this.eContainingClass= newValue;
		}
	}

	public int getFeatureID() {
		// TODO implement getFeatureID(...)
		throw new UnsupportedOperationException();
	}

	public Class<?> getContainerClass() {
		// TODO implement getContainerClass(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public abstract void accept(EcoreVisitor visitor);
	

}

