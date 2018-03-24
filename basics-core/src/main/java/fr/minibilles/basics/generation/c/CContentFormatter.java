package fr.minibilles.basics.generation.c;

import fr.minibilles.basics.generation.c.C.Parameter;
import java.util.regex.Pattern;

/**
 * Simple {@link CContentHandler} that formats code before writing.
 * It adds newlines and indentation.
 * @author Jean-Charles Roger
 */
public class CContentFormatter extends CContentFilter {

	private final String NL = System.getProperty("line.separator");
	private final Pattern NL_REGEX = Pattern.compile(NL);
	
	private int level = 0;

	private String indentString;
	
	public CContentFormatter(CContentHandler target, String indentString) {
		super(target);
		this.indentString = indentString;
	}
	
	public CContentFormatter(CContentHandler target) {
		this(target, "\t");
	}

	@Override
	public void beginFile(String name) {
		super.beginFile(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void functionDeclaration(int flags, String returnType, String name, Parameter ... parameters) {
		super.codeln(0, indentation(level));
		super.functionDeclaration(flags, returnType, name, parameters);
		super.codeln(0, NL);
	}

	@Override
	public void beginFunction(int flags, String returnType, String name, Parameter ... parameters) {
		super.codeln(0, indentation(level++));
		super.beginFunction(flags, returnType, name, parameters);
		super.codeln(0, NL);
	}

	@Override
	public void beginDirectory(String name) {
		super.beginDirectory(name);
	}

	@Override
	public void endFile(String name) {
		super.endFile(name);
	}

	@Override
	public void endFunction(String name) {
		super.codeln(0, indentation(--level));
		super.endFunction(name);
		super.codeln(0, NL);
		super.codeln(0, NL);
	}

	@Override
	public void endDirectory(String name) {
		super.endDirectory(name);
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
		case C.SINGLE_LINE:
			super.codeln(0, indentation(innerLevel));
			super.comment(type, localLevel, comment);
			break;
		case C.MULTI_LINE:
			String[] lines = NL_REGEX.split(comment);
			super.codeln(localLevel, indentation(innerLevel));
			super.codeln(localLevel, "/*");
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
