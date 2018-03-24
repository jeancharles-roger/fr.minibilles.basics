package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.EObject;
import ecore.EAnnotation;


public abstract class EModelElement extends EObject {

	/**
	 * eAnnotations field.
	 */
	private final List<EAnnotation> eAnnotationsList = new ArrayList<EAnnotation>();

	public EModelElement() {
	}

	/**
	 * Returns all values of eAnnotations.
	 */
	public List<EAnnotation> getEAnnotationsList() {
		return Collections.unmodifiableList(eAnnotationsList);
	}

	/**
	 * Gets eAnnotations object count.
	 */
	public int getEAnnotationsCount() {
		return eAnnotationsList.size();
	}

	/**
	 * Gets eAnnotations at given index.
	 */
	public EAnnotation getEAnnotations(int index) {
		if ( index < 0 || index >= getEAnnotationsCount() ) { return null; }
		return eAnnotationsList.get(index);
	}

	/**
	 * Adds an object in eAnnotations.
	 */
	public void addEAnnotations(EAnnotation newValue) {
		addEAnnotations(getEAnnotationsCount(), newValue);
	}

	/**
	 * Adds an object in eAnnotations at given index.
	 */
	public void addEAnnotations(int index, EAnnotation newValue) {
		eAnnotationsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAnnotations at given index. Returns the old value.
	 */
	public EAnnotation setEAnnotations(int index, EAnnotation newValue) {
		return eAnnotationsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAnnotations.
	 */
	public void addAllEAnnotations(Collection<EAnnotation> toAddList) {
		for (EAnnotation newValue : toAddList) {
			addEAnnotations(getEAnnotationsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAnnotations.
	 */
	public void removeEAnnotations(EAnnotation value) {
		int index = eAnnotationsList.indexOf(value);
		if (index >= 0 ) {
			removeEAnnotations(index);
		}
	}

	/**
	 * Removes object from eAnnotations at given index.
	 */
	public void removeEAnnotations(int index) {
		eAnnotationsList.remove(index);
	}

	/**
	 * Adds object to eAnnotations and sets the corresponding eModelElement.
	 */
	public void addEAnnotationsAndOpposite(EAnnotation newValue) {
		addEAnnotations(newValue);
		if ( newValue != null ) {
			newValue.setEModelElement(this);
		}
	}

	/**
	 * Adds a collection of objects to eAnnotations and sets the corresponding eModelElement.
	 */
	public void addAllEAnnotationsAndOpposite(Collection<EAnnotation> toAddList) {
		for (EAnnotation newValue : toAddList) {
			addEAnnotationsAndOpposite(getEAnnotationsCount(), newValue);
		}
	}

	/**
	 * Adds object to eAnnotations at given index and sets the corresponding eModelElement.
	 */
	public void addEAnnotationsAndOpposite(int index, EAnnotation newValue) {
		addEAnnotations(index, newValue);
		if ( newValue != null ) {
			newValue.setEModelElement(this);
		}
	}

	/**
	 * Replaces an object in eAnnotations at given index. Returns the old value.
	 */
	public EAnnotation setEAnnotationsAndOpposite(int index, EAnnotation newValue) {
		EAnnotation oldValue = eAnnotationsList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEModelElement(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eAnnotations and resets the corresponding eModelElement.
	 */
	public void removeEAnnotationsAndOpposite(EAnnotation removed) {
		removeEAnnotations(removed);
		if ( removed != null ) {
			removed.setEModelElement(null);
		}
	}

	/**
	 * Removes object at given index from eAnnotations and resets the corresponding eModelElement.
	 */
	public void removeEAnnotationsAndOpposite(int index) {
		EAnnotation removed = eAnnotationsList.get(index);
		removeEAnnotations(index);
		if ( removed != null ) {
			removed.setEModelElement(null);
		}
	}

	public EAnnotation getEAnnotation(String source) {
		// TODO implement getEAnnotation(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public abstract void accept(EcoreVisitor visitor);
	

}

