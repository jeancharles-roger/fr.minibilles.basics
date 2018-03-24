package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.ENamedElement;
import ecore.EGenericType;


public class ETypeParameter extends ENamedElement {

	/**
	 * eBounds field.
	 */
	private final List<EGenericType> eBoundsList = new ArrayList<EGenericType>();

	public ETypeParameter() {
	}

	/**
	 * Returns all values of eBounds.
	 */
	public List<EGenericType> getEBoundsList() {
		return Collections.unmodifiableList(eBoundsList);
	}

	/**
	 * Gets eBounds object count.
	 */
	public int getEBoundsCount() {
		return eBoundsList.size();
	}

	/**
	 * Gets eBounds at given index.
	 */
	public EGenericType getEBounds(int index) {
		if ( index < 0 || index >= getEBoundsCount() ) { return null; }
		return eBoundsList.get(index);
	}

	/**
	 * Adds an object in eBounds.
	 */
	public void addEBounds(EGenericType newValue) {
		addEBounds(getEBoundsCount(), newValue);
	}

	/**
	 * Adds an object in eBounds at given index.
	 */
	public void addEBounds(int index, EGenericType newValue) {
		eBoundsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eBounds at given index. Returns the old value.
	 */
	public EGenericType setEBounds(int index, EGenericType newValue) {
		return eBoundsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eBounds.
	 */
	public void addAllEBounds(Collection<EGenericType> toAddList) {
		for (EGenericType newValue : toAddList) {
			addEBounds(getEBoundsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eBounds.
	 */
	public void removeEBounds(EGenericType value) {
		int index = eBoundsList.indexOf(value);
		if (index >= 0 ) {
			removeEBounds(index);
		}
	}

	/**
	 * Removes object from eBounds at given index.
	 */
	public void removeEBounds(int index) {
		eBoundsList.remove(index);
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitETypeParameter(this);
	}

}

