package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.ETypedElement;
import ecore.ETypeParameter;
import ecore.EParameter;
import ecore.EGenericType;
import ecore.EClassifier;
import ecore.EClass;


public class EOperation extends ETypedElement {

	private transient EClass eContainingClass;

	/**
	 * eTypeParameters field.
	 */
	private final List<ETypeParameter> eTypeParametersList = new ArrayList<ETypeParameter>();

	/**
	 * eParameters field.
	 */
	private final List<EParameter> eParametersList = new ArrayList<EParameter>();

	/**
	 * eExceptions field.
	 */
	private final List<EClassifier> eExceptionsList = new ArrayList<EClassifier>();

	/**
	 * eGenericExceptions field.
	 */
	private final List<EGenericType> eGenericExceptionsList = new ArrayList<EGenericType>();

	public EOperation() {
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

	/**
	 * Returns all values of eParameters.
	 */
	public List<EParameter> getEParametersList() {
		return Collections.unmodifiableList(eParametersList);
	}

	/**
	 * Gets eParameters object count.
	 */
	public int getEParametersCount() {
		return eParametersList.size();
	}

	/**
	 * Gets eParameters at given index.
	 */
	public EParameter getEParameters(int index) {
		if ( index < 0 || index >= getEParametersCount() ) { return null; }
		return eParametersList.get(index);
	}

	/**
	 * Adds an object in eParameters.
	 */
	public void addEParameters(EParameter newValue) {
		addEParameters(getEParametersCount(), newValue);
	}

	/**
	 * Adds an object in eParameters at given index.
	 */
	public void addEParameters(int index, EParameter newValue) {
		eParametersList.add(index, newValue);
	}

	/**
	 * Replaces an object in eParameters at given index. Returns the old value.
	 */
	public EParameter setEParameters(int index, EParameter newValue) {
		return eParametersList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eParameters.
	 */
	public void addAllEParameters(Collection<EParameter> toAddList) {
		for (EParameter newValue : toAddList) {
			addEParameters(getEParametersCount(), newValue);
		}
	}

	/**
	 * Removes given object from eParameters.
	 */
	public void removeEParameters(EParameter value) {
		int index = eParametersList.indexOf(value);
		if (index >= 0 ) {
			removeEParameters(index);
		}
	}

	/**
	 * Removes object from eParameters at given index.
	 */
	public void removeEParameters(int index) {
		eParametersList.remove(index);
	}

	/**
	 * Adds object to eParameters and sets the corresponding eOperation.
	 */
	public void addEParametersAndOpposite(EParameter newValue) {
		addEParameters(newValue);
		if ( newValue != null ) {
			newValue.setEOperation(this);
		}
	}

	/**
	 * Adds a collection of objects to eParameters and sets the corresponding eOperation.
	 */
	public void addAllEParametersAndOpposite(Collection<EParameter> toAddList) {
		for (EParameter newValue : toAddList) {
			addEParametersAndOpposite(getEParametersCount(), newValue);
		}
	}

	/**
	 * Adds object to eParameters at given index and sets the corresponding eOperation.
	 */
	public void addEParametersAndOpposite(int index, EParameter newValue) {
		addEParameters(index, newValue);
		if ( newValue != null ) {
			newValue.setEOperation(this);
		}
	}

	/**
	 * Replaces an object in eParameters at given index. Returns the old value.
	 */
	public EParameter setEParametersAndOpposite(int index, EParameter newValue) {
		EParameter oldValue = eParametersList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEOperation(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eParameters and resets the corresponding eOperation.
	 */
	public void removeEParametersAndOpposite(EParameter removed) {
		removeEParameters(removed);
		if ( removed != null ) {
			removed.setEOperation(null);
		}
	}

	/**
	 * Removes object at given index from eParameters and resets the corresponding eOperation.
	 */
	public void removeEParametersAndOpposite(int index) {
		EParameter removed = eParametersList.get(index);
		removeEParameters(index);
		if ( removed != null ) {
			removed.setEOperation(null);
		}
	}

	/**
	 * Returns all values of eExceptions.
	 */
	public List<EClassifier> getEExceptionsList() {
		return Collections.unmodifiableList(eExceptionsList);
	}

	/**
	 * Gets eExceptions object count.
	 */
	public int getEExceptionsCount() {
		return eExceptionsList.size();
	}

	/**
	 * Gets eExceptions at given index.
	 */
	public EClassifier getEExceptions(int index) {
		if ( index < 0 || index >= getEExceptionsCount() ) { return null; }
		return eExceptionsList.get(index);
	}

	/**
	 * Adds an object in eExceptions.
	 */
	public void addEExceptions(EClassifier newValue) {
		addEExceptions(getEExceptionsCount(), newValue);
	}

	/**
	 * Adds an object in eExceptions at given index.
	 */
	public void addEExceptions(int index, EClassifier newValue) {
		eExceptionsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eExceptions at given index. Returns the old value.
	 */
	public EClassifier setEExceptions(int index, EClassifier newValue) {
		return eExceptionsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eExceptions.
	 */
	public void addAllEExceptions(Collection<EClassifier> toAddList) {
		for (EClassifier newValue : toAddList) {
			addEExceptions(getEExceptionsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eExceptions.
	 */
	public void removeEExceptions(EClassifier value) {
		int index = eExceptionsList.indexOf(value);
		if (index >= 0 ) {
			removeEExceptions(index);
		}
	}

	/**
	 * Removes object from eExceptions at given index.
	 */
	public void removeEExceptions(int index) {
		eExceptionsList.remove(index);
	}

	/**
	 * Returns all values of eGenericExceptions.
	 */
	public List<EGenericType> getEGenericExceptionsList() {
		return Collections.unmodifiableList(eGenericExceptionsList);
	}

	/**
	 * Gets eGenericExceptions object count.
	 */
	public int getEGenericExceptionsCount() {
		return eGenericExceptionsList.size();
	}

	/**
	 * Gets eGenericExceptions at given index.
	 */
	public EGenericType getEGenericExceptions(int index) {
		if ( index < 0 || index >= getEGenericExceptionsCount() ) { return null; }
		return eGenericExceptionsList.get(index);
	}

	/**
	 * Adds an object in eGenericExceptions.
	 */
	public void addEGenericExceptions(EGenericType newValue) {
		addEGenericExceptions(getEGenericExceptionsCount(), newValue);
	}

	/**
	 * Adds an object in eGenericExceptions at given index.
	 */
	public void addEGenericExceptions(int index, EGenericType newValue) {
		eGenericExceptionsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eGenericExceptions at given index. Returns the old value.
	 */
	public EGenericType setEGenericExceptions(int index, EGenericType newValue) {
		return eGenericExceptionsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eGenericExceptions.
	 */
	public void addAllEGenericExceptions(Collection<EGenericType> toAddList) {
		for (EGenericType newValue : toAddList) {
			addEGenericExceptions(getEGenericExceptionsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eGenericExceptions.
	 */
	public void removeEGenericExceptions(EGenericType value) {
		int index = eGenericExceptionsList.indexOf(value);
		if (index >= 0 ) {
			removeEGenericExceptions(index);
		}
	}

	/**
	 * Removes object from eGenericExceptions at given index.
	 */
	public void removeEGenericExceptions(int index) {
		eGenericExceptionsList.remove(index);
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEOperation(this);
	}

}

