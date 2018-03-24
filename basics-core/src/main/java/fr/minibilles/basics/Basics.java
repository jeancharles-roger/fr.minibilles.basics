/**
 * 
 */
package fr.minibilles.basics;

import fr.minibilles.basics.error.NumberValidator;
import fr.minibilles.basics.system.BasicShell;

/**
 * 
 * This class groups all the styles used in the library.
 * @author Jean-Charles Roger
 *
 */
public class Basics {

	/** No style*/
	public static int NONE = 0;
	
	/** 
	 * Floating point number.
	 * Used by:
	 * <ul>
	 * <li>{@link NumberValidator}</li>
	 * </ul>
	 */
	public static int FLOATING_POINT = 1;
	
	/** 
	 * Positive number.
	 * Used by:
	 * <ul>
	 * <li>{@link NumberValidator}</li>
	 * </ul>
	 */
	public static int POSITIVE = 2;
	
	/** 
	 * Not zero number.
	 * Used by:
	 * <ul>
	 * <li>{@link NumberValidator}</li>
	 * </ul>
	 */
	public static int NOT_ZERO = 4;
	
	/** 
	 * Recurses into tree
	 * Used by:
	 * <ul>
	 * <li>{@link BasicShell}</li>
	 * </ul>
	 */
	public static final int RECURSIVE = 8;

	/** 
	 * Show hidden
	 * Used by:
	 * <ul>
	 * <li>{@link BasicShell}</li>
	 * </ul>
	 */
	public static final int HIDDEN = 16;
	
	/** 
	 * Overwrite destination
	 * Used by:
	 * <ul>
	 * <li>{@link BasicShell}</li>
	 * </ul>
	 */
	public static final int OVERWRITE = 16;
	
	/** 
	 * Create directories if needed.
	 * Used by:
	 * <ul>
	 * <li>{@link BasicShell}</li>
	 * </ul>
	 */
	public static final int MKDIR = 32;
	
	/**
	 * @return true if local host runs on Mac OS X.
	 */
	public static boolean isMac() {
		return System.getProperty("os.name").equals("Mac OS X"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @return true if local host runs on Windows
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
