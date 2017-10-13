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


/**
 * <p>
 * This generator writes into a {@link Writer} Java code for one Ecore 
 * {@link EPackage} and sub packages as a walker methods for the basic 
 * framework.
 * </p>
 * 
 * @author Jean-Charles Roger
 * 
 */
public class WalkerClassGenerator {

	/** Object that calls generator. */
	private final ModelGenerator caller;

	private final boolean recursive;
	
	public WalkerClassGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}
	
	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		final String className = caller.getDependencyManager().getShortName(caller.getWalkerQualifiedName(source));
		final String visitorName = caller.getDependencyManager().getShortName(caller.getVisitorQualifiedName(source));
		
		content.beginFile(className + ".java");
		content.markImports();
		
		
		StringBuilder comment = new StringBuilder();
		comment.append("<p>Walker for package '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and sub packages");
		}
		comment.append("."); 
		comment.append(" A walker navigates throught objects using the containment tree and visit each node with the given visitor."); 
		comment.append("</p>"); 
		
		content.comment(Java.JAVA_DOC, 0, comment.toString());
		content.beginClass(Java.PUBLIC, className, null, visitorName);

		content.comment(Java.JAVA_DOC, 0, "Delegate visitor.");
		content.beginAttribute(Java.PRIVATE | Java.FINAL, visitorName, "delegate");
		content.endAttribute("delegate");
		
		// get root type for parent stack
		String type = "Object";
		final EClass root = caller.getUniqueInheritanceRootClass(source, recursive);
		if ( root != null ) {
			type = caller.getDependencyManager().getShortName(caller.getTypeName(root));
		}
		
		// declares parent stack
		final String stackName = caller.getDependencyManager().getShortName("java.util.Stack");
		content.beginAttribute(Java.PRIVATE|Java.FINAL, stackName + "<" + type + ">", "parentStack");
		content.code("new " + stackName + "<" + type + ">()");
		content.endAttribute("parentStack");
		
		// adds constructor
		content.beginMethod(Java.PUBLIC, null, className, null,
				new Java.Parameter(Java.NONE, visitorName, "delegate")
			);
		content.codeln(0, "this.delegate = delegate;");
		content.endMethod(className);
		
		// adds parent stack getter method
		content.comment(Java.JAVA_DOC, 0, "Returns the parent stack for current visited object.");
		content.beginMethod(Java.PUBLIC, stackName + "<" + type + ">", "getParentStack", null);
		content.codeln(0, "return parentStack;");
		content.endMethod("getParentStack");
		
		generateMethods(source, content);
		
		content.endClass(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}

	public void generateMethods(EPackage source, JavaContentHandler content) {
		for (EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				final EClass klass = (EClass) classifier;
				if ( !klass.isAbstract() ) {
					final String visitName = caller.getVisitMethodName(klass);
					final Parameter param = new Parameter(Java.NONE, caller.getTypeName(classifier, null), "toVisit");
					content.beginMethod(Java.PUBLIC, "void", visitName, null, param );
					content.codeln(0, "parentStack.push(toVisit);");
					for ( EReference reference : klass.getEAllReferences() ) {
						if ( reference.isContainment() ) {
							if ( reference.isMany() ) {
								String typeName = caller.getTypeName(reference.getEType(), reference.getEGenericType());
								String getterName = CodeGenUtil.safeName("get" + CodeGenUtil.capName(reference.getName()) + "List()");
								content.codeln(0, "for ( " + typeName + " child : toVisit." + getterName + ") {");
								content.codeln(1, "child.accept(this);");
								content.codeln(0, "}");
								
							} else {
								String getterName = CodeGenUtil.safeName("get" + CodeGenUtil.capName(reference.getName()) + "()");
								content.codeln(0, "if ( toVisit."+ getterName +" != null ) {");
								content.codeln(1, "toVisit." + getterName + ".accept(this);");
								content.codeln(0, "}");
							}
						}
					}
					content.codeln(0, "parentStack.pop();");
					content.codeln(0, "toVisit.accept(delegate);");
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
	
}
