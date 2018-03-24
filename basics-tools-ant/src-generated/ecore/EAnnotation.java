package ecore;

import ecore.EModelElement;
import ecore.EObject;
import ecore.EcoreVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class EAnnotation extends EModelElement {

	private String source;

	private transient EModelElement eModelElement;

	/**
	 * contents field.
	 */
	private final List<EObject> contentsList = new ArrayList<EObject>();

	/**
	 * references field.
	 */
	private final List<EObject> referencesList = new ArrayList<EObject>();

	public EAnnotation() {
	}

	/**
	 * Gets source.
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Sets source.
	 */
	public void setSource(String newValue) {
		if (source == null ? newValue != null : (source.equals(newValue) == false)) {
			this.source= newValue;
		}
	}

	/**
	 * Gets eModelElement.
	 */
	public EModelElement getEModelElement() {
		return eModelElement;
	}

	/**
	 * Sets eModelElement.
	 */
	public void setEModelElement(EModelElement newValue) {
		if (eModelElement == null ? newValue != null : (eModelElement.equals(newValue) == false)) {
			this.eModelElement= newValue;
		}
	}

	/**
	 * Returns all values of contents.
	 */
	public List<EObject> getContentsList() {
		return Collections.unmodifiableList(contentsList);
	}

	/**
	 * Gets contents object count.
	 */
	public int getContentsCount() {
		return contentsList.size();
	}

	/**
	 * Gets contents at given index.
	 */
	public EObject getContents(int index) {
		if ( index < 0 || index >= getContentsCount() ) { return null; }
		return contentsList.get(index);
	}

	/**
	 * Adds an object in contents.
	 */
	public void addContents(EObject newValue) {
		addContents(getContentsCount(), newValue);
	}

	/**
	 * Adds an object in contents at given index.
	 */
	public void addContents(int index, EObject newValue) {
		contentsList.add(index, newValue);
	}

	/**
	 * Replaces an object in contents at given index. Returns the old value.
	 */
	public EObject setContents(int index, EObject newValue) {
		return contentsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in contents.
	 */
	public void addAllContents(Collection<EObject> toAddList) {
		for (EObject newValue : toAddList) {
			addContents(getContentsCount(), newValue);
		}
	}

	/**
	 * Removes given object from contents.
	 */
	public void removeContents(EObject value) {
		int index = contentsList.indexOf(value);
		if (index >= 0 ) {
			removeContents(index);
		}
	}

	/**
	 * Removes object from contents at given index.
	 */
	public void removeContents(int index) {
		contentsList.remove(index);
	}

	/**
	 * Returns all values of references.
	 */
	public List<EObject> getReferencesList() {
		return Collections.unmodifiableList(referencesList);
	}

	/**
	 * Gets references object count.
	 */
	public int getReferencesCount() {
		return referencesList.size();
	}

	/**
	 * Gets references at given index.
	 */
	public EObject getReferences(int index) {
		if ( index < 0 || index >= getReferencesCount() ) { return null; }
		return referencesList.get(index);
	}

	/**
	 * Adds an object in references.
	 */
	public void addReferences(EObject newValue) {
		addReferences(getReferencesCount(), newValue);
	}

	/**
	 * Adds an object in references at given index.
	 */
	public void addReferences(int index, EObject newValue) {
		referencesList.add(index, newValue);
	}

	/**
	 * Replaces an object in references at given index. Returns the old value.
	 */
	public EObject setReferences(int index, EObject newValue) {
		return referencesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in references.
	 */
	public void addAllReferences(Collection<EObject> toAddList) {
		for (EObject newValue : toAddList) {
			addReferences(getReferencesCount(), newValue);
		}
	}

	/**
	 * Removes given object from references.
	 */
	public void removeReferences(EObject value) {
		int index = referencesList.indexOf(value);
		if (index >= 0 ) {
			removeReferences(index);
		}
	}

	/**
	 * Removes object from references at given index.
	 */
	public void removeReferences(int index) {
		referencesList.remove(index);
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEAnnotation(this);
	}

}

