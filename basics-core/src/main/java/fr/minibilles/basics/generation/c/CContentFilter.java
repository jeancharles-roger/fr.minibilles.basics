package fr.minibilles.basics.generation.c;

import java.io.InputStream;

/**
 * This {@link CContentHandler} delegates all calls to a given target.
 * It allows to create filters and formatters on {@link CContentHandler}.
 * @author Jean-Charles Roger
 */
public class CContentFilter implements CContentHandler {

	private final CContentHandler target;

	public CContentFilter(CContentHandler target) {
		if ( target == null ) throw new NullPointerException("Target CContentHandler can't be null.");
		this.target = target;
	}
	
	public CContentHandler getTarget() {
		return target;
	}
	
	
	public void beginDirectory(String name) {
		target.beginDirectory(name);
	}
	
	public void beginFile(String name) {
		target.beginFile(name);
	}

	public void functionDeclaration(int flags, String returnType, String name, C.Parameter... parameters) {
		target.functionDeclaration(flags, returnType, name, parameters);
	}
	
	public void beginFunction(int flags, String returnType, String name, C.Parameter... parameters) {
		target.beginFunction(flags, returnType, name, parameters);
	}

	public void comment(int flags, int level, String comment) {
		target.comment(flags, level, comment);
	}

	public void endDirectory(String name) {
		target.endDirectory(name);
	}
	
	public void endFile(String name) {
		target.endFile(name);
	}

	public void endFunction(String name) {
		target.endFunction(name);
	}

	public void codeln(int level, String statements) {
		target.codeln(level, statements);
	}
	
	public void code(String statements) {
		target.code(statements);
	}
	
	public void addFile(InputStream stream, String filename) {
		target.addFile(stream, filename);
	}
}
