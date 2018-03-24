package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.ETypeParameter;
import ecore.EPackage;
import ecore.ENamedElement;


public abstract class EClassifier extends ENamedElement {

	private String instanceClassName;

	private transient Class<?> instanceClass;

	private transient Object defaultValue;

	private String instanceTypeName;

	private transient EPackage ePackage;

	/**
	 * eTypeParameters field.
	 */
	private final List<ETypeParameter> eTypeParametersList = new ArrayList<ETypeParameter>();

	public EClassifier() {
	}

	/**
	 * Gets instanceClassName.
	 */
	public String getInstanceClassName() {
		return instanceClassName;
	}

	/**
	 * Sets instanceClassName.
	 */
	public void setInstanceClassName(String newValue) {
		if (instanceClassName == null ? newValue != null : (instanceClassName.equals(newValue) == false)) {
			this.instanceClassName= newValue;
		}
	}

	/**
	 * Gets instanceClass.
	 */
	public Class<?> getInstanceClass() {
		return instanceClass;
	}

	/**
	 * Sets instanceClass.
	 */
	public void setInstanceClass(Class<?> newValue) {
		if (instanceClass == null ? newValue != null : (instanceClass.equals(newValue) == false)) {
			this.instanceClass= newValue;
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
	 * Gets instanceTypeName.
	 */
	public String getInstanceTypeName() {
		return instanceTypeName;
	}

	/**
	 * Sets instanceTypeName.
	 */
	public void setInstanceTypeName(String newValue) {
		if (instanceTypeName == null ? newValue != null : (instanceTypeName.equals(newValue) == false)) {
			this.instanceTypeName= newValue;
		}
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
	 * Returns all values of eTypeParameters.
	 */
	public List<ETypeParameter> getETypeParametersList() {
		return Collections.unmodifiableList(eTypeParametersList);
	}

	/**
	 * Gets eTypeParameters object count.
	 */
	public int getETypeParametersCount() {
		return eTypeParametersList.size();
	}

	/**
	 * Gets eTypeParameters at given index.
	 */
	public ETypeParameter getETypeParameters(int index) {
		if ( index < 0 || index >= getETypeParametersCount() ) { return null; }
		return eTypeParametersList.get(index);
	}

	/**
	 * Adds an object in eTypeParameters.
	 */
	public void addETypeParameters(ETypeParameter newValue) {
		addETypeParameters(getETypeParametersCount(), newValue);
	}

	/**
	 * Adds an object in eTypeParameters at given index.
	 */
	public void addETypeParameters(int index, ETypeParameter newValue) {
		eTypeParametersList.add(index, newValue);
	}

	/**
	 * Replaces an object in eTypeParameters at given index. Returns the old value.
	 */
	public ETypeParameter setETypeParameters(int index, ETypeParameter newValue) {
		return eTypeParametersList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eTypeParameters.
	 */
	public void addAllETypeParameters(Collection<ETypeParameter> toAddList) {
		for (ETypeParameter newValue : toAddList) {
			addETypeParameters(getETypeParametersCount(), newValue);
		}
	}

	/**
	 * Removes given object from eTypeParameters.
	 */
	public void removeETypeParameters(ETypeParameter value) {
		int index = eTypeParametersList.indexOf(value);
		if (index >= 0 ) {
			removeETypeParameters(index);
		}
	}

	/**
	 * Removes object from eTypeParameters at given index.
	 */
	public void removeETypeParameters(int index) {
		eTypeParametersList.remove(index);
	}

	public boolean isInstance(Object object) {
		// TODO implement isInstance(...)
		throw new UnsupportedOperationException();
	}

	public int getClassifierID() {
		// TODO implement getClassifierID(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public abstract void accept(EcoreVisitor visitor);
	

}

