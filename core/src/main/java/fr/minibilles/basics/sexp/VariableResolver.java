package fr.minibilles.basics.sexp;

import java.util.Collections;
import java.util.Map;

/**
 * When translating a {@link SExp} to a model representation, a
 * {@link VariableResolver} gives for each {@link SVariable} a value.
 * 
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public interface VariableResolver {
	/**
	 * Resolves given name. When a variable resolution fails, it should throw
	 * an exception to avoid non-wanted null values.
	 * 
	 * @param name variable name.
	 * @param type result type.
	 * @return the value (can be null).
	 */
	<T> T resolve(String name, Class<T> type);
	
	
	/**
	 * Simple {@link VariableResolver} which is given a {@link Map} as
	 * variable association.
	 * 
	 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
	 *
	 */
	public static class Mapped implements VariableResolver {
		private final Map<String, Object> values;
		
		public Mapped() {
			this(null);
		}
		
		public Mapped(Map<String, Object> values) {
			if ( values == null ) {
				this.values = Collections.emptyMap();
			} else {
				this.values = values;
			}
		}
		
		public <T> T resolve(String name, Class<T> type) {
			Object value = values.get(name);
			if ( value == null ) {
				throw new IllegalArgumentException("Unknown variable '"+ name +"'.");
			}
			if ( type.isInstance(value) == false )  {
				StringBuilder message = new StringBuilder();
				message.append("Variable '");
				message.append(name);
				message.append("' needs a '");
				message.append(type.getSimpleName());
				message.append("' but received a '");
				message.append(value.getClass().getSimpleName());
				message.append("'.");
				throw new IllegalArgumentException(message.toString());
			}
			return type.cast(value);
		}
		
	}
}
