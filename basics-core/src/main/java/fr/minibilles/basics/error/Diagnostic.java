package fr.minibilles.basics.error;

/**
 * A Diagnostic provides informations on errors.
 * @author Jean-Charles Roger
 *
 */
public interface Diagnostic {

	/** Information level diagnostic */
	public static int INFO = 0;
	
	/** Warning level diagnostic */
	public static int WARNING = 1;

	/** Error level diagnostic */
	public static int ERROR = 2;
	
	public static class Stub implements Diagnostic {
		private final int level;
		private final String message;
		
		public Stub(int level, String message) {
			super();
			this.level = level;
			this.message = message;
		} 

		public int getLevel() {
			return level;
		}
		
		public String getMessage() {
			return message;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			switch (getLevel()) {
			case INFO: 
				builder.append("INFO");
				break;
			case WARNING: 
				builder.append("WARNING");
				break;
			case ERROR: 
				builder.append("ERROR");
				break;
			}
			builder.append(": ");
			builder.append(getMessage());
			return builder.toString();
		}
	}
	
	/** Diagnostic's message */
	public String getMessage();
	
	/** Diagnostic level */
	public int getLevel();
}
