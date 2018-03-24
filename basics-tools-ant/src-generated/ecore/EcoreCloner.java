package ecore;

import org.xid.basics.model.AbstractCloner;
import ecore.EcoreVisitor;
import ecore.EcoreCloner;
import ecore.ETypeParameter;
import ecore.EStructuralFeature;
import ecore.EReference;
import ecore.EParameter;
import ecore.EPackage;
import ecore.EOperation;
import ecore.EObject;
import ecore.EGenericType;
import ecore.EFactory;
import ecore.EEnumLiteral;
import ecore.EEnum;
import ecore.EDataType;
import ecore.EClassifier;
import ecore.EClass;
import ecore.EAttribute;
import ecore.EAnnotation;

/**
 * Cloner for objects in packages 'ecore'.
 */
public class EcoreCloner extends AbstractCloner implements EcoreVisitor {

	/**
	 * Clones given object. Only object and children (by containment) are cloned. Referenced objects which are not contained aren't cloned.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends EObject> T clone(T object) {
		if ( object == null ) return null;
		
		EcoreCloner cloner = new EcoreCloner();
		object.accept(cloner);
		return (T) cloner.popObject(EObject.class);
	}

	public void visitEAttribute(EAttribute toVisit) {
		EAttribute cloned = new EAttribute();
		cloned.setName(toVisit.getName());
		cloned.setOrdered(toVisit.isOrdered());
		cloned.setUnique(toVisit.isUnique());
		cloned.setLowerBound(toVisit.getLowerBound());
		cloned.setUpperBound(toVisit.getUpperBound());
		cloned.setMany(toVisit.isMany());
		cloned.setRequired(toVisit.isRequired());
		cloned.setChangeable(toVisit.isChangeable());
		cloned.setVolatile(toVisit.isVolatile());
		cloned.setTransient(toVisit.isTransient());
		cloned.setDefaultValueLiteral(toVisit.getDefaultValueLiteral());
		cloned.setDefaultValue(toVisit.getDefaultValue());
		cloned.setUnsettable(toVisit.isUnsettable());
		cloned.setDerived(toVisit.isDerived());
		cloned.setID(toVisit.isID());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEType() != null ) {
			registerReference(cloned, "eType", false, toVisit.getEType());
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
			cloned.setEGenericType(popObject(EGenericType.class));
		}
		if ( toVisit.getEContainingClass() != null ) {
			registerReference(cloned, "eContainingClass", false, toVisit.getEContainingClass());
		}
		if ( toVisit.getEAttributeType() != null ) {
			registerReference(cloned, "eAttributeType", false, toVisit.getEAttributeType());
		}
		pushObject(cloned);
	}

	public void visitEAnnotation(EAnnotation toVisit) {
		EAnnotation cloned = new EAnnotation();
		cloned.setSource(toVisit.getSource());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEModelElement() != null ) {
			registerReference(cloned, "eModelElement", false, toVisit.getEModelElement());
		}
		for ( EObject child : toVisit.getContentsList()) {
			child.accept(this);
			cloned.addContents(popObject(EObject.class));
		}
		for ( EObject child : toVisit.getReferencesList()) {
			registerReference(cloned, "references", true, child);
		}
		pushObject(cloned);
	}

	public void visitEClass(EClass toVisit) {
		EClass cloned = new EClass();
		cloned.setName(toVisit.getName());
		cloned.setInstanceClassName(toVisit.getInstanceClassName());
		cloned.setInstanceClass(toVisit.getInstanceClass());
		cloned.setDefaultValue(toVisit.getDefaultValue());
		cloned.setInstanceTypeName(toVisit.getInstanceTypeName());
		cloned.setAbstract(toVisit.isAbstract());
		cloned.setInterface(toVisit.isInterface());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEPackage() != null ) {
			registerReference(cloned, "ePackage", false, toVisit.getEPackage());
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
			cloned.addETypeParameters(popObject(ETypeParameter.class));
		}
		for ( EClass child : toVisit.getESuperTypesList()) {
			registerReference(cloned, "eSuperTypes", true, child);
		}
		for ( EOperation child : toVisit.getEOperationsList()) {
			child.accept(this);
			cloned.addEOperations(popObject(EOperation.class));
		}
		for ( EAttribute child : toVisit.getEAllAttributesList()) {
			registerReference(cloned, "eAllAttributes", true, child);
		}
		for ( EReference child : toVisit.getEAllReferencesList()) {
			registerReference(cloned, "eAllReferences", true, child);
		}
		for ( EReference child : toVisit.getEReferencesList()) {
			registerReference(cloned, "eReferences", true, child);
		}
		for ( EAttribute child : toVisit.getEAttributesList()) {
			registerReference(cloned, "eAttributes", true, child);
		}
		for ( EReference child : toVisit.getEAllContainmentsList()) {
			registerReference(cloned, "eAllContainments", true, child);
		}
		for ( EOperation child : toVisit.getEAllOperationsList()) {
			registerReference(cloned, "eAllOperations", true, child);
		}
		for ( EStructuralFeature child : toVisit.getEAllStructuralFeaturesList()) {
			registerReference(cloned, "eAllStructuralFeatures", true, child);
		}
		for ( EClass child : toVisit.getEAllSuperTypesList()) {
			registerReference(cloned, "eAllSuperTypes", true, child);
		}
		if ( toVisit.getEIDAttribute() != null ) {
			registerReference(cloned, "eIDAttribute", false, toVisit.getEIDAttribute());
		}
		for ( EStructuralFeature child : toVisit.getEStructuralFeaturesList()) {
			child.accept(this);
			cloned.addEStructuralFeatures(popObject(EStructuralFeature.class));
		}
		for ( EGenericType child : toVisit.getEGenericSuperTypesList()) {
			child.accept(this);
			cloned.addEGenericSuperTypes(popObject(EGenericType.class));
		}
		for ( EGenericType child : toVisit.getEAllGenericSuperTypesList()) {
			registerReference(cloned, "eAllGenericSuperTypes", true, child);
		}
		if ( toVisit.getEIDAttributeForTest() != null ) {
			registerReference(cloned, "eIDAttributeForTest", false, toVisit.getEIDAttributeForTest());
		}
		for ( EAttribute child : toVisit.getEAllAttributesForTestList()) {
			registerReference(cloned, "eAllAttributesForTest", true, child);
		}
		pushObject(cloned);
	}

	public void visitEDataType(EDataType toVisit) {
		EDataType cloned = new EDataType();
		cloned.setName(toVisit.getName());
		cloned.setInstanceClassName(toVisit.getInstanceClassName());
		cloned.setInstanceClass(toVisit.getInstanceClass());
		cloned.setDefaultValue(toVisit.getDefaultValue());
		cloned.setInstanceTypeName(toVisit.getInstanceTypeName());
		cloned.setSerializable(toVisit.isSerializable());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEPackage() != null ) {
			registerReference(cloned, "ePackage", false, toVisit.getEPackage());
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
			cloned.addETypeParameters(popObject(ETypeParameter.class));
		}
		pushObject(cloned);
	}

	public void visitEEnum(EEnum toVisit) {
		EEnum cloned = new EEnum();
		cloned.setName(toVisit.getName());
		cloned.setInstanceClassName(toVisit.getInstanceClassName());
		cloned.setInstanceClass(toVisit.getInstanceClass());
		cloned.setDefaultValue(toVisit.getDefaultValue());
		cloned.setInstanceTypeName(toVisit.getInstanceTypeName());
		cloned.setSerializable(toVisit.isSerializable());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEPackage() != null ) {
			registerReference(cloned, "ePackage", false, toVisit.getEPackage());
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
			cloned.addETypeParameters(popObject(ETypeParameter.class));
		}
		for ( EEnumLiteral child : toVisit.getELiteralsList()) {
			child.accept(this);
			cloned.addELiterals(popObject(EEnumLiteral.class));
		}
		pushObject(cloned);
	}

	public void visitEEnumLiteral(EEnumLiteral toVisit) {
		EEnumLiteral cloned = new EEnumLiteral();
		cloned.setName(toVisit.getName());
		cloned.setValue(toVisit.getValue());
		cloned.setInstance(toVisit.getInstance());
		cloned.setLiteral(toVisit.getLiteral());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEEnum() != null ) {
			registerReference(cloned, "eEnum", false, toVisit.getEEnum());
		}
		pushObject(cloned);
	}

	public void visitEFactory(EFactory toVisit) {
		EFactory cloned = new EFactory();
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEPackage() != null ) {
			registerReference(cloned, "ePackage", false, toVisit.getEPackage());
		}
		pushObject(cloned);
	}

	public void visitEObject(EObject toVisit) {
		EObject cloned = new EObject();
		registerClone(toVisit, cloned);
		pushObject(cloned);
	}

	public void visitEOperation(EOperation toVisit) {
		EOperation cloned = new EOperation();
		cloned.setName(toVisit.getName());
		cloned.setOrdered(toVisit.isOrdered());
		cloned.setUnique(toVisit.isUnique());
		cloned.setLowerBound(toVisit.getLowerBound());
		cloned.setUpperBound(toVisit.getUpperBound());
		cloned.setMany(toVisit.isMany());
		cloned.setRequired(toVisit.isRequired());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEType() != null ) {
			registerReference(cloned, "eType", false, toVisit.getEType());
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
			cloned.setEGenericType(popObject(EGenericType.class));
		}
		if ( toVisit.getEContainingClass() != null ) {
			registerReference(cloned, "eContainingClass", false, toVisit.getEContainingClass());
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
			cloned.addETypeParameters(popObject(ETypeParameter.class));
		}
		for ( EParameter child : toVisit.getEParametersList()) {
			child.accept(this);
			cloned.addEParameters(popObject(EParameter.class));
		}
		for ( EClassifier child : toVisit.getEExceptionsList()) {
			registerReference(cloned, "eExceptions", true, child);
		}
		for ( EGenericType child : toVisit.getEGenericExceptionsList()) {
			child.accept(this);
			cloned.addEGenericExceptions(popObject(EGenericType.class));
		}
		pushObject(cloned);
	}

	public void visitEPackage(EPackage toVisit) {
		EPackage cloned = new EPackage();
		cloned.setName(toVisit.getName());
		cloned.setNsURI(toVisit.getNsURI());
		cloned.setNsPrefix(toVisit.getNsPrefix());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEFactoryInstance() != null ) {
			registerReference(cloned, "eFactoryInstance", false, toVisit.getEFactoryInstance());
		}
		for ( EClassifier child : toVisit.getEClassifiersList()) {
			child.accept(this);
			cloned.addEClassifiers(popObject(EClassifier.class));
		}
		for ( EPackage child : toVisit.getESubpackagesList()) {
			child.accept(this);
			cloned.addESubpackages(popObject(EPackage.class));
		}
		if ( toVisit.getESuperPackage() != null ) {
			registerReference(cloned, "eSuperPackage", false, toVisit.getESuperPackage());
		}
		pushObject(cloned);
	}

	public void visitEParameter(EParameter toVisit) {
		EParameter cloned = new EParameter();
		cloned.setName(toVisit.getName());
		cloned.setOrdered(toVisit.isOrdered());
		cloned.setUnique(toVisit.isUnique());
		cloned.setLowerBound(toVisit.getLowerBound());
		cloned.setUpperBound(toVisit.getUpperBound());
		cloned.setMany(toVisit.isMany());
		cloned.setRequired(toVisit.isRequired());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEType() != null ) {
			registerReference(cloned, "eType", false, toVisit.getEType());
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
			cloned.setEGenericType(popObject(EGenericType.class));
		}
		if ( toVisit.getEOperation() != null ) {
			registerReference(cloned, "eOperation", false, toVisit.getEOperation());
		}
		pushObject(cloned);
	}

	public void visitEReference(EReference toVisit) {
		EReference cloned = new EReference();
		cloned.setName(toVisit.getName());
		cloned.setOrdered(toVisit.isOrdered());
		cloned.setUnique(toVisit.isUnique());
		cloned.setLowerBound(toVisit.getLowerBound());
		cloned.setUpperBound(toVisit.getUpperBound());
		cloned.setMany(toVisit.isMany());
		cloned.setRequired(toVisit.isRequired());
		cloned.setChangeable(toVisit.isChangeable());
		cloned.setVolatile(toVisit.isVolatile());
		cloned.setTransient(toVisit.isTransient());
		cloned.setDefaultValueLiteral(toVisit.getDefaultValueLiteral());
		cloned.setDefaultValue(toVisit.getDefaultValue());
		cloned.setUnsettable(toVisit.isUnsettable());
		cloned.setDerived(toVisit.isDerived());
		cloned.setContainment(toVisit.isContainment());
		cloned.setContainer(toVisit.isContainer());
		cloned.setResolveProxies(toVisit.isResolveProxies());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		if ( toVisit.getEType() != null ) {
			registerReference(cloned, "eType", false, toVisit.getEType());
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
			cloned.setEGenericType(popObject(EGenericType.class));
		}
		if ( toVisit.getEContainingClass() != null ) {
			registerReference(cloned, "eContainingClass", false, toVisit.getEContainingClass());
		}
		if ( toVisit.getEOpposite() != null ) {
			registerReference(cloned, "eOpposite", false, toVisit.getEOpposite());
		}
		if ( toVisit.getEReferenceType() != null ) {
			registerReference(cloned, "eReferenceType", false, toVisit.getEReferenceType());
		}
		for ( EAttribute child : toVisit.getEKeysList()) {
			registerReference(cloned, "eKeys", true, child);
		}
		pushObject(cloned);
	}

	public void visitEGenericType(EGenericType toVisit) {
		EGenericType cloned = new EGenericType();
		registerClone(toVisit, cloned);
		if ( toVisit.getEUpperBound() != null ) {
			toVisit.getEUpperBound().accept(this);
			cloned.setEUpperBound(popObject(EGenericType.class));
		}
		for ( EGenericType child : toVisit.getETypeArgumentsList()) {
			child.accept(this);
			cloned.addETypeArguments(popObject(EGenericType.class));
		}
		if ( toVisit.getERawType() != null ) {
			registerReference(cloned, "eRawType", false, toVisit.getERawType());
		}
		if ( toVisit.getELowerBound() != null ) {
			toVisit.getELowerBound().accept(this);
			cloned.setELowerBound(popObject(EGenericType.class));
		}
		if ( toVisit.getETypeParameter() != null ) {
			registerReference(cloned, "eTypeParameter", false, toVisit.getETypeParameter());
		}
		if ( toVisit.getEClassifier() != null ) {
			registerReference(cloned, "eClassifier", false, toVisit.getEClassifier());
		}
		pushObject(cloned);
	}

	public void visitETypeParameter(ETypeParameter toVisit) {
		ETypeParameter cloned = new ETypeParameter();
		cloned.setName(toVisit.getName());
		registerClone(toVisit, cloned);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
			cloned.addEAnnotations(popObject(EAnnotation.class));
		}
		for ( EGenericType child : toVisit.getEBoundsList()) {
			child.accept(this);
			cloned.addEBounds(popObject(EGenericType.class));
		}
		pushObject(cloned);
	}

}

