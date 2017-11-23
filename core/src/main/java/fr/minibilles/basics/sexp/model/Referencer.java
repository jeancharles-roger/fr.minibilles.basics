package fr.minibilles.basics.sexp.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A {@link Referencer} creates {@link String} references for objects to 
 * serialize. References are contextual, each time a call to 
 * {@link #pushContext(Object)} returns true it creates a new context where 
 * reference should be unique. A reference is created when an object is being
 * serialized. When an object is referenced by another there are two cases:
 * <ul>
 * <li>The object has been serialized so the reference exists (backward 
 * reference)</li>
 * <li>The object hasn't been serialized yet, so the reference is stored for
 * later resolving actually when the object is serialized (forward reference).
 * If the reference object is not serialized, an unresolved reference exception
 * is thrown.</li>
 * </ul>
 *
 * <p>
 * The object structure (containment tree) is reproduced using 
 * {@link #pushContext(Object)} and {@link #popContext(Object)} methods allowing
 * to construct reference depending on their parents. It's also the way to
 * tell whenever an object creates a new referencing context (by returning true
 * to those methods).
 *
 * <p>
 * References for one context should be unique, but there is a fail safe 
 * mechanism (adds '_' at the end of duplicated references) that allows to 
 * serialize the model with out errors. When it happens, the resulting text
 * file will be much more fragile to hand edition that it normally is.
 *
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public interface Referencer {

	/**
	 * An object is given for context. It's used for reference construction.
	 * It allows to construct qualified name with objects that contains the 
	 * referenced one.
	 * 
	 * @param object to push in context
	 * @return true if a new reference to object context is to be created.
	 */
	boolean pushContext(Object object);
	
	/**
	 * <p>An object is given out of context. It's always called after 
	 * {@link #pushContext(Object)} for the same object. It removes
	 * given object of context.
	 * 
	 * @param object to pop from context.
	 * @return true if a reference to object context if to be removed. It
	 * 	<b>must</b> return the same result of {@link #pushContext(Object)} for
	 * 	the same object.
	 * 		
	 */
	boolean popContext(Object object);
	
	/**
	 * Forces an adhock reference for an object. It must be called before
	 * {@link #referenceFor(Object)} to be valid.
	 *
	 * @param object referenced object
	 * @param reference the adhock reference.
	 */
	void forceReference(Object object, String reference);
	
	/**
	 * Creates a reference for given object.
	 * 
	 * @param object to create reference for.
	 * @return a {@link String} reference, <b>can't be null</b>.
	 */
	String referenceFor(Object object);
	
	/**
	 * Models to serialize often haven standard object that can be
	 * referenced without been contained in the file. These objects are builtin
	 * objects. This method can return builtin object that will be included in
	 * the reference process.
	 * @return a {@link Map} of builtin objects and their reference.
	 */
	Map<String, Object> builtins();
	
	public static class GlobalId implements Referencer {

		private int current = 0;
		private final Map<Object, String> references = new HashMap<Object, String>();
		
		public boolean pushContext(Object object) {
			// not used
			return false;
		}

		public boolean popContext(Object object) {
			// not used
			return false;
		}
		
		public void forceReference(Object object, String reference) {
			references.put(object, reference);
		}

		public String referenceFor(Object object) {
			if ( references.containsKey(object) ) {
				return references.get(object);
			} else {
				String id = Integer.toString(current);
				current += 1;
				return id;
			}
		}
		
		public Map<String, Object> builtins() {
			return Collections.emptyMap();
		}
	}

	public static class LocalId implements Referencer {
		
		private final Stack<Integer> idStack = new Stack<Integer>();
		private final Map<Object, String> references = new HashMap<Object, String>();
		
		public LocalId() {
			// inits stack
			idStack.push(0);
		}
		
		public boolean pushContext(Object object) {
			idStack.push(0);
			return false;
		}
		
		public boolean popContext(Object object) {
			idStack.pop();

			// increases local id
			idStack.push(idStack.pop() + 1);
			return false;
		}

		public void forceReference(Object object, String reference) {
			references.put(object, reference);
		}
		
		public String referenceFor(Object object) {
			if ( references.containsKey(object) ) {
				return references.get(object);
			} else {
				// constructs reference
				StringBuilder reference = new StringBuilder();
				for ( int localId : idStack ) {
					reference.append("/");
					reference.append(localId);
				}
				return reference.toString();
			}
		}
		
		public Map<String, Object> builtins() {
			return Collections.emptyMap();
		}
	}
}
