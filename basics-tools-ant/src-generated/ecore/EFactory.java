package ecore;

import ecore.EcoreVisitor;
import ecore.EPackage;
import ecore.EObject;
import ecore.EModelElement;
import ecore.EDataType;
import ecore.EClass;


public class EFactory extends EModelElement {

	private transient EPackage ePackage;

	public EFactory() {
	}

	/**
	 * Gets ePackage.
	 */
	public EPackage getEPackage() {
		return ePackage;
	}

	/**
	 * Sets ePackage.
	 */
	public void setEPackage(EPackage newValue) {
		if (ePackage == null ? newValue != null : (ePackage.equals(newValue) == false)) {
			this.ePackage= newValue;
		}
	}

	/**
	 * Sets ePackage and sets the corresponding eFactoryInstance.
	 */
	public void setEPackageAndOpposite(EPackage newValue) {
		if ( ePackage != null ) {
			ePackage.setEFactoryInstance(null);
		}
		if ( newValue != null ) {
			newValue.setEFactoryInstance(this);
		}
		setEPackage(newValue);
	}

	public EObject create(EClass eClass) {
		// TODO implement create(...)
		throw new UnsupportedOperationException();
	}

	public Object createFromString(EDataType eDataType, String literalValue) {
		// TODO implement createFromString(...)
		throw new UnsupportedOperationException();
	}

	public String convertToString(EDataType eDataType, Object instanceValue) {
		// TODO implement convertToString(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEFactory(this);
	}

}

