package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.io.Writer;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;


/**
 * This generator writes into a {@link Writer} Java code for one Ecore
 * {@link EPackage} and sub packages cloner.
 *
 * @author Jean-Charles Roger
 */
public class ClonerClassGenerator {

	/** Object that calls generator. */
	private final ModelGenerator caller;

	private final boolean recursive;
	
	public ClonerClassGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}
	
	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		
		final String className = caller.getDependencyManager().getShortName(caller.getClonerQualifiedName(source));
		final String abstractCloner = caller.getDependencyManager().getShortName("org.xid.basics.model.AbstractCloner");
		final String visitorName = caller.getDependencyManager().getShortName(caller.getVisitorQualifiedName(source));
		
		content.beginFile(className + ".java");
		content.markImports();

		StringBuilder comment = new StringBuilder();
		comment.append("Cloner for objects in packages '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and children");
		}
		comment.append("."); 
		content.comment(Java.JAVA_DOC, 0, comment.toString());
		
		content.beginClass(Java.PUBLIC, className, abstractCloner, visitorName);
		
		for ( EClass inheritanceRoot : caller.getInheritanceRootClassSet(source, recursive) ) {
			generateStaticMethod(inheritanceRoot, className, content);
		}
		
		
		generateMethods(source, content);
		
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}

	private void generateStaticMethod(EClass inherintanceRootClass, String clonerName, JavaContentHandler content) {
		String comment = "Clones given object. Only object and children (by containment) are cloned. Referenced objects which are not contained aren't cloned.";
		
		String rootShortName = caller.getDependencyManager().getShortName(caller.getTypeName(inherintanceRootClass));
		
		content.comment(Java.JAVA_DOC, 0, comment);
		content.annotation("SuppressWarnings", "\"unchecked\"");
		content.beginMethod(Java.PUBLIC | Java.STATIC, "<T extends " +  rootShortName + "> T", "clone", null, 
				new Java.Parameter(Java.NONE, "T", "object")
			);
		
		content.codeln(0, "if ( object == null ) return null;");
		content.codeln(0, "");
		content.codeln(0, clonerName + " cloner = new " + clonerName + "();");
		content.codeln(0, "object.accept(cloner);");
		content.codeln(0, "return (T) cloner.popObject("+  rootShortName + ".class);");
		content.endMethod("clone");
		
	}
	
	private void generateMethods(EPackage source, JavaContentHandler content) {
		for (EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				final EClass klass = (EClass) classifier;
				if ( !klass.isAbstract() ) {
					final String visitName = caller.getVisitMethodName(klass);
					final String typeName = caller.getTypeName(classifier, null);
					final Parameter param = new Parameter(Java.NONE, typeName, "toVisit");
					content.beginMethod(Java.PUBLIC, "void", visitName, null, param );
					content.codeln(0, typeName + " cloned = new " + typeName + "();");
					
					for ( EAttribute attribute : klass.getEAllAttributes() ) {
						// generates attribute set
						generateAttributeClone(attribute, content);
					}

					// register clone after attribute set for reference
					content.codeln(0, "registerClone(toVisit, cloned);");
					
					for ( EReference reference : klass.getEAllReferences() ) {
						if ( reference.isContainment() ) {
							// generates call to cloner
							generateContainmentClone(reference, content);
						} else {
							// generates call to reference
							generateReferenceClone(reference, content);
						}
					}
					content.codeln(0, "pushObject(cloned);");
					content.endMethod(visitName);
				}
			}
		}
		if ( recursive ) {
			for ( EPackage child : source.getESubpackages() ) {
				generateMethods(child, content);
			}
		}
	}

	private void generateAttributeClone(EAttribute attribute, JavaContentHandler content) {
		// reads attribute
		final String getter = getterName(attribute);
		final String setter = setterName(attribute);
		final EClassifier eType = attribute.getEType();
		
		if ( attribute.isMany() ) {

			final String completeTypeName = caller.getTypeName(eType, attribute.getEGenericType());
			final String typeName = caller.getDependencyManager().getShortName(completeTypeName);
			
			content.codeln(0, "for ("+ typeName +" value : toVisit."+ getter +"() ) {");
			content.codeln(1, "cloned." + setter + "(value);");
			content.codeln(0, "}");
			
		} else {
			content.codeln(0, "cloned." + setter + "(toVisit."+ getter + "());");
		}
	}
	
	private void generateContainmentClone(EReference reference, JavaContentHandler content) {
		final String getter = getterName(reference);
		final String setter = setterName(reference);
		final String referenceTypeName = caller.getTypeName(reference.getEType(), reference.getEGenericType());

		if ( reference.isMany() ) {
			content.codeln(0, "for ( " + referenceTypeName + " child : toVisit." + getter + "()) {");
			content.codeln(1, "child.accept(this);");
			content.codeln(1, "cloned."+ setter +"(popObject("+ referenceTypeName +".class));");
			content.codeln(0, "}");
			
		} else {
			content.codeln(0, "if ( toVisit."+ getter+"() != null ) {");
			content.codeln(1, "toVisit." + getter + "().accept(this);");
			content.codeln(1, "cloned."+ setter +"(popObject("+ referenceTypeName +".class));");
			content.codeln(0, "}");
		}
	}
	
	private void generateReferenceClone(EReference reference, JavaContentHandler content) {
		// reads reference
		final String referenceName = referenceName(reference);
		final String getter = getterName(reference);

		if ( reference.isMany() ) {
			String referenceTypeName = caller.getTypeName(reference.getEType(), reference.getEGenericType());
			content.codeln(0, "for ( " + referenceTypeName + " child : toVisit." + getter + "()) {");
			content.codeln(1, "registerReference(cloned, \""+ referenceName +"\", true, child);");
			content.codeln(0, "}");
			
		} else {
			content.codeln(0, "if ( toVisit."+ getter +"() != null ) {");
			content.codeln(1, "registerReference(cloned, \""+ referenceName +"\", false, toVisit."+ getter +"());");
			content.codeln(0, "}");
		}
	}
	
	private String referenceName(EStructuralFeature feature) {
		return CodeGenUtil.safeName(feature.getName());
	}
	
	private String getterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(caller.featureName(feature));
		String prefix = "boolean".equals(feature.getEType().getInstanceClassName()) && feature.isMany() == false ? "is" : "get";
		return CodeGenUtil.safeName(prefix + capName);
	}

	private String setterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String getPrefix = feature.isMany() ? "add" : "set";
		return CodeGenUtil.safeName(getPrefix + capName);
	}

}
