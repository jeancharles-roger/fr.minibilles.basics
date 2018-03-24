package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.EStructuralFeature;
import ecore.EClass;
import ecore.EAttribute;


public class EReference extends EStructuralFeature {

	private boolean containment;

	private transient boolean container;

	private boolean resolveProxies = true;

	private ecore.EReference eOpposite;

	private transient EClass eReferenceType;

	/**
	 * eKeys field.
	 */
	private final List<EAttribute> eKeysList = new ArrayList<EAttribute>();

	public EReference() {
	}

	/**
	 * Gets containment.
	 */
	public boolean isContainment() {
		return containment;
	}

	/**
	 * Sets containment.
	 */
	public void setContainment(boolean newValue) {
		if (containment != newValue) {
			this.containment= newValue;
		}
	}

	/**
	 * Gets container.
	 */
	public boolean isContainer() {
		return container;
	}

	/**
	 * Sets container.
	 */
	public void setContainer(boolean newValue) {
		if (container != newValue) {
			this.container= newValue;
		}
	}

	/**
	 * Gets resolveProxies.
	 */
	public boolean isResolveProxies() {
		return resolveProxies;
	}

	/**
	 * Sets resolveProxies.
	 */
	public void setResolveProxies(boolean newValue) {
		if (resolveProxies != newValue) {
			this.resolveProxies= newValue;
		}
	}

	/**
	 * Gets eOpposite.
	 */
	public ecore.EReference getEOpposite() {
		return eOpposite;
	}

	/**
	 * Sets eOpposite.
	 */
	public void setEOpposite(ecore.EReference newValue) {
		if (eOpposite != newValue) {
			this.eOpposite= newValue;
		}
	}

	/**
	 * Gets eReferenceType.
	 */
	public EClass getEReferenceType() {
		return eReferenceType;
	}

	/**
	 * Sets eReferenceType.
	 */
	public void setEReferenceType(EClass newValue) {
		if (eReferenceType == null ? newValue != null : (eReferenceType.equals(newValue) == false)) {
			this.eReferenceType= newValue;
		}
	}

	/**
	 * Returns all values of eKeys.
	 */
	public List<EAttribute> getEKeysList() {
		return Collections.unmodifiableList(eKeysList);
	}

	/**
	 * Gets eKeys object count.
	 */
	public int getEKeysCount() {
		return eKeysList.size();
	}

	/**
	 * Gets eKeys at given index.
	 */
	public EAttribute getEKeys(int index) {
		if ( index < 0 || index >= getEKeysCount() ) { return null; }
		return eKeysList.get(index);
	}

	/**
	 * Adds an object in eKeys.
	 */
	public void addEKeys(EAttribute newValue) {
		addEKeys(getEKeysCount(), newValue);
	}

	/**
	 * Adds an object in eKeys at given index.
	 */
	public void addEKeys(int index, EAttribute newValue) {
		eKeysList.add(index, newValue);
	}

	/**
	 * Replaces an object in eKeys at given index. Returns the old value.
	 */
	public EAttribute setEKeys(int index, EAttribute newValue) {
		return eKeysList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eKeys.
	 */
	public void addAllEKeys(Collection<EAttribute> toAddList) {
		for (EAttribute newValue : toAddList) {
			addEKeys(getEKeysCount(), newValue);
		}
	}

	/**
	 * Removes given object from eKeys.
	 */
	public void removeEKeys(EAttribute value) {
		int index = eKeysList.indexOf(value);
		if (index >= 0 ) {
			removeEKeys(index);
		}
	}

	/**
	 * Removes object from eKeys at given index.
	 */
	public void removeEKeys(int index) {
		eKeysList.remove(index);
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEReference(this);
	}

}

