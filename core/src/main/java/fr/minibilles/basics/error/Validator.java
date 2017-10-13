/**
 * 
 */
package fr.minibilles.basics.error;

import fr.minibilles.basics.Basics;

/**
 * A validator returns a diagnostic on a value
 * @author Jean-Charles Roger
 *
 */
public interface Validator<T> {

	public static abstract class Stub<T> implements Validator<T> {
		protected final int style;
		protected final Diagnostic diagnostic;
		
		public Stub(int level, String message) {
			this(new Diagnostic.Stub(level, message), Basics.NONE);
		}
		
		public Stub(int level, String message, int style) {
			this(new Diagnostic.Stub(level, message), style);
		}
		
		public Stub(Diagnostic diagnostic, int style) {
			this.diagnostic = diagnostic;
			this.style = style;
		}

		public Diagnostic getDiagnostic() { return diagnostic; }
		public boolean hasStyle(int mask) { return (style & mask) != 0; }
	}
	
	/** @return true if value is valid, false otherwise. */
	public boolean isValid(T value);
	
	/** @return the {@link Diagnostic} associated to this. */
	public Diagnostic getDiagnostic();
}
