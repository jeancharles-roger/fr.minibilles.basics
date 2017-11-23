package fr.minibilles.basics.sexp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class contains shortcuts to create {@link SExp} elements.
 * 
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public class S {
	
	/** 
	 * Creates a {@link SAtom} with given value.
	 * @return a {@link SAtom}
	 */
	public static SAtom snullatom() {
		return new SAtom("null");
	}
	

	/** 
	 * Creates a {@link SAtom} with given value.
	 * @param value the value
	 * @return a {@link SAtom}
	 */
	public static SAtom satom(String value) {
		return new SAtom(value);
	}
	
	/** 
	 * Creates a {@link SList} with given children.
	 * @param children the children
	 * @return a {@link SList}
	 */
	public static SList slist(SExp ... children) {
		return new SList(children);
	}

	/** 
	 * Creates a {@link SList} with given children.
	 * @param children the children
	 * @return a {@link SList}
	 */
	public static SList slist(List<SExp> children) {
		return new SList(children);
	}
	
	/** 
	 * Creates a {@link SVariable} with given name.
	 * @param name the name
	 * @return a {@link SVariable}
	 */
	public static SVariable svariable(String name) {
		return new SVariable(name);
	}
	
	/**
	 * Quote string.
	 * @param toQuote to quote
	 * @return quoted string
	 */
	public static String quote(String toQuote) {
		if ( toQuote == null ) return "''";
		StringBuilder quoted = new StringBuilder();
		quoted.append('\'');
		for (int i=0; i<toQuote.length(); i++ ) {
			char c = toQuote.charAt(i);
			if ( c == '\'' ) {
				quoted.append("\\'");
			} else {
				quoted.append(c);
			}
		}
		quoted.append('\'');
		return quoted.toString();
	}
	
	/**
	 * Un-quote string.
	 * @param value to un-quote
	 * @return unquoted string
	 */
	public static String unquote(String value) {
		if ( isQuoted(value) ) {
			// TODO uses an ad-hock method for performances
			int length = value.length();
			String result = value.substring(1, length-1);
			result = result.replaceAll("\\\\'", "'");
			return result;
		} else {
			return value;
		}
	}
	
	public static boolean isQuoted(String value) {
		int length = value.length();
		if ( value == null || length < 2 ) return false;
		if ( value.charAt(0) != '\'' || value.charAt(length-1) != '\'' ) return true;
		return true;
	}

	public static void addChildIfNotNull(SList container, SExp child) {
		if ( child.isNull() == false ) {
			container.addChild(child);
		}
	}
	
	public SExp parse(String contents) {
		if ( contents == null ) return null;
		ByteArrayInputStream stream = null;
		try {
			SExpParser parser = new SExpParser();
			stream = new ByteArrayInputStream(contents.getBytes());
			return parser.parse(stream);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Transforms a <b>Enum</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp enumToSExp(String name, Enum<?> value) {
		SList sexp = slist(satom(name));
		if ( value != null ) {
			sexp.addChild(satom(value.name()));
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 * Extract a <b>Enum</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static <T extends Enum<T>> T sexpToEnum(Class<T> type, SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		return Enum.valueOf(type, sexp.getChild(1).getValue());
	}
	
	
	/**
	 * Transforms a <b>Collection&lt;Enul&gt;</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static <T extends Enum<T>> SExp enumCollectionToSExp(String name, Collection<T> value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( T oneValue : value ) {
				sexp.addChild(satom(oneValue.name()));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 * Extract a <b>Collection&lt;StriCollectiont;</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static <T extends Enum<T>> Collection<T> sexpToEnumCollection(Class<T> type, SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		Collection<T> result = new ArrayList<T>();
		for ( int i=1; i<sexp.getChildCount(); i++ ) {
			result.add(Enum.valueOf(type, sexp.getChild(i).getValue()));
		}
		return result;
	}
	
	
	/**
	 * Transforms a <b>String</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp stringToSExp(String name, String value) {
		SList sexp = slist(satom(name));
		if ( value != null ) {
			sexp.addChild(satom(quote(value)));
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>String</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static String sexpToString(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		return unquote(sexp.getChild(1).getValue());
	}
	
	/**
	 * Transforms a <b>Character</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp characterToSExp(String name, Character value) {
		SList sexp = slist(satom(name));
		if ( value != null ) {
			sexp.addChild(satom(quote(value.toString())));
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>Character</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static Character sexpToCharacter(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		return unquote(sexp.getChild(1).getValue()).charAt(0);
	}
	
	/**
	 * Transforms a <b>Collection&lt;String&gt;</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp stringCollectionToSExp(String name, Collection<String> value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( String oneValue : value ) {
				sexp.addChild(satom(quote(oneValue)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>Collection&lt;StriCollectiont;</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static Collection<String> sexpToStringCollection(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		Collection<String> result = new ArrayList<String>();
		for ( int i=1; i<sexp.getChildCount(); i++ ) {
			result.add(unquote(sexp.getChild(i).getValue()));
		}
		return result;
	}
	
	/**
	 * Transforms a <b>File</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp fileToSExp(String name, File value) {
		SList result = slist(satom(name));
		if ( value != null ) {
			String path = value.getPath();
			path = path.replace('\\', '/');
			String verbatim = quote(path);
			result.addChild(satom(verbatim));
		} else {
			result.addChild(snullatom());
		}
		return result;
	}

	/**
	 *  Extract a <b>File</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static File sexpToFile(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		String path = unquote(sexp.getChild(1).getValue());
		path = path.replace('\\', '/');
		return new File(path);
	}

	/**
	 * Transforms a <b>Collection&lt;File&gt;</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp fileCollectionToSExp(String name, Collection<File> value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( File oneValue : value ) {
				String path = oneValue.getPath();
				path = path.replace('\\', '/');
				sexp.addChild(satom(quote(path)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}

	/**
	 *  Extract a <b>Collection&lt;String&gt;</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static Collection<File> sexpToFileCollection(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		Collection<File> result = new ArrayList<File>();
		for ( int i=1; i<sexp.getChildCount(); i++ ) {
			String path = unquote(sexp.getChild(i).getValue());
			path = path.replace('\\', '/');
			result.add(new File(path));
		}
		return result;
	}
	
	/**
	 * Transforms a <b>int</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp intToSExp(String name, int value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(satom(Integer.toString(value)));
		return sexp;
	}
	
	/**
	 *  Extract a <b>int</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static int sexpToInt(SExp sexp) {
		return Integer.parseInt(sexp.getChild(1).getValue());
	}

	/**
	 * Transforms a <b>int[]</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp intArrayToSExp(String name, int[] value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( int oneValue : value ) {
				sexp.addChild(satom(Integer.toString(oneValue)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>int[]</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static int[] sexpToIntArray(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		int[] result = new int[sexp.getChildCount()-1];
		for ( int i=1; i<sexp.getChildCount(); i++) {
			result[i-1] = Integer.parseInt(sexp.getChild(i).getValue());
		}
		return result;
	}
	
	
	/**
	 * Transforms a <b>long</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp longToSExp(String name, long value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(satom(Long.toString(value)));
		return sexp;
	}
	
	/**
	 *  Extract a <b>long</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static long sexpToLong(SExp sexp) {
		return Long.parseLong(sexp.getChild(1).getValue());
	}
	
	/**
	 * Transforms a <b>long[]</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp longArrayToSExp(String name, long[] value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( long oneValue : value ) {
				sexp.addChild(satom(Long.toString(oneValue)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>long[]</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static long[] sexpToLongArray(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		long[] result = new long[sexp.getChildCount()-1];
		for ( int i=1; i<sexp.getChildCount(); i++) {
			result[i-1] = Long.parseLong(sexp.getChild(i).getValue());
		}
		return result;
	}
	
	/**
	 * Transforms a <b>Integer</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp integerToSExp(String name, Integer value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(satom(value.toString()));
		return sexp;
	}


	/**
	 *  Extract a <b>Integer</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static Integer sexpToInteger(SExp sexp) {
		return new Integer(sexp.getChild(1).getValue());
	}
	
	/**
	 * Transforms a <b>Collection&lt;Integer&gt;</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp integerCollectionToSExp(String name, Collection<Integer> value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( Integer oneValue : value ) {
				sexp.addChild(satom(oneValue.toString()));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>List&lt;Integer&gt;</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static Collection<Integer> sexpToIntegerCollection(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		Collection<Integer> result = new ArrayList<Integer>();
		for ( int i=1; i<sexp.getChildCount(); i++ ) {
			result.add(new Integer(sexp.getChild(i).getValue()));
		}
		return result;
	}

	/**
	 *  Extract a <b>BigDecimal</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static BigDecimal sexpToBigDecimal(SExp sexp) {
		final SExp child = sexp.getChild(1);
		if ( child.isNull() ) {
			return null;
		} else {
			return new BigDecimal(child.getValue());
		}
	}
	
	/**
	 * Transforms a <b>BigDecimal</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp bigDecimalToSExp(String name, BigDecimal value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(value == null ? snullatom() : satom(value.toString()));
		return sexp;
	}


	/**
	 *  Extract a <b>boolean</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static boolean sexpToBoolean(SExp sexp) {
		return Boolean.parseBoolean(sexp.getChild(1).getValue());
	}

	/**
	 * Transforms a <b>boolean</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp booleanToSExp(String name, boolean value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(satom(Boolean.toString(value)));
		return sexp;
	}

	/**
	 * Transforms a <b>boolean[]</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp booleanArrayToSExp(String name, boolean[] value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( boolean oneValue : value ) {
				sexp.addChild(satom(Boolean.toString(oneValue)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}


	/**
	 *  Extract a <b>boolean[]</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static boolean[] sexpToBooleanArray(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		boolean[] result = new boolean[sexp.getChildCount()-1];
		for ( int i=1; i<sexp.getChildCount(); i++) {
			result[i-1] = Boolean.parseBoolean(sexp.getChild(i).getValue());
		}
		return result;
	}


	/**
	 * Transforms a <b>float</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp floatToSExp(String name, float value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		sexp.addChild(satom(Float.toString(value)));
		return sexp;
	}
	
	/**
	 *  Extract a <b>float</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static float sexpToFloat(SExp sexp) {
		return Float.parseFloat(sexp.getChild(1).getValue());
	}
	
	/**
	 * Transforms a <b>float[]</b> attribute to {@link SExp}.
	 * @param name attribute name
	 * @param value attribute value
	 * @return the {@link SExp}.
	 */
	public static SExp floatArrayToSExp(String name, float[] value) {
		SList sexp = new SList();
		sexp.addChild(satom(name));
		if ( value != null ) {
			for ( float oneValue : value ) {
				sexp.addChild(satom(Float.toString(oneValue)));
			}
		} else {
			sexp.addChild(snullatom());
		}
		return sexp;
	}
	
	/**
	 *  Extract a <b>float[]</b> from a {@link SExp}.
	 * @param sexp source
	 * @return the extracted value
	 */
	public static float[] sexpToFloatArray(SExp sexp) {
		// tests null
		if ( sexp.getChildCount() == 2 && sexp.getChild(1).isNull() ) {
			return null;
		}
		// constructs result
		float[] result = new float[sexp.getChildCount()-1];
		for ( int i=1; i<sexp.getChildCount(); i++) {
			result[i-1] = Float.parseFloat(sexp.getChild(i).getValue());
		}
		return result;
	}
}
