package fr.minibilles.basics.model;

import java.lang.reflect.Method;


public abstract class ChangeMark {

	protected final long timestamp;

	private static class OperationChangeMark extends ChangeMark{
		
		protected OperationChangeMark(long timestamp) {
			super(timestamp);
		}
		
		@Override
		public void undo() { }

		public String toString() {
			return "T" + getTimestamp();
		}
				
		public boolean isOperationMark() {
			return true;
		}

	}
	
	public static ChangeMark Operation(long timestamp) { return new OperationChangeMark(timestamp); }
	
	protected ChangeMark(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Undo change mark.
	 */
	public abstract void undo();
		
	public long getTimestamp() {
		return timestamp;
	}
	
	public abstract String toString();
	
	public boolean isOperationMark() {
		return false;
	}
	
	protected Method getMethod(Class<?> receiverClass, String name, Class<?> ... parameterTypes) throws SecurityException {
		Method method = null;
	    for ( Method oneMethod : receiverClass.getMethods() ) {
			if ( name.equals(oneMethod.getName()) ) {
				if ( parameterTypes != null && parameterTypes.length == oneMethod.getParameterTypes().length ) {
					boolean ok = true;
					for ( int i=0; i<parameterTypes.length; i++) {
						if ( parameterTypes[i] != null ) {
							if ( !( oneMethod.getParameterTypes()[i].isAssignableFrom(parameterTypes[i]) ) ) {
								ok = false;
							}
						}
					}
					if ( ok ) {
						method = oneMethod;
						break;
					}
				} else {
					if ( oneMethod.getParameterTypes().length == 0 ) {
						method = oneMethod;
						break;
					}
				}
			}
		}
	    if ( method != null && !method.isAccessible() ) method.setAccessible(true);
		return method;
	}
	

}
