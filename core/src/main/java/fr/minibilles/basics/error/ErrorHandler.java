/**
 *  Copyright 2008 GeenSys. All rights reserved.
 */
package fr.minibilles.basics.error;

/**
 * <p>
 * Handles errors for models actions.
 * </p>
 * 
 * @author Jean-Charles Roger
 * 
 */
public interface ErrorHandler {

	public void handleError(int type, String message);
	public void handleError(Diagnostic diagnostic);

	/**
	 * <p>
	 * This {@link ErrorHandler} prints errors and warning to console and
	 * throws a {@link RuntimeException} for ERROR and FATAL_ERROR.
	 * </p>
	 */
	public static final ErrorHandler simple = new ErrorHandler() {
		public void handleError(int type, String message) {
			handleError(new Diagnostic.Stub(type, message));
		}
		
		public void handleError(Diagnostic diagnostic) {
			switch (diagnostic.getLevel()) {
			case Diagnostic.INFO:
				System.out.println(diagnostic.getMessage());
				break;
			
			case Diagnostic.WARNING:
				System.err.println(diagnostic.getMessage());
				break;

			case Diagnostic.ERROR:
				System.err.println(diagnostic.getMessage());
				throw new RuntimeException(diagnostic.getMessage());
			}
		}
    };

}
