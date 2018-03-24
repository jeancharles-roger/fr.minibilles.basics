package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.ENamedElement;
import ecore.EFactory;
import ecore.EClassifier;


public class EPackage extends ENamedElement {

	private String nsURI;

	private String nsPrefix;

	private transient EFactory eFactoryInstance;

	/**
	 * eClassifiers field.
	 */
	private final List<EClassifier> eClassifiersList = new ArrayList<EClassifier>();

	/**
	 * eSubpackages field.
	 */
	private final List<ecore.EPackage> eSubpackagesList = new ArrayList<ecore.EPackage>();

	private transient ecore.EPackage eSuperPackage;

	public EPackage() {
	}

	/**
	 * Gets nsURI.
	 */
	public String getNsURI() {
		return nsURI;
	}

	/**
	 * Sets nsURI.
	 */
	public void setNsURI(String newValue) {
		if (nsURI == null ? newValue != null : (nsURI.equals(newValue) == false)) {
			this.nsURI= newValue;
		}
	}

	/**
	 * Gets nsPrefix.
	 */
	public String getNsPrefix() {
		return nsPrefix;
	}

	/**
	 * Sets nsPrefix.
	 */
	public void setNsPrefix(String newValue) {
		if (nsPrefix == null ? newValue != null : (nsPrefix.equals(newValue) == false)) {
			this.nsPrefix= newValue;
		}
	}

	/**
	 * Gets eFactoryInstance.
	 */
	public EFactory getEFactoryInstance() {
		return eFactoryInstance;
	}

	/**
	 * Sets eFactoryInstance.
	 */
	public void setEFactoryInstance(EFactory newValue) {
		if (eFactoryInstance == null ? newValue != null : (eFactoryInstance.equals(newValue) == false)) {
			this.eFactoryInstance= newValue;
		}
	}

	/**
	 * Sets eFactoryInstance and sets the corresponding ePackage.
	 */
	public void setEFactoryInstanceAndOpposite(EFactory newValue) {
		if ( eFactoryInstance != null ) {
			eFactoryInstance.setEPackage(null);
		}
		if ( newValue != null ) {
			newValue.setEPackage(this);
		}
		setEFactoryInstance(newValue);
	}

	/**
	 * Returns all values of eClassifiers.
	 */
	public List<EClassifier> getEClassifiersList() {
		return Collections.unmodifiableList(eClassifiersList);
	}

	/**
	 * Gets eClassifiers object count.
	 */
	public int getEClassifiersCount() {
		return eClassifiersList.size();
	}

	/**
	 * Gets eClassifiers at given index.
	 */
	public EClassifier getEClassifiers(int index) {
		if ( index < 0 || index >= getEClassifiersCount() ) { return null; }
		return eClassifiersList.get(index);
	}

	/**
	 * Adds an object in eClassifiers.
	 */
	public void addEClassifiers(EClassifier newValue) {
		addEClassifiers(getEClassifiersCount(), newValue);
	}

	/**
	 * Adds an object in eClassifiers at given index.
	 */
	public void addEClassifiers(int index, EClassifier newValue) {
		eClassifiersList.add(index, newValue);
	}

	/**
	 * Replaces an object in eClassifiers at given index. Returns the old value.
	 */
	public EClassifier setEClassifiers(int index, EClassifier newValue) {
		return eClassifiersList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eClassifiers.
	 */
	public void addAllEClassifiers(Collection<EClassifier> toAddList) {
		for (EClassifier newValue : toAddList) {
			addEClassifiers(getEClassifiersCount(), newValue);
		}
	}

	/**
	 * Removes given object from eClassifiers.
	 */
	public void removeEClassifiers(EClassifier value) {
		int index = eClassifiersList.indexOf(value);
		if (index >= 0 ) {
			removeEClassifiers(index);
		}
	}

	/**
	 * Removes object from eClassifiers at given index.
	 */
	public void removeEClassifiers(int index) {
		eClassifiersList.remove(index);
	}

	/**
	 * Adds object to eClassifiers and sets the corresponding ePackage.
	 */
	public void addEClassifiersAndOpposite(EClassifier newValue) {
		addEClassifiers(newValue);
		if ( newValue != null ) {
			newValue.setEPackage(this);
		}
	}

	/**
	 * Adds a collection of objects to eClassifiers and sets the corresponding ePackage.
	 */
	public void addAllEClassifiersAndOpposite(Collection<EClassifier> toAddList) {
		for (EClassifier newValue : toAddList) {
			addEClassifiersAndOpposite(getEClassifiersCount(), newValue);
		}
	}

	/**
	 * Adds object to eClassifiers at given index and sets the corresponding ePackage.
	 */
	public void addEClassifiersAndOpposite(int index, EClassifier newValue) {
		addEClassifiers(index, newValue);
		if ( newValue != null ) {
			newValue.setEPackage(this);
		}
	}

	/**
	 * Replaces an object in eClassifiers at given index. Returns the old value.
	 */
	public EClassifier setEClassifiersAndOpposite(int index, EClassifier newValue) {
		EClassifier oldValue = eClassifiersList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEPackage(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eClassifiers and resets the corresponding ePackage.
	 */
	public void removeEClassifiersAndOpposite(EClassifier removed) {
		removeEClassifiers(removed);
		if ( removed != null ) {
			removed.setEPackage(null);
		}
	}

	/**
	 * Removes object at given index from eClassifiers and resets the corresponding ePackage.
	 */
	public void removeEClassifiersAndOpposite(int index) {
		EClassifier removed = eClassifiersList.get(index);
		removeEClassifiers(index);
		if ( removed != null ) {
			removed.setEPackage(null);
		}
	}

	/**
	 * Returns all values of eSubpackages.
	 */
	public List<ecore.EPackage> getESubpackagesList() {
		return Collections.unmodifiableList(eSubpackagesList);
	}

	/**
	 * Gets eSubpackages object count.
	 */
	public int getESubpackagesCount() {
		return eSubpackagesList.size();
	}

	/**
	 * Gets eSubpackages at given index.
	 */
	public ecore.EPackage getESubpackages(int index) {
		if ( index < 0 || index >= getESubpackagesCount() ) { return null; }
		return eSubpackagesList.get(index);
	}

	/**
	 * Adds an object in eSubpackages.
	 */
	public void addESubpackages(ecore.EPackage newValue) {
		addESubpackages(getESubpackagesCount(), newValue);
	}

	/**
	 * Adds an object in eSubpackages at given index.
	 */
	public void addESubpackages(int index, ecore.EPackage newValue) {
		eSubpackagesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eSubpackages at given index. Returns the old value.
	 */
	public ecore.EPackage setESubpackages(int index, ecore.EPackage newValue) {
		return eSubpackagesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eSubpackages.
	 */
	public void addAllESubpackages(Collection<ecore.EPackage> toAddList) {
		for (ecore.EPackage newValue : toAddList) {
			addESubpackages(getESubpackagesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eSubpackages.
	 */
	public void removeESubpackages(ecore.EPackage value) {
		int index = eSubpackagesList.indexOf(value);
		if (index >= 0 ) {
			removeESubpackages(index);
		}
	}

	/**
	 * Removes object from eSubpackages at given index.
	 */
	public void removeESubpackages(int index) {
		eSubpackagesList.remove(index);
	}

	/**
	 * Adds object to eSubpackages and sets the corresponding eSuperPackage.
	 */
	public void addESubpackagesAndOpposite(ecore.EPackage newValue) {
		addESubpackages(newValue);
		if ( newValue != null ) {
			newValue.setESuperPackage(this);
		}
	}

	/**
	 * Adds a collection of objects to eSubpackages and sets the corresponding eSuperPackage.
	 */
	public void addAllESubpackagesAndOpposite(Collection<ecore.EPackage> toAddList) {
		for (ecore.EPackage newValue : toAddList) {
			addESubpackagesAndOpposite(getESubpackagesCount(), newValue);
		}
	}

	/**
	 * Adds object to eSubpackages at given index and sets the corresponding eSuperPackage.
	 */
	public void addESubpackagesAndOpposite(int index, ecore.EPackage newValue) {
		addESubpackages(index, newValue);
		if ( newValue != null ) {
			newValue.setESuperPackage(this);
		}
	}

	/**
	 * Replaces an object in eSubpackages at given index. Returns the old value.
	 */
	public ecore.EPackage setESubpackagesAndOpposite(int index, ecore.EPackage newValue) {
		ecore.EPackage oldValue = eSubpackagesList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setESuperPackage(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eSubpackages and resets the corresponding eSuperPackage.
	 */
	public void removeESubpackagesAndOpposite(ecore.EPackage removed) {
		removeESubpackages(removed);
		if ( removed != null ) {
			removed.setESuperPackage(null);
		}
	}

	/**
	 * Removes object at given index from eSubpackages and resets the corresponding eSuperPackage.
	 */
	public void removeESubpackagesAndOpposite(int index) {
		ecore.EPackage removed = eSubpackagesList.get(index);
		removeESubpackages(index);
		if ( removed != null ) {
			removed.setESuperPackage(null);
		}
	}

	/**
	 * Gets eSuperPackage.
	 */
	public ecore.EPackage getESuperPackage() {
		return eSuperPackage;
	}

	/**
	 * Sets eSuperPackage.
	 */
	public void setESuperPackage(ecore.EPackage newValue) {
		if (eSuperPackage != newValue) {
			this.eSuperPackage= newValue;
		}
	}

	public EClassifier getEClassifier(String name) {
		// TODO implement getEClassifier(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEPackage(this);
	}

}

