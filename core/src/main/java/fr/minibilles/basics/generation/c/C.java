package fr.minibilles.basics.generation.c;


/**
 * Sets of constants and utility methods for {@link CContentHandler}.
 * @author Jean-Charles Roger
 */
public class C {

	/** Empty flag. */
	public static final int NONE 		= 0;

	/** Static flag */
	public static final int STATIC 		= 1<<3;
	/** Const flag */
	public static final int CONST		= 1<<4;
	
	/** Single line comment */
	public static final int SINGLE_LINE	= 1;
	/** Multi line comment */
	public static final int MULTI_LINE	= 2;
	
	/** Tests a flag */
	public static boolean has(int flags, int test) {
		return (flags & test) != 0;
	}

	/** @return the declaration string for given flags. */
	public static String printFlags(int flags) {
		StringBuilder string = new StringBuilder();
		if ( has(flags, STATIC) ) string.append("static ");
		if ( has(flags, CONST) ) string.append("const ");
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
	
	/** Begins a main method. */
	public static void beginMain(CContentHandler handler) {
		handler.beginFunction(C.NONE, "int", "main", 
					new C.Parameter(C.NONE, "int", "argc"),
					new C.Parameter(C.NONE, "char*", "argv[]")
		);
	}
	
	/** Ends a main method. */
	public static void endMain(CContentHandler handler) {
		handler.endFunction("main");
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

}

