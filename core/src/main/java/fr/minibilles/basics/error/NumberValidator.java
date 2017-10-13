/**
 * 
 */
package fr.minibilles.basics.error;

import fr.minibilles.basics.Basics;

/**
 * A {@link String} is valid if it represents a number.
 * 
 * <p>
 * Supported styles:
 * <ul>
 * <li>FLOATING_POINT</li>
 * <li>POSITIVE</li>
 * <li>NOT_ZERO</li>
 *</ul>
 * 
 * @author Jean-Charles Roger
 *
 */
public class NumberValidator extends Validator.Stub<String> {

	public NumberValidator(int level, String message, int style) {
		super(level, message, style);
	}

	public NumberValidator(Diagnostic diagnostic, int style) {
		super(diagnostic, style);
	}

	public boolean isValid(String value) {
		try {
			if ( hasStyle(Basics.FLOATING_POINT) ) {
				double number = Double.valueOf(value);
				if ( hasStyle(Basics.POSITIVE) && number < 0.0) { return false; }
				if ( hasStyle(Basics.NOT_ZERO) && number == 0.0) { return false; }
			} else {
				int number = Integer.valueOf(value);
				if ( hasStyle(Basics.POSITIVE) && number < 0) { return false; }
				if ( hasStyle(Basics.NOT_ZERO) && number == 0) { return false; }
			}
		} catch (Throwable t) {
			return false;
		}
		return true;
	}
}

