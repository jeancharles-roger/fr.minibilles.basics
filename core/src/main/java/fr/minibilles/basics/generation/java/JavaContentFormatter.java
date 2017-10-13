package fr.minibilles.basics.generation.java;

import fr.minibilles.basics.generation.java.Java.Parameter;
import java.util.regex.Pattern;

/**
 * Simple {@link JavaContentHandler} that formats code before writing.
 * It adds newlines and indentation.
 * @author Jean-Charles Roger
 */
public class JavaContentFormatter extends JavaContentFilter {

	private final String NL = System.getProperty("line.separator");
	private final Pattern NL_REGEX = Pattern.compile(NL);
	
	private int level = 0;

	private String indentString;
	
	public JavaContentFormatter(JavaContentHandler target, String indentString) {
		super(target);
		this.indentString = indentString;
	}
	
	public JavaContentFormatter(JavaContentHandler target) {
		this(target, "\t");
	}

	@Override
	public void annotation(String name, String parameters) {
		super.codeln(0, indentation(level));
		super.annotation(name, parameters);
	}
	
	@Override
	public void beginAttribute(int flags, String type, String name) {
		super.codeln(0, indentation(level));
		super.beginAttribute(flags, type, name);
	}

	@Override
	public void endAttribute(String name) {
		super.endAttribute(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}
	
	@Override
	public void beginAnonymousClass(String inherited, String ... arguments) {
		super.codeln(0, indentation(level++));
		super.beginAnonymousClass(inherited, arguments);
		super.codeln(0, NL);
	}

	@Override
	public void beginClass(int flags, String name, String parentClass, String implementedInterfaces) {
		super.codeln(0, indentation(level++));
		super.beginClass(flags, name, parentClass, implementedInterfaces);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void beginEnum(int flags, String name) {
		super.codeln(0, indentation(level++));
		super.beginEnum(flags, name);
		super.codeln(0, NL);
	}
	
	@Override
	public void beginEnumLiterals() {
		super.beginEnumLiterals();
	}

	@Override
	public void enumLiteral(String name, String ... arguments) {
		super.enumLiteral(name, arguments);
		super.codeln(0, NL);
	}
	
	@Override
	public void beginFile(String name) {
		super.beginFile(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void beginInterface(int flags, String name, String extendedInterfaces) {
		super.codeln(0, indentation(level++));
		super.beginInterface(flags, name, extendedInterfaces);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void beginMethod(int flags, String returnType, String name, String exceptions, Parameter ... parameters) {
		super.codeln(0, indentation(level++));
		super.beginMethod(flags, returnType, name, exceptions, parameters);
		super.codeln(0, NL);
	}

	@Override
	public void beginPackage(String name) {
		super.beginPackage(name);
	}

	@Override
	public void endAnnonymousClass(String inherited) {
		super.codeln(0, indentation(--level));
		super.endAnnonymousClass(inherited);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endClass(String name) {
		super.codeln(0, indentation(--level));
		super.endClass(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endEnumLiterals() {
		super.endEnumLiterals();
		super.codeln(0, NL);
	}
	
	@Override
	public void endEnum(String name) {
		--level;
		super.codeln(0, indentation(level));
		super.endEnum(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endFile(String name) {
		super.endFile(name);
	}

	@Override
	public void endInterface(String name) {
		super.codeln(0, indentation(--level));
		super.endInterface(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endMethod(String name) {
		super.codeln(0, indentation(--level));
		super.endMethod(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endPackage(String name) {
		super.endPackage(name);
	}

	@Override
	public void markImports() {
		super.markImports();
	}
	
	@Override
	public void import_(int flags, String importString) {
		super.import_(flags, importString);
	}

	@Override
	public void codeln(int localLevel, String statements) {
		String[] lines = NL_REGEX.split(statements);
		for ( String line : lines ) {
			super.codeln(level+localLevel, indentation(level+localLevel));
			super.codeln(level+localLevel, line);
			super.codeln(level+localLevel, NL);
		}
	}
	
	@Override
	public void code(String statements) {
		super.code(statements);
	}
	
	@Override
	public void comment(int type, int localLevel, String comment) {
		final int innerLevel = level+localLevel;
		switch (type) {
		case Java.SINGLE_LINE:
			super.codeln(0, indentation(innerLevel));
			super.comment(type, localLevel, comment);
			break;
		case Java.MULTI_LINE:
		case Java.JAVA_DOC:
			String[] lines = NL_REGEX.split(comment);
			super.codeln(localLevel, indentation(innerLevel));
			super.codeln(localLevel, type == Java.JAVA_DOC ? "/**" : "/*");
			super.codeln(innerLevel, NL);
			for ( int i=0; i<lines.length; i++ ) {
				super.codeln(localLevel, indentation(innerLevel));
				if ( !lines[i].startsWith(" *" ) ) super.codeln(localLevel, " * ");
				super.codeln(localLevel, lines[i]);
				super.codeln(localLevel, NL);
			}
			super.codeln(localLevel, indentation(innerLevel));
			super.codeln(localLevel, " */"); 
			super.codeln(localLevel, NL);
			break;
		}
	}

	private String indentation(int level) {
		StringBuilder result = new StringBuilder();
		for ( int i=0; i<level; i++) {
			result.append(indentString);
		}
		return result.toString();
	}
}
