package ecore;

import org.xid.basics.sexp.model.SExpToModel;
import org.xid.basics.sexp.model.Referencer;
import org.xid.basics.sexp.VariableResolver;
import org.xid.basics.sexp.SVariable;
import org.xid.basics.sexp.SExp;
import org.xid.basics.sexp.S;
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
 * SExp reader for model 'ecore'.
 */
public class SExpToEcore {

	private final SExpToModel context;

	private final VariableResolver resolver;

	public SExpToEcore(Referencer referencer) {
		this(referencer, new VariableResolver.Mapped());
	}

	public SExpToEcore(Referencer referencer, VariableResolver resolver) {
		this.context = new SExpToModel(referencer, resolver);
		this.resolver = resolver;
	}

	private EAttribute createEAttribute(SExp sexp) throws IOException {
		EAttribute result = new EAttribute();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "ordered".equals(type) ) {
				result.setOrdered(S.sexpToBoolean(currentSexp) );
			} else
			if ( "unique".equals(type) ) {
				result.setUnique(S.sexpToBoolean(currentSexp) );
			} else
			if ( "lowerBound".equals(type) ) {
				result.setLowerBound(S.sexpToInt(currentSexp) );
			} else
			if ( "upperBound".equals(type) ) {
				result.setUpperBound(S.sexpToInt(currentSexp) );
			} else
			if ( "changeable".equals(type) ) {
				result.setChangeable(S.sexpToBoolean(currentSexp) );
			} else
			if ( "volatile".equals(type) ) {
				result.setVolatile(S.sexpToBoolean(currentSexp) );
			} else
			if ( "transient".equals(type) ) {
				result.setTransient(S.sexpToBoolean(currentSexp) );
			} else
			if ( "defaultValueLiteral".equals(type) ) {
				result.setDefaultValueLiteral(S.sexpToString(currentSexp) );
			} else
			if ( "unsettable".equals(type) ) {
				result.setUnsettable(S.sexpToBoolean(currentSexp) );
			} else
			if ( "derived".equals(type) ) {
				result.setDerived(S.sexpToBoolean(currentSexp) );
			} else
			if ( "iD".equals(type) ) {
				result.setID(S.sexpToBoolean(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eType".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eType", false, reference);
			} else
			if ( "eGenericType".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setEGenericType(child);
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EAttribute'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EAttribute'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EAnnotation createEAnnotation(SExp sexp) throws IOException {
		EAnnotation result = new EAnnotation();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "source".equals(type) ) {
				result.setSource(S.sexpToString(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "contentsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EObject child = create(EObject.class, currentSexp.getChild(i));
					result.addContents(child);
				}
			} else
			if ( "referencesSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					SExp reference = currentSexp.getChild(i);
					context.registerReference(result, "references", true, reference);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EAnnotation'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EAnnotation'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EClass createEClass(SExp sexp) throws IOException {
		EClass result = new EClass();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceClassName".equals(type) ) {
				result.setInstanceClassName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceTypeName".equals(type) ) {
				result.setInstanceTypeName(S.sexpToString(currentSexp) );
			} else
			if ( "abstract".equals(type) ) {
				result.setAbstract(S.sexpToBoolean(currentSexp) );
			} else
			if ( "interface".equals(type) ) {
				result.setInterface(S.sexpToBoolean(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eTypeParametersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					ETypeParameter child = create(ETypeParameter.class, currentSexp.getChild(i));
					result.addETypeParameters(child);
				}
			} else
			if ( "eSuperTypesSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					SExp reference = currentSexp.getChild(i);
					context.registerReference(result, "eSuperTypes", true, reference);
				}
			} else
			if ( "eOperationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EOperation child = create(EOperation.class, currentSexp.getChild(i));
					result.addEOperations(child);
				}
			} else
			if ( "eStructuralFeaturesSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EStructuralFeature child = create(EStructuralFeature.class, currentSexp.getChild(i));
					result.addEStructuralFeatures(child);
				}
			} else
			if ( "eGenericSuperTypesSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EGenericType child = create(EGenericType.class, currentSexp.getChild(i));
					result.addEGenericSuperTypes(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EClass'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EClass'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EDataType createEDataType(SExp sexp) throws IOException {
		EDataType result = new EDataType();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceClassName".equals(type) ) {
				result.setInstanceClassName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceTypeName".equals(type) ) {
				result.setInstanceTypeName(S.sexpToString(currentSexp) );
			} else
			if ( "serializable".equals(type) ) {
				result.setSerializable(S.sexpToBoolean(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eTypeParametersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					ETypeParameter child = create(ETypeParameter.class, currentSexp.getChild(i));
					result.addETypeParameters(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EDataType'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EDataType'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EEnum createEEnum(SExp sexp) throws IOException {
		EEnum result = new EEnum();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceClassName".equals(type) ) {
				result.setInstanceClassName(S.sexpToString(currentSexp) );
			} else
			if ( "instanceTypeName".equals(type) ) {
				result.setInstanceTypeName(S.sexpToString(currentSexp) );
			} else
			if ( "serializable".equals(type) ) {
				result.setSerializable(S.sexpToBoolean(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eTypeParametersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					ETypeParameter child = create(ETypeParameter.class, currentSexp.getChild(i));
					result.addETypeParameters(child);
				}
			} else
			if ( "eLiteralsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EEnumLiteral child = create(EEnumLiteral.class, currentSexp.getChild(i));
					result.addELiterals(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EEnum'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EEnum'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EEnumLiteral createEEnumLiteral(SExp sexp) throws IOException {
		EEnumLiteral result = new EEnumLiteral();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "value".equals(type) ) {
				result.setValue(S.sexpToInt(currentSexp) );
			} else
			if ( "literal".equals(type) ) {
				result.setLiteral(S.sexpToString(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EEnumLiteral'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EEnumLiteral'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EFactory createEFactory(SExp sexp) throws IOException {
		EFactory result = new EFactory();
		
		int current = 1;
		int count = sexp.getChildCount();
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EFactory'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EFactory'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EObject createEObject(SExp sexp) throws IOException {
		EObject result = new EObject();
		
		return result;
	}

	private EOperation createEOperation(SExp sexp) throws IOException {
		EOperation result = new EOperation();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "ordered".equals(type) ) {
				result.setOrdered(S.sexpToBoolean(currentSexp) );
			} else
			if ( "unique".equals(type) ) {
				result.setUnique(S.sexpToBoolean(currentSexp) );
			} else
			if ( "lowerBound".equals(type) ) {
				result.setLowerBound(S.sexpToInt(currentSexp) );
			} else
			if ( "upperBound".equals(type) ) {
				result.setUpperBound(S.sexpToInt(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eType".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eType", false, reference);
			} else
			if ( "eGenericType".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setEGenericType(child);
			} else
			if ( "eTypeParametersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					ETypeParameter child = create(ETypeParameter.class, currentSexp.getChild(i));
					result.addETypeParameters(child);
				}
			} else
			if ( "eParametersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EParameter child = create(EParameter.class, currentSexp.getChild(i));
					result.addEParameters(child);
				}
			} else
			if ( "eExceptionsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					SExp reference = currentSexp.getChild(i);
					context.registerReference(result, "eExceptions", true, reference);
				}
			} else
			if ( "eGenericExceptionsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EGenericType child = create(EGenericType.class, currentSexp.getChild(i));
					result.addEGenericExceptions(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EOperation'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EOperation'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EPackage createEPackage(SExp sexp) throws IOException {
		EPackage result = new EPackage();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "nsURI".equals(type) ) {
				result.setNsURI(S.sexpToString(currentSexp) );
			} else
			if ( "nsPrefix".equals(type) ) {
				result.setNsPrefix(S.sexpToString(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eClassifiersSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EClassifier child = create(EClassifier.class, currentSexp.getChild(i));
					result.addEClassifiers(child);
				}
			} else
			if ( "eSubpackagesSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EPackage child = create(EPackage.class, currentSexp.getChild(i));
					result.addESubpackages(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EPackage'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EPackage'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EParameter createEParameter(SExp sexp) throws IOException {
		EParameter result = new EParameter();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "ordered".equals(type) ) {
				result.setOrdered(S.sexpToBoolean(currentSexp) );
			} else
			if ( "unique".equals(type) ) {
				result.setUnique(S.sexpToBoolean(currentSexp) );
			} else
			if ( "lowerBound".equals(type) ) {
				result.setLowerBound(S.sexpToInt(currentSexp) );
			} else
			if ( "upperBound".equals(type) ) {
				result.setUpperBound(S.sexpToInt(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eType".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eType", false, reference);
			} else
			if ( "eGenericType".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setEGenericType(child);
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EParameter'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EParameter'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EReference createEReference(SExp sexp) throws IOException {
		EReference result = new EReference();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			if ( "ordered".equals(type) ) {
				result.setOrdered(S.sexpToBoolean(currentSexp) );
			} else
			if ( "unique".equals(type) ) {
				result.setUnique(S.sexpToBoolean(currentSexp) );
			} else
			if ( "lowerBound".equals(type) ) {
				result.setLowerBound(S.sexpToInt(currentSexp) );
			} else
			if ( "upperBound".equals(type) ) {
				result.setUpperBound(S.sexpToInt(currentSexp) );
			} else
			if ( "changeable".equals(type) ) {
				result.setChangeable(S.sexpToBoolean(currentSexp) );
			} else
			if ( "volatile".equals(type) ) {
				result.setVolatile(S.sexpToBoolean(currentSexp) );
			} else
			if ( "transient".equals(type) ) {
				result.setTransient(S.sexpToBoolean(currentSexp) );
			} else
			if ( "defaultValueLiteral".equals(type) ) {
				result.setDefaultValueLiteral(S.sexpToString(currentSexp) );
			} else
			if ( "unsettable".equals(type) ) {
				result.setUnsettable(S.sexpToBoolean(currentSexp) );
			} else
			if ( "derived".equals(type) ) {
				result.setDerived(S.sexpToBoolean(currentSexp) );
			} else
			if ( "containment".equals(type) ) {
				result.setContainment(S.sexpToBoolean(currentSexp) );
			} else
			if ( "resolveProxies".equals(type) ) {
				result.setResolveProxies(S.sexpToBoolean(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eType".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eType", false, reference);
			} else
			if ( "eGenericType".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setEGenericType(child);
			} else
			if ( "eOpposite".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eOpposite", false, reference);
			} else
			if ( "eKeysSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					SExp reference = currentSexp.getChild(i);
					context.registerReference(result, "eKeys", true, reference);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EReference'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EReference'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private EGenericType createEGenericType(SExp sexp) throws IOException {
		EGenericType result = new EGenericType();
		
		int current = 1;
		int count = sexp.getChildCount();
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eUpperBound".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setEUpperBound(child);
			} else
			if ( "eTypeArgumentsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EGenericType child = create(EGenericType.class, currentSexp.getChild(i));
					result.addETypeArguments(child);
				}
			} else
			if ( "eLowerBound".equals(type) ) {
				EGenericType child = create(EGenericType.class, currentSexp.getChild(1));
				result.setELowerBound(child);
			} else
			if ( "eTypeParameter".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eTypeParameter", false, reference);
			} else
			if ( "eClassifier".equals(type) ) {
				SExp reference = currentSexp.getChild(1);
				context.registerReference(result, "eClassifier", false, reference);
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'EGenericType'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'EGenericType'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	private ETypeParameter createETypeParameter(SExp sexp) throws IOException {
		ETypeParameter result = new ETypeParameter();
		
		int current = 1;
		int count = sexp.getChildCount();
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "name".equals(type) ) {
				result.setName(S.sexpToString(currentSexp) );
			} else
			{
				//unknown, break.
				break;
			}
			current += 1;
		}
		
		context.push(result);
		while ( current < count ) {
			final SExp currentSexp = sexp.getChild(current);
			final String type = currentSexp.getConstructor();
			
			if ( "eAnnotationsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EAnnotation child = create(EAnnotation.class, currentSexp.getChild(i));
					result.addEAnnotations(child);
				}
			} else
			if ( "eBoundsSet".equals(type) ) {
				for (int i=1; i<currentSexp.getChildCount(); i++ ) {
					EGenericType child = create(EGenericType.class, currentSexp.getChild(i));
					result.addEBounds(child);
				}
			} else
			{
				//unknown, this is an error.
				StringBuilder message = new StringBuilder();
				if ( type == null ) {
					message.append("Unknown attribute format '");
					message.append(currentSexp);
					message.append("' for type 'ETypeParameter'.");
				} else {
					message.append("Unknown attribute '");
					message.append(type);
					message.append("' for type 'ETypeParameter'.");
				}
				throw new IOException(message.toString());
			}
			current += 1;
		}
		
		context.pop(result);
		return result;
	}

	/**
	 * Creates an instance of T from given SExp. It create an instance.
	 * and tries to resolve references. If a reference isn't resolved, it
	 * throws an IOException.
	 */
	public <T> T to(Class<T> klass, SExp sexp) throws IOException {
		// calls general create method
		T result = create(klass, sexp);
		
		// checks for pending references and sends an exception if needed.
		Set<String> unresolvedReferences = unresolvedReferences();
		if ( unresolvedReferences.isEmpty() == false ) {
			StringBuilder message = new StringBuilder();
			message.append("Unresolved reference(s): ");
			int length = message.length();
			for (String reference : unresolvedReferences ) {
				if ( message.length() > length) {
					message.append(", ");
				}
				message.append(reference);
			}
			message.append(".");
			
			throw new IOException(message.toString());
			}
		
		return result;
	}

	/**
	 * Creates an instance of T from given SExp. It create an instance.
	 * and tries to resolve references. If a reference isn't resolved, it
	 * keeps them stored for later calls to create.
	 */
	public <T> T create(Class<T> klass, SExp sexp) throws IOException {
		if (sexp.isVariable() ) {
			// checks variable case
			return resolver.resolve(((SVariable) sexp).getName(), klass);
		}
		
		// not a variable
		String type = sexp.getConstructor();
		Object result = null;
		
		if ( "eAttribute".equals(type) ) {
			result = createEAttribute(sexp);
			
		} else
		if ( "eAnnotation".equals(type) ) {
			result = createEAnnotation(sexp);
			
		} else
		if ( "eClass".equals(type) ) {
			result = createEClass(sexp);
			
		} else
		if ( "eDataType".equals(type) ) {
			result = createEDataType(sexp);
			
		} else
		if ( "eEnum".equals(type) ) {
			result = createEEnum(sexp);
			
		} else
		if ( "eEnumLiteral".equals(type) ) {
			result = createEEnumLiteral(sexp);
			
		} else
		if ( "eFactory".equals(type) ) {
			result = createEFactory(sexp);
			
		} else
		if ( "eObject".equals(type) ) {
			result = createEObject(sexp);
			
		} else
		if ( "eOperation".equals(type) ) {
			result = createEOperation(sexp);
			
		} else
		if ( "ePackage".equals(type) ) {
			result = createEPackage(sexp);
			
		} else
		if ( "eParameter".equals(type) ) {
			result = createEParameter(sexp);
			
		} else
		if ( "eReference".equals(type) ) {
			result = createEReference(sexp);
			
		} else
		if ( "eGenericType".equals(type) ) {
			result = createEGenericType(sexp);
			
		} else
		if ( "eTypeParameter".equals(type) ) {
			result = createETypeParameter(sexp);
			
		} else
		{
			StringBuilder message = new StringBuilder();
			if ( type == null ) {
				message.append("Can't create a '");
				message.append(klass.getSimpleName());
				message.append("' instance from '");
				message.append(sexp);
				message.append("'.");
			} else {
				message.append("Unknown type '");
				message.append(type);
				message.append("'.");
			}
			throw new IOException(message.toString());
		}
		
		
		//return casted result.
		//If result isn't of klass, it throws a ClassCastException.
		return result == null ? null : klass.cast(result);
	}

	public Set<String> unresolvedReferences() {
		return context.unresolvedReferences();
	}

}

