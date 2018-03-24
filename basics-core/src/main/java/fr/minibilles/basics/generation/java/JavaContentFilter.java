package fr.minibilles.basics.generation.java;

import java.io.File;

/**
 * This {@link JavaContentHandler} delegates all calls to a given target.
 * It allows to create filters and formatters on {@link JavaContentHandler}.
 * @author Jean-Charles Roger
 */
public class JavaContentFilter implements JavaContentHandler {

	private final JavaContentHandler target;

	public JavaContentFilter(JavaContentHandler target) {
		if ( target == null ) throw new NullPointerException("Target JavaContentHandler can't be null.");
		this.target = target;
	}
	
	public JavaContentHandler getTarget() {
		return target;
	}
	
	public void annotation(String name, String parameters) {
		target.annotation(name, parameters);
	}
	
	public void beginAttribute(int flags, String type, String name) {
		target.beginAttribute(flags, type, name);
	}
	
	public void endAttribute(String name) {
		target.endAttribute(name);
	}

	public void beginAnonymousClass(String inherited, String ... arguments) {
		target.beginAnonymousClass(inherited, arguments);
	}

	public void beginClass(int flags, String name, String parentClass, String implementedInterfaces) {
		target.beginClass(flags, name, parentClass, implementedInterfaces);
	}

	public void beginEnum(int flags, String name) {
		target.beginEnum(flags, name);
	}
	
	public void beginEnumLiterals() {
		target.beginEnumLiterals();
	}

	public void enumLiteral(String name, String ... arguments) {
		target.enumLiteral(name, arguments);
	}
	
	public void beginFile(String name) {
		target.beginFile(name);
	}

	public void beginInterface(int flags, String name, String extendedInterfaces) {
		target.beginInterface(flags, name, extendedInterfaces);
	}

	public void beginMethod(int flags, String returnType, String name, String exceptions, Java.Parameter... parameters) {
		target.beginMethod(flags, returnType, name, exceptions, parameters);
	}

	public void beginPackage(String name) {
		target.beginPackage(name);
	}

	public void comment(int flags, int level, String comment) {
		target.comment(flags, level, comment);
	}

	public void endAnnonymousClass(String inherited) {
		target.endAnnonymousClass(inherited);
	}

	public void endClass(String name) {
		target.endClass(name);
	}

	public void endEnumLiterals() {
		target.endEnumLiterals();
	}
	
	public void endEnum(String name) {
		target.endEnum(name);
	}

	public void endFile(String name) {
		target.endFile(name);
	}

	public void endInterface(String name) {
		target.endInterface(name);
	}

	public void endMethod(String name) {
		target.endMethod(name);
	}

	public void endPackage(String name) {
		target.endPackage(name);
	}

	public void markImports() {
		target.markImports();
	}

	public void import_(int flags, String importString) {
		target.import_(flags, importString);
	}

	public void codeln(int level, String statements) {
		target.codeln(level, statements);
	}
	
	public void code(String statements) {
		target.code(statements);
	}
	
	public void binaryFile(File file) {
		target.binaryFile(file);
	}
	
	public void documentFile(File file) {
		target.documentFile(file);
	}
}
