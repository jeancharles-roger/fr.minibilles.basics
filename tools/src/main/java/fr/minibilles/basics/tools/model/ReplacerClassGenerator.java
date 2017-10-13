package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.io.Writer;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;


/**
 * <p>
 * This generator writes into a {@link Writer} Java code for one Ecore 
 * {@link EPackage} and sub packages replacer.
 * </p>
 * 
 * @author Jean-Charles Roger
 * 
 */
public class ReplacerClassGenerator {

	/** Object that calls generator. */
	private final ModelGenerator caller;

	private final boolean recursive;
	
	private EClass inheritanceRootClass;
	
	public ReplacerClassGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}
	
	public void generate(EPackage source, JavaContentHandler content) {
		inheritanceRootClass = caller.getUniqueInheritanceRootClass(source, recursive);
		if ( inheritanceRootClass == null ) {
			throw new IllegalArgumentException("Replacer needs an inheritance root EClass for all nodes.");
		}
				
		caller.getDependencyManager().clear();
		
		final String className = caller.getDependencyManager().getShortName(caller.getReplacerQualifiedName(source));
		final String abstractCloner = caller.getDependencyManager().getShortName("org.xid.basics.model.AbstractCloner");
		final String visitorName = caller.getDependencyManager().getShortName(caller.getVisitorQualifiedName(source));
		
		content.beginFile(className + ".java");
		content.markImports();

		StringBuilder comment = new StringBuilder();
		comment.append("<p>Cloner for objects in packages '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and children");
		}
		comment.append("."); 
		comment.append("</p>"); 
		content.comment(Java.JAVA_DOC, 0, comment.toString());
		
		content.beginClass(Java.PUBLIC, className, abstractCloner, visitorName);
		
		generateFields(content);
		
		generateConstructorAndUtilMethods(className, content);
		generateMethods(source, content);
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		content.endFile(className + ".java");
	
	}

	private String getInheritanceRootName() {
		return caller.getDependencyManager().getShortName(caller.getTypeName(inheritanceRootClass));
	}

	private void generateFields(JavaContentHandler content) {
		final String typeName = caller.getTypeName(inheritanceRootClass);
		final String setShortname = caller.getDependencyManager().getShortName("java.util.Set");
		final String hashsetShortname = caller.getDependencyManager().getShortName("java.util.HashSet");
		
		content.beginAttribute(Java.PRIVATE | Java.FINAL, setShortname + "<" + typeName + ">", "replaced");
		content.code("new "+ hashsetShortname +"<"+ typeName +">()");
		content.endAttribute("replaced");
	}

	private void generateConstructorAndUtilMethods(String className, JavaContentHandler content) {
		final String mapShortName = caller.getDependencyManager().getShortName("java.util.Map");
		final String entryShortName = caller.getDependencyManager().getShortName("java.util.Map.Entry");
		final String inheritanceRootName = getInheritanceRootName();
		
		content.beginMethod(Java.PUBLIC, null, className, null);
		content.codeln(0, "this(null);");
		content.endMethod(className);

		content.beginMethod(Java.PUBLIC, null, className, null, new Java.Parameter(Java.NONE, mapShortName + "<"+ inheritanceRootName +", "+ inheritanceRootName +">", "replacementMap"));
		content.codeln(0, "if ( replacementMap != null ) {");
		content.codeln(1, "for ("+ entryShortName +"<"+ inheritanceRootName +", "+ inheritanceRootName +"> entry : replacementMap.entrySet() ) {");
		content.codeln(2, "addReplacement(entry.getKey(), entry.getValue());");
		content.codeln(1, "}");
		content.codeln(0, "}");
		content.endMethod(className);
		
		content.beginMethod(Java.PUBLIC, "void", "addReplacement", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original"),
				new Java.Parameter(Java.NONE, inheritanceRootName, "replacement")
			);
		content.codeln(0, "registerClone(original, replacement);");
		content.endMethod("addReplacement");
		
		content.beginMethod(Java.PUBLIC, "void", "addRemove", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original")
				);
		content.codeln(0, "registerClone(original, null);");
		content.endMethod("addRemove");
		
		content.beginMethod(Java.PROTECTED, "boolean", "hasContainmentReplacement", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original")
			);
		content.codeln(0, "return hasClone(original) && replaced.contains(original) == false;");
		content.endMethod("hasReplacement");
		
		content.beginMethod(Java.PUBLIC, "boolean", "hasReplacement", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original")
				);
		content.codeln(0, "return hasClone(original);");
		content.endMethod("hasReplacement");
		
		content.beginMethod(Java.PROTECTED, inheritanceRootName, "getContainmentReplacement", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original")
			);
		content.codeln(0, "replaced.add(original);");
		content.codeln(0, "return getReplacement(original);");
		content.endMethod("getContainmentReplacement");
		
		content.beginMethod(Java.PUBLIC, inheritanceRootName, "getReplacement", null, 
				new Java.Parameter(Java.NONE, inheritanceRootName, "original")
				);
		content.codeln(0, "return ("+ inheritanceRootName +") getClone(original);");
		content.endMethod("getReplacement");
	}
	
	private void generateMethods(EPackage source, JavaContentHandler content) {
		final String inheritanceRootName = getInheritanceRootName();
		
		for (EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				final EClass klass = (EClass) classifier;
				if ( !klass.isAbstract() ) {
					final String visitName = caller.getVisitMethodName(klass);
					final String typeName = caller.getTypeName(classifier, null);
					final Parameter param = new Parameter(Java.NONE, typeName, "toVisit");
					content.beginMethod(Java.PUBLIC, "void", visitName, null, param );
					content.codeln(0, "if ( hasContainmentReplacement(toVisit) ) {");
					content.codeln(1, inheritanceRootName + " replacement = getContainmentReplacement(toVisit);");
					content.codeln(1, "if ( replacement != null ) {");
					content.codeln(2, "replacement.accept(this);");
					content.codeln(1, "} else {");
					content.codeln(2, "pushObject(null);");
					content.codeln(1, "}");
					content.codeln(0, "} else {");
					
					for ( EReference reference : klass.getEAllReferences() ) {
						if ( reference.isContainment() ) {
							// generates call to cloner
							generateContainmentClone(reference, content);
						} else {
							// generates call to reference
							generateReferenceClone(reference, content);
						}
					}
					content.codeln(1, "pushObject(toVisit);");
					content.codeln(0, "}");
					
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

	private void generateContainmentClone(EReference reference, JavaContentHandler content) {
		final String getter = getterName(reference);
		final String setter = setterName(reference);
		final String remover = removerName(reference);
		final String referenceTypeName = caller.getTypeName(reference.getEType(), reference.getEGenericType());

		if ( reference.isMany() ) {
			content.codeln(1, "for ( int i=0; i<toVisit." + getter + "Count(); i+=1) {");
			content.codeln(2, "toVisit." + getter + "(i).accept(this);");
			content.codeln(2, referenceTypeName + " child = popObject("+ referenceTypeName +".class);");
			content.codeln(2, "if (child != null) {");
			content.comment(Java.SINGLE_LINE, 3, " Replaces by child.");
			content.codeln(3, "toVisit." + setter + "(i, child);");
			content.codeln(2, "} else {");
			content.comment(Java.SINGLE_LINE, 3, " Removes child (decrease i to correct indexing).");
			content.codeln(3, "toVisit." + remover + "(i);");
			content.codeln(3, "i -= 1;");
			content.codeln(2, "}");
			content.codeln(1, "}");
			
		} else {
			content.codeln(1, "if ( toVisit."+ getter+"() != null ) {");
			content.codeln(2, "toVisit." + getter + "().accept(this);");
			content.codeln(2, "toVisit."+ setter +"(popObject("+ referenceTypeName +".class));");
			content.codeln(1, "}");
		}
	}
	
	private void generateReferenceClone(EReference reference, JavaContentHandler content) {
		final String getter = getterName(reference);
		final String setter = setterName(reference);
		final String remover = removerName(reference);
		final String referenceTypeName = caller.getTypeName(reference.getEType(), reference.getEGenericType());

		if ( reference.isMany() ) {
			content.codeln(1, "for ( int i=0; i<toVisit." + getter + "Count(); i+=1) {");
			content.codeln(2, referenceTypeName + " child = toVisit." + getter +"(i);");
			content.codeln(2, "if (hasReplacement(child) ) {");
			content.codeln(3, referenceTypeName + " replacement = ("+ referenceTypeName +") getReplacement(child);");
			content.codeln(3, "if (replacement != null) {");
			content.comment(Java.SINGLE_LINE, 4, " Replaces by replacement.");
			content.codeln(4, "toVisit." + setter + "(i, replacement);");
			content.codeln(3, "} else {");
			content.comment(Java.SINGLE_LINE, 4, " Removes child (decrease i to correct indexing).");
			content.codeln(4, "toVisit." + remover + "(i);");
			content.codeln(4, "i -= 1;");
			content.codeln(3, "}");
			content.codeln(2, "}");
			content.codeln(1, "}");
			
		} else {
			final String get = "toVisit."+ getter +"()";
			content.codeln(1, "if ( "+ get +" != null && hasReplacement("+ get +") ) {");
			content.codeln(2, referenceTypeName + " child = (" + referenceTypeName + ") getClone("+ get +");");
			content.codeln(2, "toVisit." + setter + "(child);");
			content.codeln(1, "}");
		}
	}
	
	private String getterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String prefix = "boolean".equals(feature.getEType().getInstanceClassName()) && feature.isMany() == false ? "is" : "get";
		return CodeGenUtil.safeName(prefix + capName);
	}

	private String setterName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String getPrefix = "set";
		return CodeGenUtil.safeName(getPrefix + capName);
	}
	private String removerName(EStructuralFeature feature) {
		String capName = CodeGenUtil.capName(feature.getName());
		String getPrefix = "remove";
		return CodeGenUtil.safeName(getPrefix + capName);
	}

}
