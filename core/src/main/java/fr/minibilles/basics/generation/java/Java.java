package fr.minibilles.basics.generation.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sets of constants and utility methods for {@link JavaContentHandler}.
 * @author Jean-Charles Roger
 */
public class Java {

	/** Empty flag. */
	public static final int NONE 		= 0;

	/** Private visibility */
	public static final int PRIVATE 	= 1;
	/** Protected visibility */
	public static final int PROTECTED 	= 1<<1;
	/** Public visibility */
	public static final int PUBLIC 		= 1<<2;

	/** Static flag */
	public static final int STATIC 		= 1<<3;
	/** Final flag */
	public static final int FINAL		= 1<<4;
	/** Abstract flag */
	public static final int ABSTRACT	= 1<<5;
	/** Transient flag */
	public static final int TRANSIENT	= 1<<6;
	
	/** Single line comment */
	public static final int SINGLE_LINE	= 1;
	/** Multi line comment */
	public static final int MULTI_LINE	= 2;
	/** Java doc comment */
	public static final int JAVA_DOC	= 3;
	
	/** Tests a flag */
	public static boolean has(int flags, int test) {
		return (flags & test) != 0;
	}

	/** @return the declaration string for given flags. */
	public static String printFlags(int flags) {
		StringBuilder string = new StringBuilder();
		if ( has(flags, PRIVATE) ) string.append("private ");
		if ( has(flags, PROTECTED) ) string.append("protected ");
		if ( has(flags, PUBLIC) ) string.append("public ");
		
		if ( has(flags, STATIC) ) string.append("static ");
		if ( has(flags, FINAL) ) string.append("final ");
		if ( has(flags, ABSTRACT) ) string.append("abstract ");
		if ( has(flags, TRANSIENT) ) string.append("transient ");
		return string.toString();
	}
	
	/** @return the string of given list separated by colons. */
	public static String printList(String [] list) {
		StringBuilder builder = new StringBuilder();
		if ( list != null && list.length > 0) {
			builder.append(list[0]);
			for ( int i=1; i<list.length; i++) {
				builder.append(", ");
				builder.append(list[i]);
			}
		}
		return builder.toString();
	}

	/** @return the string of given list separated by colons. */
	public static String printList(Parameter [] parameters) {
		StringBuilder builder = new StringBuilder();
		if ( parameters != null && parameters.length > 0) {
			builder.append(parameters[0]);
			for ( int i=1; i<parameters.length; i++) {
				builder.append(", ");
				builder.append(parameters[i]);
			}
		}
		return builder.toString();
	}
	
	/** Capitalizes the first letter. */
	public static String capitalize(String name) {
		if ( name == null ) return null;
		if (name.length() == 1 ) return name.toUpperCase();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	/** Begins a main method. */
	public static void beginMain(JavaContentHandler handler) {
		handler.beginMethod(Java.PUBLIC | Java.STATIC, "void", "main", null, new Java.Parameter(Java.NONE, "String[]", "args"));
	}
	
	/** Generates Getter and Setter */
	public static  void generateGetterSetter(JavaContentHandler g, String javaType, String name) {
		String methodName = "get" + capitalize(name);
		g.beginMethod(Java.PUBLIC, javaType, methodName, null);
		g.codeln(0, "return " + name + ";");
		g.endMethod(methodName);
		// setter
		methodName = "set" + capitalize(name);
		g.beginMethod(Java.PUBLIC, "void", methodName, null, new Parameter(Java.NONE, javaType, name));
		g.codeln(0, "this." + name + " = " + name + ";");
		g.endMethod(methodName);
	}
	
	
	/** Ends a main method. */
	public static void endMain(JavaContentHandler handler) {
		handler.endMethod("main");
	}
	
	/**
	 * A Java parameter definition.
	 * @author Jean-Charles Roger
	 */
	public static class Parameter {
		public int flags;
		public String type;
		public String name;
	
		public Parameter(int flags, String type, String name) {
			super();
			this.flags = flags;
			this.type = type;
			this.name = name;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(printFlags(flags));
			builder.append(type);
			builder.append(" ");
			builder.append(name);
			return builder.toString();
		}
	}

	/** Splits a package name into a list of short names (on '.'). */ 
	public static List<String> splitPackageName(String name) {
		if ( name == null ) return Collections.emptyList();
		String[] parts = name.split("\\.");
		ArrayList<String> result = new ArrayList<String>(parts.length);
		for ( String part : parts ) {
			if ( part != null && part.length() > 0 ) result.add(part);
		}
		return result;
	}
}

