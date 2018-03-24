package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class SExpToModelClassGenerator {


	/** Object that calls generator. */
	private final ModelGenerator caller;
	
	private final boolean recursive;

	public SExpToModelClassGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}

	private List<EClass> concreteClassList(EPackage source) {
		List<EClass> concreteClassList = new ArrayList<EClass>();
		for ( EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				EClass klass = (EClass) classifier;
				// adds klass to list if itsn't abstract or an interface
				if ( klass.isAbstract() == false && klass.isInterface() == false ) {
					concreteClassList.add(klass);
				}
			}
		}
		if ( recursive ) {
			for ( EPackage child : source.getESubpackages() ) {
				concreteClassList.addAll(concreteClassList(child));
			}
		}

		return concreteClassList;
	}
	
	private List<EAttribute> attributesToImplement(EClass source) {
		List<EAttribute> allAttributesToImplement = new ArrayList<EAttribute>();

		// searches attributes in source
		for ( EStructuralFeature feature : source.getEAllStructuralFeatures() ) {
			if ( feature.isTransient() == false && feature instanceof EAttribute ) {
				allAttributesToImplement.add((EAttribute) feature);
			}
		}
		return allAttributesToImplement;
	}
	
	private List<EReference> referencesToImplement(EClass source) {
		List<EReference> allReferencesToImplement = new ArrayList<EReference>();

		// searches attributes in source
		for ( EStructuralFeature feature : source.getEAllStructuralFeatures() ) {
			if ( feature.isTransient() == false && feature instanceof EReference ) {
				allReferencesToImplement.add((EReference) feature);
			}
		}
		
		return allReferencesToImplement;
	}

	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		String className = "SExpTo" + CodeGenUtil.capName(source.getName());
		// protects class name from imports
		caller.getDependencyManager().getShortName(className);

		// starts class
		content.beginFile(className + ".java");
		content.markImports();
		
		StringBuilder comment = new StringBuilder();
		comment.append("SExp reader for model '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and children");
		}
		comment.append("."); 

		content.comment(Java.JAVA_DOC, 0, comment.toString());

		content.beginClass(Java.PUBLIC, className, null, null);

		// adds context
		String sexpToModelShortName = caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.model.SExpToModel");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, sexpToModelShortName, "context");
		content.endAttribute("context");
		
		String variableResolverShortName = caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.VariableResolver");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, variableResolverShortName, "resolver");
		content.endAttribute("resolver");
		
		// constructors
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.model.Referencer");
		content.beginMethod(Java.PUBLIC, null, className, null, 
					new Java.Parameter(Java.NONE, "Referencer", "referencer")
				);
		content.codeln(0, "this(referencer, new " + variableResolverShortName +".Mapped());");
		content.endMethod(className);
		
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.model.Referencer");
		content.beginMethod(Java.PUBLIC, null, className, null, 
				new Java.Parameter(Java.NONE, "Referencer", "referencer"),
				new Java.Parameter(Java.NONE, variableResolverShortName, "resolver")
			);
		content.codeln(0, "this.context = new "+ sexpToModelShortName +"(referencer, resolver);");
		content.codeln(0, "this.resolver = resolver;");
		content.endMethod(className);
		
		List<EClass> concreteClassList = concreteClassList(source);
		
		
		// generates create method
		for ( EClass eClass : concreteClassList ) {
			generateCreateMethod(eClass, content);
		}
		
		// generates main to
		generateMainToMethod(concreteClassList, content);
		
		// generates main create
		generateMainCreateMethod(concreteClassList, content);

		// generates unresolved references method
		final String setShortName = caller.getDependencyManager().getShortName("java.util.Set");
		content.beginMethod(Java.PUBLIC, setShortName + "<String>", "unresolvedReferences", null);
		content.codeln(0, "return context.unresolvedReferences();");
		content.endMethod("unresolvedReferences");
		
		// ends class
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}

	private void generateMainToMethod(List<EClass> concreteClassList, JavaContentHandler content) {
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.SExp");
		
		content.comment(Java.JAVA_DOC, 0, 
		  "Creates an instance of T from given SExp. It create an instance.\n" +
		  "and tries to resolve references. If a reference isn't resolved, it\n" +
	      "throws an IOException.\n"
		);
		content.beginMethod(Java.PUBLIC, "<T> T", "to", "IOException",
				new Java.Parameter(Java.NONE, "Class<T>", "klass"),
				new Java.Parameter(Java.NONE, "SExp", "sexp")
			);
		
		
		content.comment(Java.SINGLE_LINE, 0, " calls general create method");
		content.codeln(0, "T result = create(klass, sexp);");
		content.codeln(0, "");
		
		final String setShortName = caller.getDependencyManager().getShortName("java.util.Set");
		content.comment(Java.SINGLE_LINE, 0, " checks for pending references and sends an exception if needed.");
		content.codeln(0, setShortName + "<String> unresolvedReferences = unresolvedReferences();");
		content.codeln(0, "if ( unresolvedReferences.isEmpty() == false ) {");
		content.codeln(1, "StringBuilder message = new StringBuilder();");
		content.codeln(1, "message.append(\"Unresolved reference(s): \");");
		content.codeln(1, "int length = message.length();");
		content.codeln(1, "for (String reference : unresolvedReferences ) {");
		content.codeln(2, "if ( message.length() > length) {");
		content.codeln(3, "message.append(\", \");");
		content.codeln(2, "}");
		content.codeln(2, "message.append(reference);");
		content.codeln(1, "}");
		content.codeln(1, "message.append(\".\");");
		content.codeln(1, "");
		content.codeln(1, "throw new IOException(message.toString());");
		content.codeln(1, "}");
		
		content.codeln(0, "");
		content.codeln(0, "return result;");
		content.endMethod("to");
		
	}

	private void generateMainCreateMethod(List<EClass> concreteClassList, JavaContentHandler content) {
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.SExp");
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.SVariable");
		
		content.comment(Java.JAVA_DOC, 0, 
		  "Creates an instance of T from given SExp. It create an instance.\n" +
		  "and tries to resolve references. If a reference isn't resolved, it\n" +
	      "keeps them stored for later calls to create.\n"
		);
		content.beginMethod(Java.PUBLIC, "<T> T", "create", "IOException",
				new Java.Parameter(Java.NONE, "Class<T>", "klass"),
				new Java.Parameter(Java.NONE, "SExp", "sexp")
			);
		
		content.codeln(0, "if (sexp.isVariable() ) {");
		content.comment(Java.SINGLE_LINE, 1, " checks variable case");
		content.codeln(1, "return resolver.resolve(((SVariable) sexp).getName(), klass);");
		content.codeln(0, "}");
		content.codeln(0, "");
		
		// local variable.
		content.comment(Java.SINGLE_LINE, 0, " not a variable");
		content.codeln(0, "String type = sexp.getConstructor();");
		content.codeln(0, "Object result = null;");
		content.codeln(0, "");
		
		// test all names
		for (EClass eClass : concreteClassList ) {
			caller.getDependencyManager().getShortName(eClass.getName());
			String sexpClassName = CodeGenUtil.uncapName(eClass.getName());
			String createMethodName = "create" + CodeGenUtil.capName(eClass.getName());
			content.codeln(0, "if ( \"" + sexpClassName + "\".equals(type) ) {");
			content.codeln(1, "result = " + createMethodName + "(sexp);");
			content.codeln(1, "");
			content.codeln(0, "} else");
		}
		// else case
		content.codeln(0, "{");
		content.codeln(1, "StringBuilder message = new StringBuilder();");
		content.codeln(1, "if ( type == null ) {");
		content.codeln(2, "message.append(\"Can't create a '\");");
		content.codeln(2, "message.append(klass.getSimpleName());");
		content.codeln(2, "message.append(\"' instance from '\");");
		content.codeln(2, "message.append(sexp);");
		content.codeln(2, "message.append(\"'.\");");
		content.codeln(1, "} else {");
		content.codeln(2, "message.append(\"Unknown type '\");");
		content.codeln(2, "message.append(type);");
		content.codeln(2, "message.append(\"'.\");");
		content.codeln(1, "}");
		content.codeln(1, "throw new IOException(message.toString());");
		
		content.codeln(0, "}");
		content.codeln(0, "");
		content.codeln(0, "");
		
		
		// cast result to T
		content.comment(Java.SINGLE_LINE, 0, "return casted result.");
		content.comment(Java.SINGLE_LINE, 0, "If result isn't of klass, it throws a ClassCastException.");
		content.codeln(0, "return result == null ? null : klass.cast(result);");
		
		content.endMethod("create");
		
	}

	private void generateCreateMethod(EClass eClass, JavaContentHandler content) {
		String className = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(eClass));
		String createMethodName = "create" + CodeGenUtil.capName(eClass.getName());

		List<EAttribute> attributeList = attributesToImplement(eClass);
		List<EReference> referenceList = referencesToImplement(eClass);
		boolean hasAttribute = attributeList.size() > 0;
		boolean hasReference = referenceList.size() > 0;
		
		caller.getDependencyManager().getShortName("java.io.IOException");
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.SExp");
		
		// starts method
		content.beginMethod(Java.PRIVATE, className, createMethodName, "IOException", 
					new Java.Parameter(Java.NONE, "SExp", "sexp")
				);

		// creates result
		content.codeln(0, className + " result = new " + className + "();");
		content.codeln(0, "");
		
		if ( attributeList.size() > 0 || referenceList.size() > 0 ) {
			// prepares variables
			content.codeln(0, "int current = 1;");
			content.codeln(0, "int count = sexp.getChildCount();");
			
			if ( hasAttribute ) {
				// loads attributes first before push object
				// attributes can be used for referencing
				content.codeln(0, "while ( current < count ) {");
				content.codeln(1, "final SExp currentSexp = sexp.getChild(current);");
				content.codeln(1, "final String type = currentSexp.getConstructor();");
				content.codeln(1, "");
				
				for ( EAttribute attribute : attributeList ) {
					String attributeName = sexpName(attribute);;
					content.codeln(1, "if ( \""+ attributeName +"\".equals(type) ) {");
					generateAttributeRead(attribute, content);
					content.codeln(1, "} else");
				}
				
				// handles old attributes and reference to ignore
				EAnnotation basicsAnnotation = eClass.getEAnnotation(ModelGenerator.BasicsSource);
				if ( basicsAnnotation != null ) {
					String oldFeatures = basicsAnnotation.getDetails().get(ModelGenerator.BasicsOldAttributes);
					if ( oldFeatures != null ) {
						for ( String oldFeature : oldFeatures.split(",") ) {
							oldFeature = oldFeature.trim();
							
							content.codeln(1, "if (\""+ oldFeature + "\".equals(type) ) {");
							content.comment(Java.SINGLE_LINE, 2, "Ignores old attributes: " + oldFeature);
							content.codeln(1, "} else");
						}
					}
				}
				
				// else close
				generateElseCase(hasReference == false, eClass, className, content);
				
				content.codeln(1, "current += 1;");
				content.codeln(0, "}");
				
				content.codeln(0, "");
			}
			
			// pushes objects
			content.codeln(0, "context.push(result);");
			
			// loads containments
			if ( hasReference ) {
				// loads references
				content.codeln(0, "while ( current < count ) {");
				content.codeln(1, "final SExp currentSexp = sexp.getChild(current);");
				content.codeln(1, "final String type = currentSexp.getConstructor();");
				content.codeln(1, "");
				
				for ( EReference reference : referenceList ) {
					String referenceName = sexpName(reference);
					content.codeln(1, "if ( \""+ referenceName +"\".equals(type) ) {");
					if ( reference.isContainment() ) {
						generateContainmentRead(reference, content);
					} else {
						generateReferenceRead(reference, content);
					}
					content.codeln(1, "} else");
				}
				
				// handles old attributes and reference to ignore
				EAnnotation basicsAnnotation = eClass.getEAnnotation(ModelGenerator.BasicsSource);
				if ( basicsAnnotation != null ) {
					String oldContainments = basicsAnnotation.getDetails().get(ModelGenerator.BasicsOldReferences);
					if ( oldContainments != null ) {
						for ( String oldContainement : oldContainments.split(",") ) {
							oldContainement = oldContainement.trim();
							
							content.codeln(1, "if (\""+ oldContainement + "\".equals(type) ) {");
							content.comment(Java.SINGLE_LINE, 2, "Ignores old references: " + oldContainement);
							content.codeln(1, "} else");
						}
					}
				}

				// else close
				generateElseCase(true, eClass, className, content);
				
				content.codeln(1, "current += 1;");
				content.codeln(0, "}");
			
				content.codeln(0, "");
			}
			
			// pops object and return result
			content.codeln(0, "context.pop(result);");
			
		}
		content.codeln(0, "return result;");

		// ends method
		content.endMethod(createMethodName);
		
	}
	
	private void generateAttributeRead(EAttribute attribute, JavaContentHandler content) {
		// reads attribute
		final String methodName = setterName(attribute);
		final EClassifier eType = attribute.getEType();
		
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.SExp");
		
		final String readCall = getSExpTypeReadCall(attribute, "currentSexp");
		if ( attribute.isMany() ) {
			caller.getDependencyManager().getShortName("java.util.Collection");

			final String completeTypeName = caller.getTypeName(eType, attribute.getEGenericType());
			final String typeName = caller.getDependencyManager().getShortName(completeTypeName);
			
			content.codeln(2, "Collection<"+typeName+"> valueList = "+ readCall + ";");
			content.codeln(2, "for ("+ typeName +" value : valueList) {");
			content.codeln(3, "result." + methodName + "(value);");
			content.codeln(2, "}");
			
		} else {
			content.codeln(2, "result." + methodName + "("+ readCall + " );");
		}
	}
	
	private void generateReferenceRead(EReference reference, JavaContentHandler content) {
		// reads reference
		final String referenceName = CodeGenUtil.uncapName(reference.getName());;
		
		if ( reference.isMany() ) {
			content.codeln(2, "for (int i=1; i<currentSexp.getChildCount(); i++ ) {");
			content.codeln(3, "SExp reference = currentSexp.getChild(i);");
			content.codeln(3, "context.registerReference(result, \""+ referenceName +"\", true, reference);");
			content.codeln(2, "}");
		} else {
			content.codeln(2, "SExp reference = currentSexp.getChild(1);");
			content.codeln(2, "context.registerReference(result, \""+ referenceName +"\", false, reference);");
		}
	}
	
	private void generateContainmentRead(EReference reference, JavaContentHandler content) {
		// reads containment reference
		
		final String typeName = caller.getDependencyManager().getShortName(caller.getClassQualifiedName(reference.getEReferenceType()));
		final String methodName = setterName(reference);
		
		if ( reference.isMany() ) {
			content.codeln(2, "for (int i=1; i<currentSexp.getChildCount(); i++ ) {");
			content.codeln(3, typeName + " child = create("+ typeName + ".class, currentSexp.getChild(i));");
			content.codeln(3, "result." + methodName + "(child);");
			content.codeln(2, "}");
		} else {
			content.codeln(2, typeName + " child = create("+ typeName + ".class, currentSexp.getChild(1));");
			content.codeln(2, "result." + methodName + "(child);");
		}
		
	}
	
	private void generateElseCase(boolean error, EClass eClass, String className, JavaContentHandler content) {
		if ( error ) {
			// handles error creation
			content.codeln(1, "{");
			content.comment(Java.SINGLE_LINE, 2, "unknown, this is an error.");
			content.codeln(2, "StringBuilder message = new StringBuilder();");
			content.codeln(2, "if ( type == null ) {");
			content.codeln(3, "message.append(\"Unknown attribute format '\");");
			content.codeln(3, "message.append(currentSexp);");
			content.codeln(3, "message.append(\"' for type '"+ className +"'.\");");
			content.codeln(2, "} else {");
			content.codeln(3, "message.append(\"Unknown attribute '\");");
			content.codeln(3, "message.append(type);");
			content.codeln(3, "message.append(\"' for type '"+ className +"'.\");");
			content.codeln(2, "}");
			content.codeln(2, "throw new IOException(message.toString());");
			content.codeln(1, "}");
		} else {
			content.codeln(1, "{");
			content.comment(Java.SINGLE_LINE, 2, "unknown, break.");
			content.codeln(2, "break;");
			content.codeln(1, "}");
		}
		
	}
	
	private String sexpName(EStructuralFeature feature) {
		String base = CodeGenUtil.uncapName(feature.getName());
		String suffix = feature.isMany() ? "Set" : "";
		return base + suffix;	
	}

	private String setterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String getPrefix = feature.isMany() ? "add" : "set";
		return CodeGenUtil.safeName(getPrefix + capName);
	}

	private String getSExpTypeReadCall(EAttribute attribute, String value) {
		final String suffix = attribute.isMany() ? "Collection" : "";
		final EClassifier classifier = attribute.getEType();
	
		caller.getDependencyManager().getShortName("fr.minibilles.basics.sexp.S");
		if (classifier instanceof EEnum ) {
			String shortName = caller.getDependencyManager().getShortName(caller.getTypeName(classifier, null));
			return "S.sexpToEnum"+ suffix +"(" + shortName +".class, " + value + ")";
		} else {
			String sexpType = classifier.getName();
			if ( sexpType.startsWith("E") && sexpType.length() > 1 && Character.isUpperCase(sexpType.charAt(1)) ) {
				sexpType = sexpType.substring(1);
			}
			if ( classifier.getInstanceTypeName() != null ) { 
				sexpType = caller.getDependencyManager().getShortName(classifier.getInstanceTypeName());
				sexpType = CodeGenUtil.capName(sexpType);
				if ( sexpType.endsWith("[]") ) {
					sexpType = sexpType.substring(0, sexpType.length()-2) + "Array";
				}
			}
			return "S.sexpTo" + sexpType + suffix +"(" + value + ")";
		}
	}
}
