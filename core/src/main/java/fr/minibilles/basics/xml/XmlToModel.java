package fr.minibilles.basics.xml;

import fr.minibilles.basics.error.DiagnosticUtil;
import fr.minibilles.basics.sexp.model.Referencer;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>{@link XmlToModel} handle reference resolving for de-serialisation. It's
 * able to resolve {@link String} created by a {@link Referencer}. References 
 * can forward or backward (see {@link Referencer} for limitations).</p>
 * 
 * @author Jean-Charles Roger (jean-charles.roger@gmail.com)
 *
 */
public final class XmlToModel {

	/**
	 * <p>Private inner class that stores references for which the object isn't
	 * deserialized yet. When a new object is deserialized, all 
	 * {@link PendingReference} for new object reference stored in 
	 * {@link ContextNode} are resolved.</p> 
	 */
	private class PendingReference {
		final private Object receiver;
		final private Method method;
		
		public PendingReference(Object receiver, Method method) {
			this.receiver = receiver;
			this.method = method;
		}
	}
	
	private class ContextNode {
		
		/**
		 * {@link Map}s that stores objects by their reference.
		 */
		final HashMap<String, Object> objectMap = new HashMap<String, Object>();
		
		/**
		 * Stores {@link PendingReference} that references object that aren't de-serialized yet.
		 */
		final Map<String, List<PendingReference>> pendingReferenceMap = new HashMap<String, List<PendingReference>>();
		
		final ContextNode parent;
		
		ArrayList<ContextNode> children;
		
		ContextNode(ContextNode parent) {
			this.parent = parent;
			// registers to parent
			if ( parent != null ) {
				parent.addChild(this);
			}
		}
		
		void addChild(ContextNode child) {
			if ( children == null ) {
				children = new ArrayList<ContextNode>();
			}
			children.add(child);
		}
		
		void registerPendingReference(String reference, Object receiver, Method method) {
			List<PendingReference> toResolveList = pendingReferenceMap.get(reference);
			if ( toResolveList == null ) {
				toResolveList = new ArrayList<PendingReference>();
				pendingReferenceMap.put(reference, toResolveList);
			}
			toResolveList.add(new PendingReference(receiver, method));
		}

		void collectPendingReferencesFor(String reference, List<PendingReference> result) {
			// does current node have pendings for reference ?
			List<PendingReference> pendings = pendingReferenceMap.get(reference);
			if ( pendings != null ) {
				result.addAll(pendings);
				// clears pendings for reference
				pendingReferenceMap.remove(reference);
			}
			
			// checks chidlren if any.
			if ( children != null ) {
				for ( ContextNode child : children ) {
					child.collectPendingReferencesFor(reference, result);
				}
			}
		}

		Set<String> unresolvedReferences()  {
			HashSet<String> missing = new HashSet<String>(pendingReferenceMap.keySet());
			if ( children != null ) {
				for ( ContextNode child : children ) {
					missing.addAll(child.unresolvedReferences());
				}
			}
			return missing;
		}

		
	}
	
	/**
	 * Used to create and keep track of references.
	 */
	private final Referencer referencer;
	
	/**
	 * <p>Cache that stores found method used to set references. It allows to 
	 * search only once for each reference setter.</p>
	 */
	private final Map<String, Method> referenceMethodCacheMap = new HashMap<String, Method>();

	private final ContextNode contextRoot = new ContextNode(null);
	private ContextNode contextCurrent = contextRoot;
	
	public XmlToModel(Referencer referencer) {
		this.referencer = referencer;
		registerBuiltins();
	}
	
	/**
	 * Registers builtin objects in {@link #contextCurrent}.
	 */
	private void registerBuiltins() {
		for ( Map.Entry<String, Object> entry : referencer.builtins().entrySet() ) {
			register(entry.getKey(), entry.getValue());
		}
	}
	
	private void register(String reference, Object object) {
		final Map<String, Object> current = contextCurrent.objectMap;
		current.put(reference, object);	
	}
	
	private Object resolve(String reference) {
		// searches in current context node and parents
		ContextNode iterator = contextCurrent;
		while (iterator != null ) {
			final Map<String, Object> current = iterator.objectMap;
			final Object resolved = current.get(reference);
			if (resolved != null ) return resolved;
			
			iterator = iterator.parent;
		}
		
		// nothing found
		return null;
	}
	
	private void pushReferenceContext() {
		ContextNode contextNew = new ContextNode(contextCurrent);
		contextCurrent = contextNew;
	}
	
	private void popReferenceContext() {
		contextCurrent = contextCurrent.parent;
	}

	private String uniqueReference(String reference) {
		// ensure uniqueness of reference in current reference context
		final Map<String, Object> current = contextCurrent.objectMap;
		while ( current.containsKey(reference) ) {
			reference += "_";
		}
		return reference;
	}

	public void push(Object object) throws IOException {
		String reference = referencer.referenceFor(object);
		reference = uniqueReference(reference);
		
		// adds object to reference context
		register(reference, object);
		
		// checks if there aren't pending reference for object
		List<PendingReference> toResolveList = new ArrayList<PendingReference>();
		contextCurrent.collectPendingReferencesFor(reference, toResolveList);
		for ( PendingReference pending : toResolveList ) {
			try {
				pending.method.invoke(pending.receiver, object);
			} catch (Exception e) {
				String message = DiagnosticUtil.createMessage(e);
				throw new IOException(message);
			}
		}
		
		
		// pushes object to referencer
		if ( referencer.pushContext(object) ) {
			pushReferenceContext();
		}
	}
	
	public void pop(Object object) {
		// pops object from referencer
		if ( referencer.popContext(object) ) {
			popReferenceContext();
		}
	}
	
	public void registerReference(Object receiver, String name, boolean many, String reference) throws IOException {
		// if reference is null, abort
		if ( reference == null ) return;
			
		// searches for the method, abort if it can't find it. 
		if ( receiver == null || name == null ) return;
	
		final String prefix = many ? "add" : "set";
		final String methodName = prefix + upcaseFirstCharacter(name);
		Class<?> klazz = receiver.getClass();
		
		String methodKey = klazz.getCanonicalName() + "#" + methodName;
		Method method = referenceMethodCacheMap.get(methodKey);
		
		if ( method == null ) {
			for ( Method oneMethod : klazz.getMethods() ) {
				if (	methodName.equals(oneMethod.getName()) && 
						oneMethod.getParameterTypes().length == 1 
					) {
					
					method = oneMethod;
					break;
				}
			}
			if ( method == null ) {
				throw new IOException("Method '"+ methodName +"' not found on class '"+ klazz.getSimpleName() +"'.");
			}
			
			referenceMethodCacheMap.put(methodKey, method);
		}
		
		
		Object referenced = resolve(reference);
		
		if ( referenced != null ) {
			// id is already known, set the reference.
			try {
				method.invoke(receiver, referenced);
			} catch (Exception e) {
				String message = DiagnosticUtil.createMessage(e);
				throw new IOException(message);
			}

		} else {
			// reference is unknown, store the reference for later on
			contextCurrent.registerPendingReference(reference, receiver, method);
		}
	}
	
	public Set<String> unresolvedReferences() {
		return contextRoot.unresolvedReferences();
	}
	
	public static String upcaseFirstCharacter(String value) {
		if (value == null) return null;
		if (value.length() == 1 ) return value.toUpperCase();
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

}
