package fr.minibilles.basics.model;

import fr.minibilles.basics.error.DiagnosticUtil;
import fr.minibilles.basics.sexp.model.SExpToModel;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public abstract class AbstractCloner {

	/**
	 * Private inner class that stores references for which the object isn't
	 * cloned yet. When a new object is cloned, all
	 * {@link PendingReference} for new object reference stored in 
	 * {@link SExpToModel#contextCurrent} are resolved.
	 */
	private class PendingReference {
		final private Object receiver;
		final private Method method;
		
		public PendingReference(Object receiver, Method method) {
			this.receiver = receiver;
			this.method = method;
		}
	}
	
	/**
	 * Object stack used to returns values from visit methods.
	 */
	private final Stack<Object> objectStack = new Stack<Object>();
	
	/**
	 * One to one map from original object to its clone.
	 */
	private final Map<Object, Object> clonedMap = new HashMap<Object, Object>();

	/**
	 * Stores {@link PendingReference} that references object that aren't cloned yet.
	 */
	private final Map<Object, List<PendingReference>> pendingReferenceMap = new HashMap<Object, List<PendingReference>>();

	/**
	 * Cache that stores found method used to set references. It allows to
	 * search only once for each reference setter.
	 */
	private final Map<String, Method> referenceMethodCacheMap = new HashMap<String, Method>();

	public int stackSize() {
		return objectStack.size();
	}
	
	public <T> T peekObject(Class<T> type) {
		Object node = objectStack.peek();
		// if node isn't of type, an ClassCastException will be thrown.
		return type.cast(node);
	}
	
	/**
	 * Pops as many element as needed to get back to expected size.
	 * @param expectedSize resulting {@link #stackSize()}.
	 * @param type type of each element
	 * @return a {@link List} of pop elements
	 */
	public <T> List<T> popObjectTo(int expectedSize, Class<T> type) {
		int currentSize = stackSize();
		int delta = currentSize - expectedSize;
		if ( delta < 0) {
			// nothing to pop
			return Collections.emptyList();
		} else if ( delta == 1 ) {
			// just one to pop
			return Collections.singletonList(popObject(type));
		} else {
			// several to pop
			List<T> result = new ArrayList<T>(delta);
			for ( int i=0; i<delta; i++ ) {
				result.add(popObject(type));
			}
			Collections.reverse(result);
			return result;
		}
	}

	public <T> T popObject(Class<T> type) {
		Object node = objectStack.pop();
		return type.cast(node);
	}
	
	public void pushObject(Object node) {
		objectStack.push(node);
	}

	protected void registerClone(Object original, Object cloned) {
		// registers clone
		clonedMap.put(original, cloned);
		
		// checks if there aren't pending reference for object
		List<PendingReference> toResolveList = pendingReferenceMap.get(original);
		if ( toResolveList != null ) {
			for ( PendingReference pending : toResolveList ) {
				try {
					pending.method.invoke(pending.receiver, cloned);
				} catch (Exception e) {
					String message = DiagnosticUtil.createMessage(e);
					throw new IllegalArgumentException(message);
				}
			}
		}
	}
	
	protected boolean hasClone(Object original) {
		return clonedMap.containsKey(original);
	}
	
	protected Object getClone(Object original) {
		return clonedMap.get(original);
	}
	
	protected Object removeClone(Object original) {
		return clonedMap.remove(original);
	}
	
	protected void registerReference(Object receiver, String name, boolean many, Object original) {
		// treats null case.
		if ( original == null ) return;
		
		// not cloned yet adds to pending references.
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
				throw new IllegalArgumentException("Method '"+ methodName +"' not found on class '"+ klazz.getSimpleName() +"'.");
			}
			
			referenceMethodCacheMap.put(methodKey, method);
		}
		
		Object cloned = clonedMap.get(original);
		// sets the reference to original, it will be override by cloned if object is cloned
		Object referenced = cloned == null ? original : cloned;

		// sets the reference to either cloned or original
		try {
			method.invoke(receiver, referenced);
		} catch (Exception e) {
			String message = DiagnosticUtil.createMessage(e);
			throw new IllegalArgumentException(message);
		}

		if (cloned == null) {
			// original isn't cloned yet, store the reference for later on
			registerPendingReference(original, receiver, method);
		}

	}

	private void registerPendingReference(Object original, Object receiver, Method method) {
		List<PendingReference> toResolveList = pendingReferenceMap.get(original);
		if ( toResolveList == null ) {
			toResolveList = new ArrayList<PendingReference>();
			pendingReferenceMap.put(original, toResolveList);
		}
		toResolveList.add(new PendingReference(receiver, method));
	}

	protected static String upcaseFirstCharacter(String value) {
		if (value == null) return null;
		if (value.length() == 1 ) return value.toUpperCase();
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}

}
