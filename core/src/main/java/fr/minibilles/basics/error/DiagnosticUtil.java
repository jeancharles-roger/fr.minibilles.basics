package fr.minibilles.basics.error;

/**
 * <p>Set of method to help {@link Diagnostic} use.</p>
 * @author Jean-Charles Roger
 */
public class DiagnosticUtil {

	public static String createMessage(Throwable e) {
		
		StringBuilder message = new StringBuilder();
		message.append(e.getClass().getSimpleName());
		if ( e.getLocalizedMessage() != null ) {
			message.insert(0, "[");
			message.append("] ");
			message.append(e.getLocalizedMessage());
		} else if ( e.getMessage() != null ) {
			message.insert(0, "[");
			message.append("] ");
			message.append(e.getMessage());
		}
		if (e.getCause() != null && e.getCause() != e ) {
			message.append("\n");
			message.append(createMessage(e.getCause()));
		}
		return message.toString();
	}
	
}
