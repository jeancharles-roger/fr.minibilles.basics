package ecore;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.EList;
import ecore.EcoreVisitor;
import ecore.EStructuralFeature;
import ecore.EReference;
import ecore.EClass;


public class EObject {

	public EObject() {
	}

	public EClass eClass() {
		// TODO implement eClass(...)
		throw new UnsupportedOperationException();
	}

	public boolean eIsProxy() {
		// TODO implement eIsProxy(...)
		throw new UnsupportedOperationException();
	}

	public Resource eResource() {
		// TODO implement eResource(...)
		throw new UnsupportedOperationException();
	}

	public ecore.EObject eContainer() {
		// TODO implement eContainer(...)
		throw new UnsupportedOperationException();
	}

	public EStructuralFeature eContainingFeature() {
		// TODO implement eContainingFeature(...)
		throw new UnsupportedOperationException();
	}

	public EReference eContainmentFeature() {
		// TODO implement eContainmentFeature(...)
		throw new UnsupportedOperationException();
	}

	public EList<ecore.EObject> eContents() {
		// TODO implement eContents(...)
		throw new UnsupportedOperationException();
	}

	public TreeIterator<ecore.EObject> eAllContents() {
		// TODO implement eAllContents(...)
		throw new UnsupportedOperationException();
	}

	public EList<ecore.EObject> eCrossReferences() {
		// TODO implement eCrossReferences(...)
		throw new UnsupportedOperationException();
	}

	public Object eGet(EStructuralFeature feature) {
		// TODO implement eGet(...)
		throw new UnsupportedOperationException();
	}

	public Object eGet(EStructuralFeature feature, boolean resolve) {
		// TODO implement eGet(...)
		throw new UnsupportedOperationException();
	}

	public void eSet(EStructuralFeature feature, Object newValue) {
		// TODO implement eSet(...)
		throw new UnsupportedOperationException();
	}

	public boolean eIsSet(EStructuralFeature feature) {
		// TODO implement eIsSet(...)
		throw new UnsupportedOperationException();
	}

	public void eUnset(EStructuralFeature feature) {
		// TODO implement eUnset(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEObject(this);
	}

}

