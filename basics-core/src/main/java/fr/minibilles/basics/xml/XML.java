package fr.minibilles.basics.xml;

import java.math.BigDecimal;

public class XML {

	/**
	 * Transforms a <b>Enum</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String enumToString(Enum<?> value) {
		return value == null ? null : value.name();
	}
	
	/**
	 * Extract a <b>Enum</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static <T extends Enum<T>> T stringToEnum(Class<T> type, String string) {
		return string == null ? null : Enum.valueOf(type, string);
	}
	
	/**
	 * Transforms a <b>Character</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String characterToString(Character value) {
		return Character.toString(value);
	}
	
	/**
	 * Extract a <b>Character</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static Character stringToCharacter(String string) {
		return string.length() > 0 ? string.charAt(0) : '\0';
	}
	
	/**
	 * Transforms a <b>int</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String intToString(int value) {
		return Integer.toString(value);
	}
	
	/**
	 * Extract a <b>int</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static int stringToInt(String string) {
		return Integer.parseInt(string);
	}

	/**
	 * Transforms a <b>Integer</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String integerToString(Integer value) {
		return Integer.toString(value);
	}
	
	/**
	 *  Extract a <b>Integer</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static int stringToInteger(String string) {
		return Integer.parseInt(string);
	}
	
	/**
	 * Transforms a <b>long</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String longToString(long value) {
		return Long.toString(value);
	}
	
	/**
	 * Extract a <b>long</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static long stringToLong(String string) {
		return Long.parseLong(string);
	}
	
	/**
	 * Transforms a <b>Integer</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String integerObjectToString(Integer value) {
		return value == null ? null : value.toString();
	}


	/**
	 * Extract a <b>Integer</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static Integer stringToIntegerObject(String string) {
		return new Integer(string);
	}
	

	/**
	 * Extract a <b>BigDecimal</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static BigDecimal stringToBigDecimal(String string) {
		return string == null ? null : new BigDecimal(string);
	}
	
	/**
	 * Transforms a <b>BigDecimal</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String bigDecimalToString(BigDecimal value) {
		return value == null ? null : value.toString();
	}


	/**
	 * Extract a <b>boolean</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static boolean stringToBoolean(String string) {
		return Boolean.parseBoolean(string);
	}

	/**
	 * Transforms a <b>boolean</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String booleanToString(boolean value) {
		return Boolean.toString(value);
	}


	/**
	 * Transforms a <b>float</b> attribute to {@link String}.
	 * @param value attribute value
	 * @return the {@link String}.
	 */
	public static String floatToString(float value) {
		return Float.toString(value);
	}
	
	/**
	 * Extract a <b>float</b> from a {@link String}.
	 * @param string source
	 * @return the extracted value
	 */
	public static float stringToFloat(String string) {
		return Float.parseFloat(string);
	}
	
}
