package ecore;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import ecore.EcoreVisitor;
import ecore.EStructuralFeature;
import ecore.EReference;
import ecore.EOperation;
import ecore.EGenericType;
import ecore.EClassifier;
import ecore.EAttribute;


public class EClass extends EClassifier {

	private boolean abstract_;

	private boolean interface_;

	/**
	 * eSuperTypes field.
	 */
	private final List<ecore.EClass> eSuperTypesList = new ArrayList<ecore.EClass>();

	/**
	 * eOperations field.
	 */
	private final List<EOperation> eOperationsList = new ArrayList<EOperation>();

	/**
	 * eAllAttributes field.
	 */
	private final transient List<EAttribute> eAllAttributesList = new ArrayList<EAttribute>();

	/**
	 * eAllReferences field.
	 */
	private final transient List<EReference> eAllReferencesList = new ArrayList<EReference>();

	/**
	 * eReferences field.
	 */
	private final transient List<EReference> eReferencesList = new ArrayList<EReference>();

	/**
	 * eAttributes field.
	 */
	private final transient List<EAttribute> eAttributesList = new ArrayList<EAttribute>();

	/**
	 * eAllContainments field.
	 */
	private final transient List<EReference> eAllContainmentsList = new ArrayList<EReference>();

	/**
	 * eAllOperations field.
	 */
	private final transient List<EOperation> eAllOperationsList = new ArrayList<EOperation>();

	/**
	 * eAllStructuralFeatures field.
	 */
	private final transient List<EStructuralFeature> eAllStructuralFeaturesList = new ArrayList<EStructuralFeature>();

	/**
	 * eAllSuperTypes field.
	 */
	private final transient List<ecore.EClass> eAllSuperTypesList = new ArrayList<ecore.EClass>();

	private transient EAttribute eIDAttribute;

	/**
	 * eStructuralFeatures field.
	 */
	private final List<EStructuralFeature> eStructuralFeaturesList = new ArrayList<EStructuralFeature>();

	/**
	 * eGenericSuperTypes field.
	 */
	private final List<EGenericType> eGenericSuperTypesList = new ArrayList<EGenericType>();

	/**
	 * eAllGenericSuperTypes field.
	 */
	private final transient List<EGenericType> eAllGenericSuperTypesList = new ArrayList<EGenericType>();

	private transient EAttribute eIDAttributeForTest;

	/**
	 * eAllAttributesForTest field.
	 */
	private final transient List<EAttribute> eAllAttributesForTestList = new ArrayList<EAttribute>();

	public EClass() {
	}

	/**
	 * Gets abstract.
	 */
	public boolean isAbstract() {
		return abstract_;
	}

	/**
	 * Sets abstract.
	 */
	public void setAbstract(boolean newValue) {
		if (abstract_ != newValue) {
			this.abstract_= newValue;
		}
	}

	/**
	 * Gets interface.
	 */
	public boolean isInterface() {
		return interface_;
	}

	/**
	 * Sets interface.
	 */
	public void setInterface(boolean newValue) {
		if (interface_ != newValue) {
			this.interface_= newValue;
		}
	}

	/**
	 * Returns all values of eSuperTypes.
	 */
	public List<ecore.EClass> getESuperTypesList() {
		return Collections.unmodifiableList(eSuperTypesList);
	}

	/**
	 * Gets eSuperTypes object count.
	 */
	public int getESuperTypesCount() {
		return eSuperTypesList.size();
	}

	/**
	 * Gets eSuperTypes at given index.
	 */
	public ecore.EClass getESuperTypes(int index) {
		if ( index < 0 || index >= getESuperTypesCount() ) { return null; }
		return eSuperTypesList.get(index);
	}

	/**
	 * Adds an object in eSuperTypes.
	 */
	public void addESuperTypes(ecore.EClass newValue) {
		addESuperTypes(getESuperTypesCount(), newValue);
	}

	/**
	 * Adds an object in eSuperTypes at given index.
	 */
	public void addESuperTypes(int index, ecore.EClass newValue) {
		eSuperTypesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eSuperTypes at given index. Returns the old value.
	 */
	public ecore.EClass setESuperTypes(int index, ecore.EClass newValue) {
		return eSuperTypesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eSuperTypes.
	 */
	public void addAllESuperTypes(Collection<ecore.EClass> toAddList) {
		for (ecore.EClass newValue : toAddList) {
			addESuperTypes(getESuperTypesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eSuperTypes.
	 */
	public void removeESuperTypes(ecore.EClass value) {
		int index = eSuperTypesList.indexOf(value);
		if (index >= 0 ) {
			removeESuperTypes(index);
		}
	}

	/**
	 * Removes object from eSuperTypes at given index.
	 */
	public void removeESuperTypes(int index) {
		eSuperTypesList.remove(index);
	}

	/**
	 * Returns all values of eOperations.
	 */
	public List<EOperation> getEOperationsList() {
		return Collections.unmodifiableList(eOperationsList);
	}

	/**
	 * Gets eOperations object count.
	 */
	public int getEOperationsCount() {
		return eOperationsList.size();
	}

	/**
	 * Gets eOperations at given index.
	 */
	public EOperation getEOperations(int index) {
		if ( index < 0 || index >= getEOperationsCount() ) { return null; }
		return eOperationsList.get(index);
	}

	/**
	 * Adds an object in eOperations.
	 */
	public void addEOperations(EOperation newValue) {
		addEOperations(getEOperationsCount(), newValue);
	}

	/**
	 * Adds an object in eOperations at given index.
	 */
	public void addEOperations(int index, EOperation newValue) {
		eOperationsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eOperations at given index. Returns the old value.
	 */
	public EOperation setEOperations(int index, EOperation newValue) {
		return eOperationsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eOperations.
	 */
	public void addAllEOperations(Collection<EOperation> toAddList) {
		for (EOperation newValue : toAddList) {
			addEOperations(getEOperationsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eOperations.
	 */
	public void removeEOperations(EOperation value) {
		int index = eOperationsList.indexOf(value);
		if (index >= 0 ) {
			removeEOperations(index);
		}
	}

	/**
	 * Removes object from eOperations at given index.
	 */
	public void removeEOperations(int index) {
		eOperationsList.remove(index);
	}

	/**
	 * Adds object to eOperations and sets the corresponding eContainingClass.
	 */
	public void addEOperationsAndOpposite(EOperation newValue) {
		addEOperations(newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
	}

	/**
	 * Adds a collection of objects to eOperations and sets the corresponding eContainingClass.
	 */
	public void addAllEOperationsAndOpposite(Collection<EOperation> toAddList) {
		for (EOperation newValue : toAddList) {
			addEOperationsAndOpposite(getEOperationsCount(), newValue);
		}
	}

	/**
	 * Adds object to eOperations at given index and sets the corresponding eContainingClass.
	 */
	public void addEOperationsAndOpposite(int index, EOperation newValue) {
		addEOperations(index, newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
	}

	/**
	 * Replaces an object in eOperations at given index. Returns the old value.
	 */
	public EOperation setEOperationsAndOpposite(int index, EOperation newValue) {
		EOperation oldValue = eOperationsList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eOperations and resets the corresponding eContainingClass.
	 */
	public void removeEOperationsAndOpposite(EOperation removed) {
		removeEOperations(removed);
		if ( removed != null ) {
			removed.setEContainingClass(null);
		}
	}

	/**
	 * Removes object at given index from eOperations and resets the corresponding eContainingClass.
	 */
	public void removeEOperationsAndOpposite(int index) {
		EOperation removed = eOperationsList.get(index);
		removeEOperations(index);
		if ( removed != null ) {
			removed.setEContainingClass(null);
		}
	}

	/**
	 * Returns all values of eAllAttributes.
	 */
	public List<EAttribute> getEAllAttributesList() {
		return Collections.unmodifiableList(eAllAttributesList);
	}

	/**
	 * Gets eAllAttributes object count.
	 */
	public int getEAllAttributesCount() {
		return eAllAttributesList.size();
	}

	/**
	 * Gets eAllAttributes at given index.
	 */
	public EAttribute getEAllAttributes(int index) {
		if ( index < 0 || index >= getEAllAttributesCount() ) { return null; }
		return eAllAttributesList.get(index);
	}

	/**
	 * Adds an object in eAllAttributes.
	 */
	public void addEAllAttributes(EAttribute newValue) {
		addEAllAttributes(getEAllAttributesCount(), newValue);
	}

	/**
	 * Adds an object in eAllAttributes at given index.
	 */
	public void addEAllAttributes(int index, EAttribute newValue) {
		eAllAttributesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllAttributes at given index. Returns the old value.
	 */
	public EAttribute setEAllAttributes(int index, EAttribute newValue) {
		return eAllAttributesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllAttributes.
	 */
	public void addAllEAllAttributes(Collection<EAttribute> toAddList) {
		for (EAttribute newValue : toAddList) {
			addEAllAttributes(getEAllAttributesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllAttributes.
	 */
	public void removeEAllAttributes(EAttribute value) {
		int index = eAllAttributesList.indexOf(value);
		if (index >= 0 ) {
			removeEAllAttributes(index);
		}
	}

	/**
	 * Removes object from eAllAttributes at given index.
	 */
	public void removeEAllAttributes(int index) {
		eAllAttributesList.remove(index);
	}

	/**
	 * Returns all values of eAllReferences.
	 */
	public List<EReference> getEAllReferencesList() {
		return Collections.unmodifiableList(eAllReferencesList);
	}

	/**
	 * Gets eAllReferences object count.
	 */
	public int getEAllReferencesCount() {
		return eAllReferencesList.size();
	}

	/**
	 * Gets eAllReferences at given index.
	 */
	public EReference getEAllReferences(int index) {
		if ( index < 0 || index >= getEAllReferencesCount() ) { return null; }
		return eAllReferencesList.get(index);
	}

	/**
	 * Adds an object in eAllReferences.
	 */
	public void addEAllReferences(EReference newValue) {
		addEAllReferences(getEAllReferencesCount(), newValue);
	}

	/**
	 * Adds an object in eAllReferences at given index.
	 */
	public void addEAllReferences(int index, EReference newValue) {
		eAllReferencesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllReferences at given index. Returns the old value.
	 */
	public EReference setEAllReferences(int index, EReference newValue) {
		return eAllReferencesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllReferences.
	 */
	public void addAllEAllReferences(Collection<EReference> toAddList) {
		for (EReference newValue : toAddList) {
			addEAllReferences(getEAllReferencesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllReferences.
	 */
	public void removeEAllReferences(EReference value) {
		int index = eAllReferencesList.indexOf(value);
		if (index >= 0 ) {
			removeEAllReferences(index);
		}
	}

	/**
	 * Removes object from eAllReferences at given index.
	 */
	public void removeEAllReferences(int index) {
		eAllReferencesList.remove(index);
	}

	/**
	 * Returns all values of eReferences.
	 */
	public List<EReference> getEReferencesList() {
		return Collections.unmodifiableList(eReferencesList);
	}

	/**
	 * Gets eReferences object count.
	 */
	public int getEReferencesCount() {
		return eReferencesList.size();
	}

	/**
	 * Gets eReferences at given index.
	 */
	public EReference getEReferences(int index) {
		if ( index < 0 || index >= getEReferencesCount() ) { return null; }
		return eReferencesList.get(index);
	}

	/**
	 * Adds an object in eReferences.
	 */
	public void addEReferences(EReference newValue) {
		addEReferences(getEReferencesCount(), newValue);
	}

	/**
	 * Adds an object in eReferences at given index.
	 */
	public void addEReferences(int index, EReference newValue) {
		eReferencesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eReferences at given index. Returns the old value.
	 */
	public EReference setEReferences(int index, EReference newValue) {
		return eReferencesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eReferences.
	 */
	public void addAllEReferences(Collection<EReference> toAddList) {
		for (EReference newValue : toAddList) {
			addEReferences(getEReferencesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eReferences.
	 */
	public void removeEReferences(EReference value) {
		int index = eReferencesList.indexOf(value);
		if (index >= 0 ) {
			removeEReferences(index);
		}
	}

	/**
	 * Removes object from eReferences at given index.
	 */
	public void removeEReferences(int index) {
		eReferencesList.remove(index);
	}

	/**
	 * Returns all values of eAttributes.
	 */
	public List<EAttribute> getEAttributesList() {
		return Collections.unmodifiableList(eAttributesList);
	}

	/**
	 * Gets eAttributes object count.
	 */
	public int getEAttributesCount() {
		return eAttributesList.size();
	}

	/**
	 * Gets eAttributes at given index.
	 */
	public EAttribute getEAttributes(int index) {
		if ( index < 0 || index >= getEAttributesCount() ) { return null; }
		return eAttributesList.get(index);
	}

	/**
	 * Adds an object in eAttributes.
	 */
	public void addEAttributes(EAttribute newValue) {
		addEAttributes(getEAttributesCount(), newValue);
	}

	/**
	 * Adds an object in eAttributes at given index.
	 */
	public void addEAttributes(int index, EAttribute newValue) {
		eAttributesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAttributes at given index. Returns the old value.
	 */
	public EAttribute setEAttributes(int index, EAttribute newValue) {
		return eAttributesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAttributes.
	 */
	public void addAllEAttributes(Collection<EAttribute> toAddList) {
		for (EAttribute newValue : toAddList) {
			addEAttributes(getEAttributesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAttributes.
	 */
	public void removeEAttributes(EAttribute value) {
		int index = eAttributesList.indexOf(value);
		if (index >= 0 ) {
			removeEAttributes(index);
		}
	}

	/**
	 * Removes object from eAttributes at given index.
	 */
	public void removeEAttributes(int index) {
		eAttributesList.remove(index);
	}

	/**
	 * Returns all values of eAllContainments.
	 */
	public List<EReference> getEAllContainmentsList() {
		return Collections.unmodifiableList(eAllContainmentsList);
	}

	/**
	 * Gets eAllContainments object count.
	 */
	public int getEAllContainmentsCount() {
		return eAllContainmentsList.size();
	}

	/**
	 * Gets eAllContainments at given index.
	 */
	public EReference getEAllContainments(int index) {
		if ( index < 0 || index >= getEAllContainmentsCount() ) { return null; }
		return eAllContainmentsList.get(index);
	}

	/**
	 * Adds an object in eAllContainments.
	 */
	public void addEAllContainments(EReference newValue) {
		addEAllContainments(getEAllContainmentsCount(), newValue);
	}

	/**
	 * Adds an object in eAllContainments at given index.
	 */
	public void addEAllContainments(int index, EReference newValue) {
		eAllContainmentsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllContainments at given index. Returns the old value.
	 */
	public EReference setEAllContainments(int index, EReference newValue) {
		return eAllContainmentsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllContainments.
	 */
	public void addAllEAllContainments(Collection<EReference> toAddList) {
		for (EReference newValue : toAddList) {
			addEAllContainments(getEAllContainmentsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllContainments.
	 */
	public void removeEAllContainments(EReference value) {
		int index = eAllContainmentsList.indexOf(value);
		if (index >= 0 ) {
			removeEAllContainments(index);
		}
	}

	/**
	 * Removes object from eAllContainments at given index.
	 */
	public void removeEAllContainments(int index) {
		eAllContainmentsList.remove(index);
	}

	/**
	 * Returns all values of eAllOperations.
	 */
	public List<EOperation> getEAllOperationsList() {
		return Collections.unmodifiableList(eAllOperationsList);
	}

	/**
	 * Gets eAllOperations object count.
	 */
	public int getEAllOperationsCount() {
		return eAllOperationsList.size();
	}

	/**
	 * Gets eAllOperations at given index.
	 */
	public EOperation getEAllOperations(int index) {
		if ( index < 0 || index >= getEAllOperationsCount() ) { return null; }
		return eAllOperationsList.get(index);
	}

	/**
	 * Adds an object in eAllOperations.
	 */
	public void addEAllOperations(EOperation newValue) {
		addEAllOperations(getEAllOperationsCount(), newValue);
	}

	/**
	 * Adds an object in eAllOperations at given index.
	 */
	public void addEAllOperations(int index, EOperation newValue) {
		eAllOperationsList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllOperations at given index. Returns the old value.
	 */
	public EOperation setEAllOperations(int index, EOperation newValue) {
		return eAllOperationsList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllOperations.
	 */
	public void addAllEAllOperations(Collection<EOperation> toAddList) {
		for (EOperation newValue : toAddList) {
			addEAllOperations(getEAllOperationsCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllOperations.
	 */
	public void removeEAllOperations(EOperation value) {
		int index = eAllOperationsList.indexOf(value);
		if (index >= 0 ) {
			removeEAllOperations(index);
		}
	}

	/**
	 * Removes object from eAllOperations at given index.
	 */
	public void removeEAllOperations(int index) {
		eAllOperationsList.remove(index);
	}

	/**
	 * Returns all values of eAllStructuralFeatures.
	 */
	public List<EStructuralFeature> getEAllStructuralFeaturesList() {
		return Collections.unmodifiableList(eAllStructuralFeaturesList);
	}

	/**
	 * Gets eAllStructuralFeatures object count.
	 */
	public int getEAllStructuralFeaturesCount() {
		return eAllStructuralFeaturesList.size();
	}

	/**
	 * Gets eAllStructuralFeatures at given index.
	 */
	public EStructuralFeature getEAllStructuralFeatures(int index) {
		if ( index < 0 || index >= getEAllStructuralFeaturesCount() ) { return null; }
		return eAllStructuralFeaturesList.get(index);
	}

	/**
	 * Adds an object in eAllStructuralFeatures.
	 */
	public void addEAllStructuralFeatures(EStructuralFeature newValue) {
		addEAllStructuralFeatures(getEAllStructuralFeaturesCount(), newValue);
	}

	/**
	 * Adds an object in eAllStructuralFeatures at given index.
	 */
	public void addEAllStructuralFeatures(int index, EStructuralFeature newValue) {
		eAllStructuralFeaturesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllStructuralFeatures at given index. Returns the old value.
	 */
	public EStructuralFeature setEAllStructuralFeatures(int index, EStructuralFeature newValue) {
		return eAllStructuralFeaturesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllStructuralFeatures.
	 */
	public void addAllEAllStructuralFeatures(Collection<EStructuralFeature> toAddList) {
		for (EStructuralFeature newValue : toAddList) {
			addEAllStructuralFeatures(getEAllStructuralFeaturesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllStructuralFeatures.
	 */
	public void removeEAllStructuralFeatures(EStructuralFeature value) {
		int index = eAllStructuralFeaturesList.indexOf(value);
		if (index >= 0 ) {
			removeEAllStructuralFeatures(index);
		}
	}

	/**
	 * Removes object from eAllStructuralFeatures at given index.
	 */
	public void removeEAllStructuralFeatures(int index) {
		eAllStructuralFeaturesList.remove(index);
	}

	/**
	 * Returns all values of eAllSuperTypes.
	 */
	public List<ecore.EClass> getEAllSuperTypesList() {
		return Collections.unmodifiableList(eAllSuperTypesList);
	}

	/**
	 * Gets eAllSuperTypes object count.
	 */
	public int getEAllSuperTypesCount() {
		return eAllSuperTypesList.size();
	}

	/**
	 * Gets eAllSuperTypes at given index.
	 */
	public ecore.EClass getEAllSuperTypes(int index) {
		if ( index < 0 || index >= getEAllSuperTypesCount() ) { return null; }
		return eAllSuperTypesList.get(index);
	}

	/**
	 * Adds an object in eAllSuperTypes.
	 */
	public void addEAllSuperTypes(ecore.EClass newValue) {
		addEAllSuperTypes(getEAllSuperTypesCount(), newValue);
	}

	/**
	 * Adds an object in eAllSuperTypes at given index.
	 */
	public void addEAllSuperTypes(int index, ecore.EClass newValue) {
		eAllSuperTypesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllSuperTypes at given index. Returns the old value.
	 */
	public ecore.EClass setEAllSuperTypes(int index, ecore.EClass newValue) {
		return eAllSuperTypesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllSuperTypes.
	 */
	public void addAllEAllSuperTypes(Collection<ecore.EClass> toAddList) {
		for (ecore.EClass newValue : toAddList) {
			addEAllSuperTypes(getEAllSuperTypesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllSuperTypes.
	 */
	public void removeEAllSuperTypes(ecore.EClass value) {
		int index = eAllSuperTypesList.indexOf(value);
		if (index >= 0 ) {
			removeEAllSuperTypes(index);
		}
	}

	/**
	 * Removes object from eAllSuperTypes at given index.
	 */
	public void removeEAllSuperTypes(int index) {
		eAllSuperTypesList.remove(index);
	}

	/**
	 * Gets eIDAttribute.
	 */
	public EAttribute getEIDAttribute() {
		return eIDAttribute;
	}

	/**
	 * Sets eIDAttribute.
	 */
	public void setEIDAttribute(EAttribute newValue) {
		if (eIDAttribute == null ? newValue != null : (eIDAttribute.equals(newValue) == false)) {
			this.eIDAttribute= newValue;
		}
	}

	/**
	 * Returns all values of eStructuralFeatures.
	 */
	public List<EStructuralFeature> getEStructuralFeaturesList() {
		return Collections.unmodifiableList(eStructuralFeaturesList);
	}

	/**
	 * Gets eStructuralFeatures object count.
	 */
	public int getEStructuralFeaturesCount() {
		return eStructuralFeaturesList.size();
	}

	/**
	 * Gets eStructuralFeatures at given index.
	 */
	public EStructuralFeature getEStructuralFeatures(int index) {
		if ( index < 0 || index >= getEStructuralFeaturesCount() ) { return null; }
		return eStructuralFeaturesList.get(index);
	}

	/**
	 * Adds an object in eStructuralFeatures.
	 */
	public void addEStructuralFeatures(EStructuralFeature newValue) {
		addEStructuralFeatures(getEStructuralFeaturesCount(), newValue);
	}

	/**
	 * Adds an object in eStructuralFeatures at given index.
	 */
	public void addEStructuralFeatures(int index, EStructuralFeature newValue) {
		eStructuralFeaturesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eStructuralFeatures at given index. Returns the old value.
	 */
	public EStructuralFeature setEStructuralFeatures(int index, EStructuralFeature newValue) {
		return eStructuralFeaturesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eStructuralFeatures.
	 */
	public void addAllEStructuralFeatures(Collection<EStructuralFeature> toAddList) {
		for (EStructuralFeature newValue : toAddList) {
			addEStructuralFeatures(getEStructuralFeaturesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eStructuralFeatures.
	 */
	public void removeEStructuralFeatures(EStructuralFeature value) {
		int index = eStructuralFeaturesList.indexOf(value);
		if (index >= 0 ) {
			removeEStructuralFeatures(index);
		}
	}

	/**
	 * Removes object from eStructuralFeatures at given index.
	 */
	public void removeEStructuralFeatures(int index) {
		eStructuralFeaturesList.remove(index);
	}

	/**
	 * Adds object to eStructuralFeatures and sets the corresponding eContainingClass.
	 */
	public void addEStructuralFeaturesAndOpposite(EStructuralFeature newValue) {
		addEStructuralFeatures(newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
	}

	/**
	 * Adds a collection of objects to eStructuralFeatures and sets the corresponding eContainingClass.
	 */
	public void addAllEStructuralFeaturesAndOpposite(Collection<EStructuralFeature> toAddList) {
		for (EStructuralFeature newValue : toAddList) {
			addEStructuralFeaturesAndOpposite(getEStructuralFeaturesCount(), newValue);
		}
	}

	/**
	 * Adds object to eStructuralFeatures at given index and sets the corresponding eContainingClass.
	 */
	public void addEStructuralFeaturesAndOpposite(int index, EStructuralFeature newValue) {
		addEStructuralFeatures(index, newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
	}

	/**
	 * Replaces an object in eStructuralFeatures at given index. Returns the old value.
	 */
	public EStructuralFeature setEStructuralFeaturesAndOpposite(int index, EStructuralFeature newValue) {
		EStructuralFeature oldValue = eStructuralFeaturesList.set(index, newValue);
		if ( newValue != null ) {
			newValue.setEContainingClass(this);
		}
		return oldValue;
	}

	/**
	 * Removes object from eStructuralFeatures and resets the corresponding eContainingClass.
	 */
	public void removeEStructuralFeaturesAndOpposite(EStructuralFeature removed) {
		removeEStructuralFeatures(removed);
		if ( removed != null ) {
			removed.setEContainingClass(null);
		}
	}

	/**
	 * Removes object at given index from eStructuralFeatures and resets the corresponding eContainingClass.
	 */
	public void removeEStructuralFeaturesAndOpposite(int index) {
		EStructuralFeature removed = eStructuralFeaturesList.get(index);
		removeEStructuralFeatures(index);
		if ( removed != null ) {
			removed.setEContainingClass(null);
		}
	}

	/**
	 * Returns all values of eGenericSuperTypes.
	 */
	public List<EGenericType> getEGenericSuperTypesList() {
		return Collections.unmodifiableList(eGenericSuperTypesList);
	}

	/**
	 * Gets eGenericSuperTypes object count.
	 */
	public int getEGenericSuperTypesCount() {
		return eGenericSuperTypesList.size();
	}

	/**
	 * Gets eGenericSuperTypes at given index.
	 */
	public EGenericType getEGenericSuperTypes(int index) {
		if ( index < 0 || index >= getEGenericSuperTypesCount() ) { return null; }
		return eGenericSuperTypesList.get(index);
	}

	/**
	 * Adds an object in eGenericSuperTypes.
	 */
	public void addEGenericSuperTypes(EGenericType newValue) {
		addEGenericSuperTypes(getEGenericSuperTypesCount(), newValue);
	}

	/**
	 * Adds an object in eGenericSuperTypes at given index.
	 */
	public void addEGenericSuperTypes(int index, EGenericType newValue) {
		eGenericSuperTypesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eGenericSuperTypes at given index. Returns the old value.
	 */
	public EGenericType setEGenericSuperTypes(int index, EGenericType newValue) {
		return eGenericSuperTypesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eGenericSuperTypes.
	 */
	public void addAllEGenericSuperTypes(Collection<EGenericType> toAddList) {
		for (EGenericType newValue : toAddList) {
			addEGenericSuperTypes(getEGenericSuperTypesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eGenericSuperTypes.
	 */
	public void removeEGenericSuperTypes(EGenericType value) {
		int index = eGenericSuperTypesList.indexOf(value);
		if (index >= 0 ) {
			removeEGenericSuperTypes(index);
		}
	}

	/**
	 * Removes object from eGenericSuperTypes at given index.
	 */
	public void removeEGenericSuperTypes(int index) {
		eGenericSuperTypesList.remove(index);
	}

	/**
	 * Returns all values of eAllGenericSuperTypes.
	 */
	public List<EGenericType> getEAllGenericSuperTypesList() {
		return Collections.unmodifiableList(eAllGenericSuperTypesList);
	}

	/**
	 * Gets eAllGenericSuperTypes object count.
	 */
	public int getEAllGenericSuperTypesCount() {
		return eAllGenericSuperTypesList.size();
	}

	/**
	 * Gets eAllGenericSuperTypes at given index.
	 */
	public EGenericType getEAllGenericSuperTypes(int index) {
		if ( index < 0 || index >= getEAllGenericSuperTypesCount() ) { return null; }
		return eAllGenericSuperTypesList.get(index);
	}

	/**
	 * Adds an object in eAllGenericSuperTypes.
	 */
	public void addEAllGenericSuperTypes(EGenericType newValue) {
		addEAllGenericSuperTypes(getEAllGenericSuperTypesCount(), newValue);
	}

	/**
	 * Adds an object in eAllGenericSuperTypes at given index.
	 */
	public void addEAllGenericSuperTypes(int index, EGenericType newValue) {
		eAllGenericSuperTypesList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllGenericSuperTypes at given index. Returns the old value.
	 */
	public EGenericType setEAllGenericSuperTypes(int index, EGenericType newValue) {
		return eAllGenericSuperTypesList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllGenericSuperTypes.
	 */
	public void addAllEAllGenericSuperTypes(Collection<EGenericType> toAddList) {
		for (EGenericType newValue : toAddList) {
			addEAllGenericSuperTypes(getEAllGenericSuperTypesCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllGenericSuperTypes.
	 */
	public void removeEAllGenericSuperTypes(EGenericType value) {
		int index = eAllGenericSuperTypesList.indexOf(value);
		if (index >= 0 ) {
			removeEAllGenericSuperTypes(index);
		}
	}

	/**
	 * Removes object from eAllGenericSuperTypes at given index.
	 */
	public void removeEAllGenericSuperTypes(int index) {
		eAllGenericSuperTypesList.remove(index);
	}

	/**
	 * Gets eIDAttributeForTest.
	 */
	public EAttribute getEIDAttributeForTest() {
		return eIDAttributeForTest;
	}

	/**
	 * Sets eIDAttributeForTest.
	 */
	public void setEIDAttributeForTest(EAttribute newValue) {
		if (eIDAttributeForTest == null ? newValue != null : (eIDAttributeForTest.equals(newValue) == false)) {
			this.eIDAttributeForTest= newValue;
		}
	}

	/**
	 * Returns all values of eAllAttributesForTest.
	 */
	public List<EAttribute> getEAllAttributesForTestList() {
		return Collections.unmodifiableList(eAllAttributesForTestList);
	}

	/**
	 * Gets eAllAttributesForTest object count.
	 */
	public int getEAllAttributesForTestCount() {
		return eAllAttributesForTestList.size();
	}

	/**
	 * Gets eAllAttributesForTest at given index.
	 */
	public EAttribute getEAllAttributesForTest(int index) {
		if ( index < 0 || index >= getEAllAttributesForTestCount() ) { return null; }
		return eAllAttributesForTestList.get(index);
	}

	/**
	 * Adds an object in eAllAttributesForTest.
	 */
	public void addEAllAttributesForTest(EAttribute newValue) {
		addEAllAttributesForTest(getEAllAttributesForTestCount(), newValue);
	}

	/**
	 * Adds an object in eAllAttributesForTest at given index.
	 */
	public void addEAllAttributesForTest(int index, EAttribute newValue) {
		eAllAttributesForTestList.add(index, newValue);
	}

	/**
	 * Replaces an object in eAllAttributesForTest at given index. Returns the old value.
	 */
	public EAttribute setEAllAttributesForTest(int index, EAttribute newValue) {
		return eAllAttributesForTestList.set(index, newValue);
	}

	/**
	 * Adds a collection of objects in eAllAttributesForTest.
	 */
	public void addAllEAllAttributesForTest(Collection<EAttribute> toAddList) {
		for (EAttribute newValue : toAddList) {
			addEAllAttributesForTest(getEAllAttributesForTestCount(), newValue);
		}
	}

	/**
	 * Removes given object from eAllAttributesForTest.
	 */
	public void removeEAllAttributesForTest(EAttribute value) {
		int index = eAllAttributesForTestList.indexOf(value);
		if (index >= 0 ) {
			removeEAllAttributesForTest(index);
		}
	}

	/**
	 * Removes object from eAllAttributesForTest at given index.
	 */
	public void removeEAllAttributesForTest(int index) {
		eAllAttributesForTestList.remove(index);
	}

	public boolean isSuperTypeOf(ecore.EClass someClass) {
		// TODO implement isSuperTypeOf(...)
		throw new UnsupportedOperationException();
	}

	public int getFeatureCount() {
		// TODO implement getFeatureCount(...)
		throw new UnsupportedOperationException();
	}

	public EStructuralFeature getEStructuralFeature(int featureID) {
		// TODO implement getEStructuralFeature(...)
		throw new UnsupportedOperationException();
	}

	public int getFeatureID(EStructuralFeature feature) {
		// TODO implement getFeatureID(...)
		throw new UnsupportedOperationException();
	}

	public EStructuralFeature getEStructuralFeature(String featureName) {
		// TODO implement getEStructuralFeature(...)
		throw new UnsupportedOperationException();
	}

	/**
	 * Visitor accept method.
	 */
	public void accept(EcoreVisitor visitor) {
		visitor.visitEClass(this);
	}

}

