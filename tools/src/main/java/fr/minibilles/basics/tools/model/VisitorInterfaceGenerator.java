package fr.minibilles.basics.tools.model;

import fr.minibilles.basics.generation.java.Java;
import fr.minibilles.basics.generation.java.Java.Parameter;
import fr.minibilles.basics.generation.java.JavaContentHandler;
import java.io.Writer;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;


/**
 * This generator writes into a {@link Writer} Java code for one Ecore
 * {@link EPackage} and sub packages as a visitor interface for the basic 
 * framework.
 *
 * @author Jean-Charles Roger
 */
public class VisitorInterfaceGenerator {

	/** Object that calls generator. */
	private final ModelGenerator caller;

	private final boolean recursive;
	
	public VisitorInterfaceGenerator(ModelGenerator caller, boolean recursive) {
		this.caller = caller;
		this.recursive = recursive;
	}
	
	public void generate(EPackage source, JavaContentHandler content) {

		caller.getDependencyManager().clear();
		String className = caller.getDependencyManager().getShortName(caller.getVisitorQualifiedName(source));
		content.beginFile(className + ".java");
		content.markImports();
		
		StringBuilder comment = new StringBuilder();
		comment.append("Visitor interface for package '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and sub packages");
		}
		comment.append(".");

		content.comment(Java.JAVA_DOC, 0, comment.toString());
		content.beginInterface(Java.PUBLIC, className, null);

		generateStub(source, content);
		
		generateInterfaceMethods(source, content);
		content.endInterface(className);

		for ( String oneImport : caller.getDependencyManager().getJavaImports() ) {
			content.import_(Java.NONE, oneImport);
		}
		
		content.endFile(className + ".java");
	
	}

	private void generateInterfaceMethods(EPackage source, JavaContentHandler content) {
		for (EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				final EClass eClass = (EClass) classifier;
				if ( !eClass.isAbstract() && !eClass.isInterface() ) {
					final String visitName = caller.getVisitMethodName(eClass);
					final Parameter param = new Parameter(Java.NONE, caller.getTypeName(classifier, null), "toVisit");
					content.comment(Java.JAVA_DOC, 0, "Visit method for " + param.type + ".");
					content.beginMethod(Java.NONE, "void", visitName, null, param );
					content.endMethod(visitName);
				}
			}
		}
		if ( recursive ) {
			for ( EPackage child : source.getESubpackages() ) {
				generateInterfaceMethods(child, content);
			}
		}
	}

	protected void generateStub(EPackage source, JavaContentHandler content) {
		StringBuilder comment = new StringBuilder();
		comment.append("Empty visitor implementation for package '");
		comment.append(source.getName()); 
		comment.append("'");
		if ( recursive ) {
			comment.append(" and sub packages");
		}
		comment.append(".");
		content.comment(Java.JAVA_DOC, 0, comment.toString());
		
		String className = caller.getDependencyManager().getShortName(caller.getVisitorQualifiedName(source));
		content.beginClass(Java.PUBLIC | Java.STATIC, "Stub", null, className);
		generateStubMethods(source, content);
		content.endClass("Stub");
	}

	private void generateStubMethods(EPackage source, JavaContentHandler content) {
		for (EClassifier classifier : source.getEClassifiers() ) {
			if (classifier instanceof EClass ) {
				final EClass eClass = (EClass) classifier;
				if ( !(eClass).isAbstract() ) {
					final String visitName = caller.getVisitMethodName(eClass);
					final Parameter param = new Parameter(Java.NONE, caller.getTypeName(classifier, null), "toVisit");
					
					content.comment(Java.JAVA_DOC, 0, "Empty visit method for " + param.type + ".");
					content.beginMethod(Java.PUBLIC, "void", visitName, null, param );
					content.comment(Java.SINGLE_LINE, 0, "do nothing");
					content.endMethod(visitName);
				}
			}
		}
		if ( recursive ) {
			for ( EPackage child : source.getESubpackages() ) {
				generateStubMethods(child, content);
			}
		}
	}
	
}
