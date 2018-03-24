package ecore;

import org.xid.basics.model.AbstractCloner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Map;
import java.util.HashSet;
import ecore.EcoreVisitor;
import ecore.EcoreReplacer;
import ecore.ETypeParameter;
import ecore.EStructuralFeature;
import ecore.EReference;
import ecore.EParameter;
import ecore.EPackage;
import ecore.EOperation;
import ecore.EObject;
import ecore.EModelElement;
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
public class EcoreReplacer extends AbstractCloner implements EcoreVisitor {

	private final Set<EObject> replaced = new HashSet<EObject>();

	public EcoreReplacer() {
		this(null);
	}

	public EcoreReplacer(Map<EObject, EObject> replacementMap) {
		if ( replacementMap != null ) {
			for (Entry<EObject, EObject> entry : replacementMap.entrySet() ) {
				addReplacement(entry.getKey(), entry.getValue());
			}
		}
	}

	public void addReplacement(EObject original, EObject replacement) {
		registerClone(original, replacement);
	}

	public void addRemove(EObject original) {
		registerClone(original, null);
	}

	protected boolean hasContainmentReplacement(EObject original) {
		return hasClone(original) && replaced.contains(original) == false;
	}

	public boolean hasReplacement(EObject original) {
		return hasClone(original);
	}

	protected EObject getContainmentReplacement(EObject original) {
		replaced.add(original);
		return getReplacement(original);
	}

	public EObject getReplacement(EObject original) {
		return (EObject) getClone(original);
	}

	public void visitEAttribute(EAttribute toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEType() != null && hasReplacement(toVisit.getEType()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getEType());
				toVisit.setEType(child);
			}
			if ( toVisit.getEGenericType() != null ) {
				toVisit.getEGenericType().accept(this);
				toVisit.setEGenericType(popObject(EGenericType.class));
			}
			if ( toVisit.getEContainingClass() != null && hasReplacement(toVisit.getEContainingClass()) ) {
				EClass child = (EClass) getClone(toVisit.getEContainingClass());
				toVisit.setEContainingClass(child);
			}
			if ( toVisit.getEAttributeType() != null && hasReplacement(toVisit.getEAttributeType()) ) {
				EDataType child = (EDataType) getClone(toVisit.getEAttributeType());
				toVisit.setEAttributeType(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitEAnnotation(EAnnotation toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEModelElement() != null && hasReplacement(toVisit.getEModelElement()) ) {
				EModelElement child = (EModelElement) getClone(toVisit.getEModelElement());
				toVisit.setEModelElement(child);
			}
			for ( int i=0; i<toVisit.getContentsCount(); i+=1) {
				toVisit.getContents(i).accept(this);
				EObject child = popObject(EObject.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setContents(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeContents(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getReferencesCount(); i+=1) {
				EObject child = toVisit.getReferences(i);
				if (hasReplacement(child) ) {
					EObject replacement = (EObject) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setReferences(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeReferences(i);
						i -= 1;
					}
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEClass(EClass toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEPackage() != null && hasReplacement(toVisit.getEPackage()) ) {
				EPackage child = (EPackage) getClone(toVisit.getEPackage());
				toVisit.setEPackage(child);
			}
			for ( int i=0; i<toVisit.getETypeParametersCount(); i+=1) {
				toVisit.getETypeParameters(i).accept(this);
				ETypeParameter child = popObject(ETypeParameter.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setETypeParameters(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeETypeParameters(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getESuperTypesCount(); i+=1) {
				EClass child = toVisit.getESuperTypes(i);
				if (hasReplacement(child) ) {
					EClass replacement = (EClass) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setESuperTypes(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeESuperTypes(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEOperationsCount(); i+=1) {
				toVisit.getEOperations(i).accept(this);
				EOperation child = popObject(EOperation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEOperations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEOperations(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEAllAttributesCount(); i+=1) {
				EAttribute child = toVisit.getEAllAttributes(i);
				if (hasReplacement(child) ) {
					EAttribute replacement = (EAttribute) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllAttributes(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllAttributes(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAllReferencesCount(); i+=1) {
				EReference child = toVisit.getEAllReferences(i);
				if (hasReplacement(child) ) {
					EReference replacement = (EReference) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllReferences(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllReferences(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEReferencesCount(); i+=1) {
				EReference child = toVisit.getEReferences(i);
				if (hasReplacement(child) ) {
					EReference replacement = (EReference) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEReferences(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEReferences(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAttributesCount(); i+=1) {
				EAttribute child = toVisit.getEAttributes(i);
				if (hasReplacement(child) ) {
					EAttribute replacement = (EAttribute) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAttributes(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAttributes(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAllContainmentsCount(); i+=1) {
				EReference child = toVisit.getEAllContainments(i);
				if (hasReplacement(child) ) {
					EReference replacement = (EReference) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllContainments(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllContainments(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAllOperationsCount(); i+=1) {
				EOperation child = toVisit.getEAllOperations(i);
				if (hasReplacement(child) ) {
					EOperation replacement = (EOperation) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllOperations(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllOperations(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAllStructuralFeaturesCount(); i+=1) {
				EStructuralFeature child = toVisit.getEAllStructuralFeatures(i);
				if (hasReplacement(child) ) {
					EStructuralFeature replacement = (EStructuralFeature) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllStructuralFeatures(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllStructuralFeatures(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEAllSuperTypesCount(); i+=1) {
				EClass child = toVisit.getEAllSuperTypes(i);
				if (hasReplacement(child) ) {
					EClass replacement = (EClass) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllSuperTypes(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllSuperTypes(i);
						i -= 1;
					}
				}
			}
			if ( toVisit.getEIDAttribute() != null && hasReplacement(toVisit.getEIDAttribute()) ) {
				EAttribute child = (EAttribute) getClone(toVisit.getEIDAttribute());
				toVisit.setEIDAttribute(child);
			}
			for ( int i=0; i<toVisit.getEStructuralFeaturesCount(); i+=1) {
				toVisit.getEStructuralFeatures(i).accept(this);
				EStructuralFeature child = popObject(EStructuralFeature.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEStructuralFeatures(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEStructuralFeatures(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEGenericSuperTypesCount(); i+=1) {
				toVisit.getEGenericSuperTypes(i).accept(this);
				EGenericType child = popObject(EGenericType.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEGenericSuperTypes(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEGenericSuperTypes(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEAllGenericSuperTypesCount(); i+=1) {
				EGenericType child = toVisit.getEAllGenericSuperTypes(i);
				if (hasReplacement(child) ) {
					EGenericType replacement = (EGenericType) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllGenericSuperTypes(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllGenericSuperTypes(i);
						i -= 1;
					}
				}
			}
			if ( toVisit.getEIDAttributeForTest() != null && hasReplacement(toVisit.getEIDAttributeForTest()) ) {
				EAttribute child = (EAttribute) getClone(toVisit.getEIDAttributeForTest());
				toVisit.setEIDAttributeForTest(child);
			}
			for ( int i=0; i<toVisit.getEAllAttributesForTestCount(); i+=1) {
				EAttribute child = toVisit.getEAllAttributesForTest(i);
				if (hasReplacement(child) ) {
					EAttribute replacement = (EAttribute) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEAllAttributesForTest(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEAllAttributesForTest(i);
						i -= 1;
					}
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEDataType(EDataType toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEPackage() != null && hasReplacement(toVisit.getEPackage()) ) {
				EPackage child = (EPackage) getClone(toVisit.getEPackage());
				toVisit.setEPackage(child);
			}
			for ( int i=0; i<toVisit.getETypeParametersCount(); i+=1) {
				toVisit.getETypeParameters(i).accept(this);
				ETypeParameter child = popObject(ETypeParameter.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setETypeParameters(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeETypeParameters(i);
					i -= 1;
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEEnum(EEnum toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEPackage() != null && hasReplacement(toVisit.getEPackage()) ) {
				EPackage child = (EPackage) getClone(toVisit.getEPackage());
				toVisit.setEPackage(child);
			}
			for ( int i=0; i<toVisit.getETypeParametersCount(); i+=1) {
				toVisit.getETypeParameters(i).accept(this);
				ETypeParameter child = popObject(ETypeParameter.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setETypeParameters(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeETypeParameters(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getELiteralsCount(); i+=1) {
				toVisit.getELiterals(i).accept(this);
				EEnumLiteral child = popObject(EEnumLiteral.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setELiterals(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeELiterals(i);
					i -= 1;
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEEnumLiteral(EEnumLiteral toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEEnum() != null && hasReplacement(toVisit.getEEnum()) ) {
				EEnum child = (EEnum) getClone(toVisit.getEEnum());
				toVisit.setEEnum(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitEFactory(EFactory toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEPackage() != null && hasReplacement(toVisit.getEPackage()) ) {
				EPackage child = (EPackage) getClone(toVisit.getEPackage());
				toVisit.setEPackage(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitEObject(EObject toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			pushObject(toVisit);
		}
	}

	public void visitEOperation(EOperation toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEType() != null && hasReplacement(toVisit.getEType()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getEType());
				toVisit.setEType(child);
			}
			if ( toVisit.getEGenericType() != null ) {
				toVisit.getEGenericType().accept(this);
				toVisit.setEGenericType(popObject(EGenericType.class));
			}
			if ( toVisit.getEContainingClass() != null && hasReplacement(toVisit.getEContainingClass()) ) {
				EClass child = (EClass) getClone(toVisit.getEContainingClass());
				toVisit.setEContainingClass(child);
			}
			for ( int i=0; i<toVisit.getETypeParametersCount(); i+=1) {
				toVisit.getETypeParameters(i).accept(this);
				ETypeParameter child = popObject(ETypeParameter.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setETypeParameters(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeETypeParameters(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEParametersCount(); i+=1) {
				toVisit.getEParameters(i).accept(this);
				EParameter child = popObject(EParameter.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEParameters(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEParameters(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEExceptionsCount(); i+=1) {
				EClassifier child = toVisit.getEExceptions(i);
				if (hasReplacement(child) ) {
					EClassifier replacement = (EClassifier) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEExceptions(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEExceptions(i);
						i -= 1;
					}
				}
			}
			for ( int i=0; i<toVisit.getEGenericExceptionsCount(); i+=1) {
				toVisit.getEGenericExceptions(i).accept(this);
				EGenericType child = popObject(EGenericType.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEGenericExceptions(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEGenericExceptions(i);
					i -= 1;
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEPackage(EPackage toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEFactoryInstance() != null && hasReplacement(toVisit.getEFactoryInstance()) ) {
				EFactory child = (EFactory) getClone(toVisit.getEFactoryInstance());
				toVisit.setEFactoryInstance(child);
			}
			for ( int i=0; i<toVisit.getEClassifiersCount(); i+=1) {
				toVisit.getEClassifiers(i).accept(this);
				EClassifier child = popObject(EClassifier.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEClassifiers(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEClassifiers(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getESubpackagesCount(); i+=1) {
				toVisit.getESubpackages(i).accept(this);
				EPackage child = popObject(EPackage.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setESubpackages(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeESubpackages(i);
					i -= 1;
				}
			}
			if ( toVisit.getESuperPackage() != null && hasReplacement(toVisit.getESuperPackage()) ) {
				EPackage child = (EPackage) getClone(toVisit.getESuperPackage());
				toVisit.setESuperPackage(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitEParameter(EParameter toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEType() != null && hasReplacement(toVisit.getEType()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getEType());
				toVisit.setEType(child);
			}
			if ( toVisit.getEGenericType() != null ) {
				toVisit.getEGenericType().accept(this);
				toVisit.setEGenericType(popObject(EGenericType.class));
			}
			if ( toVisit.getEOperation() != null && hasReplacement(toVisit.getEOperation()) ) {
				EOperation child = (EOperation) getClone(toVisit.getEOperation());
				toVisit.setEOperation(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitEReference(EReference toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			if ( toVisit.getEType() != null && hasReplacement(toVisit.getEType()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getEType());
				toVisit.setEType(child);
			}
			if ( toVisit.getEGenericType() != null ) {
				toVisit.getEGenericType().accept(this);
				toVisit.setEGenericType(popObject(EGenericType.class));
			}
			if ( toVisit.getEContainingClass() != null && hasReplacement(toVisit.getEContainingClass()) ) {
				EClass child = (EClass) getClone(toVisit.getEContainingClass());
				toVisit.setEContainingClass(child);
			}
			if ( toVisit.getEOpposite() != null && hasReplacement(toVisit.getEOpposite()) ) {
				EReference child = (EReference) getClone(toVisit.getEOpposite());
				toVisit.setEOpposite(child);
			}
			if ( toVisit.getEReferenceType() != null && hasReplacement(toVisit.getEReferenceType()) ) {
				EClass child = (EClass) getClone(toVisit.getEReferenceType());
				toVisit.setEReferenceType(child);
			}
			for ( int i=0; i<toVisit.getEKeysCount(); i+=1) {
				EAttribute child = toVisit.getEKeys(i);
				if (hasReplacement(child) ) {
					EAttribute replacement = (EAttribute) getReplacement(child);
					if (replacement != null) {
						// Replaces by replacement.
						toVisit.setEKeys(i, replacement);
					} else {
						// Removes child (decrease i to correct indexing).
						toVisit.removeEKeys(i);
						i -= 1;
					}
				}
			}
			pushObject(toVisit);
		}
	}

	public void visitEGenericType(EGenericType toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			if ( toVisit.getEUpperBound() != null ) {
				toVisit.getEUpperBound().accept(this);
				toVisit.setEUpperBound(popObject(EGenericType.class));
			}
			for ( int i=0; i<toVisit.getETypeArgumentsCount(); i+=1) {
				toVisit.getETypeArguments(i).accept(this);
				EGenericType child = popObject(EGenericType.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setETypeArguments(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeETypeArguments(i);
					i -= 1;
				}
			}
			if ( toVisit.getERawType() != null && hasReplacement(toVisit.getERawType()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getERawType());
				toVisit.setERawType(child);
			}
			if ( toVisit.getELowerBound() != null ) {
				toVisit.getELowerBound().accept(this);
				toVisit.setELowerBound(popObject(EGenericType.class));
			}
			if ( toVisit.getETypeParameter() != null && hasReplacement(toVisit.getETypeParameter()) ) {
				ETypeParameter child = (ETypeParameter) getClone(toVisit.getETypeParameter());
				toVisit.setETypeParameter(child);
			}
			if ( toVisit.getEClassifier() != null && hasReplacement(toVisit.getEClassifier()) ) {
				EClassifier child = (EClassifier) getClone(toVisit.getEClassifier());
				toVisit.setEClassifier(child);
			}
			pushObject(toVisit);
		}
	}

	public void visitETypeParameter(ETypeParameter toVisit) {
		if ( hasContainmentReplacement(toVisit) ) {
			EObject replacement = getContainmentReplacement(toVisit);
			if ( replacement != null ) {
				replacement.accept(this);
			} else {
				pushObject(null);
			}
		} else {
			for ( int i=0; i<toVisit.getEAnnotationsCount(); i+=1) {
				toVisit.getEAnnotations(i).accept(this);
				EAnnotation child = popObject(EAnnotation.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEAnnotations(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEAnnotations(i);
					i -= 1;
				}
			}
			for ( int i=0; i<toVisit.getEBoundsCount(); i+=1) {
				toVisit.getEBounds(i).accept(this);
				EGenericType child = popObject(EGenericType.class);
				if (child != null) {
					// Replaces by child.
					toVisit.setEBounds(i, child);
				} else {
					// Removes child (decrease i to correct indexing).
					toVisit.removeEBounds(i);
					i -= 1;
				}
			}
			pushObject(toVisit);
		}
	}

}

