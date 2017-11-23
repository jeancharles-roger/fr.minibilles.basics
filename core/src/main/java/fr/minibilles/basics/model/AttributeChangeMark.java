package fr.minibilles.basics.model;

import java.lang.reflect.Method;


/**
 * {@link ChangeMark} to save a attribute that changed.
 *
 * @author Jean-Charles Roger
 *
 */
public class AttributeChangeMark extends ChangeMark {

	protected final ModelObject receiver;
	protected final String attributeName;
	protected final Object oldValue;
	
	public AttributeChangeMark (long timestamp, ModelObject receiver, String attributeName, Object oldValue) {
		super(timestamp);
		this.receiver = receiver;
		this.attributeName = attributeName;
		this.oldValue = oldValue;
	}
	
	protected String getSetterName() {
		String firstUppercaseAttribute = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
		return "set" + firstUppercaseAttribute;
	}
	
	protected Method getUndoMethod() {
		Method method = null;
		String name = getSetterName();
		final Class<? extends ModelObject> receiverClass = receiver.getClass();
		Class<?> parameterType = oldValue == null ? null : oldValue.getClass();
		method = getMethod(receiverClass, name, parameterType);
		if ( method == null ) {
			if ( parameterType == Boolean.class ) {
				method = getMethod(receiverClass, name, boolean.class);
			} else if ( parameterType == Character.class ) {
				method = getMethod(receiverClass, name, char.class);				
			} else if ( parameterType == Byte.class ) {
				method = getMethod(receiverClass, name, byte.class);				
			} else if ( parameterType == Short.class ) {
				method = getMethod(receiverClass, name, short.class);				
			} else if ( parameterType == Integer.class ) {
				method = getMethod(receiverClass, name, int.class);				
			} else if ( parameterType == Long.class ) {
				method = getMethod(receiverClass, name, long.class);				
			} else if ( parameterType == Float.class ) {
				method = getMethod(receiverClass, name, float.class);				
			} else if ( parameterType == Double.class ) {
				method = getMethod(receiverClass, name, double.class);				
			}
		}
		return method;
	}
	
	public void undo() {
		try {
			Method method = getUndoMethod();
			if ( method != null ) {
				method.invoke(receiver, oldValue);
			} else {
				System.err.println("AttributeChangeMark.undo(): No method to undo");
			}
		} catch (Exception e) {
			System.err.println("AttributeChangeMark.undo(): " + e.getClass().getName());
			// can't undo
		}
	}
	
	public String toString() {
		return "At|" + attributeName;
	}
}
