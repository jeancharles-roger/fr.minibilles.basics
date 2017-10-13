package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public class ModelToSExpClassGenerator {


	/** Object that calls generator. */
	private final ModelGenerator caller;
	
	private final boolean recursive;
	
	public ModelToSExpClassGenerator(ModelGenerator caller, boolean recursive) {
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
		List<EReference> allAttributesToImplement = new ArrayList<EReference>();

		// searches attributes in source
		for ( EStructuralFeature feature : source.getEAllStructuralFeatures() ) {
			if ( feature.isTransient() == false && feature instanceof EReference ) {
				allAttributesToImplement.add((EReference) feature);
			}
		}
		
		return allAttributesToImplement;
	}

	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		String className = CodeGenUtil.capName(source.getName()) + "ToSExp";
		// protects class name from imports
		caller.getDependencyManager().getShortName(className);
		
		// starts class
		content.beginFile(className + ".java");
		content.markImports();
		
		StringBuilder comment = new StringBuilder();
		comment.append("<p>SExp writer for model '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and children");
		}
		comment.append("."); 
		comment.append("</p>"); 
		content.comment(Java.JAVA_DOC, 0, comment.toString());
		
		content.beginClass(Java.PUBLIC, className, null, caller.getCurrentVisitorName());

		// adds context
		String modelToSexpShortName = caller.getDependencyManager().getShortName("org.xid.basics.sexp.model.ModelToSExp");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, modelToSexpShortName, "context");
		content.endAttribute("context");

		// adds sexp stack
		caller.getDependencyManager().getShortName("java.util.Stack");
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.SExp");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, "Stack<SExp>", "sexpStack");
		content.code("new Stack<SExp>()");
		content.endAttribute("sexpStack");
		
		// constructor
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.model.Referencer");
		content.beginMethod(Java.PUBLIC, null, className, null, 
					new Java.Parameter(Java.NONE, "Referencer", "referencer")
				);
		content.codeln(0, "this.context = new "+ modelToSexpShortName +"(referencer);");
		content.endMethod(className);
		
		// stack methods
		content.beginMethod(Java.PUBLIC, "void", "pushSExp", null, 
				new Java.Parameter(Java.NONE, "SExp", "sexp")
			);
		content.codeln(0,"sexpStack.push(sexp);");
		content.endMethod("pushSExp");
		
		content.beginMethod(Java.PUBLIC, "SExp", "popSExp", null);
		content.codeln(0,"return sexpStack.pop();");
		content.endMethod("popSExp");
		
		List<EClass> concreteClassList = concreteClassList(source);
		
		// generates visit methods
		for ( EClass eClass : concreteClassList ) {
			generateVisitMethod(eClass, content);
		}

		
		final String setShortName = caller.getDependencyManager().getShortName("java.util.Set");
		
		// generates get result method
		caller.getDependencyManager().getShortName("java.io.IOException");
		content.beginMethod(Java.PUBLIC, "SExp", "getResult", "IOException");
		content.comment(Java.SINGLE_LINE, 0, " checks for missing objects and sends an exception if needed.");
		content.codeln(0, setShortName + "<Object> missingObjects = missingObjects();");
		content.codeln(0, "if ( missingObjects.isEmpty() == false ) {");
		content.codeln(1, "StringBuilder message = new StringBuilder();");
		content.codeln(1, "message.append(\"Missing object(s): \");");
		content.codeln(1, "int length = message.length();");
		content.codeln(1, "for (Object object : missingObjects ) {");
		content.codeln(2, "if ( message.length() > length) {");
		content.codeln(3, "message.append(\", \");");
		content.codeln(2, "}");
		content.codeln(2, "message.append(object);");
		content.codeln(1, "}");
		content.codeln(1, "message.append(\".\");");
		content.codeln(1, "");
		content.codeln(1, "throw new IOException(message.toString());");
		content.codeln(1, "}");
		content.codeln(0, "return popSExp();");
		content.endMethod("getResult");
		
		// generates missing objects method
		content.beginMethod(Java.PUBLIC, setShortName + "<Object>", "missingObjects", null);
		content.codeln(0, "return context.missingObjects();");
		content.endMethod("missingObjects");
		
		// ends class
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}

	private void generateVisitMethod(EClass eClass, JavaContentHandler content) {
		final String visitName = caller.getVisitMethodName(eClass);
		final Parameter param = new Parameter(Java.NONE, caller.getTypeName(eClass, null), "toVisit");
		content.beginMethod(Java.PUBLIC, "void", visitName, null, param );
		
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.SList");
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.S");

		content.codeln(0, "context.push(toVisit);");
		content.codeln(0, "SList result = new SList();");
		
		String sexpClassName = CodeGenUtil.uncapName(eClass.getName());
		content.codeln(0, "result.addChild(S.satom(\""+ sexpClassName +"\"));");
		
		for (EAttribute attribute : attributesToImplement(eClass) ) {
			generateAttributeWrite(attribute, content);
		}
		
		List<EReference> referenceList = referencesToImplement(eClass);
		for ( EReference reference : referenceList ) {
			if ( reference.isContainment() ) {
				generateContainmentWrite(reference, content);
			} else {
				generateReferenceWrite(reference, content);
			}
		}
		
		content.codeln(0, "context.pop(toVisit);");
		content.codeln(0, "pushSExp(result);");
		
		content.endMethod(visitName);
	}
	
	private void generateAttributeWrite(EAttribute attribute, JavaContentHandler content) {
		// writes attribute
		final String methodName = getterName(attribute);
		if ( "boolean".equals(attribute.getEType().getInstanceClassName()) ) {
			// boolean attribute only write if true
			content.codeln(0, "if ( toVisit."+methodName+"() ) {");
			content.codeln(1, "result.addChild("+ getSExpTypeWriteCall(attribute, "toVisit."+methodName+"()") +");");
			content.codeln(0, "}");
		} else {
			content.codeln(0, "S.addChildIfNotNull(result, "+ getSExpTypeWriteCall(attribute, "toVisit."+methodName+"()") +");");
		}
	}
	
	private void generateReferenceWrite(EReference reference, JavaContentHandler content) {
		// writes reference
		final String referenceName = referenceName(reference);
		final String methodName = getterName(reference);
		
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.S");
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.SExp");

		if ( reference.isMany() ) {
			final String typeName = caller.getTypeName(reference.getEType());
			final String countMethodName = "get" + CodeGenUtil.capName(reference.getName()) + "Count";
			content.codeln(0, "if (toVisit."+countMethodName+"() > 0) {");
			content.codeln(1, "SExp referenceSExp = S.slist(S.satom(\""+ referenceName +"\"));");
			content.codeln(1, "for (" + typeName +" child : toVisit."+methodName+"()) {");
			content.codeln(2, "referenceSExp.addChild(context.createReference(child));");
			content.codeln(1, "}");
			content.codeln(1, "result.addChild(referenceSExp);");
			content.codeln(0, "}");
		} else {
			content.codeln(0, "if ( toVisit."+ methodName +"() != null ) {");
			content.codeln(1, "SExp referenceSExp = S.slist(S.satom(\""+ referenceName +"\"));");
			content.codeln(1, "referenceSExp.addChild(context.createReference(toVisit."+ methodName +"()));");
			content.codeln(1, "result.addChild(referenceSExp);");
			content.codeln(0, "}");
		}
	}
	
	private void generateContainmentWrite(EReference reference, JavaContentHandler content) {
		// writes containment reference
		final String referenceName = referenceName(reference);
		final String methodName = getterName(reference);
		
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.S");
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.SExp");

		if ( reference.isMany() ) {
			final String typeName = caller.getTypeName(reference.getEType());
			final String countMethodName = "get" + CodeGenUtil.capName(reference.getName()) + "Count";
			content.codeln(0, "if (toVisit."+countMethodName+"() > 0) {");
			content.codeln(1, "SExp referenceSExp = S.slist(S.satom(\""+ referenceName +"\"));");
			content.codeln(1, "for (" + typeName +" child : toVisit."+methodName+"()) {");
			content.codeln(2, "child.accept(this);");
			content.codeln(2, "referenceSExp.addChild(popSExp());");
			content.codeln(1, "}");
			content.codeln(1, "result.addChild(referenceSExp);");
			content.codeln(0, "}");
		} else {
			content.codeln(0, "if (toVisit."+ methodName +"() != null) {");
			content.codeln(1, "SExp referenceSExp = S.slist(S.satom(\""+ referenceName +"\"));");
			content.codeln(1, "toVisit."+ methodName +"().accept(this);");
			content.codeln(1, "referenceSExp.addChild(popSExp());");
			content.codeln(1, "result.addChild(referenceSExp);");
			content.codeln(0, "}");
		}
	}
	
	private String referenceName(EStructuralFeature feature) {
		String base = CodeGenUtil.uncapName(feature.getName());
		String suffix = feature.isMany() ? "Set" : "";
		return base + suffix;	
	}
	
	private String getterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(caller.featureName(feature));
		String prefix = "boolean".equals(feature.getEType().getInstanceClassName()) && feature.isMany() == false ? "is" : "get";
		return CodeGenUtil.safeName(prefix + capName);
	}


	private String getSExpTypeWriteCall(EAttribute attribute, String value) {
		final String suffix = attribute.isMany() ? "Collection" : "";
		final EClassifier classifier = attribute.getEType();
		final String attributeName = referenceName(attribute);
		
		caller.getDependencyManager().getShortName("org.xid.basics.sexp.S");
		if (classifier instanceof EEnum ) {
			return "S.enum"+ suffix +"ToSExp(\"" + attributeName +"\", " + value + ")";
		} else {
			String sexpType = classifier.getName();
			if ( sexpType.startsWith("E") && sexpType.length() > 1 && Character.isUpperCase(sexpType.charAt(1)) ) {
				sexpType = sexpType.substring(1);
			}
			if ( classifier.getInstanceTypeName() != null ) { 
				sexpType = caller.getDependencyManager().getShortName(classifier.getInstanceTypeName());
				if ( sexpType.endsWith("[]") ) {
					sexpType = sexpType.substring(0, sexpType.length()-2) + "Array";
				}
				}
			return "S." + CodeGenUtil.uncapName(sexpType) + suffix + "ToSExp(\""+ attributeName +"\", " + value + ")";
		}
	}
}
