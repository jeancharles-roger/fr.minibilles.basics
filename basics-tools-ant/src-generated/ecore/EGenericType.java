package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.ETypeParameter;
import ecore.EObject;
import ecore.EClassifier;


public class EGenericType extends EObject {

	private ecore.EGenericType eUpperBound;

	/**
	 * eTypeArguments field.
	 */
	private final List<ecore.EGenericType> eTypeArgumentsList = new ArrayList<ecore.EGenericType>();

	private transient EClassifier eRawType;

	private ecore.EGenericType eLowerBound;

	private ETypeParameter eTypeParameter;

	private EClassifier eClassifier;

	public EGenericType() {
	}

	/**
	 * Gets eUpperBound.
	 */
	public ecore.EGenericType getEUpperBound() {
		return eUpperBound;
	}

	/**
	 * Sets eUpperBound.
	 */
	public void setEUpperBound(ecore.EGenericType newValue) {
		if (eUpperBound != newValue) {
			this.eUpperBound= newValue;
		}
	}

	/**
	 * Returns all values of eTypeArguments.
	 */
	public List<ecore.EGenericType> getETypeArgumentsList() {
		return Collections.unmodifiableList(eTypeArgumentsList);
	}

	/**
	 * Gets eTypeArguments object count.
	 */
	public int getETypeArgumentsCount() {
		return eTypeArgumentsList.size();
	}

	/**
	 * Gets eTypeArguments at given index.
	 */
	public ecore.EGenericType getETypeArguments(int index) {
		if ( index < 0 || index >= getETypeArgumentsCount() ) { return null; }
		return eTypeArgumentsList.get(index);
	}

	/**
	 * Adds an object in eTypeArguments.
	 */
	public void addETypeArguments(ecore.EGenericType newValue) {
		addETypeArguments(getETypeArgumentsCount(), newValue);
	}

	/**
	 * Adds an object in eTypeArguments at given index.
	 */
	public void addETypeArguments(int index, ecore.EGenericType newValue) {
		eTypeArgumentsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eTypeArguments at given index. Returns the old value.
	 */
	public ecore.EGenericType setETypeArguments(int index, ecore.EGenericType newValue) {
		return eTypeArgumentsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eTypeArguments.
	 */
	public void addAllETypeArguments(Collection<ecore.EGenericType> toAddList) {
		for (ecore.EGenericType newValue : toAddList) {
			addETypeArguments(getETypeArgumentsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eTypeArguments.
	 */
	public void removeETypeArguments(ecore.EGenericType value) {
		int index = eTypeArgumentsList.indexOf(value);
		if (index >= 0 ) {
			removeETypeArguments(index);
		}
	}

	/**
	 * Removes object from eTypeArguments at given index.
	 */
	public void removeETypeArguments(int index) {
		eTypeArgumentsList.remove(index);
	}

	/**
	 * Gets eRawType.
	 */
	public EClassifier getERawType() {
		return eRawType;
	}

	/**
	 * Sets eRawType.
	 */
	public void setERawType(EClassifier newValue) {
		if (eRawType == null ? newValue != null : (eRawType.equals(newValue) == false)) {
			this.eRawType= newValue;
		}
	}

	/**
	 * Gets eLowerBound.
	 */
	public ecore.EGenericType getELowerBound() {
		return eLowerBound;
	}

	/**
	 * Sets eLowerBound.
	 */
	public void setELowerBound(ecore.EGenericType newValue) {
		if (eLowerBound != newValue) {
			this.eLowerBound= newValue;
		}
	}

	/**
	 * Gets eTypeParameter.
	 */
	public ETypeParameter getETypeParameter() {
		return eTypeParameter;
	}

	/**
	 * Sets eTypeParameter.
	 */
	public void setETypeParameter(ETypeParameter newValue) {
		if (eTypeParameter == null ? newValue != null : (eTypeParameter.equals(newValue) == false)) {
			this.eTypeParameter= newValue;
		}
	}

	/**
	 * Gets eClassifier.
	 */
	public EClassifier getEClassifier() {
		return eClassifier;
	}

	/**
	 * Sets eClassifier.
	 */
	public void setEClassifier(EClassifier newValue) {
		if (eClassifier == null ? newValue != null : (eClassifier.equals(newValue) == false)) {
			this.eClassifier= newValue;
		}
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEGenericType(this);
	}

}

