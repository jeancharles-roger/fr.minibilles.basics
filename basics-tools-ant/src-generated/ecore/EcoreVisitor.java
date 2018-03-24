package ecore;

import ecore.EcoreVisitor;
import ecore.ETypeParameter;
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
import ecore.EClass;
import ecore.EAttribute;
import ecore.EAnnotation;

/**
 * Visitor interface for package 'ecore'.
 */
public interface EcoreVisitor {

	/**
	 * Empty visitor implementation for package 'ecore'.
	 */
	public static class Stub implements EcoreVisitor {

		/**
		 * Empty visit method for EAttribute.
		 */
		public void visitEAttribute(EAttribute toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EAnnotation.
		 */
		public void visitEAnnotation(EAnnotation toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EClass.
		 */
		public void visitEClass(EClass toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EDataType.
		 */
		public void visitEDataType(EDataType toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EEnum.
		 */
		public void visitEEnum(EEnum toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EEnumLiteral.
		 */
		public void visitEEnumLiteral(EEnumLiteral toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EFactory.
		 */
		public void visitEFactory(EFactory toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EObject.
		 */
		public void visitEObject(EObject toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EOperation.
		 */
		public void visitEOperation(EOperation toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EPackage.
		 */
		public void visitEPackage(EPackage toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EParameter.
		 */
		public void visitEParameter(EParameter toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EReference.
		 */
		public void visitEReference(EReference toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for EGenericType.
		 */
		public void visitEGenericType(EGenericType toVisit) {
			//do nothing
		}

		/**
		 * Empty visit method for ETypeParameter.
		 */
		public void visitETypeParameter(ETypeParameter toVisit) {
			//do nothing
		}

	}

	/**
	 * Visit method for EAttribute.
	 */
	void visitEAttribute(EAttribute toVisit);
	

	/**
	 * Visit method for EAnnotation.
	 */
	void visitEAnnotation(EAnnotation toVisit);
	

	/**
	 * Visit method for EClass.
	 */
	void visitEClass(EClass toVisit);
	

	/**
	 * Visit method for EDataType.
	 */
	void visitEDataType(EDataType toVisit);
	

	/**
	 * Visit method for EEnum.
	 */
	void visitEEnum(EEnum toVisit);
	

	/**
	 * Visit method for EEnumLiteral.
	 */
	void visitEEnumLiteral(EEnumLiteral toVisit);
	

	/**
	 * Visit method for EFactory.
	 */
	void visitEFactory(EFactory toVisit);
	

	/**
	 * Visit method for EObject.
	 */
	void visitEObject(EObject toVisit);
	

	/**
	 * Visit method for EOperation.
	 */
	void visitEOperation(EOperation toVisit);
	

	/**
	 * Visit method for EPackage.
	 */
	void visitEPackage(EPackage toVisit);
	

	/**
	 * Visit method for EParameter.
	 */
	void visitEParameter(EParameter toVisit);
	

	/**
	 * Visit method for EReference.
	 */
	void visitEReference(EReference toVisit);
	

	/**
	 * Visit method for EGenericType.
	 */
	void visitEGenericType(EGenericType toVisit);
	

	/**
	 * Visit method for ETypeParameter.
	 */
	void visitETypeParameter(ETypeParameter toVisit);
	

}

