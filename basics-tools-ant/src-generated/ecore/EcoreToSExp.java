package ecore;

import org.xid.basics.sexp.model.Referencer;
import org.xid.basics.sexp.model.ModelToSExp;
import org.xid.basics.sexp.SList;
import org.xid.basics.sexp.SExp;
import org.xid.basics.sexp.S;
import java.util.Stack;
import java.util.Set;
import java.io.IOException;
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
 * SExp writer for model 'ecore'.
 */
public class EcoreToSExp implements ecore.EcoreVisitor {

	private final ModelToSExp context;

	private final Stack<SExp> sexpStack = new Stack<SExp>();

	public EcoreToSExp(Referencer referencer) {
		this.context = new ModelToSExp(referencer);
	}

	public void pushSExp(SExp sexp) {
		sexpStack.push(sexp);
	}

	public SExp popSExp() {
		return sexpStack.pop();
	}

	public void visitEAttribute(EAttribute toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eAttribute"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		if ( toVisit.isOrdered() ) {
			result.addChild(S.booleanToSExp("ordered", toVisit.isOrdered()));
		}
		if ( toVisit.isUnique() ) {
			result.addChild(S.booleanToSExp("unique", toVisit.isUnique()));
		}
		S.addChildIfNotNull(result, S.intToSExp("lowerBound", toVisit.getLowerBound()));
		S.addChildIfNotNull(result, S.intToSExp("upperBound", toVisit.getUpperBound()));
		if ( toVisit.isChangeable() ) {
			result.addChild(S.booleanToSExp("changeable", toVisit.isChangeable()));
		}
		if ( toVisit.isVolatile() ) {
			result.addChild(S.booleanToSExp("volatile", toVisit.isVolatile()));
		}
		if ( toVisit.isTransient() ) {
			result.addChild(S.booleanToSExp("transient", toVisit.isTransient()));
		}
		S.addChildIfNotNull(result, S.stringToSExp("defaultValueLiteral", toVisit.getDefaultValueLiteral()));
		if ( toVisit.isUnsettable() ) {
			result.addChild(S.booleanToSExp("unsettable", toVisit.isUnsettable()));
		}
		if ( toVisit.isDerived() ) {
			result.addChild(S.booleanToSExp("derived", toVisit.isDerived()));
		}
		if ( toVisit.isID() ) {
			result.addChild(S.booleanToSExp("iD", toVisit.isID()));
		}
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEType() != null ) {
			SExp referenceSExp = S.slist(S.satom("eType"));
			referenceSExp.addChild(context.createReference(toVisit.getEType()));
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericType() != null) {
			SExp referenceSExp = S.slist(S.satom("eGenericType"));
			toVisit.getEGenericType().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEAnnotation(EAnnotation toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eAnnotation"));
		S.addChildIfNotNull(result, S.stringToSExp("source", toVisit.getSource()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getContentsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("contentsSet"));
			for (EObject child : toVisit.getContentsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getReferencesCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("referencesSet"));
			for (EObject child : toVisit.getReferencesList()) {
				referenceSExp.addChild(context.createReference(child));
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEClass(EClass toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eClass"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceClassName", toVisit.getInstanceClassName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceTypeName", toVisit.getInstanceTypeName()));
		if ( toVisit.isAbstract() ) {
			result.addChild(S.booleanToSExp("abstract", toVisit.isAbstract()));
		}
		if ( toVisit.isInterface() ) {
			result.addChild(S.booleanToSExp("interface", toVisit.isInterface()));
		}
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getETypeParametersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eTypeParametersSet"));
			for (ETypeParameter child : toVisit.getETypeParametersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getESuperTypesCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eSuperTypesSet"));
			for (EClass child : toVisit.getESuperTypesList()) {
				referenceSExp.addChild(context.createReference(child));
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEOperationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eOperationsSet"));
			for (EOperation child : toVisit.getEOperationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEStructuralFeaturesCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eStructuralFeaturesSet"));
			for (EStructuralFeature child : toVisit.getEStructuralFeaturesList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericSuperTypesCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eGenericSuperTypesSet"));
			for (EGenericType child : toVisit.getEGenericSuperTypesList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEDataType(EDataType toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eDataType"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceClassName", toVisit.getInstanceClassName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceTypeName", toVisit.getInstanceTypeName()));
		if ( toVisit.isSerializable() ) {
			result.addChild(S.booleanToSExp("serializable", toVisit.isSerializable()));
		}
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getETypeParametersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eTypeParametersSet"));
			for (ETypeParameter child : toVisit.getETypeParametersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEEnum(EEnum toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eEnum"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceClassName", toVisit.getInstanceClassName()));
		S.addChildIfNotNull(result, S.stringToSExp("instanceTypeName", toVisit.getInstanceTypeName()));
		if ( toVisit.isSerializable() ) {
			result.addChild(S.booleanToSExp("serializable", toVisit.isSerializable()));
		}
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getETypeParametersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eTypeParametersSet"));
			for (ETypeParameter child : toVisit.getETypeParametersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getELiteralsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eLiteralsSet"));
			for (EEnumLiteral child : toVisit.getELiteralsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEEnumLiteral(EEnumLiteral toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eEnumLiteral"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		S.addChildIfNotNull(result, S.intToSExp("value", toVisit.getValue()));
		S.addChildIfNotNull(result, S.stringToSExp("literal", toVisit.getLiteral()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEFactory(EFactory toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eFactory"));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEObject(EObject toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eObject"));
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEOperation(EOperation toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eOperation"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		if ( toVisit.isOrdered() ) {
			result.addChild(S.booleanToSExp("ordered", toVisit.isOrdered()));
		}
		if ( toVisit.isUnique() ) {
			result.addChild(S.booleanToSExp("unique", toVisit.isUnique()));
		}
		S.addChildIfNotNull(result, S.intToSExp("lowerBound", toVisit.getLowerBound()));
		S.addChildIfNotNull(result, S.intToSExp("upperBound", toVisit.getUpperBound()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEType() != null ) {
			SExp referenceSExp = S.slist(S.satom("eType"));
			referenceSExp.addChild(context.createReference(toVisit.getEType()));
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericType() != null) {
			SExp referenceSExp = S.slist(S.satom("eGenericType"));
			toVisit.getEGenericType().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		if (toVisit.getETypeParametersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eTypeParametersSet"));
			for (ETypeParameter child : toVisit.getETypeParametersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEParametersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eParametersSet"));
			for (EParameter child : toVisit.getEParametersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEExceptionsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eExceptionsSet"));
			for (EClassifier child : toVisit.getEExceptionsList()) {
				referenceSExp.addChild(context.createReference(child));
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericExceptionsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eGenericExceptionsSet"));
			for (EGenericType child : toVisit.getEGenericExceptionsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEPackage(EPackage toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("ePackage"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		S.addChildIfNotNull(result, S.stringToSExp("nsURI", toVisit.getNsURI()));
		S.addChildIfNotNull(result, S.stringToSExp("nsPrefix", toVisit.getNsPrefix()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEClassifiersCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eClassifiersSet"));
			for (EClassifier child : toVisit.getEClassifiersList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getESubpackagesCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eSubpackagesSet"));
			for (EPackage child : toVisit.getESubpackagesList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEParameter(EParameter toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eParameter"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		if ( toVisit.isOrdered() ) {
			result.addChild(S.booleanToSExp("ordered", toVisit.isOrdered()));
		}
		if ( toVisit.isUnique() ) {
			result.addChild(S.booleanToSExp("unique", toVisit.isUnique()));
		}
		S.addChildIfNotNull(result, S.intToSExp("lowerBound", toVisit.getLowerBound()));
		S.addChildIfNotNull(result, S.intToSExp("upperBound", toVisit.getUpperBound()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEType() != null ) {
			SExp referenceSExp = S.slist(S.satom("eType"));
			referenceSExp.addChild(context.createReference(toVisit.getEType()));
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericType() != null) {
			SExp referenceSExp = S.slist(S.satom("eGenericType"));
			toVisit.getEGenericType().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEReference(EReference toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eReference"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		if ( toVisit.isOrdered() ) {
			result.addChild(S.booleanToSExp("ordered", toVisit.isOrdered()));
		}
		if ( toVisit.isUnique() ) {
			result.addChild(S.booleanToSExp("unique", toVisit.isUnique()));
		}
		S.addChildIfNotNull(result, S.intToSExp("lowerBound", toVisit.getLowerBound()));
		S.addChildIfNotNull(result, S.intToSExp("upperBound", toVisit.getUpperBound()));
		if ( toVisit.isChangeable() ) {
			result.addChild(S.booleanToSExp("changeable", toVisit.isChangeable()));
		}
		if ( toVisit.isVolatile() ) {
			result.addChild(S.booleanToSExp("volatile", toVisit.isVolatile()));
		}
		if ( toVisit.isTransient() ) {
			result.addChild(S.booleanToSExp("transient", toVisit.isTransient()));
		}
		S.addChildIfNotNull(result, S.stringToSExp("defaultValueLiteral", toVisit.getDefaultValueLiteral()));
		if ( toVisit.isUnsettable() ) {
			result.addChild(S.booleanToSExp("unsettable", toVisit.isUnsettable()));
		}
		if ( toVisit.isDerived() ) {
			result.addChild(S.booleanToSExp("derived", toVisit.isDerived()));
		}
		if ( toVisit.isContainment() ) {
			result.addChild(S.booleanToSExp("containment", toVisit.isContainment()));
		}
		if ( toVisit.isResolveProxies() ) {
			result.addChild(S.booleanToSExp("resolveProxies", toVisit.isResolveProxies()));
		}
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEType() != null ) {
			SExp referenceSExp = S.slist(S.satom("eType"));
			referenceSExp.addChild(context.createReference(toVisit.getEType()));
			result.addChild(referenceSExp);
		}
		if (toVisit.getEGenericType() != null) {
			SExp referenceSExp = S.slist(S.satom("eGenericType"));
			toVisit.getEGenericType().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEOpposite() != null ) {
			SExp referenceSExp = S.slist(S.satom("eOpposite"));
			referenceSExp.addChild(context.createReference(toVisit.getEOpposite()));
			result.addChild(referenceSExp);
		}
		if (toVisit.getEKeysCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eKeysSet"));
			for (EAttribute child : toVisit.getEKeysList()) {
				referenceSExp.addChild(context.createReference(child));
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitEGenericType(EGenericType toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eGenericType"));
		if (toVisit.getEUpperBound() != null) {
			SExp referenceSExp = S.slist(S.satom("eUpperBound"));
			toVisit.getEUpperBound().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		if (toVisit.getETypeArgumentsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eTypeArgumentsSet"));
			for (EGenericType child : toVisit.getETypeArgumentsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getELowerBound() != null) {
			SExp referenceSExp = S.slist(S.satom("eLowerBound"));
			toVisit.getELowerBound().accept(this);
			referenceSExp.addChild(popSExp());
			result.addChild(referenceSExp);
		}
		if ( toVisit.getETypeParameter() != null ) {
			SExp referenceSExp = S.slist(S.satom("eTypeParameter"));
			referenceSExp.addChild(context.createReference(toVisit.getETypeParameter()));
			result.addChild(referenceSExp);
		}
		if ( toVisit.getEClassifier() != null ) {
			SExp referenceSExp = S.slist(S.satom("eClassifier"));
			referenceSExp.addChild(context.createReference(toVisit.getEClassifier()));
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public void visitETypeParameter(ETypeParameter toVisit) {
		context.push(toVisit);
		SList result = new SList();
		result.addChild(S.satom("eTypeParameter"));
		S.addChildIfNotNull(result, S.stringToSExp("name", toVisit.getName()));
		if (toVisit.getEAnnotationsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eAnnotationsSet"));
			for (EAnnotation child : toVisit.getEAnnotationsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		if (toVisit.getEBoundsCount() > 0) {
			SExp referenceSExp = S.slist(S.satom("eBoundsSet"));
			for (EGenericType child : toVisit.getEBoundsList()) {
				child.accept(this);
				referenceSExp.addChild(popSExp());
			}
			result.addChild(referenceSExp);
		}
		context.pop(toVisit);
		pushSExp(result);
	}

	public SExp getResult() throws IOException {
		// checks for missing objects and sends an exception if needed.
		Set<Object> missingObjects = missingObjects();
		if ( missingObjects.isEmpty() == false ) {
			StringBuilder message = new StringBuilder();
			message.append("Missing object(s): ");
			int length = message.length();
			for (Object object : missingObjects ) {
				if ( message.length() > length) {
					message.append(", ");
				}
				message.append(object);
			}
			message.append(".");
			
			throw new IOException(message.toString());
			}
		return popSExp();
	}

	public Set<Object> missingObjects() {
		return context.missingObjects();
	}

}

