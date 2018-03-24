package ecore;

import java.util.Stack;
import ecore.EcoreWalker;
import ecore.EcoreVisitor;
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
 * Walker for package 'ecore'. A walker navigates throught objects using the containment tree and visit each node with the given visitor.
 */
public class EcoreWalker implements EcoreVisitor {

	/**
	 * Delegate visitor.
	 */
	private final EcoreVisitor delegate;

	private final Stack<EObject> parentStack = new Stack<EObject>();

	public EcoreWalker(EcoreVisitor delegate) {
		this.delegate = delegate;
	}

	/**
	 * Returns the parent stack for current visited object.
	 */
	public Stack<EObject> getParentStack() {
		return parentStack;
	}

	public void visitEAttribute(EAttribute toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEAnnotation(EAnnotation toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( EObject child : toVisit.getContentsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEClass(EClass toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
		}
		for ( EOperation child : toVisit.getEOperationsList()) {
			child.accept(this);
		}
		for ( EStructuralFeature child : toVisit.getEStructuralFeaturesList()) {
			child.accept(this);
		}
		for ( EGenericType child : toVisit.getEGenericSuperTypesList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEDataType(EDataType toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEEnum(EEnum toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
		}
		for ( EEnumLiteral child : toVisit.getELiteralsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEEnumLiteral(EEnumLiteral toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEFactory(EFactory toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEObject(EObject toVisit) {
		parentStack.push(toVisit);
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEOperation(EOperation toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
		}
		for ( ETypeParameter child : toVisit.getETypeParametersList()) {
			child.accept(this);
		}
		for ( EParameter child : toVisit.getEParametersList()) {
			child.accept(this);
		}
		for ( EGenericType child : toVisit.getEGenericExceptionsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEPackage(EPackage toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( EClassifier child : toVisit.getEClassifiersList()) {
			child.accept(this);
		}
		for ( EPackage child : toVisit.getESubpackagesList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEParameter(EParameter toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEReference(EReference toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		if ( toVisit.getEGenericType() != null ) {
			toVisit.getEGenericType().accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitEGenericType(EGenericType toVisit) {
		parentStack.push(toVisit);
		if ( toVisit.getEUpperBound() != null ) {
			toVisit.getEUpperBound().accept(this);
		}
		for ( EGenericType child : toVisit.getETypeArgumentsList()) {
			child.accept(this);
		}
		if ( toVisit.getELowerBound() != null ) {
			toVisit.getELowerBound().accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

	public void visitETypeParameter(ETypeParameter toVisit) {
		parentStack.push(toVisit);
		for ( EAnnotation child : toVisit.getEAnnotationsList()) {
			child.accept(this);
		}
		for ( EGenericType child : toVisit.getEBoundsList()) {
			child.accept(this);
		}
		parentStack.pop();
		toVisit.accept(delegate);
	}

}

